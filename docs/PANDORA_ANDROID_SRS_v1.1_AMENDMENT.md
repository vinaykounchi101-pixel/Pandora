# Pandora Android SRS — v1.1 Amendment

**Status:** Proposed decisions and requirements to incorporate before implementation.  
**Applies to:** `PANDORA_ANDROID_SRS.md` v1.0.  
**Product posture:** Local-first, internet-optional, no required account, no automatic sync, and no paid backend assumed.

## 1. Decisions adopted for Version 1

1. The offline core comprises capture, timeline, item viewing, folders, tags, multi-folder membership, collections, manual organization, keyword search over locally available text, optional access lock, and user-initiated backup/export. It must remain usable with no network connection.
2. Network use is optional and limited to user-initiated link-preview retrieval and AI features. The app must not crawl, upload, index remotely, or contact a service in the background without a user action.
3. There is no Pandora-operated cloud AI service in v1. If cloud AI is enabled, it uses a user-supplied provider key. The app shall never embed a shared production API key or secret in its APK.
4. AI features are optional enhancements, not a prerequisite for the core product. If no provider key is configured, AI controls explain how to configure one and manual alternatives remain available.
5. Backup uses a user-selected destination through Android's Storage Access Framework. The first v1 restore mode is **replace the current library**, not merge. A merge restore is deferred until conflict and ID rules are specified and tested.
6. Imported images and documents are copied into app-private storage after capture. Persisted URI access may be retained only until copying succeeds; source URIs are not relied on for permanent availability.
7. Full local encryption is deferred from the v1 baseline. All data must use app-private storage; access lock is an app-entry control, not a substitute for encryption. Encryption may be added only with a documented key-recovery and backup strategy.

## 2. Required replacement: Open Items (§32)

Replace §32 with the following items. They are decisions required before the corresponding optional feature begins, rather than blockers for the offline core.

- **AI provider contract:** select a provider supported by user-supplied credentials; document its pricing, data-retention terms, supported regions, and minimum Android networking requirements. A provider abstraction remains mandatory.
- **AI consent text and payload limits:** approve the first-use disclosure and define the minimum content sent for each AI action. The app must never send an entire library, history from another conversation, or data from an unselected collection.
- **On-device OCR/text extraction:** choose an on-device approach and supported image/document formats. Until selected, image/document extraction is best-effort and must not block capture.
- **Supported import contract:** approve supported MIME types, maximum file size, maximum total import size, and handling of password-protected or corrupt documents.
- **Backup format:** define archive layout, manifest schema/version, checksum algorithm, and whether an optional password-protected export will be supported. Replace-restore is the only v1 restore behavior.
- **Encryption roadmap:** decide whether and when database/file encryption is introduced; define Android Keystore usage, recovery limitations, and encrypted-backup compatibility before adding it.
- **Device support:** select minimum and target Android SDK levels; define phone orientation support. Tablet/foldable optimization is deferred unless expressly included.
- **Storage thresholds:** set an operational low-space threshold and a preflight policy using estimated import/backup size plus a safety margin.

## 3. New §9.5 — Permissions and Android capture contract

- The app shall prefer Android Photo Picker and Storage Access Framework document picker over broad media/storage permissions.
- The app shall request a permission only when the user invokes the capability that requires it. A denial shall preserve access to all unrelated features and present a clear alternative where available.
- Supported Android Share intents, MIME types, maximum import size, and unsupported-content behavior shall be documented in the release configuration.
- On successful import, external image/document content shall be copied atomically to app-private storage before the item is committed to the database. A failed copy shall leave no visible item or orphaned partial file.
- A shared link shall store the original URL and user-editable title. Preview metadata retrieval is opt-in per link and network-dependent.
- “Screenshot capture” means a user-mediated Android screen-capture/share flow. Pandora shall not silently capture other apps or the device display.

## 4. New §9.6 — Network, AI, and privacy contract

### 4.1 Connectivity behavior

| Capability | Network required | Offline behavior |
|---|---:|---|
| Core library, organization, timeline, keyword search, backup/export | No | Fully available |
| OCR/indexing | No, if on-device approach is selected | Item is still saved if extraction is unavailable or fails |
| Link preview | Yes, user initiated | URL and user-entered title remain usable |
| AI suggestions, AI chat, cloud semantic search | Yes | Show an unavailable/configuration state; never block manual work |

### 4.2 AI provider credentials and usage

- A provider key shall be entered only in Settings, stored using Android Keystore-backed encrypted storage, masked in the UI, excluded from backups, excluded from logs, and removable by the user.
- Before the first request to each provider, the app shall obtain explicit consent identifying the provider, the selected item/collection content that will be sent, and that provider terms may apply.
- Each request shall send only the minimum selected context. Collection chat may send only the items deliberately included in that collection; it shall disclose an estimated payload size before submission when practical.
- The app shall use TLS for all network requests, impose configurable timeouts, and never log request bodies, responses, keys, item content, chat text, or URLs with sensitive query values.
- Invalid credentials, exhausted provider quotas, network loss, and provider errors shall be distinguishable in the UI and shall offer retry/settings/manual-workflow recovery.
- The AI usage indicator shall show locally counted requests by feature and, where supplied by the provider, estimated tokens or provider-reported usage. It shall not claim a monetary cost unless one is reliably available.

## 5. Required additions to §§10, 15, 17, and 19 — lifecycle and restore

### Content and deletion

- The supported content types and limits shall be configured explicitly. Unsupported, corrupt, password-protected, or oversized input shall be rejected before a database row is committed.
- Deleting an item requires confirmation and atomically removes its app-private content file, extracted text, local search/index records, memberships, pending suggestions, and item-scoped conversations. If secure deletion cannot be guaranteed by the filesystem, the app shall not claim that it is guaranteed.
- Deleting a collection removes collection membership only after confirmation. Collection-scoped conversations shall be deleted after an explicit statement in that confirmation; they must never be silently orphaned.
- Deleting a folder removes only its item-folder associations. Deleting a tag removes only its tag associations. Neither operation deletes underlying items.
- A duplicate “merge” is deferred from v1. v1 duplicate resolution offers **keep both** or **delete one after confirmation**. A future merge must define canonical-content selection, metadata union, memberships, chats, file retention, and rollback.

### Backup and replace restore

- A backup archive shall contain a versioned manifest, structured metadata, all referenced app-private item files, and an integrity checksum for each entry or the archive as a whole.
- Backups shall exclude API keys, Android Keystore material, biometric state, transient caches, logs, and unfinished work.
- Restore shall validate archive format, manifest version, checksums, available storage, and all required payloads before altering the current library.
- Replace restore shall display the item/attachment counts and state that it permanently replaces the current local library. It requires a final explicit confirmation.
- Restore must be transactional as far as platform storage allows: import and validate into a staging area, preserve the existing library until validation succeeds, then switch atomically. On failure or cancellation, discard staging data and leave the existing library intact.
- The app shall support import from the current backup format and reject unsupported future formats without partial restoration. Backup-format migrations require automated compatibility tests.

## 6. Required replacements for vague non-functional requirements (§7)

The project shall set device-specific values during implementation; the following are acceptance targets for a representative supported mid-range device and a library of 10,000 items with 2,000 attachments:

- App launch to a usable timeline: 2 seconds or less when the local database is warm; no blocking network request.
- Timeline first page: 500 ms or less from a warm local database; additional pages load incrementally.
- Manual folder/tag change: visible confirmation within 300 ms after local commit.
- Local keyword search: first results within 500 ms for a query of three or more characters; results continue to update without freezing the UI.
- Capture: a visible saving state immediately; a successful item is committed only after its content copy and database transaction complete.
- Link preview and AI requests: user-visible progress; network timeout no longer than 30 seconds; retry only after explicit user action except WorkManager retries for an already-user-requested job.
- Every background task must have a unique idempotency key and may not create duplicate items, suggestions, index entries, or backup archives after process death/retry.

## 7. New §29.1 — mandatory test additions

- Permission-denial, picker cancellation, revoked/external-URI, malformed-share, oversized-file, and interrupted-copy tests.
- Backup checksum failure, insufficient staging space, cancellation, replace-restore rollback, and backup-version compatibility tests.
- AI consent, key exclusion from backups/logs, minimum-context payload, invalid-key, quota, offline, timeout, and redacted-logging tests.
- Lifecycle tests covering deletion of each scope and all dependent records/files.
- Performance tests against the agreed library-size target and accessibility tests at the largest supported font scale.

## 8. Delivery order

**Release 1 — free local core:** capture/import, timeline, detail screens, folders/tags/multi-folder membership, collections, local keyword search, deletion safeguards, backup/export, and replace restore.
**Release 2 — AI enhancements:** link preview, Gemini BYOK client integration, AI organization proposal review sheet, and grounded collection conversations.

---

## 9. New §9.7 — Ingestion, Security & Integrity Requirements

### 9.7.1 Inbound Android Share Sheet Receiver
- The application shall declare an `intent-filter` in `AndroidManifest.xml` targeting `android.intent.action.SEND` and `android.intent.action.SEND_MULTIPLE` supporting MIME types `text/plain`, `text/x-vcard`, and `image/*`.
- On launch from a share intent, `MainActivity` shall extract `Intent.EXTRA_TEXT`, `Intent.EXTRA_SUBJECT`, or `Intent.EXTRA_STREAM` URIs, pre-populate the capture pipeline, and route directly to the quick capture sheet without blocking app startup.

### 9.7.2 Speech-to-Text Voice Dictation
- The quick note capture interface shall provide a microphone trigger invoking Android's `SpeechRecognizer` with `RecognizerIntent.ACTION_RECOGNIZE_SPEECH` and `EXTRA_PARTIAL_RESULTS`.
- Transcribed speech segments shall be appended reactively to the note body with graceful offline fallback and explicit error handling.

### 9.7.3 Proactive Ingestion Duplicate Guard
- Prior to committing an incoming URL or exact note title, the app shall query the local Room database (`ItemDao.findItemByUrl` / `ItemDao.findItemByExactTitle`).
- If an existing record is detected, a non-blocking informational banner shall be presented (`DuplicateGuardState.DuplicateFound(existingItem)`) displaying the previous capture date while allowing the user to proceed or view the existing record.

### 9.7.4 Topological Foreign-Key Safe Backup & Restorer
- The `.pandora` archive generator shall serialize tables in deterministic foreign-key dependency order:
  `Folders` $\rightarrow$ `Tags` $\rightarrow$ `Items` $\rightarrow$ `ItemFolderCrossRef` $\rightarrow$ `ItemTagCrossRef` $\rightarrow$ `Collections` $\rightarrow$ `CollectionItemCrossRef`.
- The restoration engine shall stage and validate archive checksums, clear staging upon failure, and execute database inserts in the exact topological order inside a single Room transaction to eliminate SQLite FK constraint violations.

### 9.7.5 Interactive 3x3 Canvas Pattern Lock
- The access security subsystem shall provide an optional 3x3 node gesture Pattern Lock rendered via Jetpack Compose `Canvas`.
- The pattern sequence shall be hashed using `SHA-256` with a per-device salt and persisted securely in Preferences DataStore.

**Release 1.1 — local refinement:** on-device OCR if selected, access lock, scalable indexing, and duplicate detection with keep/delete resolution.

**Release 2 — optional connected features:** user-supplied-key AI suggestions/chat and provider-backed semantic search, only after the provider, consent, privacy, and usage requirements above are accepted.

