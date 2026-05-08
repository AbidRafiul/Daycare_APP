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
import com.klmpk5.daycare_app.ui.LoginScreen
import com.klmpk5.daycare_app.ui.theme.DaycareAppTheme
import com.klmpk5.daycare_app.viewModel.ChildViewModel
import com.klmpk5.daycare_app.viewModel.LoginViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Panggil kedua ViewModel dengan cara standar
        val loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        val childViewModel = ViewModelProvider(this)[ChildViewModel::class.java]

        setContent {
            DaycareAppTheme {
                var currentScreen by remember { mutableStateOf("login") }
                var parentUidFromLogin by remember { mutableStateOf("") }

                when (currentScreen) {
                    "login" -> {
                        LoginScreen(
                            viewModel = loginViewModel,
                            onLoginSuccess = { realUid ->
                                parentUidFromLogin = realUid
                                currentScreen = "detail"
                            }
                        )
                    }
                    "detail" -> {
                        ChildDetailScreen(
                            parentUid = parentUidFromLogin,
                            viewModel = childViewModel,
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