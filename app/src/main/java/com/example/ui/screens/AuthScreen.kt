package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.UserAccount

@Composable
fun AuthScreen(
  authTab: String,
  onAuthTabChange: (String) -> Unit,
  onGoogleSignInClick: () -> Unit,
  onEmailSignIn: (email: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var email by remember { mutableStateOf("silgrakmarak1309@gmail.com") }
  var password by remember { mutableStateOf("••••••••") }
  var phone by remember { mutableStateOf("") }
  var isSignUp by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFE0F2FE)) // Light soft cyan/blue canvas matching screenshot
      .verticalScroll(rememberScrollState()),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top Logo
      Box(
        modifier = Modifier
          .size(76.dp)
          .clip(RoundedCornerShape(18.dp))
          .background(Color(0xFF15803D))
      ) {
        Image(
          painter = painterResource(id = R.drawable.meri_local_bazaar_logo_1786770166058),
          contentDescription = "LocalBazaar Logo",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Title & Subtitle
      Text(
        text = "LocalBazaar",
        fontSize = 26.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color(0xFF0F172A)
      )
      Spacer(modifier = Modifier.height(3.dp))
      Text(
        text = "India\'s Hyper-Local Marketplace & Classifieds",
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF0284C7)
      )

      Spacer(modifier = Modifier.height(24.dp))

      // White Auth Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("auth_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Tab Switcher [ Email / Google ] | [ Phone OTP ]
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFF1F5F9)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
            ) {
              // Email / Google Tab
              Surface(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .clickable { onAuthTabChange("EMAIL_GOOGLE") }
                  .testTag("auth_tab_email_google"),
                color = if (authTab == "EMAIL_GOOGLE") Color.White else Color.Transparent,
                shape = RoundedCornerShape(8.dp),
                shadowElevation = if (authTab == "EMAIL_GOOGLE") 2.dp else 0.dp
              ) {
                Box(
                  modifier = Modifier.padding(vertical = 10.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "Email / Google",
                    fontSize = 13.sp,
                    fontWeight = if (authTab == "EMAIL_GOOGLE") FontWeight.Bold else FontWeight.Medium,
                    color = if (authTab == "EMAIL_GOOGLE") Color(0xFF0284C7) else Color(0xFF64748B)
                  )
                }
              }

              // Phone OTP Tab
              Surface(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .clickable { onAuthTabChange("PHONE_OTP") }
                  .testTag("auth_tab_phone_otp"),
                color = if (authTab == "PHONE_OTP") Color.White else Color.Transparent,
                shape = RoundedCornerShape(8.dp),
                shadowElevation = if (authTab == "PHONE_OTP") 2.dp else 0.dp
              ) {
                Box(
                  modifier = Modifier.padding(vertical = 10.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "Phone OTP",
                    fontSize = 13.sp,
                    fontWeight = if (authTab == "PHONE_OTP") FontWeight.Bold else FontWeight.Medium,
                    color = if (authTab == "PHONE_OTP") Color(0xFF0284C7) else Color(0xFF64748B)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          if (authTab == "EMAIL_GOOGLE") {
            // Google Sign In Button
            OutlinedButton(
              onClick = onGoogleSignInClick,
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("google_sign_in_button"),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF1E293B)
              )
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.AccountCircle,
                  contentDescription = null,
                  tint = Color(0xFF0284C7),
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Sign in with Google Account",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Divider: or sign in with email
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Divider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
              Text(
                text = "  or sign in with email  ",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
              )
              Divider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Email Address Input
            OutlinedTextField(
              value = email,
              onValueChange = { email = it },
              placeholder = {
                Text(
                  "Email Address (e.g. silgrakmarak1309@gmail.com)",
                  fontSize = 12.sp,
                  color = Color(0xFF94A3B8)
                )
              },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("email_input_field"),
              shape = RoundedCornerShape(10.dp),
              colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedBorderColor = Color(0xFF0284C7)
              )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password Input
            OutlinedTextField(
              value = password,
              onValueChange = { password = it },
              placeholder = {
                Text(
                  "Password (min 4 characters)",
                  fontSize = 12.sp,
                  color = Color(0xFF94A3B8)
                )
              },
              singleLine = true,
              visualTransformation = PasswordVisualTransformation(),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("password_input_field"),
              shape = RoundedCornerShape(10.dp),
              colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedBorderColor = Color(0xFF0284C7)
              )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Sign In Orange Button
            Button(
              onClick = { onEmailSignIn(email) },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("auth_submit_button"),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF97316) // Vibrant Orange
              )
            ) {
              Text(
                text = if (isSignUp) "Create Account" else "Sign In",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign Up link
            Text(
              text = if (isSignUp) "Already have an account? Sign In" else "Don\'t have an account? Sign Up",
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF0284C7),
              modifier = Modifier
                .clickable { isSignUp = !isSignUp }
                .testTag("toggle_signup_link")
            )
          } else {
            // Phone OTP Flow
            OutlinedTextField(
              value = phone,
              onValueChange = { phone = it },
              placeholder = { Text("+91 Enter 10 digit phone number", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
              singleLine = true,
              leadingIcon = {
                Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = Color(0xFF0284C7))
              },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("phone_input_field"),
              shape = RoundedCornerShape(10.dp),
              colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedBorderColor = Color(0xFF0284C7)
              )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
              onClick = { onEmailSignIn("phone_user@localbazaar.com") },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("send_otp_button"),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF97316)
              )
            ) {
              Text(
                text = "Send Instant OTP",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }
      }
    }
  }
}
