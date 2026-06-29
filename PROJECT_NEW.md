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
| Layout adaptável | `constraintlayout-compose` | 1.1.0 |

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
constraintlayout-compose = "1.1.0"

[libraries]
# Android Core
core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "core-ktx" }
core-splashscreen = { group = "androidx.core", name = "core-splashscreen", version.ref = "splashscreen" }
constraintlayout-compose = { group = "androidx.constraintlayout", name = "constraintlayout-compose", version.ref = "constraintlayout-compose" }

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
    implementation(libs.constraintlayout.compose)   // suporte a rotação de tela

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
│   ├── model/              # Account, Category, Transaction (data classes)
│   ├── remote/             # Retrofit interfaces e DTOs da Sheets API
│   └── repository/         # AccountRepository, CategoryRepository, TransactionRepository
├── domain
│   └── usecase/            # GetMonthTransactionsUseCase, GetReportUseCase etc.
├── ui
│   ├── overview/           # OverviewScreen + OverviewViewModel
│   ├── transaction/        # AddEditTransactionScreen + ViewModel
│   ├── statement/          # StatementScreen (extrato de conta) + ViewModel
│   ├── creditcard/         # CreditCardScreen (fatura) + ViewModel
│   ├── registrations/      # RegistrationsScreen (hub de cadastros)
│   │   ├── account/        # AccountsListScreen + AddEditAccountScreen + ViewModel
│   │   └── category/       # CategoriesListScreen + AddEditCategoryScreen + ViewModel
│   ├── reports/            # ReportsScreen + ReportsViewModel
│   ├── login/              # LoginScreen + LoginViewModel
│   └── components/         # CalendarCard, SummaryCard, DayTransactionsDialog etc.
├── di/                     # AppModule, NetworkModule, RepositoryModule
├── navigation/             # Routes (sealed class), NavGraph
└── util/                   # Extensions, formatadores de moeda/data
```

---

## Telas e Navegação

### BottomNavigationBar — 5 abas
| Ícone | Label | Tela |
|---|---|---|
| `Home` | Visão Geral | `OverviewScreen` |
| `CreditCard` | Fatura | `CreditCardScreen` |
| `Receipt` | Extrato | `StatementScreen` |
| `BarChart` | Relatórios | `ReportsScreen` |
| `Settings` | Cadastros | `RegistrationsScreen` |

FAB `+` (Adicionar lançamento) presente em: `OverviewScreen`, `StatementScreen`, `CreditCardScreen`.

### Fluxo completo
```
LoginScreen
    └── (após login) → OverviewScreen
            ├── [FAB "+"] → AddEditTransactionScreen
            ├── [Ver todos — lançamentos] → AllTransactionsScreen
            │       └── [item] → AddEditTransactionScreen (edição)
            └── [dia no calendário] → DayTransactionsDialog

    BottomNav → CreditCardScreen (Fatura)
            ├── [FAB "+"] → AddEditTransactionScreen
            └── [item] → AddEditTransactionScreen (edição)

    BottomNav → StatementScreen (Extrato)
            ├── [FAB "+"] → AddEditTransactionScreen
            └── [item] → AddEditTransactionScreen (edição)

    BottomNav → ReportsScreen (Relatórios)

    BottomNav → RegistrationsScreen (Cadastros)
            ├── [Contas] → AccountsListScreen
            │       ├── [FAB "+"] → AddEditAccountScreen (criação)
            │       └── [item] → AddEditAccountScreen (edição)
            └── [Categorias] → CategoriesListScreen
                    ├── [FAB "+"] → AddEditCategoryScreen (criação)
                    └── [item] → AddEditCategoryScreen (edição)
```

---

## Tela 1 — Visão Geral (OverviewScreen)

Tela principal. Usa `PullToRefreshBox` (Material 3) — **sem SwipeRefresh nem botão de refresh**.
Usa `ConstraintLayout` do Compose para suportar rotação de tela.

### Seletor de Mês
- Exibido no topo da tela: `< Maio 2025 >`
- Deve ter **fundo diferenciado** (ex: `Surface` com cor `secondaryContainer` ou card elevado)
- Setas `<` e `>` para navegar entre meses
- Não é uma TopAppBar — é um componente próprio dentro do scroll

### Cards (ordem de cima para baixo), separados por `HorizontalDivider`:

**1. Card Resumo do Mês**
- Saldo total atual (soma de todas as contas, exceto crédito)
- Total receitas do mês / Total despesas do mês
- Balanço do mês (receitas − despesas)

`HorizontalDivider`

**2. Card Saldo por Conta**
- Lista de contas com saldo atual, uma por linha
- Contas de débito: exibe saldo disponível
- Contas de crédito: exibe `Limite: R$ X | Fatura: R$ Y | Vence: dia DD`
- Exibe no máximo **5 contas**; se houver mais, botão `Ver todas` no rodapé do card

`HorizontalDivider`

**3. Card Cartão de Crédito** *(aparece só se houver conta do tipo CREDIT)*
- Uma linha por cartão cadastrado
- Mostra: nome | valor da fatura atual | data de vencimento
- Cor de alerta se vencimento ≤ 3 dias

`HorizontalDivider`

**4. Card Calendário (expansível)**
- **Recolhido (padrão):** `WeekCalendar` mostrando a semana atual
- **Expandido (toque no card):** `HorizontalCalendar` com o mês inteiro
- Animação suave com `AnimatedContent`
- Marcação nos dias:
  - Só receita → bolinha **verde** sobre o número do dia
  - Só despesa → bolinha **vermelha** sobre o número do dia
  - Ambos → bolinha **azul** sobre o número do dia
- **Toque em um dia** → abre `DayTransactionsDialog`

`HorizontalDivider`

**5. Card Últimos Lançamentos**
- Exibe no máximo **5 lançamentos** do mês, ordenados por data decrescente
- Cada item: ícone da categoria | descrição | nome da conta | valor colorido
  - Despesa → vermelho | Receita → verde | Transferência → azul
- Botão `Ver todos` no rodapé → `AllTransactionsScreen`

`HorizontalDivider`

**6. Card Despesa e Receita por Categoria**
- Exibe no máximo **5 categorias** com maior movimentação no mês
- Cada linha: nome da categoria | barra de progresso proporcional | valor
- Botão `Ver todas` no rodapé → `ReportsScreen` aba Gastos por Categoria

---

## Tela 1.1 — Dialog de Transações do Dia (DayTransactionsDialog)

Abre como `AlertDialog` ao tocar em um dia no calendário.

- Título: data formatada (ex: `Terça, 10 de maio`)
- Lista de todas as transações do dia selecionado
- Cada item: tipo (ícone) | descrição | categoria | valor colorido
- Botão `Fechar`
- Botão `Adicionar lançamento` → `AddEditTransactionScreen` com data pré-preenchida

---

## Tela 1.2 — Todos os Lançamentos (AllTransactionsScreen)

Acessada pelo botão `Ver todos` do Card de Últimos Lançamentos.

- Exibe todos os lançamentos do mês navegado
- Agrupados por data (cabeçalho com a data)
- Mesmo visual do card, sem limite de itens
- Toque no item → `AddEditTransactionScreen` (modo edição)

---

## Tela 2 — Fatura do Cartão (CreditCardScreen)

Acessada pela aba `Fatura` no BottomNav. Usa `PullToRefreshBox` e `ConstraintLayout`.

- Se houver mais de um cartão de crédito, exibe seletor de conta no topo (`ExposedDropdownMenuBox`)
- Navegação por ciclo de fatura: `< Fev 2025 >` (baseado no `billingDay` da conta)
- **Card de Resumo da Fatura:**
  - Valor total da fatura do ciclo
  - Limite disponível (creditLimit − fatura)
  - Data de fechamento e data de vencimento
  - Cor de alerta se vencimento ≤ 3 dias
- **Lista de lançamentos da fatura:**
  - Agrupados por data
  - Cada item: descrição | categoria | valor
  - Toque no item → `AddEditTransactionScreen` (edição)
- FAB `+` → `AddEditTransactionScreen` com conta pré-selecionada

---

## Tela 3 — Extrato de Conta (StatementScreen)

Acessada pela aba `Extrato` no BottomNav. Usa `PullToRefreshBox` e `ConstraintLayout`.

- Seletor de conta no topo (`ExposedDropdownMenuBox`) — lista todas as contas (exceto CREDIT)
- Navegação por mês: `< Maio 2025 >`
- **Card de Resumo da Conta:**
  - Saldo atual da conta selecionada
  - Total de entradas do mês / Total de saídas do mês
- **Filtro por tipo:** `TabRow` com abas Todos / Receitas / Despesas / Transferências
- **Lista de transações:**
  - Agrupadas por data com cabeçalho
  - Saldo acumulado exibido ao final de cada dia
  - Toque no item → `AddEditTransactionScreen` (edição)
- FAB `+` → `AddEditTransactionScreen` com conta pré-selecionada

---

## Tela 4 — Cadastros (RegistrationsScreen)

Hub de cadastros acessado pela aba `Cadastros` no BottomNav.
Tela simples com dois itens de menu em lista:

- `Contas` (ícone `AccountBalance`) → navega para `AccountsListScreen`
- `Categorias` (ícone `Category`) → navega para `CategoriesListScreen`

---

## Tela 4.1 — Lista de Contas (AccountsListScreen)

- FAB `+` → `AddEditAccountScreen` (criação)
- Contas agrupadas por tipo com **título de seção** e `HorizontalDivider` entre grupos:
  ```
  CARTEIRA
  ─────────────────────
  [Carteira]  R$ 150,00

  CONTA CORRENTE
  ─────────────────────
  [Nubank CC]  R$ 2.340,00
  [Inter]      R$ 800,00

  POUPANÇA
  ─────────────────────
  [Poupança Caixa]  R$ 5.000,00

  CARTÃO DE CRÉDITO
  ─────────────────────
  [Nubank]  Fatura: R$ 320,00 | Vence: dia 22
  ```
- Toque em qualquer conta → `AddEditAccountScreen` (edição)
- Swipe para deletar → abre `ReassignTransactionsDialog`

---

## Tela 4.1.1 — Dialog de Reatribuição ao Deletar Conta (ReassignTransactionsDialog)

Abre quando o usuário tenta deletar uma conta que possui transações vinculadas.

- Mensagem: `"Esta conta possui X lançamentos. Selecione uma conta para reatribuí-los:"`
- `ExposedDropdownMenuBox` com todas as outras contas disponíveis
- Botão `Confirmar` → reatribui os lançamentos e deleta a conta
- Botão `Cancelar`

---

## Tela 4.2 — Lista de Categorias (CategoriesListScreen)

- FAB `+` → `AddEditCategoryScreen` (criação)
- `TabRow` no topo com duas abas: **Despesas** | **Receitas**
- Cada aba exibe apenas as categorias do tipo correspondente
- Estrutura da lista dentro de cada aba:
  ```
  [Moradia]                    (categoria raiz — sem indentação)
      [Aluguel]                (subcategoria — indentada)
      [Energia]
      [Água]
  [Alimentação]
      [Supermercado]
      [Restaurante]
  [Saúde]                      (categoria raiz sem filhos)
  ```
- Categorias raiz em destaque (peso de fonte maior ou cor diferente)
- Subcategorias indentadas com padding à esquerda
- Toque em qualquer categoria → `AddEditCategoryScreen` (edição)
- Swipe para deletar → abre `ReassignCategoryDialog`

---

## Tela 4.2.1 — Dialog de Reatribuição ao Deletar Categoria (ReassignCategoryDialog)

Abre quando o usuário tenta deletar uma categoria que possui transações vinculadas.

- Mensagem: `"Esta categoria possui X lançamentos. Selecione uma categoria para reatribuí-los:"`
- `ExposedDropdownMenuBox` com categorias do mesmo tipo (exceto a que está sendo deletada)
- Se a categoria deletada for raiz com subcategorias, avisa que as subcategorias também serão deletadas/reatribuídas
- Botão `Confirmar` → reatribui lançamentos e deleta
- Botão `Cancelar`

---

## Tela 6 — Adicionar / Editar Lançamento (AddEditTransactionScreen)

Tela única para criar e editar. Usa `ConstraintLayout` para suportar rotação.

| Campo | Componente | Obrigatório |
|---|---|---|
| Tipo | `SegmentedButton`: Despesa / Receita / Transferência | Sim |
| Valor | `OutlinedTextField` teclado numérico (R$) | Sim |
| Data | `DatePicker` Material 3 | Sim |
| Descrição | `OutlinedTextField` | Não |
| Conta | `ExposedDropdownMenuBox` | Sim |
| Categoria | `ExposedDropdownMenuBox` hierárquico (Pai > Filho) | Sim (exceto Transferência) |
| Conta Destino | `ExposedDropdownMenuBox` | Só em Transferência |

- Botão **Salvar** → grava nova linha na aba `transactions` e atualiza saldo em `accounts`
- Botão **Excluir** (só em edição) → remove linha de `transactions` e recalcula saldo

---

## Tela 7 — Criar / Editar Conta (AddEditAccountScreen)

Tela única para criar e editar contas. Usa `ConstraintLayout`.

| Campo | Componente | Obrigatório |
|---|---|---|
| Nome | `OutlinedTextField` | Sim |
| Tipo | `SegmentedButton`: Carteira / Conta Corrente / Poupança / Crédito | Sim |
| Saldo Inicial | `OutlinedTextField` numérico (R$) | Sim (0,00 padrão) |
| Limite do Cartão | `OutlinedTextField` numérico (R$) | Só se tipo = CREDIT |
| Dia de Fechamento | `OutlinedTextField` numérico (1–28) | Só se tipo = CREDIT |
| Dia de Vencimento | `OutlinedTextField` numérico (1–28) | Só se tipo = CREDIT |

- Campos de crédito aparecem/somem com `AnimatedVisibility` conforme o tipo selecionado
- Botão **Salvar** → grava/atualiza linha na aba `accounts`
- Botão **Excluir** (só em edição) → remove conta (só permitido se não houver transações vinculadas)

---

## Tela 8 — Criar / Editar Categoria (AddEditCategoryScreen)

Tela única para criar e editar categorias. Usa `ConstraintLayout`.

| Campo | Componente | Obrigatório |
|---|---|---|
| Nome | `OutlinedTextField` | Sim |
| Tipo | `SegmentedButton`: Despesa / Receita | Sim |
| Categoria Pai | `ExposedDropdownMenuBox` (opcional) | Não |

- `Categoria Pai` lista apenas categorias **raiz** do mesmo tipo (sem parentId)
- Uma **subcategoria não pode ter subcategoria** — o campo Categoria Pai só aparece se a categoria sendo criada/editada não tiver filhos
- Se selecionada uma categoria pai, esta vira subcategoria (profundidade máxima: 2 níveis)
- Botão **Salvar** → grava/atualiza linha na aba `categories`
- Botão **Excluir** (só em edição) → abre `ReassignCategoryDialog` se houver lançamentos vinculados

---

## Tela 9 — Relatórios (ReportsScreen)

Navegação por período no topo: `< Maio 2025 >`.
Três seções em abas (`TabRow`). Usa `ConstraintLayout`.

### Aba 1 — Fluxo de Caixa
- Gráfico de barras agrupadas (Vico) — Receitas (verde) vs Despesas (vermelho) últimos 6 meses
- Tabela: Total Receitas | Total Despesas | Saldo do período

### Aba 2 — Gastos por Categoria
- Gráfico de pizza (Vico) — fatias por categoria no mês selecionado
- Lista: ícone | nome | valor | % do total
- Categorias filhas agrupadas sob o pai

### Aba 3 — Extrato por Conta
- Dropdown para selecionar a conta
- Saldo inicial → transações → saldo final
- Filtro: Todos / Receitas / Despesas / Transferências

---

## Estrutura da Planilha (Google Sheets)

Criada automaticamente na **primeira execução** com o nome `FinanceControl_Data`.
A **primeira linha de cada aba é sempre o cabeçalho**.

### Aba `accounts`
| id | name | type | balance | creditLimit | billingDay | dueDay | createdAt |
|----|------|------|---------|-------------|------------|--------|-----------|
| UUID | "Nubank" | CASH/CHECKING/SAVINGS/CREDIT | 150.00 | 5000.00 | 15 | 22 | 2025-05-01 |

- `creditLimit`, `billingDay`, `dueDay` → preenchidos apenas quando `type = CREDIT`; vazios nos demais
- `balance` em contas de crédito representa o valor atual da fatura (gasto no ciclo)
- `billingDay` = dia de fechamento da fatura (1–28)
- `dueDay` = dia de vencimento da fatura (1–28)

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
enum class AccountType { CASH, CHECKING, SAVINGS, CREDIT }
enum class CategoryType { INCOME, EXPENSE }

// Estado do dia no calendário
enum class DayMarkType { NONE, INCOME_ONLY, EXPENSE_ONLY, BOTH }
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
- Usar `PullToRefreshBox` (Material 3) em todas as telas com dados remotos — **nunca** botão de refresh manual
- Usar `ConstraintLayout` do Compose (`androidx.constraintlayout:constraintlayout-compose`) em todas as telas para suportar rotação sem quebrar layout
- Cards com lista limitada a **5 itens** sempre exibem botão `Ver todos` quando há mais itens
- Campos condicionais (ex: crédito) usam `AnimatedVisibility` — nunca `if` direto sem animação

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
- ❌ Não usar `SwipeRefresh` da Accompanist — use `PullToRefreshBox` do Material 3
- ❌ Não exibir mais de 5 itens em cards da OverviewScreen sem botão `Ver todos`
- ❌ Não permitir subcategoria ter subcategoria (profundidade máxima: 2 níveis)
- ❌ Nunca deletar conta ou categoria sem oferecer reatribuição dos lançamentos vinculados
