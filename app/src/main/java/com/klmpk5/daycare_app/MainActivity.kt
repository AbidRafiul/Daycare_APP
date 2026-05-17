package com.klmpk5.daycare_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.klmpk5.daycare_app.ui.navigation.AppNavigation
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
                AppNavigation(loginViewModel = loginViewModel)
            }
        }
    }
}
