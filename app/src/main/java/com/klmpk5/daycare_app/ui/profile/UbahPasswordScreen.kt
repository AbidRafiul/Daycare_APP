package com.klmpk5.daycare_app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klmpk5.daycare_app.ui.theme.DaycareBackground
import com.klmpk5.daycare_app.ui.theme.DaycarePrimary
import com.klmpk5.daycare_app.ui.theme.DaycarePrimaryLight
import com.klmpk5.daycare_app.ui.theme.DaycareTextSecondary
import com.klmpk5.daycare_app.viewModel.ChangePasswordState
import com.klmpk5.daycare_app.viewModel.ProfileViewModel

@Composable
fun UbahPasswordPage(
    profileViewModel: ProfileViewModel,
    contentPadding: PaddingValues,
    onBack: () -> Unit
) {
    val changeState by profileViewModel.changePasswordState.collectAsState()

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var oldVisible by remember { mutableStateOf(false) }
    var newVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(changeState) {
        when (val state = changeState) {
            ChangePasswordState.Success -> {
                message = "Password berhasil diperbarui"
                oldPassword = ""
                newPassword = ""
                confirmPassword = ""
                profileViewModel.resetChangePasswordState()
            }

            is ChangePasswordState.Error -> {
                message = state.message
                profileViewModel.resetChangePasswordState()
            }

            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DaycareBackground)
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
    ) {
        ProfileSubHeader(
            title = "Ubah Password",
            subtitle = "Perbarui password akun Anda",
            marker = "P",
            onBack = onBack
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .offset(y = (-36).dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(color = DaycarePrimaryLight, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "PW",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = DaycarePrimary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                ProfileEditTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = "Password Lama",
                    placeholder = "Masukkan password lama",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (oldVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        PasswordToggleButton(
                            visible = oldVisible,
                            onClick = { oldVisible = !oldVisible }
                        )
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                ProfileEditTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = "Password Baru",
                    placeholder = "Minimal 6 karakter",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (newVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        PasswordToggleButton(
                            visible = newVisible,
                            onClick = { newVisible = !newVisible }
                        )
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                ProfileEditTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Konfirmasi Password",
                    placeholder = "Ulangi password baru",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        PasswordToggleButton(
                            visible = confirmVisible,
                            onClick = { confirmVisible = !confirmVisible }
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = DaycarePrimaryLight.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        text = "Untuk keamanan, Firebase meminta password lama sebelum mengganti password baru.",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 13.sp,
                        color = DaycareTextSecondary,
                        lineHeight = 18.sp
                    )
                }

                if (!message.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = message.orEmpty(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (message?.contains("berhasil", true) == true) {
                            DaycarePrimary
                        } else {
                            Color(0xFFB91C1C)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        profileViewModel.changePassword(
                            oldPassword = oldPassword,
                            newPassword = newPassword,
                            confirmPassword = confirmPassword
                        )
                    },
                    enabled = changeState !is ChangePasswordState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DaycarePrimary,
                        disabledContainerColor = DaycarePrimary.copy(alpha = 0.45f)
                    )
                ) {
                    if (changeState is ChangePasswordState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "Simpan Password",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PasswordToggleButton(
    visible: Boolean,
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        Text(
            text = if (visible) "Hide" else "Show",
            color = DaycarePrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
