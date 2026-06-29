package com.lucasneves.financecontrol.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lucasneves.financecontrol.ui.components.BottomNavBar
import com.lucasneves.financecontrol.ui.creditcard.CreditCardScreen
import com.lucasneves.financecontrol.ui.login.LoginScreen
import com.lucasneves.financecontrol.ui.overview.AllTransactionsScreen
import com.lucasneves.financecontrol.ui.overview.OverviewScreen
import com.lucasneves.financecontrol.ui.registrations.RegistrationsScreen
import com.lucasneves.financecontrol.ui.registrations.account.AccountsListScreen
import com.lucasneves.financecontrol.ui.registrations.account.AddEditAccountScreen
import com.lucasneves.financecontrol.ui.registrations.category.AddEditCategoryScreen
import com.lucasneves.financecontrol.ui.registrations.category.CategoriesListScreen
import com.lucasneves.financecontrol.ui.reports.ReportsScreen
import com.lucasneves.financecontrol.ui.statement.StatementScreen
import com.lucasneves.financecontrol.ui.transaction.TransactionScreen

private val mainRoutes = setOf(
    Screen.Overview.route,
    Screen.CreditCard.route,
    Screen.Statement.route,
    Screen.Reports.route,
    Screen.Registrations.route
)

private val fabRoutes = setOf(
    Screen.Overview.route,
    Screen.CreditCard.route,
    Screen.Statement.route
)

@Composable
fun AppNavGraph(startSignedIn: Boolean = false) {
    val navController = rememberNavController()
    val backstackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backstackEntry?.destination?.route ?: ""
    val start = if (startSignedIn) Screen.Overview.route else Screen.Login.route

    Scaffold(
        bottomBar = {
            if (currentRoute in mainRoutes) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onSelect = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Overview.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (currentRoute in fabRoutes) {
                FloatingActionButton(onClick = {
                    navController.navigate(Screen.AddTransaction.createRoute())
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Novo lançamento")
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = start) {
            composable(Screen.Login.route) {
                LoginScreen(onLoginSuccess = {
                    navController.navigate(Screen.Overview.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                })
            }

            composable(Screen.Overview.route) {
                OverviewScreen(
                    bottomPadding = innerPadding,
                    onEditTransaction = { id -> navController.navigate(Screen.EditTransaction.createRoute(id)) },
                    onAllTransactions = { navController.navigate(Screen.AllTransactions.route) },
                    onDayAddTransaction = { date -> navController.navigate(Screen.AddTransaction.createRoute(date)) }
                )
            }

            composable(Screen.CreditCard.route) {
                CreditCardScreen(
                    bottomPadding = innerPadding,
                    onEditTransaction = { id -> navController.navigate(Screen.EditTransaction.createRoute(id)) }
                )
            }

            composable(Screen.Statement.route) {
                StatementScreen(
                    bottomPadding = innerPadding,
                    onEditTransaction = { id -> navController.navigate(Screen.EditTransaction.createRoute(id)) }
                )
            }

            composable(Screen.Reports.route) {
                ReportsScreen(
                    bottomPadding = innerPadding,
                    onNavigateBack = { navController.popBackStack() },
                    onAddTransaction = { navController.navigate(Screen.AddTransaction.createRoute()) }
                )
            }

            composable(Screen.Registrations.route) {
                RegistrationsScreen(
                    bottomPadding = innerPadding,
                    onAccounts = { navController.navigate(Screen.AccountsList.route) },
                    onCategories = { navController.navigate(Screen.CategoriesList.route) }
                )
            }

            composable(
                route = Screen.AddTransaction.route,
                arguments = listOf(navArgument("date") {
                    type = NavType.StringType; nullable = true; defaultValue = null
                })
            ) { backStack ->
                TransactionScreen(
                    transactionId = null,
                    prefilledDate = backStack.arguments?.getString("date"),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.EditTransaction.route,
                arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
            ) { backStack ->
                TransactionScreen(
                    transactionId = backStack.arguments?.getString("transactionId"),
                    prefilledDate = null,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AllTransactions.route) {
                AllTransactionsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onEditTransaction = { id -> navController.navigate(Screen.EditTransaction.createRoute(id)) }
                )
            }

            composable(Screen.AccountsList.route) {
                AccountsListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onAddAccount = { navController.navigate(Screen.AddEditAccount.createRoute()) },
                    onEditAccount = { id -> navController.navigate(Screen.AddEditAccount.createRoute(id)) }
                )
            }

            composable(
                route = Screen.AddEditAccount.route,
                arguments = listOf(navArgument("accountId") {
                    type = NavType.StringType; nullable = true; defaultValue = null
                })
            ) { backStack ->
                AddEditAccountScreen(
                    accountId = backStack.arguments?.getString("accountId"),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.CategoriesList.route) {
                CategoriesListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onAddCategory = { navController.navigate(Screen.AddEditCategory.createRoute()) },
                    onEditCategory = { id -> navController.navigate(Screen.AddEditCategory.createRoute(id)) }
                )
            }

            composable(
                route = Screen.AddEditCategory.route,
                arguments = listOf(navArgument("categoryId") {
                    type = NavType.StringType; nullable = true; defaultValue = null
                })
            ) { backStack ->
                AddEditCategoryScreen(
                    categoryId = backStack.arguments?.getString("categoryId"),
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
