package com.klmpk5.daycare_app.ui.profile

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klmpk5.daycare_app.data.local.entities.User
import com.klmpk5.daycare_app.ui.theme.DaycareBackground
import com.klmpk5.daycare_app.ui.theme.DaycareBorder
import com.klmpk5.daycare_app.ui.theme.DaycarePrimary
import com.klmpk5.daycare_app.ui.theme.DaycarePrimaryLight
import com.klmpk5.daycare_app.ui.theme.DaycareTextPrimary
import com.klmpk5.daycare_app.ui.theme.DaycareTextSecondary
import com.klmpk5.daycare_app.viewModel.ProfileUpdateState
import com.klmpk5.daycare_app.viewModel.ProfileViewModel

@Composable
fun EditProfilePage(
    profileViewModel: ProfileViewModel,
    contentPadding: PaddingValues,
    onBack: () -> Unit
) {
    val uid = profileViewModel.getCurrentUid()
    val userFlow = remember(uid) {
        uid?.let { profileViewModel.getUserProfile(it) }
    }
    val emptyUserState = remember { mutableStateOf<User?>(null) }
    val user by userFlow?.collectAsState(initial = null) ?: emptyUserState
    val updateState by profileViewModel.profileUpdateState.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uid) {
        uid?.let { profileViewModel.syncUserProfile(it) }
    }

    LaunchedEffect(user) {
        user?.let {
            fullName = it.fullName
            phoneNumber = it.phoneNumber.orEmpty()
        }
    }

    LaunchedEffect(updateState) {
        when (val state = updateState) {
            ProfileUpdateState.Success -> {
                message = "Profile berhasil diperbarui"
                profileViewModel.resetProfileUpdateState()
            }

            is ProfileUpdateState.Error -> {
                message = state.message
                profileViewModel.resetProfileUpdateState()
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
            title = "Edit Profile",
            subtitle = "Ubah nama dan nomor HP",
            marker = "E",
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
                        text = fullName.take(1).ifBlank { "P" }.uppercase(),
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = DaycarePrimary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                ProfileEditTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = "Nama Lengkap",
                    placeholder = "Masukkan nama lengkap"
                )

                Spacer(modifier = Modifier.height(14.dp))

                ProfileEditTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = "No. WhatsApp",
                    placeholder = "0812-3456-7890",
                    keyboardType = KeyboardType.Phone
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = DaycarePrimaryLight.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        text = "Pastikan nomor WhatsApp aktif agar pihak daycare dapat menghubungi Anda dengan mudah.",
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
                        val currentUser = user
                        if (currentUser != null) {
                            profileViewModel.updateProfile(
                                currentUser = currentUser,
                                fullName = fullName,
                                phoneNumber = phoneNumber
                            )
                        } else {
                            message = "Data user belum tersedia"
                        }
                    },
                    enabled = updateState !is ProfileUpdateState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DaycarePrimary,
                        disabledContainerColor = DaycarePrimary.copy(alpha = 0.45f)
                    )
                ) {
                    if (updateState is ProfileUpdateState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "Simpan",
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
fun ProfileSubHeader(
    title: String,
    subtitle: String,
    marker: String,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(DaycarePrimary, Color(0xFF23897D))
                )
            )
            .padding(horizontal = 20.dp)
    ) {
        TextButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Text(text = "< Kembali", color = Color.White, fontWeight = FontWeight.SemiBold)
        }

        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(marker, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ProfileEditTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        singleLine = true,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DaycarePrimary,
            unfocusedBorderColor = DaycareBorder,
            focusedLabelColor = DaycarePrimary,
            cursorColor = DaycarePrimary
        )
    )
}
