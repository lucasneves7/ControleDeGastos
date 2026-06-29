package com.lucasneves.financecontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import com.lucasneves.financecontrol.navigation.AppNavGraph
import com.lucasneves.financecontrol.ui.login.LoginViewModel
import com.lucasneves.financecontrol.ui.theme.FinanceControlTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinanceControlTheme {
                val loginViewModel: LoginViewModel = hiltViewModel()
                AppNavGraph(startSignedIn = loginViewModel.isAlreadySignedIn())
            }
        }
    }
}
