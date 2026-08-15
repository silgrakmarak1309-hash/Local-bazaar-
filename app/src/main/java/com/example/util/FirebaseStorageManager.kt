package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.UUID

object FirebaseStorageManager {

    private const val MAX_IMAGE_DIMENSION = 1600
    private const val JPEG_COMPRESSION_QUALITY = 82
    private const val MAX_FILE_SIZE_BYTES = 20 * 1024 * 1024 // 20 MB max before compression

    private val storage: FirebaseStorage by lazy {
        try {
            FirebaseStorage.getInstance("gs://localbazar-cff07.firebasestorage.app")
        } catch (e: Exception) {
            try {
                FirebaseStorage.getInstance()
            } catch (e2: Exception) {
                FirebaseStorage.getInstance()
            }
        }
    }

    /**
     * Caches a selected Content Uri into the app's local cache directory.
     * This avoids SecurityExceptions / permission revocations when accessing the image across screens.
     */
    fun cacheImageUri(context: Context, sourceUri: Uri): Uri {
        if (sourceUri.scheme == "file") return sourceUri
        return try {
            val cacheDir = java.io.File(context.cacheDir, "listing_images").apply { mkdirs() }
            val ext = when (context.contentResolver.getType(sourceUri)) {
                "image/png" -> "png"
                "image/webp" -> "webp"
                else -> "jpg"
            }
            val cacheFile = java.io.File(cacheDir, "img_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.$ext")
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                cacheFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(cacheFile)
        } catch (e: Exception) {
            sourceUri
        }
    }

    /**
     * Validates that the given Uri represents a valid image file within allowed size limits.
     */
    fun validateImage(context: Context, uri: Uri): Result<Unit> {
        return try {
            val uriStr = uri.toString()
            val mimeType = context.contentResolver.getType(uri)
            val isImageMime = mimeType?.startsWith("image/") ?: true

            if (!isImageMime && !uriStr.endsWith(".jpg", true) &&
                !uriStr.endsWith(".jpeg", true) &&
                !uriStr.endsWith(".png", true) &&
                !uriStr.endsWith(".webp", true) &&
                !uriStr.contains("image", true)
            ) {
                return Result.failure(IllegalArgumentException("Unsupported file type. Please select a valid image (JPEG, PNG, WebP)."))
            }

            var fileSize: Long = -1
            try {
                if (uri.scheme == "file") {
                    val path = uri.path
                    if (path != null) {
                        fileSize = java.io.File(path).length()
                    }
                } else {
                    context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { afd ->
                        fileSize = afd.length
                    }
                }
            } catch (_: Exception) {}

            if (fileSize > MAX_FILE_SIZE_BYTES) {
                return Result.failure(IllegalArgumentException("Image is too large (max 20MB). Please select a smaller file."))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit) // Allow soft-pass if descriptor cannot be read
        }
    }

    /**
     * Compresses the selected image URI to an optimized JPEG byte array.
     */
    suspend fun compressImage(context: Context, uri: Uri): ByteArray = withContext(Dispatchers.IO) {
        var inputStream: InputStream? = null
        try {
            // First decode with inJustDecodeBounds = true to check dimensions
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            val srcWidth = options.outWidth
            val srcHeight = options.outHeight

            // Calculate sample size
            var inSampleSize = 1
            if (srcHeight > MAX_IMAGE_DIMENSION || srcWidth > MAX_IMAGE_DIMENSION) {
                val halfHeight = srcHeight / 2
                val halfWidth = srcWidth / 2
                while ((halfHeight / inSampleSize) >= MAX_IMAGE_DIMENSION && (halfWidth / inSampleSize) >= MAX_IMAGE_DIMENSION) {
                    inSampleSize *= 2
                }
            }

            // Decode bitmap with inSampleSize
            val decodeOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            inputStream = context.contentResolver.openInputStream(uri)
            var bitmap = BitmapFactory.decodeStream(inputStream, null, decodeOptions)
                ?: throw IllegalStateException("Could not decode image.")

            // Handle Exif rotation
            try {
                context.contentResolver.openInputStream(uri)?.use { exifStream ->
                    val exif = ExifInterface(exifStream)
                    val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                    val rotationAngle = when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                        else -> 0f
                    }
                    if (rotationAngle != 0f) {
                        val matrix = Matrix().apply { postRotate(rotationAngle) }
                        val rotatedBitmap = Bitmap.createBitmap(
                            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                        )
                        if (rotatedBitmap != bitmap) {
                            bitmap.recycle()
                            bitmap = rotatedBitmap
                        }
                    }
                }
            } catch (_: Exception) {
                // Ignore exif failure and proceed with decoded bitmap
            }

            // Scale down if still larger than max dimensions
            val currentWidth = bitmap.width
            val currentHeight = bitmap.height
            if (currentWidth > MAX_IMAGE_DIMENSION || currentHeight > MAX_IMAGE_DIMENSION) {
                val ratio = minOf(
                    MAX_IMAGE_DIMENSION.toFloat() / currentWidth,
                    MAX_IMAGE_DIMENSION.toFloat() / currentHeight
                )
                val destWidth = (currentWidth * ratio).toInt()
                val destHeight = (currentHeight * ratio).toInt()
                val scaled = Bitmap.createScaledBitmap(bitmap, destWidth, destHeight, true)
                if (scaled != bitmap) {
                    bitmap.recycle()
                    bitmap = scaled
                }
            }

            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_COMPRESSION_QUALITY, outputStream)
            bitmap.recycle()
            outputStream.toByteArray()
        } finally {
            try {
                inputStream?.close()
            } catch (_: Exception) {}
        }
    }

    /**
     * Uploads a single image to Firebase Storage under listings/{userId}/{listingId}/{imageId}.
     * Returns the Firebase Storage public download URL.
     */
    suspend fun uploadListingImage(
        context: Context,
        userId: String,
        listingId: String,
        imageUriString: String,
        index: Int
    ): String = withContext(Dispatchers.IO) {
        // If it's already an HTTP URL or local static asset, don't re-upload
        if (imageUriString.startsWith("http://") || imageUriString.startsWith("https://") ||
            imageUriString == "localbazaar_hero" || imageUriString == "localbazaar_logo"
        ) {
            return@withContext imageUriString
        }

        val uri = Uri.parse(imageUriString)
        val validation = validateImage(context, uri)
        if (validation.isFailure) {
            throw validation.exceptionOrNull() ?: IllegalArgumentException("Invalid image file")
        }

        val compressedBytes = compressImage(context, uri)
        val imageId = "img_${System.currentTimeMillis()}_${index}_${UUID.randomUUID().toString().take(4)}.jpg"
        
        // Path: listings/{userId}/{listingId}/{imageId}
        val storageRef = storage.reference
            .child("listings")
            .child(userId)
            .child(listingId)
            .child(imageId)

        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .setCustomMetadata("uploadedBy", userId)
            .setCustomMetadata("listingId", listingId)
            .build()

        try {
            val uploadTask = storageRef.putBytes(compressedBytes, metadata).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            downloadUrl
        } catch (e: Exception) {
            // Provide clear, actionable error message
            val msg = e.localizedMessage ?: e.message ?: "Firebase Storage error"
            throw IllegalStateException("Failed to upload photo #$index to Firebase Storage ($msg). Please check network and try again.")
        }
    }

    /**
     * Uploads a list of images to Firebase Storage sequentially, invoking a progress callback.
     */
    suspend fun uploadAllListingImages(
        context: Context,
        userId: String,
        listingId: String,
        images: List<String>,
        onProgress: (current: Int, total: Int) -> Unit = { _, _ -> }
    ): List<String> = withContext(Dispatchers.IO) {
        val resultUrls = mutableListOf<String>()
        val total = images.size

        images.forEachIndexed { index, imgUri ->
            onProgress(index + 1, total)
            val url = uploadListingImage(
                context = context,
                userId = userId,
                listingId = listingId,
                imageUriString = imgUri,
                index = index + 1
            )
            resultUrls.add(url)
        }

        resultUrls
    }
}
