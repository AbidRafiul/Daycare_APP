package com.klmpk5.daycare_app.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klmpk5.daycare_app.data.local.entities.User
import com.klmpk5.daycare_app.ui.theme.DaycareBackground
import com.klmpk5.daycare_app.ui.theme.DaycareBorder
import com.klmpk5.daycare_app.ui.theme.DaycarePrimary
import com.klmpk5.daycare_app.ui.theme.DaycarePrimaryLight
import com.klmpk5.daycare_app.ui.theme.DaycareTextMuted
import com.klmpk5.daycare_app.ui.theme.DaycareTextPrimary
import com.klmpk5.daycare_app.ui.theme.DaycareTextSecondary
import com.klmpk5.daycare_app.viewModel.ProfileViewModel

@Composable
fun ProfilePage(
    profileViewModel: ProfileViewModel,
    contentPadding: PaddingValues,
    onEditProfileClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    val uid = profileViewModel.getCurrentUid()
    val userFlow = remember(uid) {
        uid?.let { profileViewModel.getUserProfile(it) }
    }
    val emptyUserState = remember { mutableStateOf<User?>(null) }
    val user by userFlow?.collectAsState(initial = null) ?: emptyUserState
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uid) {
        uid?.let { profileViewModel.syncUserProfile(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DaycareBackground)
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
    ) {
        ProfileMainHeader()

        ProfileAccountCard(
            user = user,
            fallbackEmail = profileViewModel.getCurrentEmail().orEmpty(),
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .offset(y = (-40).dp)
        )

        Text(
            text = "Menu",
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .offset(y = (-24).dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = DaycareTextPrimary
        )

        ProfileMenuCard(
            onEditProfileClick = onEditProfileClick,
            onChangePasswordClick = onChangePasswordClick,
            onLogoutClick = { showLogoutDialog = true },
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .offset(y = (-12).dp)
        )

        Spacer(modifier = Modifier.height(20.dp))
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Logout Akun?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(text = "Apakah Anda yakin ingin keluar dari akun ini?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        profileViewModel.logout()
                        onLogoutSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C))
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(text = "Batal", color = DaycarePrimary)
                }
            }
        )
    }
}

@Composable
private fun ProfileMainHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(238.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(DaycarePrimary, Color(0xFF23897D))
                )
            )
            .padding(horizontal = 22.dp)
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.TopEnd)
                .offset(x = 36.dp, y = 24.dp)
                .background(
                    color = Color.White.copy(alpha = 0.10f),
                    shape = CircleShape
                )
        )

        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Text(
                text = "Profile",
                color = Color.White,
                fontSize = 31.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Kelola akun dan keamanan Anda",
                color = Color.White.copy(alpha = 0.90f),
                fontSize = 14.sp
            )
        }

        Box(
            modifier = Modifier
                .size(96.dp)
                .align(Alignment.BottomCenter)
                .offset(y = 38.dp)
                .background(color = Color.White, shape = CircleShape)
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = DaycarePrimaryLight, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials(userName = "Parent"),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = DaycarePrimary
                )
            }
        }
    }
}

@Composable
private fun ProfileAccountCard(
    user: User?,
    fallbackEmail: String,
    modifier: Modifier = Modifier
) {
    val fullName = user?.fullName?.ifBlank { "Parent Daycare" } ?: "Parent Daycare"
    val email = user?.email?.ifBlank { fallbackEmail } ?: fallbackEmail
    val phoneNumber = user?.phoneNumber?.ifBlank { "-" } ?: "-"

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = fullName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DaycareTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = email,
                fontSize = 14.sp,
                color = DaycareTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                color = DaycarePrimaryLight.copy(alpha = 0.55f),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, DaycarePrimary.copy(alpha = 0.12f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Informasi Akun",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = DaycareTextPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    ProfileInfoRow(label = "No. WhatsApp", value = phoneNumber)
                    Spacer(modifier = Modifier.height(10.dp))
                    ProfileInfoRow(label = "Role", value = "Parent")
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(DaycarePrimaryLight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label.take(1),
                fontSize = 14.sp,
                color = DaycarePrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 13.sp,
            color = DaycareTextSecondary
        )

        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = DaycareTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ProfileMenuCard(
    onEditProfileClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            ProfileMenuItem(
                leading = "E",
                title = "Edit Profile",
                subtitle = "Ubah nama dan nomor HP",
                onClick = onEditProfileClick
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = DaycareBorder.copy(alpha = 0.5f)
            )

            ProfileMenuItem(
                leading = "P",
                title = "Ubah Password",
                subtitle = "Ganti password akun Anda",
                onClick = onChangePasswordClick
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = DaycareBorder.copy(alpha = 0.5f)
            )

            ProfileMenuItem(
                leading = "K",
                title = "Logout",
                subtitle = "Keluar dari akun ini",
                titleColor = Color(0xFFB91C1C),
                onClick = onLogoutClick
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    leading: String,
    title: String,
    subtitle: String,
    titleColor: Color = DaycareTextPrimary,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(color = DaycarePrimaryLight, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = leading,
                    fontSize = 16.sp,
                    color = DaycarePrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = DaycareTextSecondary
                )
            }

            Text(text = ">", fontSize = 22.sp, color = DaycareTextMuted)
        }
    }
}

private fun initials(userName: String): String {
    return userName
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.take(1).uppercase() }
        .ifBlank { "P" }
}
