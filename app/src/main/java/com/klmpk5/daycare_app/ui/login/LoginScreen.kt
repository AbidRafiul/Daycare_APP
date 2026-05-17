package com.klmpk5.daycare_app.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.klmpk5.daycare_app.R
import com.klmpk5.daycare_app.ui.theme.DaycareBackground
import com.klmpk5.daycare_app.ui.theme.DaycareBorder
import com.klmpk5.daycare_app.ui.theme.DaycareDisabled
import com.klmpk5.daycare_app.ui.theme.DaycarePrimary
import com.klmpk5.daycare_app.ui.theme.DaycarePrimaryLight
import com.klmpk5.daycare_app.ui.theme.DaycareSoftMint
import com.klmpk5.daycare_app.ui.theme.DaycareTextPrimary
import com.klmpk5.daycare_app.ui.theme.DaycareTextSecondary
import com.klmpk5.daycare_app.viewModel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: (uid: String, email: String) -> Unit
) {
    var currentAuthScreen by remember { mutableStateOf("login") }
    val loginSuccessUid = viewModel.loginSuccessUid
    val loginSuccessEmail = viewModel.loginSuccessEmail

    LaunchedEffect(loginSuccessUid, loginSuccessEmail) {
        val uid = loginSuccessUid
        if (!uid.isNullOrBlank()) {
            onLoginSuccess(uid, loginSuccessEmail.orEmpty())
        }
    }

    LaunchedEffect(viewModel.successMessage) {
        if (
            currentAuthScreen == "register" &&
            viewModel.successMessage.contains("berhasil", ignoreCase = true)
        ) {
            currentAuthScreen = "login"
        }
    }

    when (currentAuthScreen) {
        "register" -> ParentRegisterScreen(
            isLoading = viewModel.isLoading,
            errorMessage = viewModel.errorMessage,
            successMessage = viewModel.successMessage,
            onRegisterClick = viewModel::registerParent,
            onBackToLoginClick = {
                viewModel.resetState()
                currentAuthScreen = "login"
            }
        )

        else -> ParentLoginScreen(
            isLoading = viewModel.isLoading,
            errorMessage = viewModel.errorMessage,
            successMessage = viewModel.successMessage,
            onLoginClick = viewModel::login,
            onForgotPasswordClick = viewModel::sendResetPassword,
            onRegisterClick = {
                viewModel.resetState()
                currentAuthScreen = "register"
            }
        )
    }
}

/**
 * Login screen untuk aplikasi Daycare sisi Parent.
 *
 * Fitur UI:
 * - Login email dan password
 * - Lupa password
 * - Navigasi ke Register
 *
 * Logic Firebase nanti dipanggil lewat callback:
 * - onLoginClick
 * - onForgotPasswordClick
 */
@Composable
fun ParentLoginScreen(
    isLoading: Boolean = false,
    errorMessage: String? = null,
    successMessage: String? = null,
    onLoginClick: (email: String, password: String) -> Unit,
    onForgotPasswordClick: (email: String) -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DaycareSoftMint,
                        DaycareBackground
                    )
                )
            )
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            Box(
                modifier = Modifier
                    .size(92.dp)
                    .background(
                        color = DaycarePrimaryLight,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "DC",
                    fontSize = 44.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Daycare Parent",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = DaycareTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Pantau aktivitas dan perkembangan anak Anda",
                fontSize = 14.sp,
                color = DaycareTextSecondary
            )

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(28.dp),
                        spotColor = Color(0x22000000)
                    ),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.daykids_logo),
                        contentDescription = "DayKids Logo",
                        modifier = Modifier
                            .size(112.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Selamat Datang",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = DaycareTextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Masuk sebagai Orang Tua",
                        fontSize = 14.sp,
                        color = DaycareTextSecondary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    AuthTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        placeholder = "parent@email.com",
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Password") },
                        placeholder = { Text("Masukkan password") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            TextButton(
                                onClick = { passwordVisible = !passwordVisible }
                            ) {
                                Text(
                                    text = if (passwordVisible) "Hide" else "Show",
                                    color = DaycarePrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DaycarePrimary,
                            unfocusedBorderColor = DaycareBorder,
                            focusedLabelColor = DaycarePrimary,
                            cursorColor = DaycarePrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                onForgotPasswordClick(email)
                            }
                        ) {
                            Text(
                                text = "Lupa Password?",
                                color = DaycarePrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (!errorMessage.isNullOrBlank()) {
                        Text(
                            text = errorMessage,
                            color = Color(0xFFB91C1C),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (!successMessage.isNullOrBlank()) {
                        Text(
                            text = successMessage,
                            color = DaycarePrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Button(
                        onClick = {
                            onLoginClick(email, password)
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DaycarePrimary,
                            disabledContainerColor = DaycareDisabled
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "Masuk",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Belum punya akun?",
                            fontSize = 13.sp,
                            color = DaycareTextSecondary
                        )

                        TextButton(
                            onClick = onRegisterClick
                        ) {
                            Text(
                                text = "Daftar",
                                color = DaycarePrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

/**
 * Register screen untuk aplikasi Daycare sisi Parent.
 *
 * Fitur UI:
 * - Nama lengkap parent
 * - Email
 * - No. WhatsApp
 * - Password
 * - Konfirmasi password
 *
 * Role parent nantinya bisa diset otomatis di Firebase/Firestore.
 */
@Composable
fun ParentRegisterScreen(
    isLoading: Boolean = false,
    errorMessage: String? = null,
    successMessage: String? = null,
    onRegisterClick: (
        fullName: String,
        email: String,
        phoneNumber: String,
        password: String,
        confirmPassword: String
    ) -> Unit,
    onBackToLoginClick: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DaycareSoftMint,
                        DaycareBackground
                    )
                )
            )
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(84.dp)
                    .background(
                        color = DaycarePrimaryLight,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "DC",
                    fontSize = 40.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Daftar Akun Parent",
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                color = DaycareTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Buat akun untuk memantau aktivitas anak",
                fontSize = 14.sp,
                color = DaycareTextSecondary
            )

            Spacer(modifier = Modifier.height(26.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(28.dp),
                        spotColor = Color(0x22000000)
                    ),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    AuthTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = "Nama Lengkap",
                        placeholder = "Contoh: Siti Rahma"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    AuthTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        placeholder = "parent@email.com",
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    AuthTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = "No. WhatsApp",
                        placeholder = "0812-3456-7890",
                        keyboardType = KeyboardType.Phone
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    PasswordAuthField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        passwordVisible = passwordVisible,
                        onToggleVisibility = {
                            passwordVisible = !passwordVisible
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    PasswordAuthField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Konfirmasi Password",
                        passwordVisible = confirmPasswordVisible,
                        onToggleVisibility = {
                            confirmPasswordVisible = !confirmPasswordVisible
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        color = DaycarePrimaryLight.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            text = "Setelah daftar, akun parent dipakai untuk melihat data anak, presensi, aktivitas, dan raport.",
                            modifier = Modifier.padding(16.dp),
                            fontSize = 13.sp,
                            color = DaycareTextSecondary,
                            lineHeight = 18.sp
                        )
                    }

                    if (!errorMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = errorMessage,
                            color = Color(0xFFB91C1C),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (!successMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = successMessage,
                            color = DaycarePrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Button(
                        onClick = {
                            onRegisterClick(
                                fullName,
                                email,
                                phoneNumber,
                                password,
                                confirmPassword
                            )
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DaycarePrimary,
                            disabledContainerColor = DaycareDisabled
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "Daftar Sekarang",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sudah punya akun?",
                            fontSize = 13.sp,
                            color = DaycareTextSecondary
                        )

                        TextButton(
                            onClick = onBackToLoginClick
                        ) {
                            Text(
                                text = "Masuk",
                                color = DaycarePrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(label)
        },
        placeholder = {
            Text(placeholder)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DaycarePrimary,
            unfocusedBorderColor = DaycareBorder,
            focusedLabelColor = DaycarePrimary,
            cursorColor = DaycarePrimary
        )
    )
}

@Composable
fun PasswordAuthField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    passwordVisible: Boolean,
    onToggleVisibility: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(label)
        },
        singleLine = true,
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            TextButton(
                onClick = onToggleVisibility
            ) {
                Text(
                    text = if (passwordVisible) "Hide" else "Show",
                    color = DaycarePrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DaycarePrimary,
            unfocusedBorderColor = DaycareBorder,
            focusedLabelColor = DaycarePrimary,
            cursorColor = DaycarePrimary
        )
    )
}
