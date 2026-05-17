package com.klmpk5.daycare_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import com.klmpk5.daycare_app.ui.ChildDetailScreen
import com.klmpk5.daycare_app.ui.login.LoginScreen
import com.klmpk5.daycare_app.ui.theme.DaycareAppTheme
import com.klmpk5.daycare_app.viewModel.LoginViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as App
        val loginViewModel = ViewModelProvider(
            this,
            LoginViewModel.Factory(app.userRepository)
        )[LoginViewModel::class.java]

        setContent {
            DaycareAppTheme {
                var currentScreen by remember { mutableStateOf("login") }
                var parentUidFromLogin by remember { mutableStateOf("") }
                var parentEmailFromLogin by remember { mutableStateOf("") }

                when (currentScreen) {
                    "login" -> {
                        LoginScreen(
                            viewModel = loginViewModel,
                            onLoginSuccess = { realUid, realEmail ->
                                parentUidFromLogin = realUid
                                parentEmailFromLogin = realEmail
                                currentScreen = "detail"
                            }
                        )
                    }
                    "detail" -> {
                        ChildDetailScreen(
                            parentUid = parentUidFromLogin,
                            parentEmail = parentEmailFromLogin,
                            onBackClick = {
                                loginViewModel.resetState()
                                currentScreen = "login"
                            }
                        )
                    }
                }
            }
        }
    }
}
