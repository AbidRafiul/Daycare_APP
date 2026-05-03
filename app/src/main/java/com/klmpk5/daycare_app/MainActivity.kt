package com.klmpk5.daycare_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.klmpk5.daycare_app.ui.ChildListScreen
import com.klmpk5.daycare_app.ui.theme.DaycareAppTheme
import com.klmpk5.daycare_app.viewModel.ChildViewModel
import com.klmpk5.daycare_app.viewModel.ChildViewModelFactory
import kotlin.jvm.java

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(
            this,
            ChildViewModelFactory((application as App).childRepository)
        )[ChildViewModel::class.java]

        setContent {
            DaycareAppTheme {
                ChildListScreen(viewModel = viewModel)
            }
        }
    }
}