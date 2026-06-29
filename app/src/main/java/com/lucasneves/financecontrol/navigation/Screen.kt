package com.lucasneves.financecontrol.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Overview : Screen("overview")
    object CreditCard : Screen("credit_card")
    object Statement : Screen("statement")
    object Reports : Screen("reports")
    object Registrations : Screen("registrations")

    object AddTransaction : Screen("add_transaction?date={date}") {
        fun createRoute(date: String? = null) =
            if (date != null) "add_transaction?date=$date" else "add_transaction"
    }
    object EditTransaction : Screen("edit_transaction/{transactionId}") {
        fun createRoute(transactionId: String) = "edit_transaction/$transactionId"
    }

    object AllTransactions : Screen("all_transactions")

    object AccountsList : Screen("accounts_list")
    object AddEditAccount : Screen("add_edit_account?accountId={accountId}") {
        fun createRoute(accountId: String? = null) =
            if (accountId != null) "add_edit_account?accountId=$accountId" else "add_edit_account"
    }

    object CategoriesList : Screen("categories_list")
    object AddEditCategory : Screen("add_edit_category?categoryId={categoryId}") {
        fun createRoute(categoryId: String? = null) =
            if (categoryId != null) "add_edit_category?categoryId=$categoryId" else "add_edit_category"
    }
}
