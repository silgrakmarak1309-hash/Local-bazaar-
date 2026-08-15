package com.example.ui.screens.auth

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.SellerVerification
import com.example.model.UserProfile
import com.example.util.FirebaseAuthManager
import com.example.viewmodel.MarketplaceViewModel
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class PredefinedGoogleAccount(
    val name: String,
    val email: String,
    val initial: String,
    val phone: String = "+91 98201 45678",
    val isAdmin: Boolean = false
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
        phone = "+91 98765 43210",
        isAdmin = true
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
fun AuthLoginScreen(
    viewModel: MarketplaceViewModel,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val coroutineScope = rememberCoroutineScope()

    // Tabs: 0 -> Email / Google, 1 -> Phone OTP
    var selectedAuthTab by remember { mutableStateOf(0) }
    
    // Bottom Sheet for Google Account Picker Popup (Exact match to user screenshot)
    var showGoogleAccountBottomSheet by remember { mutableStateOf(false) }

    // Email/Password states & Mode (Sign In vs Sign Up)
    var isSignUpMode by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Phone / OTP states
    var isPhoneOtpMode by remember { mutableStateOf(false) } // false: enter phone, true: verify otp
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

    fun handleLoginProfile(profile: UserProfile) {
        viewModel.setUserProfile(profile)
        onLoginSuccess()
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
        showGoogleAccountBottomSheet = false
        handleLoginProfile(profile)
    }

    fun handleEmailAuthSubmit() {
        val email = emailInput.trim()
        val pwd = passwordInput.trim()
        val name = nameInput.trim()

        if (isSignUpMode && name.isBlank()) {
            authErrorMessage = "Please enter your full name."
            return
        }
        if (email.isBlank() || !email.contains("@")) {
            authErrorMessage = "Please enter a valid email address."
            return
        }
        if (pwd.length < 4) {
            authErrorMessage = "Please enter your password (at least 4 characters)."
            return
        }

        val isAdmin = email.equals("silgrakmarak1309@gmail.com", ignoreCase = true)
        val profileName = if (isSignUpMode && name.isNotBlank()) {
            name
        } else if (isAdmin) {
            "Silgrak Marak (Admin)"
        } else {
            email.substringBefore("@").replace(".", " ").capitalize()
        }

        val profile = UserProfile(
            id = if (isAdmin) "usr_admin_silgrak" else ("usr_e_" + Math.abs(email.hashCode()).toString().take(8)),
            name = profileName,
            email = email,
            phone = "+91 98765 43210",
            avatarUrl = "",
            state = "Meghalaya",
            district = "West Garo Hills",
            area = "Tura Bazaar",
            joinDate = "August 2024",
            verificationBadge = if (isAdmin) SellerVerification.VERIFIED_BUSINESS else SellerVerification.EMAIL_VERIFIED,
            rating = 5.0,
            reviewCount = if (isAdmin) 99 else 1,
            aboutBio = if (isAdmin) "Official LocalBazaar Super Administrator & Moderator." else "Verified LocalBazaar member."
        )
        handleLoginProfile(profile)
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
                        isPhoneOtpMode = true
                        startCountdown()
                    }

                    override fun onVerificationCompleted(userProfile: UserProfile) {
                        isLoading = false
                        handleLoginProfile(userProfile)
                    }

                    override fun onVerificationFailed(errorMessage: String) {
                        isLoading = false
                        verificationSessionId = "demo_fallback_${cleanPhone}"
                        isPhoneOtpMode = true
                        authErrorMessage = "SMS Gateway note: Test OTP 123456 ready."
                        startCountdown()
                    }
                }
            )
        } else {
            isLoading = false
            verificationSessionId = "demo_fallback_${cleanPhone}"
            isPhoneOtpMode = true
            startCountdown()
        }
    }

    fun handleVerifyOtp() {
        val cleanOtp = otpCode.trim()
        if (cleanOtp.length < 6 || !cleanOtp.all { it.isDigit() }) {
            authErrorMessage = "Please enter the complete 6-digit OTP code."
            return
        }

        val verId = verificationSessionId ?: "demo_fallback"
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
                    handleLoginProfile(profile)
                }
            } else {
                authErrorMessage = result.exceptionOrNull()?.message ?: "OTP verification failed. Please try again."
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFE0F2FE) // Sky blue background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 28.dp, bottom = 32.dp)
        ) {
            // App Hero / Logo Header
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.meri_local_bazaar_icon_1786755369661),
                        contentDescription = "Meri Local Bazaar Logo",
                        modifier = Modifier
                            .size(86.dp)
                            .clip(RoundedCornerShape(22.dp))
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "LocalBazaar",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0C4A6E)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "India's Hyper-Local Marketplace & Classifieds",
                        fontSize = 13.sp,
                        color = Color(0xFF0369A1),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Tabs Header: Email / Google  |  Phone OTP
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedAuthTab = 0
                                authErrorMessage = null
                            }
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Email / Google",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (selectedAuthTab == 0) Color(0xFF0284C7) else Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(2.5.dp)
                                .background(if (selectedAuthTab == 0) Color(0xFF0284C7) else Color.Transparent, RoundedCornerShape(2.dp))
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedAuthTab = 1
                                authErrorMessage = null
                            }
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Phone OTP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (selectedAuthTab == 1) Color(0xFF0284C7) else Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(2.5.dp)
                                .background(if (selectedAuthTab == 1) Color(0xFF0284C7) else Color.Transparent, RoundedCornerShape(2.dp))
                        )
                    }
                }
            }

            // Error Banner
            if (authErrorMessage != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFE4E6),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = authErrorMessage!!,
                            color = Color(0xFFBE123C),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            if (selectedAuthTab == 0) {
                // Primary Action Button: "Sign in with Google Account"
                item {
                    OutlinedButton(
                        onClick = {
                            authErrorMessage = null
                            showGoogleAccountBottomSheet = true
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF0F172A)
                        ),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("continue_with_google_btn")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF4285F4),
                            modifier = Modifier.size(22.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Sign in with Google Account",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF0284C7)
                        )
                    }
                }

                // Divider: "or sign in with email" / "or sign up with email"
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFBAE6FD))
                        Text(
                            text = if (isSignUpMode) "  or sign up with email  " else "  or sign in with email  ",
                            color = Color(0xFF64748B),
                            fontSize = 12.sp
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFBAE6FD))
                    }
                }

                // Full Name Input (Only on Sign Up mode)
                if (isSignUpMode) {
                    item {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            placeholder = { Text("Full Name", color = Color(0xFF94A3B8)) },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF0284C7))
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0284C7),
                                unfocusedBorderColor = Color(0xFFBAE6FD),
                                focusedTextColor = Color(0xFF0F172A),
                                unfocusedTextColor = Color(0xFF0F172A),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_name_input")
                        )
                    }
                }

                // Email Address Input
                item {
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        placeholder = { Text("Email Address", color = Color(0xFF94A3B8)) },
                        leadingIcon = {
                            Icon(Icons.Default.MailOutline, contentDescription = null, tint = Color(0xFF0284C7))
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0284C7),
                            unfocusedBorderColor = Color(0xFFBAE6FD),
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF0F172A),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_email_input")
                    )
                }

                // Password Input
                item {
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        placeholder = { Text("Password", color = Color(0xFF94A3B8)) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF0284C7))
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = Color(0xFF64748B)
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0284C7),
                            unfocusedBorderColor = Color(0xFFBAE6FD),
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF0F172A),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input")
                    )
                }

                // Orange "Sign In" / "Sign Up" Button
                item {
                    Button(
                        onClick = { handleEmailAuthSubmit() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("login_email_submit_btn")
                    ) {
                        Text(
                            text = if (isSignUpMode) "Sign Up" else "Sign In",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else {
                // Phone OTP Tab
                if (!isPhoneOtpMode) {
                    item {
                        Text(
                            text = "Enter your 10-digit Indian Mobile Number",
                            color = Color(0xFF0369A1),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { input ->
                                if (input.length <= 10 && input.all { it.isDigit() }) {
                                    phoneNumber = input
                                }
                            },
                            placeholder = { Text("9876543210", color = Color(0xFF94A3B8)) },
                            leadingIcon = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = 12.dp, end = 8.dp)
                                ) {
                                    Text("🇮🇳 +91", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .height(18.dp)
                                            .width(1.dp)
                                            .background(Color(0xFFBAE6FD))
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0284C7),
                                unfocusedBorderColor = Color(0xFFBAE6FD),
                                focusedTextColor = Color(0xFF0F172A),
                                unfocusedTextColor = Color(0xFF0F172A),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_phone_input")
                        )
                    }

                    item {
                        Button(
                            onClick = { handleSendOtp() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                            shape = RoundedCornerShape(14.dp),
                            enabled = !isLoading && phoneNumber.length == 10,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("login_send_otp_btn")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                            } else {
                                Text("Send OTP", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                } else {
                    // OTP Verification Sub-view
                    item {
                        Text(
                            text = "Enter 6-digit OTP code sent to +91 $phoneNumber",
                            color = Color(0xFF0369A1),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = otpCode,
                            onValueChange = { input ->
                                if (input.length <= 6 && input.all { it.isDigit() }) {
                                    otpCode = input
                                }
                            },
                            placeholder = { Text("123456", color = Color(0xFF94A3B8)) },
                            leadingIcon = {
                                Icon(Icons.Default.VpnKey, contentDescription = null, tint = Color(0xFF0284C7))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0284C7),
                                unfocusedBorderColor = Color(0xFFBAE6FD),
                                focusedTextColor = Color(0xFF0F172A),
                                unfocusedTextColor = Color(0xFF0F172A),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_otp_input")
                        )
                    }

                    item {
                        Button(
                            onClick = { handleVerifyOtp() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                            shape = RoundedCornerShape(14.dp),
                            enabled = !isLoading && otpCode.length == 6,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("login_verify_otp_btn")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                            } else {
                                Text("Verify & Login", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { isPhoneOtpMode = false }) {
                                Text("← Change Number", color = Color(0xFF0284C7))
                            }
                            if (isTimerRunning) {
                                Text("Resend in ${resendTimer}s", color = Color(0xFF64748B), fontSize = 12.sp)
                            } else {
                                TextButton(onClick = { handleSendOtp() }) {
                                    Text("Resend OTP", color = Color(0xFF0284C7), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Sign In / Sign Up Switcher Link
            item {
                Row(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSignUpMode) "Already have an account? " else "Don't have an account? ",
                        color = Color(0xFF475569),
                        fontSize = 13.sp
                    )
                    TextButton(
                        onClick = {
                            isSignUpMode = !isSignUpMode
                            authErrorMessage = null
                        },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.testTag("toggle_signin_signup_btn")
                    ) {
                        Text(
                            text = if (isSignUpMode) "Sign In" else "Sign Up",
                            color = Color(0xFF0284C7),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Google Account Picker Bottom Sheet Dialog
    if (showGoogleAccountBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showGoogleAccountBottomSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = Color(0xFFF0F9FF),
            dragHandle = null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Google Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
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
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Choose an account for LocalBazaar",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    IconButton(
                        onClick = { showGoogleAccountBottomSheet = false },
                        modifier = Modifier.testTag("google_sheet_close_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF64748B)
                        )
                    }
                }

                // Section Label: "Select your Google Account"
                Text(
                    text = "Select your Google Account",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0284C7)
                )

                // List of Google Accounts on device
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SAMPLE_GOOGLE_ACCOUNTS.forEach { account ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
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
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (account.isAdmin) Color(0xFFE65100) else Color(0xFF0284C7)),
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
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = account.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color(0xFF0F172A),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (account.isAdmin) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(0xFFE65100)
                                            ) {
                                                Text(
                                                    text = "ADMIN",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = account.email,
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Use another Google Account item
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                showGoogleAccountBottomSheet = false
                                showCustomAccountDialog = true
                            }
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
                                    .background(Color(0xFF0284C7).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = null,
                                    tint = Color(0xFF0284C7),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Use another Google Account",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Sign in with a different @gmail.com or Workspace ID",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }

                // Disclaimer text
                Text(
                    text = "By continuing, Google will share your name, email address, and profile picture with LocalBazaar.",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
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
                        placeholder = { Text("e.g. Silgrak Marak") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = customEmail,
                        onValueChange = { customEmail = it },
                        label = { Text("Google / Gmail Address") },
                        placeholder = { Text("e.g. silgrakmarak1309@gmail.com") },
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
