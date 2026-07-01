package com.lucasneves.financecontrol.ui.registrations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationsScreen(
    bottomPadding: PaddingValues,
    onAccounts: () -> Unit,
    onCategories: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(bottomPadding)
    ) {
        item {
            TopAppBar(
                title  = { Text("Cadastros") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
        item {
            ListItem(
                headlineContent = { Text("Contas") },
                leadingContent = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable(onClick = onAccounts)
            )
            HorizontalDivider()
        }
        item {
            ListItem(
                headlineContent = { Text("Categorias") },
                leadingContent = { Icon(Icons.Default.Category, contentDescription = null) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable(onClick = onCategories)
            )
            HorizontalDivider()
        }
    }
}
