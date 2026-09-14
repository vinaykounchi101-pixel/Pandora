# Paradox Android App — Complete Features & Implementation Guide

A comprehensive architectural and implementation manual of all **AI / Machine-Assisted** and **Non-AI / Platform** features built in Paradox. This guide contains architectural patterns, data models, business logic algorithms, and native Android code structures required to port or implement these features in another project.

---

## Table of Contents
1. [Core Architecture & Foundation](#1-core-architecture--foundation)
2. [AI & Machine-Assisted Features](#2-ai--machine-assisted-features)
   - 2.1 [Ask Paradox Grounded Conversational Engine](#21-ask-paradox-grounded-conversational-engine)
   - 2.2 [CameraX & Google ML Kit Receipt OCR Scanner](#22-camerax--google-ml-kit-receipt-ocr-scanner)
   - 2.3 [Smart Receipt Entity Extractor](#23-smart-receipt-entity-extractor)
   - 2.4 [Natural Language Quick Add Parser](#24-natural-language-quick-add-parser)
   - 2.5 [Speech-to-Expense Voice Recognition Engine](#25-speech-to-expense-voice-recognition-engine)
   - 2.6 [Proactive Duplicate Guard Warning Engine](#26-proactive-duplicate-guard-warning-engine)
   - 2.7 [Smart CSV Statement Importer & Auto-Header Detector](#27-smart-csv-statement-importer--auto-header-detector)
   - 2.8 [5-Pillar Financial Health Scoring Algorithm](#28-5-pillar-financial-health-scoring-algorithm)
   - 2.9 [Safe-to-Spend Multi-Horizon Buffer Engine](#29-safe-to-spend-multi-horizon-buffer-engine)
   - 2.10 [Spending Leak Hunter](#210-spending-leak-hunter)
   - 2.11 [Purchase Simulator & Impact Modeler](#211-purchase-simulator--impact-modeler)
   - 2.12 [Trend-Aware Spending Forecaster](#212-trend-aware-spending-forecaster)
   - 2.13 [Monthly Narrative Digest & Financial Vibe Analyzer](#213-monthly-narrative-digest--financial-vibe-analyzer)
   - 2.14 [AI Insight Audit Logging](#214-ai-insight-audit-logging)
3. [Non-AI & Native Android Platform Features](#3-non-ai--native-android-platform-features)
   - 3.1 [SQLCipher Encrypted Room Vault & Keystore Security](#31-sqlcipher-encrypted-room-vault--keystore-security)
   - 3.2 [PBKDF2 Credential Hashing & Multi-Modal Auth](#32-pbkdf2-credential-hashing--multi-modal-auth)
   - 3.3 [Interactive 3x3 Canvas Pattern Lock Component](#33-interactive-3x3-canvas-pattern-lock-component)
   - 3.4 [Biometric Prompt Integration](#34-biometric-prompt-integration)
   - 3.5 [Paradox Quick Ball Floating Assistive Menu & Overlay Service](#35-paradox-quick-ball-floating-assistive-menu--overlay-service)
   - 3.6 [Debts & Udhaar (Khata) Peer-to-Peer Ledger](#36-debts--udhaar-khata-peer-to-peer-ledger)
   - 3.7 [Native Contact Picker Integration](#37-native-contact-picker-integration)
   - 3.8 [Wallets & Accounts Model with Net Balance Hero](#38-wallets--accounts-model-with-net-balance-hero)
   - 3.9 [Exact Decimal `Money` Arithmetic & Domain Models](#39-exact-decimal-money-arithmetic--domain-models)
   - 3.10 [Deterministic Budgets & Guardrails](#310-deterministic-budgets--guardrails)
   - 3.11 [Recurring Expenses & Subscriptions Engine](#311-recurring-expenses--subscriptions-engine)
   - 3.12 [Savings Goals & Contributions Tracker](#312-savings-goals--contributions-tracker)
   - 3.13 [Group Bill Splitting Engine](#313-group-bill-splitting-engine)
   - 3.14 [Discipline Logging Streak Counter](#314-discipline-logging-streak-counter)
   - 3.15 [Encrypted Vault Backup, Topological Restorer & SAF](#315-encrypted-vault-backup-topological-restorer--saf)
   - 3.16 [Vector PDF & CSV Export via FileProvider](#316-vector-pdf--csv-export-via-fileprovider)
   - 3.17 [Offline-First Outbox Sync Engine](#317-offline-first-outbox-sync-engine)
   - 3.18 [Jetpack Glance Home Screen Widget & Dynamic Shortcuts](#318-jetpack-glance-home-screen-widget--dynamic-shortcuts)
   - 3.19 [Inbound Share Sheet Receiver for UPI Receipts](#319-inbound-share-sheet-receiver-for-upi-receipts)
   - 3.20 [Curated Pastel Design System & In-App Localization](#320-curated-pastel-design-system--in-app-localization)

---

## 1. Core Architecture & Foundation

Paradox follows **Clean Architecture** with **Unidirectional Data Flow (UDF)**:

```
┌──────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
│   Jetpack Compose UI (Material 3) + ViewModels (StateFlow)   │
└──────────────────────────────┬───────────────────────────────┘
                               │ Observes UIState / Sends Events
┌──────────────────────────────▼───────────────────────────────┐
│                      Domain Layer                            │
│   Pure Kotlin UseCases, Domain Models (Money), Repositories  │
│   (Zero Android framework or Room dependencies)              │
└──────────────────────────────┬───────────────────────────────┘
                               │ Implements Interfaces
┌──────────────────────────────▼───────────────────────────────┐
│                       Data Layer                             │
│   Room Entities, DAOs, Repositories, SQLCipher Vault,        │
│   Keystore, Preferences DataStore, Android Platform Clients  │
└──────────────────────────────────────────────────────────────┘
```

### Key Architectural Guidelines
1. **Zero Math Errors**: Never use `Double` or `Float` for currency. Always use `BigDecimal` wrapped in an immutable `Money` inline value class.
2. **Local-First & Encrypted**: All user records reside in an on-device SQLCipher encrypted SQLite database. No external cloud server is required for core functionality.
3. **SQL-Level Profile Isolation**: Every table holds a foreign key `profileId`. All queries enforce `WHERE profileId = :profileId`.

---

## 2. AI & Machine-Assisted Features

### 2.1 Ask Paradox Grounded Conversational Engine
* **File Reference**: `com.paradox.app.domain.usecase.askparadox.AskParadoxUseCase`
* **Concept**: A deterministic, zero-hallucination conversational AI solver. Instead of passing sensitive user finance databases to a generative LLM prompt, it parses natural language queries, maps them to verified database queries, calculates exact totals, and renders formatted conversational responses.

#### Implementation Pattern:
```kotlin
enum class AskIntent {
    CATEGORY_SPEND,        // "How much did I spend on food this month?"
    SAFE_TO_SPEND,         // "Can I afford dinner tonight?" / "What is my safe to spend?"
    TOP_EXPENSES,          // "What are my biggest expenses?"
    BUDGET_STATUS,         // "How is my budget doing?"
    TOTAL_INCOME,          // "How much did I earn this month?"
    NET_CASH_FLOW,         // "What is my net savings this month?"
    SAVINGS_PROGRESS,      // "How are my savings goals?"
    GENERAL_SUMMARY        // "Give me a financial summary"
}

class AskParadoxUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository,
    private val calculateSafeToSpendUseCase: CalculateSafeToSpendUseCase,
    private val calculateCashFlowUseCase: CalculateCashFlowUseCase
) {
    suspend operator fun invoke(profileId: String, query: String): AskParadoxResponse {
        val intent = classifyIntent(query.lowercase())
        return when (intent) {
            AskIntent.CATEGORY_SPEND -> resolveCategorySpend(profileId, query)
            AskIntent.SAFE_TO_SPEND -> resolveSafeToSpend(profileId)
            AskIntent.TOP_EXPENSES -> resolveTopExpenses(profileId)
            // ... other deterministic resolvers
        }
    }
}
```

---

### 2.2 CameraX & Google ML Kit Receipt OCR Scanner
* **File References**: `com.paradox.app.feature.capture.ocr.CameraScannerView`, `ReceiptImageOcrHelper`
* **Dependencies**: `androidx.camera:camera-camera2`, `androidx.camera:camera-lifecycle`, `com.google.mlkit:text-recognition:16.0.0`
* **Concept**: Uses CameraX for real-time receipt frame preview with a target scanning rectangle, captures high-resolution still images, and runs on-device Google ML Kit Vision text recognition.

#### Implementation Pattern:
```kotlin
class ReceiptImageOcrHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val receiptOcrParser: ReceiptOcrParser
) {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun processImageUri(imageUri: Uri): ReceiptOcrResult = suspendCancellableCoroutine { continuation ->
        val image = InputImage.fromFilePath(context, imageUri)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val parsed = receiptOcrParser.parse(visionText.text)
                continuation.resume(parsed)
            }
            .addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
    }
}
```

---

### 2.3 Smart Receipt Entity Extractor
* **File Reference**: `com.paradox.app.feature.capture.ocr.ReceiptOcrParser`
* **Concept**: Parses unformatted multi-line OCR text to extract:
  1. **Grand Total**: Evaluates keyword indicators (`Total`, `Grand Total`, `Amount Due`, `Net Amount`, `UPI Total`) and matches the highest reasonable numeric token.
  2. **Merchant / Store Header**: Filters out transaction prefixes and identifies clean business titles from top lines.
  3. **Date**: Regex matching across Indian & International date formats (`dd/MM/yyyy`, `yyyy-MM-dd`, `dd-MMM-yyyy`).
  4. **Payment Method Hints**: Regex for `UPI`, `GPay`, `PhonePe`, `HDFC`, `ICICI`, `Cash`, `Card`.

---

### 2.4 Natural Language Quick Add Parser
* **File Reference**: `com.paradox.app.feature.capture.nlp.NaturalLanguageParser`
* **Concept**: Converts natural language logs (*"Spent 850 on grocery at DMart using HDFC card yesterday"*) into structured data objects.
* **Extraction Pipeline**:
  - **Amount Extraction**: Matches currency symbols (`₹`, `Rs`, `INR`, `$`) followed by digits, or freestanding amounts associated with spending keywords.
  - **Date Normalization**: Resolves relative words (`today`, `yesterday`, `day before yesterday`, `last sunday`) into ISO `LocalDate`.
  - **Category Scoring**: Compares words against known category keywords (`food`, `fuel`, `petrol`, `rent`, `movie`, `wifi`, `groceries`).
  - **Payment Method Tokenizer**: Matches `UPI`, `Credit Card`, `Debit Card`, `Cash`, `NetBanking`.
  - **Title Normalization**: Strips parsed entities, conjunctions, and prepositions to extract a clean merchant/item title (*"Grocery at DMart"*).

---

### 2.5 Speech-to-Expense Voice Recognition Engine
* **File Reference**: `com.paradox.app.core.util.VoiceRecognitionManager`
* **Concept**: Listens to microphone audio using Android's native `SpeechRecognizer` API with offline-first recognition models and passes transcription directly into the Natural Language Parser.

```kotlin
class VoiceRecognitionManager(private val context: Context) {
    private var speechRecognizer: SpeechRecognizer? = null

    fun startListening(
        onResult: (String) -> Unit,
        onError: (String) -> Unit,
        onRmsChanged: (Float) -> Unit
    ) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    matches?.firstOrNull()?.let(onResult)
                }
                override fun onRmsChanged(rmsdB: Float) { onRmsChanged(rmsdB) }
                override fun onError(error: Int) { onError("Speech recognition error: $error") }
                // ...
            })
            startListening(intent)
        }
    }
}
```

---

### 2.6 Proactive Duplicate Guard Warning Engine
* **File Reference**: `com.paradox.app.domain.usecase.capture.DuplicateGuardUseCase`
* **Algorithm**:
  1. Searches existing expenses within a **3-day temporal window** (`targetDate - 3d` to `targetDate + 3d`).
  2. Compares **Exact Amount** matches.
  3. Runs **Levenshtein Distance / Token Similarity** between existing titles and new title.
  4. Returns non-blocking warning state:
     - `ExactDuplicateFound(existingExpense)`: 100% same amount and same merchant on the same day.
     - `PossibleDuplicateFound(existingExpense)`: Same amount and similar merchant within 72 hours.
     - `NoDuplicate`.

---

### 2.7 Smart CSV Statement Importer & Auto-Header Detector
* **File Reference**: `com.paradox.app.domain.usecase.capture.CsvImportUseCase`
* **Concept**: Parses standard bank statement CSV exports. Automatically detects variable column names:
  - **Date Column**: Matches `date`, `transaction date`, `txn date`, `value date`.
  - **Description Column**: Matches `narration`, `description`, `particulars`, `remarks`, `details`.
  - **Amount Column**: Matches `debit`, `withdrawal`, `amount`, `dr amount`, `expense`.
  - Validates row records, flags credit entries (optionally categorizing as Income), and generates batch insert previews before committing to the database.

---

### 2.8 5-Pillar Financial Health Scoring Algorithm
* **File Reference**: `com.paradox.app.domain.usecase.intelligence.CalculateFinancialHealthScoreUseCase`
* **Scoring Rubric (Total: 100 Points)**:
  1. **Savings Discipline (20 pts)**: `(Savings Amount / Total Income) * 100`. Full marks if savings rate $\ge 20\%$.
  2. **Budget Adherence (20 pts)**: Penalizes categories exceeding $>100\%$ of allocated budget.
  3. **Bill Regularity (20 pts)**: Measures whether recurring subscriptions were logged within scheduled due dates.
  4. **Net Cash Flow (20 pts)**: Scores positive vs negative monthly net cash balance.
  5. **Emergency Buffer (20 pts)**: Evaluates total liquid assets divided by average monthly spend (target: $\ge 3$ months).
* **Grade Assignment**: `A+ (90-100)`, `A (80-89)`, `B (70-79)`, `C (50-69)`, `D (<50)`.

---

### 2.9 Safe-to-Spend Multi-Horizon Buffer Engine
* **File Reference**: `com.paradox.app.domain.usecase.intelligence.CalculateSafeToSpendUseCase`
* **Mathematical Formula**:
  $$\text{Safe-to-Spend} = \text{Liquid Accounts Balance} - \text{Remaining Budget Allocations} - \text{Upcoming Unpaid Bills}$$
* **Horizons**:
  - **Today**: `Safe-to-Spend / Days Remaining in Month`.
  - **This Week**: `Daily Buffer * 7`.
  - **Rest of Month**: Total Safe Buffer.

---

### 2.10 Spending Leak Hunter
* **File Reference**: `com.paradox.app.domain.usecase.intelligence.DetectSpendingLeaksUseCase`
* **Detection Criteria**:
  1. **Idle Subscriptions**: Recurring subscriptions with recurring flags active but no associated activity recorded.
  2. **Micro-Leaks**: Frequent low-ticket expenses ($< \text{₹}100$) in categories like *Snacks*, *Cigarettes*, *Coffee*, recurring $>3\times$ weekly.
  3. **Category Inflation**: Compares current month category spend against a 3-month rolling median. Flags categories growing $>25\%$.

---

### 2.11 Purchase Simulator & Impact Modeler
* **File Reference**: `com.paradox.app.domain.usecase.intelligence.SimulatePurchaseUseCase`
* **Function**: Takes a prospective purchase amount and category. Calculates:
  - New Category Spend & Budget Status (`Safe`, `Near Limit`, `Over Budget by ₹X`).
  - Drop in Monthly Health Score.
  - Reduction in Safe-to-Spend daily runway.

---

### 2.12 Trend-Aware Spending Forecaster
* **File Reference**: `com.paradox.app.domain.usecase.intelligence.ForecastSpendingUseCase`
* **Algorithm**:
  - Computes **Daily Velocity**: $\text{Spend To Date} / \text{Current Day of Month}$.
  - Computes **Projected Month End Spend**: $\text{Spend To Date} + (\text{Daily Velocity} \times \text{Remaining Days})$.
  - Compares Projected Total against Total Monthly Incomes and Active Budgets.

---

### 2.13 Monthly Narrative Digest & Financial Vibe Analyzer
* **File Reference**: `com.paradox.app.domain.usecase.engagement.GenerateMonthlyDigestUseCase`
* **Classifications**:
  - **"Disciplined Saver"**: Savings rate $>30\%$, zero budget overflows.
  - **"Balanced Spender"**: 10-30% savings rate, budgets controlled.
  - **"Impulsive Explorer"**: Multiple leak alerts, spending velocity $>120\%$ of income.
* Generates human-friendly milestone narratives summarizing highest spending day, top merchant, and total discretionary spend.

---

### 2.14 AI Insight Audit Logging
* **File Reference**: `com.paradox.app.data.local.entity.AiInsightLogEntity`
* **Schema**:
  `id`, `profileId`, `type` (`HEALTH_SCORE`, `LEAK_DETECTED`, `SIMULATION`), `payload` (JSON metadata), `isEstimate` (Boolean), `generatedAt` (Timestamp).
* Maintains a permanent audit history so users can review historical recommendations.

---

## 3. Non-AI & Native Android Platform Features

### 3.1 SQLCipher Encrypted Room Vault & Keystore Security
* **File Reference**: `com.paradox.app.data.local.ParadoxDatabase`, `KeystoreManager`, `PassphraseManager`
* **Security Architecture**:
  1. `AndroidKeyStore` generates an AES-256-GCM master key bound to hardware (TEE / StrongBox).
  2. Hardware key wraps/encrypts a 256-bit random passphrase stored in private app storage.
  3. The unwrapped passphrase is fed into SQLCipher's `SupportFactory` to encrypt the SQLite database file (`paradox_vault.db`).

```kotlin
val factory = SupportFactory(SQLiteDatabase.getBytes(passphrase))
Room.databaseBuilder(context, ParadoxDatabase::class.java, "paradox_vault.db")
    .openHelperFactory(factory)
    .build()
```

---

### 3.2 PBKDF2 Credential Hashing & Multi-Modal Auth
* **File Reference**: `com.paradox.app.core.security.CredentialHasher`
* **Parameters**:
  - Algorithm: `PBKDF2WithHmacSHA256`
  - Iterations: `12,000`
  - Salt: 16-byte cryptographically secure random (`SecureRandom`)
  - Key Length: 256 bits

---

### 3.3 Interactive 3x3 Canvas Pattern Lock Component
* **File Reference**: `com.paradox.app.feature.auth.PatternLockView`
* **Concept**: Custom Jetpack Compose `Canvas` tracking real-time drag gestures (`pointerInput`), rendering:
  - 9 node dots with dynamic radius scaling on touch.
  - Outer glow rings on active nodes.
  - Real-time connecting lines following finger position.
  - Tactile haptic feedback on node registration.
  - Dot sequence serialization (e.g., `"0-1-2-4"`).

---

### 3.4 Biometric Prompt Integration
* **File Reference**: `com.paradox.app.core.security.BiometricPromptManager`
* **Implementation**: Wraps `androidx.biometric.BiometricPrompt` supporting `BIOMETRIC_STRONG` and `BIOMETRIC_WEAK` with credential fallback.

---

### 3.5 Paradox Quick Ball Floating Assistive Menu & Overlay Service
* **File Reference**: `com.paradox.app.feature.quickball.QuickBallOverlayService`, `QuickBallView`
* **Key Mechanisms**:
  1. **Foreground Service**: Runs with a low-priority notification channel.
  2. **WindowManager View**: Injects a custom `ComposeView` with `TYPE_APPLICATION_OVERLAY` and attaches AndroidX Lifecycle Owners (`ViewTreeLifecycleOwner`, `ViewTreeSavedStateRegistryOwner`, `ViewTreeViewModelStoreOwner`).
  3. **Edge-Snap Physics**: Calculates distance to left/right screen edges on drag release and animates the orb to the nearest edge with spring physics.
  4. **Radial Crescent Arc Menu**: Popping out 4 circular shortcut capsules (Quick Add, Voice Log, Receipt Scan, Ask Paradox) in an ergonomic C-curve arc.
  5. **3-Second Auto-Tuck**: Coroutine timer tucking the orb 50% into the screen bezel and dimming opacity to 38% after 3s of inactivity.
  6. **Android 14 & 15 Background Activity Launch Compliance**: Uses `PendingIntent.getActivity` configured with `ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED` and `Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP` to reliably launch deep-linked screens over third-party apps.

---

### 3.6 Debts & Udhaar (Khata) Peer-to-Peer Ledger
* **File Reference**: `com.paradox.app.domain.usecase.debt.AddDebtUseCase`, `AddRepaymentUseCase`
* **Data Model**:
  - `DebtEntity`: `id`, `profileId`, `type` (`LENT` / `BORROWED`), `contactName`, `contactPhone`, `initialAmount`, `remainingBalance`, `dueDate`, `isSettled`.
  - `DebtRepaymentEntity`: `id`, `debtId`, `profileId`, `amount`, `date`, `notes`.
* **Repayment Logic**: Deducts repayment amounts from `remainingBalance`. Automatically flags `isSettled = true` when `remainingBalance == 0`.

---

### 3.7 Native Contact Picker Integration
* **File Reference**: `com.paradox.app.feature.debt.DebtScreen`
* **Implementation**: Uses `ActivityResultContracts.PickContact` querying `ContactsContract.CommonDataKinds.Phone` to safely retrieve contact names and telephone numbers with graceful manual entry fallback.

---

### 3.8 Wallets & Accounts Model with Net Balance Hero
* **File Reference**: `com.paradox.app.feature.accounts.AccountsScreen`
* **Types**: `CASH`, `BANK_ACCOUNT`, `CREDIT_CARD`, `SAVINGS_ACCOUNT`, `DIGITAL_WALLET`.
* **Net Balance Calculation**:
  $$\text{Net Worth} = \sum \text{Liquid Assets (Cash, Bank, Savings)} - \sum \text{Liabilities (Credit Cards)}$$

---

### 3.9 Exact Decimal `Money` Arithmetic & Domain Models
* **File Reference**: `com.paradox.app.domain.model.Money`
* **Structure**: Immutable value class wrapping `BigDecimal` with strict currency validation and `RoundingMode.HALF_EVEN` (Banker's Rounding).

---

### 3.10 Deterministic Budgets & Guardrails
* **File Reference**: `com.paradox.app.domain.usecase.budget.GetBudgetStatusesUseCase`
* **Status Transitions**:
  - `ON_TRACK`: Spend $< 80\%$ of budget.
  - `NEAR_LIMIT`: Spend between $80\%$ and $99.9\%$.
  - `OVER_BUDGET`: Spend $\ge 100\%$.

---

### 3.11 Recurring Expenses & Subscriptions Engine
* **File Reference**: `com.paradox.app.domain.usecase.recurring.GetUpcomingRecurringUseCase`
* **Frequencies**: `DAILY`, `WEEKLY`, `MONTHLY`, `QUARTERLY`, `YEARLY`.
* **Monthly Normalization**: Converts non-monthly billing frequencies into equivalent monthly cost burdens for cash-flow projection.

---

### 3.12 Savings Goals & Contributions Tracker
* **File Reference**: `com.paradox.app.domain.usecase.savings.AddContributionUseCase`
* **Calculations**: Live progress percentage, remaining target balance, and suggested monthly contribution rate to meet `targetDate`.

---

### 3.13 Group Bill Splitting Engine
* **File Reference**: `com.paradox.app.domain.usecase.engagement.SplitExpenseUseCase`
* **Features**:
  - Equal splitting with remainder cent/paisa rounding distribution.
  - Unequal percentage/custom share splitting with sum validation.

---

### 3.14 Discipline Logging Streak Counter
* **File Reference**: `com.paradox.app.domain.usecase.engagement.CalculateStreakUseCase`
* **Logic**: Evaluates consecutive calendar days containing at least 1 verified expense or income entry.

---

### 3.15 Encrypted Vault Backup, Topological Restorer & SAF
* **File Reference**: `com.paradox.app.domain.usecase.backup.EncryptedBackupUseCase`, `RestoreBackupUseCase`
* **Encryption Format**: PBKDF2-HMAC-SHA256 (12,000 iterations) + AES-256-GCM with 16-byte salt, 12-byte IV, and 128-bit authentication tag.
* **Topological Entity Insertion**: Restores records in FK-dependent order (Categories & Payment Methods $\rightarrow$ Accounts $\rightarrow$ Budgets $\rightarrow$ Recurring $\rightarrow$ Goals $\rightarrow$ Expenses $\rightarrow$ Incomes $\rightarrow$ Debts) to prevent SQLite foreign key constraint failures.
* **Storage Access Framework**: `ActivityResultContracts.CreateDocument` and `OpenDocument` allowing users to save `.paradoxvault` files anywhere in Android's document provider tree.

---

### 3.16 Vector PDF & CSV Export via FileProvider
* **File Reference**: `com.paradox.app.domain.usecase.export.ExportUseCases`
* **Vector PDF**: Renders multi-page financial statements using `android.graphics.pdf.PdfDocument` with custom headers, category summary charts, and transaction tables.
* **FileProvider**: Exposes exports via `content://` URIs with grant read permissions.

---

### 3.17 Offline-First Outbox Sync Engine
* **File Reference**: `com.paradox.app.feature.sync.SyncEngine`, `SyncQueueDao`
* **Mechanism**: Records all mutations into a `sync_queue` table with entity reference, operation type (`INSERT`, `UPDATE`, `DELETE`), retry counts, and timestamp.

---

### 3.18 Jetpack Glance Home Screen Widget & Dynamic Shortcuts
* **File Reference**: `com.paradox.app.feature.widget.ParadoxSpendWidget`, `res/xml/shortcuts.xml`
* **Glance AppWidget**: Displays live daily Safe-to-Spend balance and 1-tap quick capture buttons.
* **Shortcuts**: Native launcher shortcuts deep-linking into Quick Add, Voice Entry, Receipt Scan, and Ask Paradox.

---

### 3.19 Inbound Share Sheet Receiver for UPI Receipts
* **File Reference**: `AndroidManifest.xml`, `MainActivity.kt`
* **Intent Filter**: Registers `ACTION_SEND` for `text/plain` and `image/*`. Automatically parses shared payment confirmation texts from UPI apps (Google Pay, PhonePe, Paytm).

---

### 3.20 Curated Pastel Design System & In-App Localization
* **File References**: `com.paradox.app.ui.theme.ThemePalette`, `SessionDataStore`, `res/values/strings.xml`, `res/values-hi/`, `res/values-mr/`
* **6 Curated Pastel Palettes**:
  1. `Ocean Slate` (Default)
  2. `Forest Sage`
  3. `Twilight Lilac`
  4. `Warm Clay`
  5. `Matcha Tea`
  6. `Vintage Rose`
* **In-App Localization Switcher**: Runtime locale switching for English (`en`), Hindi (`hi`), Marathi (`mr`), and Hinglish (`hi-Latn`) persisted in DataStore.

---

*Document generated for architectural reference and project portability.*
