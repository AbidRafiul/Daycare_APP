package com.klmpk5.daycare_app.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klmpk5.daycare_app.ui.login.AuthTextField
import com.klmpk5.daycare_app.ui.login.PasswordAuthField
import com.klmpk5.daycare_app.ui.theme.DaycareBackground
import com.klmpk5.daycare_app.ui.theme.DaycareDisabled
import com.klmpk5.daycare_app.ui.theme.DaycarePrimary
import com.klmpk5.daycare_app.ui.theme.DaycarePrimaryLight
import com.klmpk5.daycare_app.ui.theme.DaycareSoftMint
import com.klmpk5.daycare_app.ui.theme.DaycareTextPrimary
import com.klmpk5.daycare_app.ui.theme.DaycareTextSecondary

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
