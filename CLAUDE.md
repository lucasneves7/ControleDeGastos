# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**FinanceControl** — Android personal finance app (GnuCash-inspired). No backend. All data lives in a **Google Sheets spreadsheet** owned by the user. Login via Google Sign-In with OAuth2 scopes for the Sheets API.

- **applicationId / package:** `com.lucasneves.financecontrol`
- **Min SDK:** 28 | **Target/Compile SDK:** 35
- **Language:** Kotlin | **UI:** Jetpack Compose + Material3
- **Architecture:** MVVM · Hilt DI · Navigation Compose · Retrofit · Coroutines/StateFlow

## Commands

```bash
gradlew assembleDebug
gradlew test
gradlew connectedAndroidTest
gradlew test --tests "com.lucasneves.financecontrol.ExampleUnitTest"
gradlew clean
```

## Architecture

### Layer diagram
```
UI (Compose screens + ViewModels)
    ↓ calls
Domain (use-case logic inside ViewModels — no separate use-case layer at this stage)
    ↓ calls
Data
  ├── repository/     — one repo per entity (Account, Category, Transaction)
  │                     + SpreadsheetRepository (lifecycle of the Google Sheet)
  │                     + AuthRepository (Google Sign-In + OAuth2 token)
  └── remote/         — Retrofit SheetsApiService + DTOs (SheetsDto.kt)
```

### Google Sheets data store
The spreadsheet (`FinanceControl_Data`) is created automatically on first run. Three tabs:
- `accounts` — id, name, type, balance, createdAt
- `categories` — id, name, parentId, type, isDefault (max depth 2)
- `transactions` — id, date, description, amount, type, accountId, categoryId, toAccountId

All dates are ISO-8601 (`yyyy-MM-dd`); amounts are `Double`; IDs are UUIDs.

### Auth flow
1. `AuthRepository.getSignInIntent()` launches `GoogleSignInClient` with `spreadsheets` + `drive.file` scopes.
2. `LoginViewModel.onSignInResult()` handles the intent result, then calls `SpreadsheetRepository.getOrCreateSpreadsheet()` to bootstrap the sheet and `CategoryRepository.addDefaultCategories()` if empty.
3. `AuthRepository.getAccessToken()` (called on each HTTP request via `NetworkModule`'s OkHttp interceptor) uses `GoogleAuthUtil.getToken()` on `Dispatchers.IO`.

### DI
`AppModule` — `SharedPreferences`  
`NetworkModule` — OkHttp (with auth interceptor) → Retrofit → `SheetsApiService`

### Navigation
Single `NavHost` in `AppNavGraph`. Start destination: `Login` or `Overview` (determined by `AuthRepository.isSignedIn()` at startup via `LoginViewModel.isAlreadySignedIn()`).

Routes: `Login → Overview ↔ Reports`, `Overview → AddTransaction`, `Overview → EditTransaction/{id}`

### Code rules (from PROJECT.md)
- Sheets API calls only in `repository/`, never in ViewModel
- ViewModels expose `StateFlow<UiState>` only
- No business logic in `@Composable` functions
- Dates stored as `String` ISO-8601; amounts as `Double`
- No Firebase, Room, SQLite, local cache, GlobalScope, or XML layouts

## Setup required (Google Cloud)
1. Create a Google Cloud project
2. Enable **Google Sheets API** and **Google Drive API**
3. Add an OAuth2 credential → **Android** type, package `com.lucasneves.financecontrol`, debug SHA-1
4. No Web Client ID is needed (uses `GoogleAuthUtil` flow, not server-side token exchange)
