package com.klmpk5.daycare_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.klmpk5.daycare_app.ui.dashboard.DashboardScreen
import com.klmpk5.daycare_app.ui.login.ParentLoginScreen
import com.klmpk5.daycare_app.ui.register.ParentRegisterScreen
import com.klmpk5.daycare_app.viewModel.LoginViewModel
import com.klmpk5.daycare_app.viewModel.ProfileViewModel

private enum class AppRoute {
    Login,
    Register,
    Dashboard
}

@Composable
fun AppNavigation(
    loginViewModel: LoginViewModel,
    profileViewModel: ProfileViewModel
) {
    var currentRoute by remember { mutableStateOf(AppRoute.Login) }
    var parentEmailFromLogin by remember { mutableStateOf("") }

    val loginSuccessUid = loginViewModel.loginSuccessUid
    val loginSuccessEmail = loginViewModel.loginSuccessEmail
    val successMessage = loginViewModel.successMessage

    LaunchedEffect(loginSuccessUid, loginSuccessEmail) {
        val uid = loginSuccessUid
        if (!uid.isNullOrBlank()) {
            parentEmailFromLogin = loginSuccessEmail.orEmpty()
            currentRoute = AppRoute.Dashboard
        }
    }

    LaunchedEffect(successMessage, currentRoute) {
        if (
            currentRoute == AppRoute.Register &&
            successMessage.contains("berhasil", ignoreCase = true)
        ) {
            currentRoute = AppRoute.Login
        }
    }

    when (currentRoute) {
        AppRoute.Login -> ParentLoginScreen(
            isLoading = loginViewModel.isLoading,
            errorMessage = loginViewModel.errorMessage,
            successMessage = loginViewModel.successMessage,
            onLoginClick = loginViewModel::login,
            onForgotPasswordClick = loginViewModel::sendResetPassword,
            onRegisterClick = {
                loginViewModel.resetState()
                currentRoute = AppRoute.Register
            }
        )

        AppRoute.Register -> ParentRegisterScreen(
            isLoading = loginViewModel.isLoading,
            errorMessage = loginViewModel.errorMessage,
            successMessage = loginViewModel.successMessage,
            onRegisterClick = loginViewModel::registerParent,
            onBackToLoginClick = {
                loginViewModel.resetState()
                currentRoute = AppRoute.Login
            }
        )

        AppRoute.Dashboard -> DashboardScreen(
            parentEmail = parentEmailFromLogin,
            profileViewModel = profileViewModel,
            onLogout = {
                loginViewModel.resetState()
                currentRoute = AppRoute.Login
            }
        )
    }
}
