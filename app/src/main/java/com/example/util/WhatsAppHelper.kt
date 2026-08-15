package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.graphics.Color

val WhatsAppGreenColor = Color(0xFF25D366)

object WhatsAppHelper {
    /**
     * Launches WhatsApp chat with the specified phone number and optional message.
     */
    fun openWhatsAppChat(context: Context, phoneNumber: String, message: String = "") {
        try {
            val cleanPhone = phoneNumber.replace(Regex("[^0-9]"), "")
            val finalPhone = when {
                cleanPhone.length == 10 -> "91$cleanPhone"
                cleanPhone.startsWith("0") && cleanPhone.length == 11 -> "91${cleanPhone.drop(1)}"
                else -> cleanPhone
            }
            val encodedMsg = java.net.URLEncoder.encode(message, "UTF-8")
            val url = if (finalPhone.isNotBlank()) {
                "https://api.whatsapp.com/send?phone=$finalPhone&text=$encodedMsg"
            } else {
                "https://api.whatsapp.com/send?text=$encodedMsg"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "WhatsApp could not be opened. Please make sure WhatsApp is installed.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Returns a representative phone number for sample sellers.
     */
    fun getPhoneForSeller(sellerId: String): String {
        return when (sellerId) {
            "usr_rohit_m" -> "+91 98201 45678"
            "usr_priya_s" -> "+91 98332 98765"
            "usr_amit_v" -> "+91 91672 34567"
            "usr_neha_k" -> "+91 99203 12345"
            "usr_vikram_p" -> "+91 98450 87654"
            "usr_ananya_b" -> "+91 97401 23456"
            "usr_rahul_d" -> "+91 98110 56789"
            "usr_pooja_n" -> "+91 99100 43210"
            else -> "+91 98765 43210"
        }
    }
}
