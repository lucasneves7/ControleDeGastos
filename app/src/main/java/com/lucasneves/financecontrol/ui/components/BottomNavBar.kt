package com.lucasneves.financecontrol.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.lucasneves.financecontrol.navigation.Screen

private data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)

private val navItems = listOf(
    BottomNavItem(Screen.Overview.route, "Visão Geral", Icons.Default.Home),
    BottomNavItem(Screen.CreditCard.route, "Fatura", Icons.Default.CreditCard),
    BottomNavItem(Screen.Statement.route, "Extrato", Icons.Default.Receipt),
    BottomNavItem(Screen.Reports.route, "Relatórios", Icons.Default.BarChart),
    BottomNavItem(Screen.Registrations.route, "Cadastros", Icons.Default.Settings)
)

@Composable
fun BottomNavBar(currentRoute: String, onSelect: (String) -> Unit) {
    NavigationBar {
        navItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onSelect(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
