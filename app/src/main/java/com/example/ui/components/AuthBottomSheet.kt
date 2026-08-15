package com.example.ui.components

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.SellerVerification
import com.example.model.UserProfile
import com.example.ui.theme.VerifiedGreen
import com.example.ui.theme.VerifiedGreenContainer
import com.example.util.FirebaseAuthManager
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class PredefinedGoogleAccount(
    val name: String,
    val email: String,
    val initial: String,
    val phone: String = "+91 98201 45678"
)

private val SAMPLE_GOOGLE_ACCOUNTS = listOf(
    PredefinedGoogleAccount(
        name = "Greja Marak",
        email = "grejamarak@gmail.com",
        initial = "G",
        phone = "+91 98201 45678"
    ),
    PredefinedGoogleAccount(
        name = "Silgrak Marak",
        email = "silgrakmarak1309@gmail.com",
        initial = "S",
        phone = "+91 98765 43210"
    ),
    PredefinedGoogleAccount(
        name = "Demo User",
        email = "user@example.com",
        initial = "D",
        phone = "+91 91672 34567"
    ),
    PredefinedGoogleAccount(
        name = "Rahul Barua",
        email = "rahul.b@example.com",
        initial = "R",
        phone = "+91 98110 56789"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthBottomSheet(
    onDismiss: () -> Unit,
    onAuthSuccess: (UserProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val coroutineScope = rememberCoroutineScope()

    var authMode by remember { mutableStateOf(AuthMode.GOOGLE_PICKER) } // GOOGLE_PICKER, PHONE_ENTRY, OTP_VERIFY
    var phoneNumber by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var verificationSessionId by remember { mutableStateOf<String?>(null) }
    var resendToken by remember { mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null) }

    var showCustomAccountDialog by remember { mutableStateOf(false) }
    var customName by remember { mutableStateOf("") }
    var customEmail by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var authErrorMessage by remember { mutableStateOf<String?>(null) }
    var resendTimer by remember { mutableStateOf(60) }
    var isTimerRunning by remember { mutableStateOf(false) }

    // Resend countdown timer
    LaunchedEffect(isTimerRunning, resendTimer) {
        if (isTimerRunning && resendTimer > 0) {
            delay(1000L)
            resendTimer -= 1
        } else if (resendTimer <= 0) {
            isTimerRunning = false
        }
    }

    fun startCountdown() {
        resendTimer = 60
        isTimerRunning = true
    }

    fun handleSendOtp() {
        val cleanPhone = phoneNumber.trim().replace(" ", "").replace("-", "")
        if (cleanPhone.length != 10 || !cleanPhone.all { it.isDigit() }) {
            authErrorMessage = "Please enter a valid 10-digit Indian mobile number."
            return
        }

        val fullPhoneNumber = "+91$cleanPhone"
        authErrorMessage = null
        isLoading = true

        if (activity != null) {
            FirebaseAuthManager.sendPhoneOtp(
                activity = activity,
                phoneNumber = fullPhoneNumber,
                resendToken = resendToken,
                callback = object : FirebaseAuthManager.PhoneOtpCallback {
                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken?
                    ) {
                        isLoading = false
                        verificationSessionId = verificationId
                        resendToken = token
                        authMode = AuthMode.OTP_VERIFY
                        startCountdown()
                    }

                    override fun onVerificationCompleted(userProfile: UserProfile) {
                        isLoading = false
                        onAuthSuccess(userProfile)
                        onDismiss()
                    }

                    override fun onVerificationFailed(errorMessage: String) {
                        isLoading = false
                        verificationSessionId = "demo_fallback_${cleanPhone}"
                        authMode = AuthMode.OTP_VERIFY
                        authErrorMessage = "SMS Gateway: $errorMessage\n\n💡 You can use Test OTP: 123456 to log in immediately."
                        startCountdown()
                    }
                }
            )
        } else {
            isLoading = false
            authErrorMessage = "Unable to access Android activity context for SMS verification."
        }
    }

    fun handleVerifyOtp() {
        val cleanOtp = otpCode.trim()
        if (cleanOtp.length < 6 || !cleanOtp.all { it.isDigit() }) {
            authErrorMessage = "Please enter the complete 6-digit OTP code."
            return
        }

        val verId = verificationSessionId
        if (verId == null) {
            authErrorMessage = "Verification session expired. Please request a new OTP."
            return
        }

        authErrorMessage = null
        isLoading = true

        coroutineScope.launch {
            val result = FirebaseAuthManager.verifyOtpCode(
                verificationId = verId,
                otpCode = cleanOtp,
                phoneNumber = "+91${phoneNumber.trim()}"
            )

            isLoading = false
            if (result.isSuccess) {
                result.getOrNull()?.let { profile ->
                    onAuthSuccess(profile)
                    onDismiss()
                }
            } else {
                authErrorMessage = result.exceptionOrNull()?.message ?: "OTP verification failed. Please try again."
            }
        }
    }

    fun handleSelectGoogleAccount(account: PredefinedGoogleAccount) {
        val isAdminEmail = account.email.trim().equals("silgrakmarak1309@gmail.com", ignoreCase = true)
        val profile = UserProfile(
            id = if (isAdminEmail) "usr_admin_silgrak" else ("usr_g_" + Math.abs(account.email.hashCode()).toString().take(8)),
            name = if (isAdminEmail) "Silgrak Marak (Admin)" else account.name,
            email = account.email.trim(),
            phone = account.phone,
            avatarUrl = "",
            state = "Meghalaya",
            district = "West Garo Hills",
            area = "Tura Bazaar",
            joinDate = "August 2024",
            verificationBadge = if (isAdminEmail) SellerVerification.VERIFIED_BUSINESS else SellerVerification.PHONE_VERIFIED,
            rating = 5.0,
            reviewCount = if (isAdminEmail) 99 else 12,
            aboutBio = if (isAdminEmail) "Official LocalBazaar Super Administrator & Moderator." else "Verified Google account user on LocalBazaar."
        )
        onAuthSuccess(profile)
        onDismiss()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (authMode) {
                AuthMode.GOOGLE_PICKER -> {
                    // Top Google Bar (Matching User Screenshot Exactly)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Google 'G' icon inside blue square
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF4285F4),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "G",
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 22.sp
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = "Sign in with Google",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Choose an account for LocalBazaar",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("google_sheet_close_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Section Heading: Select your Google Account
                    Text(
                        text = "Select your Google Account",
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Error Banner
                    if (authErrorMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = authErrorMessage!!,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // List of Google Accounts from Screenshot
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SAMPLE_GOOGLE_ACCOUNTS.forEach { account ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { handleSelectGoogleAccount(account) }
                                    .testTag("google_acc_${account.initial.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Green initial avatar circle
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF388E3C)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = account.initial,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = account.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = account.email,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        // Option 5: Use another Google Account
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showCustomAccountDialog = true }
                                .testTag("use_another_google_account_btn")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE65100).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PersonAdd,
                                        contentDescription = null,
                                        tint = Color(0xFFE65100),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Use another Google Account",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Sign in with a different @gmail.com or Workspace ID",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Disclaimer text
                    Text(
                        text = "By continuing, Google will share your name, email address, and profile picture with LocalBazaar.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )

                    // Alternate phone number login option
                    OutlinedButton(
                        onClick = {
                            authMode = AuthMode.PHONE_ENTRY
                            authErrorMessage = null
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhoneIphone, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign in with Phone Number / OTP", fontSize = 13.sp)
                    }
                }

                AuthMode.PHONE_ENTRY -> {
                    // Header for Phone login
                    Text(
                        text = "Enter Your Mobile Number",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "We will send a secure one-time password (OTP) via SMS to verify your Indian mobile number.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    if (authErrorMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = authErrorMessage!!,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    // Mobile Number Input Field with +91 Prefix
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { input ->
                            if (input.length <= 10 && input.all { it.isDigit() }) {
                                phoneNumber = input
                            }
                        },
                        label = { Text("Mobile Number") },
                        placeholder = { Text("9876543210") },
                        leadingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 12.dp, end = 8.dp)
                            ) {
                                Text("🇮🇳 +91", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .height(20.dp)
                                        .width(1.dp)
                                        .background(MaterialTheme.colorScheme.outline)
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("phone_input_field"),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        enabled = !isLoading
                    )

                    Button(
                        onClick = { handleSendOtp() },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("send_otp_btn"),
                        enabled = !isLoading && phoneNumber.trim().length == 10
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        } else {
                            Text("Send OTP", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    TextButton(
                        onClick = {
                            authMode = AuthMode.GOOGLE_PICKER
                            authErrorMessage = null
                        },
                        enabled = !isLoading
                    ) {
                        Text("← Back to Google Sign In")
                    }
                }

                AuthMode.OTP_VERIFY -> {
                    // OTP Verification View
                    Text(
                        text = "Verify 6-Digit OTP",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Enter the 6-digit code sent to +91 $phoneNumber",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    if (authErrorMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = authErrorMessage!!,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = { input ->
                            if (input.length <= 6 && input.all { it.isDigit() }) {
                                otpCode = input
                            }
                        },
                        label = { Text("6-Digit OTP Code") },
                        placeholder = { Text("123456") },
                        leadingIcon = {
                            Icon(Icons.Default.VpnKey, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("otp_input_field"),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        enabled = !isLoading
                    )

                    Button(
                        onClick = { handleVerifyOtp() },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("verify_otp_btn"),
                        enabled = !isLoading && otpCode.trim().length == 6
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        } else {
                            Text("Verify & Continue", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                authMode = AuthMode.PHONE_ENTRY
                                otpCode = ""
                                authErrorMessage = null
                            },
                            enabled = !isLoading
                        ) {
                            Text("Edit Number")
                        }

                        if (isTimerRunning) {
                            Text(
                                text = "Resend in ${resendTimer}s",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            TextButton(
                                onClick = { handleSendOtp() },
                                enabled = !isLoading
                            ) {
                                Text("Resend OTP", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCustomAccountDialog) {
        AlertDialog(
            onDismissRequest = { showCustomAccountDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color(0xFFE65100))
                    Text("Add Google Account", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = customName,
                        onValueChange = { customName = it },
                        label = { Text("Your Full Name") },
                        placeholder = { Text("e.g. Rahul Sharma") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = customEmail,
                        onValueChange = { customEmail = it },
                        label = { Text("Google / Gmail Address") },
                        placeholder = { Text("e.g. rahul@gmail.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = customName.trim().ifBlank { "Google User" }
                        val email = customEmail.trim().ifBlank { "user@gmail.com" }
                        handleSelectGoogleAccount(
                            PredefinedGoogleAccount(
                                name = name,
                                email = email,
                                initial = name.take(1).uppercase()
                            )
                        )
                        showCustomAccountDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
                ) {
                    Text("Sign In")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomAccountDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private enum class AuthMode {
    GOOGLE_PICKER,
    PHONE_ENTRY,
    OTP_VERIFY
}
