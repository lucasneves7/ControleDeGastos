# FinanceControl — Contexto do Projeto

## Visão Geral
Aplicativo Android nativo de gestão financeira pessoal inspirado no GnuCash.
Usa a conta Google do usuário para login e salva todos os dados em uma planilha
no Google Sheets do próprio usuário.
Sem servidor próprio, sem banco de dados, 100% gratuito.

---

## Identificação
- **applicationId:** `com.lucasneves.financecontrol`
- **minSdk:** 28 (Android 9.0)
- **targetSdk:** 35
- **compileSdk:** 35
- **versionCode:** 1
- **versionName:** 1.0.0

---

## Stack Técnica
| Camada | Biblioteca | Versão |
|---|---|---|
| Linguagem | Kotlin | 2.0.21 |
| UI | Jetpack Compose + Material 3 | BOM 2024.12.01 |
| Ícones | Material Icons Extended | (via BOM) |
| Arquitetura | MVVM | — |
| ViewModel | `lifecycle-viewmodel-compose` | 2.10.0 |
| StateFlow → UI | `lifecycle-runtime-compose` | 2.10.0 |
| Autenticação | Google Credential Manager | 1.3.0 |
| Persistência | Google Sheets API v4 REST | — |
| HTTP | Retrofit + OkHttp + Gson | 2.11.0 / 4.12.0 |
| DI | Hilt | 2.52 |
| Navegação | Navigation Compose | 2.8.5 |
| Async | Coroutines + StateFlow | 1.9.0 |
| Data/Hora | `kotlinx-datetime` | 0.6.2 |
| Calendário UI | `kizitonwose/calendar` | 2.9.0 |
| Gráficos | Vico (`compose-m3`) | 3.1.0 |
| Imagens (avatar) | Coil 3 | 3.0.4 |
| Splash Screen | `core-splashscreen` | 1.2.0 |

---

## Dependências — libs.versions.toml

```toml
[versions]
kotlin = "2.0.21"
agp = "8.7.3"
compose-bom = "2024.12.01"
hilt = "2.52"
retrofit = "2.11.0"
okhttp = "4.12.0"
navigation-compose = "2.8.5"
lifecycle = "2.10.0"
kotlinx-datetime = "0.6.2"
calendar = "2.9.0"
vico = "3.1.0"
coroutines = "1.9.0"
credentials = "1.3.0"
googleid = "1.1.1"
coil = "3.0.4"
splashscreen = "1.2.0"
core-ktx = "1.15.0"

[libraries]
# Android Core
core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "core-ktx" }
core-splashscreen = { group = "androidx.core", name = "core-splashscreen", version.ref = "splashscreen" }

# Compose (versões gerenciadas pelo BOM)
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }          # só debugImplementation
compose-material3 = { group = "androidx.compose.material3", name = "material3" }
compose-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }  # ícones Material
activity-compose = { group = "androidx.activity", name = "activity-compose", version = "1.9.3" }

# Lifecycle + ViewModel
lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycle" }
lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycle" }
lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycle" }

# Navegação
navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation-compose" }

# Hilt
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }  # ksp
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version = "1.2.0" }

# Rede
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-gson = { group = "com.squareup.retrofit2", name = "converter-gson", version.ref = "retrofit" }
okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
okhttp-logging = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttp" }

# Coroutines
coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }

# Data/Hora
kotlinx-datetime = { group = "org.jetbrains.kotlinx", name = "kotlinx-datetime", version.ref = "kotlinx-datetime" }

# Calendário UI
calendar-compose = { group = "com.kizitonwose.calendar", name = "compose", version.ref = "calendar" }

# Gráficos
vico-compose-m3 = { group = "com.patrykandpatrick.vico", name = "compose-m3", version.ref = "vico" }

# Carregamento de imagens (avatar Google do usuário)
coil-compose = { group = "io.coil-kt.coil3", name = "coil-compose", version.ref = "coil" }
coil-network-okhttp = { group = "io.coil-kt.coil3", name = "coil-network-okhttp", version.ref = "coil" }

# Autenticação Google
credentials = { group = "androidx.credentials", name = "credentials", version.ref = "credentials" }
credentials-play = { group = "androidx.credentials", name = "credentials-play-services-auth", version.ref = "credentials" }
googleid = { group = "com.google.android.libraries.identity.googleid", name = "googleid", version.ref = "googleid" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
ksp = { id = "com.google.devtools.ksp", version = "2.0.21-1.0.28" }
```

## Como usar no build.gradle.kts (app)

```kotlin
dependencies {
    // Core
    implementation(libs.core.ktx)
    implementation(libs.core.splashscreen)

    // Compose (BOM garante versões compatíveis entre si)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.icons.extended)   // ícones Material (Account, ArrowBack, Add etc.)
    implementation(libs.activity.compose)

    // Lifecycle + ViewModel
    implementation(libs.lifecycle.viewmodel.compose)   // viewModel() em Composables
    implementation(libs.lifecycle.runtime.compose)     // collectAsStateWithLifecycle()
    implementation(libs.lifecycle.runtime.ktx)

    // Navegação
    implementation(libs.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)   // hiltViewModel()

    // Rede
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)            // logs de request/response em debug

    // Coroutines
    implementation(libs.coroutines.android)

    // Data/Hora
    implementation(libs.kotlinx.datetime)

    // Calendário
    implementation(libs.calendar.compose)

    // Gráficos
    implementation(libs.vico.compose.m3)

    // Imagens (avatar do usuário Google)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Autenticação Google
    implementation(libs.credentials)
    implementation(libs.credentials.play)
    implementation(libs.googleid)

    // Debug only
    debugImplementation(libs.compose.ui.tooling)
}
```

---

## Uso das Bibliotecas de Data/Hora

Usar **sempre** `kotlinx-datetime` — nunca `java.util.Date` ou `java.util.Calendar`.

```kotlin
// Data atual
val today: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

// Criar data
val date = LocalDate(2025, 5, 10)

// Formatar para salvar na planilha (ISO-8601)
val str = date.toString() // "2025-05-10"

// Parsear da planilha
val date = LocalDate.parse("2025-05-10")

// Navegação por mês
val nextMonth = date.plus(1, DateTimeUnit.MONTH)
val prevMonth = date.minus(1, DateTimeUnit.MONTH)

// Primeiro e último dia do mês (para filtrar transações)
val firstDay = LocalDate(date.year, date.month, 1)
val lastDay = firstDay.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)
```

---

## Uso da Biblioteca de Calendário (kizitonwose)

O card de calendário usa **duas variantes** da mesma biblioteca:
- **Recolhido:** `WeekCalendar()` — mostra só a semana atual
- **Expandido:** `HorizontalCalendar()` — mostra o mês inteiro

```kotlin
// Animação de expansão controlada por estado no ViewModel
var isExpanded by remember { mutableStateOf(false) }

AnimatedContent(targetState = isExpanded) { expanded ->
    if (expanded) {
        HorizontalCalendar(
            state = rememberCalendarState(
                startMonth = currentMonth.minusMonths(12),
                endMonth = currentMonth.plusMonths(12),
                firstVisibleMonth = currentMonth,
                firstDayOfWeek = DayOfWeek.SUNDAY
            ),
            dayContent = { day -> CalendarDay(day, transactions) }
        )
    } else {
        WeekCalendar(
            state = rememberWeekCalendarState(startDate = firstDayOfWeek),
            dayContent = { day -> CalendarDay(day, transactions) }
        )
    }
}
```

---

## Uso da Biblioteca de Gráficos (Vico)

### Gráfico de Barras — Fluxo de Caixa
```kotlin
// Dados: receitas e despesas dos últimos 6 meses
CartesianChartHost(
    chart = rememberCartesianChart(
        rememberColumnCartesianLayer() // barras agrupadas receita/despesa
    ),
    modelProducer = modelProducer // alimentado pelo ViewModel via StateFlow
)
```

### Gráfico de Pizza — Gastos por Categoria
```kotlin
PieChart(
    modelProducer = pieModelProducer // fatias por categoria
)
```

---

## Estrutura de Pacotes
```
com.lucasneves.financecontrol
├── data
│   ├── model/          # Account, Category, Transaction (data classes)
│   ├── remote/         # Retrofit interfaces e DTOs da Sheets API
│   └── repository/     # AccountRepository, CategoryRepository, TransactionRepository
├── domain
│   └── usecase/        # GetMonthTransactionsUseCase, GetReportUseCase etc.
├── ui
│   ├── overview/       # OverviewScreen + OverviewViewModel
│   ├── transaction/    # AddEditTransactionScreen + ViewModel
│   ├── reports/        # ReportsScreen + ReportsViewModel
│   ├── login/          # LoginScreen + LoginViewModel
│   └── components/     # Componentes reutilizáveis (CalendarCard, SummaryCard etc.)
├── di/                 # AppModule, NetworkModule, RepositoryModule
├── navigation/         # Routes (sealed class), NavGraph
└── util/               # Extensions, formatadores de moeda/data
```

---

## Telas e Navegação

### Fluxo completo
```
LoginScreen
    └── (após login) → OverviewScreen  ← tela principal
            ├── [FAB "+"] → AddTransactionScreen
            ├── [toque em lançamento] → EditTransactionScreen
            └── [BottomNav] → ReportsScreen
                    └── [FAB "+"] → AddTransactionScreen
```

### Navegação principal
- `BottomNavigationBar` com 2 abas: **Visão Geral** e **Relatórios**
- FAB `+` presente em `OverviewScreen` e `ReportsScreen`

---

## Tela 1 — Visão Geral (OverviewScreen)

Tela principal. Navegação por mês no topo: `< Maio 2025 >`.

### Cards (ordem de cima para baixo):

**1. Card Resumo do Mês**
- Saldo total atual (soma de todas as contas)
- Total receitas do mês / Total despesas do mês
- Balanço do mês (receitas − despesas)

**2. Card de Contas**
- Scroll horizontal de chips, um por conta
- Cada chip: nome + saldo atual
- Toque filtra os lançamentos abaixo

**3. Card Calendário (expansível)**
- **Recolhido:** `WeekCalendar` mostrando a semana atual
  - Dias com despesas: ponto vermelho abaixo do número
  - Dias com receitas: ponto verde
  - Dia atual: círculo destacado (cor primária)
- **Expandido (toque no card):** troca para `HorizontalCalendar` com mês inteiro
  - Mesma lógica de marcação de pontos
  - Animação suave com `AnimatedContent`

**4. Card Lançamentos do Mês**
- Agrupados por data (cabeçalho com a data formatada)
- Cada item: ícone da categoria | descrição | nome da conta | valor colorido
- Despesa → vermelho, Receita → verde, Transferência → azul
- Toque no item → `EditTransactionScreen`

---

## Tela 2 — Adicionar / Editar Lançamento (AddEditTransactionScreen)

Tela única para criar e editar transações.

| Campo | Componente | Obrigatório |
|---|---|---|
| Tipo | SegmentedButton: Despesa / Receita / Transferência | Sim |
| Valor | `OutlinedTextField` com teclado numérico (R$) | Sim |
| Data | `DatePicker` do Material 3 | Sim |
| Descrição | `OutlinedTextField` | Não |
| Conta | `ExposedDropdownMenuBox` | Sim |
| Categoria | `ExposedDropdownMenuBox` hierárquico (Pai > Filho) | Sim (exceto Transferência) |
| Conta Destino | `ExposedDropdownMenuBox` | Só em Transferência |

- Botão **Salvar** → grava nova linha na aba `transactions`
- Botão **Excluir** (só em edição) → remove a linha da aba `transactions`
- Ao salvar/excluir → recalcula e atualiza o saldo na aba `accounts`

---

## Tela 3 — Relatórios (ReportsScreen)

Navegação por período no topo: `< Maio 2025 >`.
Três seções em abas (`TabRow`):

### Aba 1 — Fluxo de Caixa
- Inspirado no "Cash Flow Report" do GnuCash
- Gráfico de barras agrupadas (Vico `rememberColumnCartesianLayer`)
- Barras: Receitas (verde) vs Despesas (vermelho) por mês — últimos 6 meses
- Tabela abaixo: Total Receitas | Total Despesas | Saldo do período

### Aba 2 — Gastos por Categoria
- Inspirado no "Expense Piechart" do GnuCash
- Gráfico de pizza (Vico `PieChart`) — fatias por categoria no mês selecionado
- Lista abaixo: ícone da categoria | nome | valor | % do total
- Categorias filhas aparecem agrupadas sob o pai

### Aba 3 — Extrato por Conta
- Inspirado no "Account Summary" do GnuCash
- Dropdown para selecionar a conta
- Saldo inicial → lista de transações → saldo final
- Filtro por tipo: Todos / Receitas / Despesas / Transferências

---

## Estrutura da Planilha (Google Sheets)

Criada automaticamente na **primeira execução** com o nome `FinanceControl_Data`.
A **primeira linha de cada aba é sempre o cabeçalho**.

### Aba `accounts`
| id | name | type | balance | createdAt |
|----|------|------|---------|-----------|
| UUID | "Carteira" | CASH/CHECKING/SAVINGS | 150.00 | 2025-05-01 |

### Aba `categories`
| id | name | parentId | type | isDefault |
|----|------|----------|------|-----------|
| UUID | "Energia" | UUID-pai | INCOME/EXPENSE | true |

- `parentId` vazio = categoria raiz
- Profundidade máxima: 2 níveis (pai > filho)

### Aba `transactions`
| id | date | description | amount | type | accountId | categoryId | toAccountId |
|----|------|-------------|--------|------|-----------|------------|-------------|
| UUID | 2025-05-10 | "Conta de luz" | 150.00 | EXPENSE/INCOME/TRANSFER | UUID | UUID | UUID ou vazio |

- `toAccountId` preenchido apenas em `TRANSFER`
- `categoryId` vazio em `TRANSFER`
- `amount` sempre positivo; `type` define se é entrada ou saída
- Datas sempre no formato `yyyy-MM-dd` (ISO-8601)

---

## Categorias Padrão (inseridas na criação da planilha)
```
DESPESAS
├── Moradia
│   ├── Aluguel
│   ├── Energia
│   └── Água
├── Alimentação
│   ├── Supermercado
│   └── Restaurante
├── Transporte
│   ├── Combustível
│   └── Transporte Público
├── Saúde
├── Lazer
└── Outros

RECEITAS
├── Salário
├── Freelance
├── Investimentos
└── Outros
```

---

## Sealed Classes e Enums obrigatórios

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

enum class TransactionType { INCOME, EXPENSE, TRANSFER }
enum class AccountType { CASH, CHECKING, SAVINGS }
enum class CategoryType { INCOME, EXPENSE }
```

---

## Padrões de Código — SEMPRE siga isso
- Toda chamada à Sheets API fica em `repository`, nunca no ViewModel
- ViewModels expõem apenas `StateFlow<UiState>`
- Sem lógica de negócio em funções `@Composable`
- Strings visíveis ao usuário sempre em `res/values/strings.xml`
- Datas: usar `LocalDate` do `kotlinx-datetime` internamente; `toString()` para salvar
- Valores monetários: `Double` na planilha, formatados como `R$ 0,00` na UI via `util/`
- Nunca usar `java.util.Date`, `java.util.Calendar` ou `SimpleDateFormat`

---

## Restrições — NUNCA faça isso
- ❌ Sem Firebase (nenhum produto)
- ❌ Sem Room, SQLite ou qualquer banco local
- ❌ Sem cache local de dados
- ❌ Não usar `GlobalScope`
- ❌ Não usar `findViewById` ou XML de layout
- ❌ Não inventar telas além das definidas acima
- ❌ Não alterar a estrutura de pacotes sem avisar
- ❌ Não usar `java.util.Date` ou `SimpleDateFormat` — use `kotlinx-datetime`
