# Product Requirements Document: Pandora for Android

**Version:** 1.0  
**Status:** Product definition  
**Platform:** Native Android app

## 1. Product summary

Pandora is a personal knowledge organizer for Android. It gives people one dependable place to capture things they want to remember, organize them in ways that make sense to them, and revisit or understand them later. Its name references Pandora’s box: a personal place that holds discoveries, while leaving the user in control of what they open, keep, and connect.

The product is **an organizer first and an AI assistant second**. People remain in control of their knowledge structure. AI provides useful suggestions and helps people understand what they have saved, but it never changes organization on its own.

The experience should feel as natural to browse as a photo gallery: saved items appear in a chronological timeline, while folders, tags, collections, search, and AI conversations help users find context and make sense of what they have kept.

## 2. Problem

Useful information is scattered across screenshots, images, links, notes, documents, and other saved material. Existing tools often make capture difficult, require users to organize everything immediately, or treat AI as an automatic system that users cannot easily supervise.

People need a private, flexible place to save knowledge quickly and return to it later without losing ownership of how it is organized.

## 3. Target audience

Version 1.0 is for Android users who regularly save information they expect to revisit, including students, professionals, creators, researchers, and curious everyday users. They may collect ideas, learning resources, references, visual inspiration, receipts of thought, or work-related material and want a more useful alternative to an unstructured gallery, browser bookmarks, or disconnected note apps.

## 4. Product vision

Help users build a personal, searchable memory that grows naturally over time: easy to capture, clear to organize, pleasant to browse, and intelligent enough to help them understand connections in their own saved knowledge.

## 5. Goals

- Make it easy to capture information from wherever the user encounters it.
- Let users create and maintain their own organization with folders and tags.
- Make saved content easy to browse chronologically and find later.
- Use AI to reduce organizational effort and deepen understanding, while preserving user control.
- Surface useful relationships, duplicates, and related concepts without making irreversible changes.
- Support both quick retrieval and reflective exploration of a user’s saved knowledge.

## 6. Non-goals for Version 1.0

- AI independently organizing, moving, tagging, merging, or deleting user content.
- Replacing manual organization with AI.
- Requiring a Google account, Pandora account, or other sign-in to use the Version 1.0 organizer.
- Automatic cloud sync, shared libraries, or simultaneous multi-device use.
- Defining or introducing a Version 1.0 paid plan, subscription, advertising model, or other monetization mechanism.
- Defining technical architecture, data models, APIs, or implementation choices.
- Inventing later-version capabilities before they are discussed and prioritized.

## 7. Product principles

### Organizer first, AI second

Manual organization is the foundation of the product. AI is an assistive layer: it suggests, explains, and helps users explore; it does not take over.

### User approval is required

Any AI-proposed organization remains a proposal until the user approves it. The user can edit a proposed folder placement, tags, or other suggested metadata before accepting it.

### Capture now, organize when ready

Users should be able to save useful material quickly without being forced to decide its final structure in the moment.

### Many valid ways to organize

Knowledge can fit more than one context. The product supports flexible, overlapping organization rather than forcing every item into one place.

### Explain, do not obscure

AI should help users understand their saved concepts, connections, and material. Its behavior and suggestions should remain clear and reviewable.

### Protect user content

No destructive action happens without a clear confirmation from the user.

### Ownership and portability

Users own the knowledge they save and the structure they create around it. Pandora must make it possible for users to create a user-initiated, portable backup of their saved items, folders, tags, collections, item relationships, and saved AI chat history. Version 1.0 is single-device by design; portability is for user control and recovery, not automatic synchronization.

### Local by default

Pandora Version 1.0 is a personal, device-first experience. It does not require an account or sign-in. It does not automatically synchronize a library across devices. The user’s existing device protections, together with an optional in-app access lock, protect access to the personal library.

## 8. Core content model

An **item** is a saved piece of content. Items may be different kinds of material, such as an image, link, note, or document.

Users organize items through:

- **Folders:** User-created spaces for manual organization. Folders can be nested.
- **Tags:** Flexible labels that can be applied across folders and content types.
- **Multiple folder membership:** A single item can belong to more than one folder when it is relevant in multiple contexts.
- **Collections:** Purposeful groups of items that users can browse together and discuss with AI.

## 9. Core user experiences and requirements

### 9.1 Capture from anywhere

Users can bring useful content into Pandora from the places they encounter it on Android. Version 1.0 supports these capture entry points:

- **Inbound Android Share Sheet:** Sharing links, text selections, and images directly into Pandora via `ACTION_SEND` and `ACTION_SEND_MULTIPLE` from any third-party app (Chrome, Twitter/X, Reddit, Docs).
- **Speech-to-Text Voice Dictation:** Hands-free voice note transcription directly inside the Quick Note dialog using native Android `SpeechRecognizer`.
- **Pasting from Clipboard:** Pasting a copied link or text from the clipboard into Pandora.
- **Manual In-App Capture:** Adding an image, link, note, or document manually through the floating capture capsule.
- **OCR Document & Quote Scanning:** Capturing quotes and physical notes via device camera text recognition.
- **Saving Screenshots:** Ingesting screenshots through Pandora's capture pipeline.

Capture should be quick and should not require immediate organization.

After capture, the item becomes available in the user’s timeline and can later be organized manually or reviewed with AI assistance.

### 9.2 Chronological timeline

The primary browsing experience is a chronological timeline, inspired by the ease of browsing a photo gallery. It gives users a natural view of their saved history, including content that has not yet been organized.

The timeline is not an “inbox” that implies failure or requires users to clear it. It is a permanent, useful way to revisit saved knowledge over time.

### 9.3 Manual folders and tags

Users can create folders and nested folder structures that reflect their own mental model. They can add tags to items and use both systems together.

Users decide which folders and tags apply to an item. An item may be placed in multiple folders without needing to duplicate the item itself.

### 9.4 AI organization suggestions

AI can analyze an item and suggest relevant organization, such as folder placements and tags. Suggestions must be presented for review, never applied automatically.

Before accepting a suggestion, users can:

- Change the suggested folder or folders.
- Add, remove, or revise suggested tags.
- Accept only the parts they want.
- Decline the suggestion and organize the item themselves.

The product must make it clear that accepted changes are user-approved decisions.

### 9.5 Collections

Users can create collections to gather related items for a particular topic, project, question, or purpose. A collection provides a focused view across saved items and organization structures.

Users can ask AI about a collection to help them explore or understand the material gathered there.

### 9.6 AI conversations about saved knowledge

Users can select an individual item and ask AI about it. They can also ask AI about a collection.

AI conversations are intended to help users understand saved concepts and material—for example, by answering questions, explaining ideas, drawing connections, or helping users explore what they have collected.

Each new conversation starts as a fresh chat. Chat history is saved so users can return to previous discussions about their items and collections.

### 9.7 Content-aware detail screens

Opening an item shows a detail experience suited to that item’s content type. An image can support image-appropriate interaction; a link, note, or document should have interactions appropriate to that kind of content.

Detail screens should provide a consistent path to:

- View the saved content.
- See and edit its folders and tags.
- Access related items.
- Ask AI about that specific item.
- Review relevant saved AI chat history.

The image interaction is a reference for the principle of content-aware design, not a template to copy literally to every content type.

### 9.8 Search and discovery

Users can find saved knowledge through both:

- **Keyword search:** Finds explicit words and matching metadata.
- **Semantic search:** Helps find material by meaning, even when the exact words differ.

Search should help users retrieve items, folders, tags, and collections relevant to what they are looking for.

### 9.9 Related items

The product can surface items that are meaningfully related to the item currently being viewed. Related items are discovery aids, not automatic organizational changes.

Users should be able to inspect related items and decide whether they are useful to their current task or understanding.

### 9.10 Duplicate and similar-item detection

The product actively detects potential duplicates before and after save:
- **Proactive Ingestion Duplicate Guard:** When capturing an incoming link URL or exact note title, the app queries the local vault and displays a subtle non-blocking banner (*"Already in Vault: Saved on [Date]"*) to prevent duplicate clutter.
- **Library Duplicate Scanner:** Identifies existing identical or near-duplicate items in the vault and presents them for user review.

Any action that changes or removes items requires explicit user confirmation. The user can keep both items if that is their preference.

### 9.11 Safe actions and confirmations

Destructive actions—including deleting an item or taking action on possible duplicates—require confirmation before they happen. Users should have enough context to understand what will change before confirming.

### 9.12 AI usage indicator

The app displays a small, understandable indicator of AI usage. This gives users visibility into their AI consumption without distracting from the organizer experience.

### 9.13 Data ownership, backup, and device scope

Pandora Version 1.0 keeps a user’s personal library on their device and does not require an account. Users can create a portable `.pandora` backup containing their saved items, folders, tags, multi-folder membership, collections, item relationships, and saved AI chat history.

**Topological Integrity Restorer:** The backup and restoration system strictly serializes and restores entities in foreign-key topological order (`Folders` $\rightarrow$ `Tags` $\rightarrow$ `Items` $\rightarrow$ `ItemFolderCrossRef` $\rightarrow$ `ItemTagCrossRef` $\rightarrow$ `Collections` $\rightarrow$ `CollectionItemCrossRef`) with checksum validation to ensure zero SQLite foreign-key constraint violations on restore.

Users can use this backup to retain control of their information and recover or move their library manually. Automatic backup, automatic cross-device sync, collaboration, and shared libraries are outside the scope of Version 1.0.

### 9.14 Access to the personal library

Pandora is intended for the device owner’s personal use. It does not ask users to create or use a Google account or a Pandora account for Version 1.0.

The app offers an optional in-app multi-modal access lock supporting:
- **Biometric Authentication:** Fingerprint and Face Unlock via AndroidX `BiometricPrompt`.
- **Numeric PIN:** Custom PIN hashing.
- **Interactive 3x3 Canvas Pattern Lock:** Tactile gesture node connection lock.

Whether or not this option is enabled, Pandora must clearly communicate that access to an unlocked device can affect the privacy of locally saved knowledge.

## 10. Key user journeys

### Save first, organize later

1. A user encounters useful content elsewhere on their Android device.
2. They capture it into Pandora quickly.
3. It appears in their chronological timeline.
4. When ready, they manually add it to folders and tags, or request an AI suggestion.
5. If AI suggests organization, the user edits or approves it before anything changes.

### Revisit and understand a saved item

1. A user finds an item through the timeline, folders, tags, or search.
2. They open its content-specific detail screen.
3. They review its organization and related items.
4. They start a fresh AI chat to ask about the item.
5. The conversation is saved for later return.

### Explore a collection

1. A user gathers relevant items into a collection.
2. They browse the collection as a focused body of knowledge.
3. They ask AI questions about the collection to understand its concepts, patterns, or relationships.
4. The resulting conversation remains available in saved chat history.

### Resolve similar items safely

1. The product identifies items that may be duplicates or very similar.
2. The user reviews the items and their context.
3. The user chooses what, if anything, to change.
4. The product asks for confirmation before any destructive action.

## 11. Success criteria

Version 1.0 succeeds when users can reliably:

- Capture content without interrupting their flow.
- Browse their saved history naturally through the timeline.
- Organize knowledge manually using nested folders, tags, and multiple folder membership.
- Receive, revise, and approve AI organization suggestions.
- Find content through keyword and semantic search.
- Use related items, duplicate suggestions, and collections to rediscover useful knowledge.
- Ask AI about an individual item or collection and return to saved chats.
- Trust that the app will not make organizational or destructive changes without their approval.

Version 1.0 will also be evaluated against these outcome-oriented measures in representative user testing and early product use:

- At least 90% of participants can save a supported item through a supported capture entry point and find it in the timeline within 30 seconds, without help.
- At least 80% of participants can find a previously saved target item in a library of at least 500 items within one minute, using timeline browsing, folders, tags, or search.
- At least 80% of participants who receive an AI organization suggestion can correctly review, edit, accept in part or in full, or decline it—and understand that nothing changes until they approve it.
- The product will measure the proportion of AI organization suggestions that users accept, edit before accepting, and decline. These outcomes will be used to assess suggestion relevance and user control; acceptance alone is not a success measure.
- At least 90% of participants can identify the confirmation step and its consequences before completing a destructive action in a guided safety scenario.

## 12. User stories

### Capture and browse

- As a user, I want to save useful content from wherever I encounter it on my Android device so that I do not lose information I may need later.
- As a user, I want to save something quickly without deciding how to organize it immediately so that capture does not interrupt what I am doing.
- As a user, I want every saved item to appear in a chronological timeline so that I can revisit my saved history naturally.
- As a user, I want to browse older saved content in the timeline so that I can rediscover things I may have forgotten.
- As a user, I want an item screen that fits the type of content I saved so that I can view and interact with it comfortably.
- As a user, I want to capture content through sharing, pasting, screenshot saving, or manual addition so that I can use the quickest appropriate method in the moment.

### Organize my knowledge

- As a user, I want to create folders and nested folders so that my knowledge can reflect the structure that makes sense to me.
- As a user, I want to add tags to items so that I can group related knowledge across different folders.
- As a user, I want to place one item in more than one folder so that I do not have to choose only one context for it.
- As a user, I want to see and change an item’s folders and tags so that I remain in control of my organization.
- As a user, I want to create collections of related items so that I can focus on a topic, project, or question in one place.

### Use AI with control

- As a user, I want AI to suggest relevant folders and tags for a saved item so that organization takes less effort.
- As a user, I want to review and edit each AI suggestion before accepting it so that the final organization matches my intent.
- As a user, I want to accept only the useful parts of an AI suggestion so that I can combine AI help with my own judgment.
- As a user, I want to decline an AI suggestion without changing my item so that AI assistance remains optional.
- As a user, I want to ask AI about a selected item so that I can understand the concepts or information it contains.
- As a user, I want to ask AI about a collection so that I can explore the material I have gathered as a whole.
- As a user, I want a new AI conversation to start fresh so that unrelated questions do not get mixed together.
- As a user, I want prior AI conversations to be saved and accessible from the relevant item or collection so that I can continue learning later.
- As a user, I want to see my AI usage through a small indicator so that I understand my current use without being distracted from organizing my knowledge.

### Find and rediscover

- As a user, I want to search by words so that I can quickly find content whose name, text, tag, or other explicit information I remember.
- As a user, I want to search by meaning so that I can find relevant content even when I do not remember the exact words used.
- As a user, I want to see related saved items when viewing an item so that I can discover useful connections in my knowledge.
- As a user, I want the app to point out likely duplicate or very similar items so that I can make informed decisions about my saved content.
- As a user, I want to review duplicate or similar-item suggestions before taking action so that I can keep items that are similar but intentionally distinct.

### Trust and safety

- As a user, I want the app never to change my organization automatically so that I remain the owner of my knowledge system.
- As a user, I want a clear confirmation before an item is deleted or another destructive action is taken so that I do not lose content by accident.
- As a user, I want to understand what will change before I confirm a destructive action so that I can decide confidently.
- As a user, I want to create a portable backup of my items, organization, collections, and saved AI chats so that I retain control of my knowledge.
- As a user, I want to use Pandora without creating an account so that I can keep my personal organizer device-first.
- As a user, I want the option to lock Pandora using my device’s authentication experience so that other people cannot casually open my personal library.
- As a user, I want to know that Pandora does not automatically synchronize my library to another device so that I understand the boundaries of Version 1.0.

## 13. Non-functional requirements

These requirements describe the quality, trust, and experience expected of Version 1.0. They do not prescribe implementation or architecture.

### 13.1 Usability and clarity

- The app must make capture, browsing, manual organization, search, and AI assistance understandable without requiring technical knowledge.
- The primary timeline experience must remain simple and familiar to browse, even as a user’s saved library grows.
- Folder, tag, collection, and AI-suggestion actions must be clearly labelled so users understand whether they are viewing, suggesting, editing, or applying a change.
- AI suggestions must be visually and conceptually distinct from user-approved organization.
- Users must be able to cancel or decline optional AI assistance without penalty or disruption to their normal workflow.
- Destructive actions must use clear language, identify the affected content, and require an explicit confirmation.
- The product must avoid presenting AI outputs as certain when they are suggestions, interpretations, or possible relationships.

### 13.2 Performance and responsiveness

- Common interactions—including opening the app, browsing the timeline, opening an item, navigating folders or collections, and applying a manual organization change—must feel responsive in ordinary use.
- Capturing content must provide prompt feedback that the item has been saved or that further user action is needed.
- Search results should appear quickly enough to support active exploration rather than interrupting the user’s train of thought.
- AI requests must clearly communicate that they are in progress and must provide a helpful recovery path if a response cannot be completed.
- Loading states must preserve context and must not leave users uncertain whether a requested action succeeded.

### 13.3 Reliability and data integrity

- Once the app confirms that an item or approved organization change has been saved, it must remain available to the user unless the user explicitly changes or removes it.
- The app must prevent accidental duplication caused by repeated taps, interrupted capture flows, or uncertain request states where practical.
- Folder, tag, collection, and multiple-folder relationships must remain consistent when users browse, search, or edit them.
- A failed or interrupted AI operation must not alter a user’s item, organization, or saved chat history without explicit user approval.
- The app must provide understandable feedback when content cannot be saved, loaded, searched, or processed, including a way for the user to retry where appropriate.

### 13.4 Privacy and user control

- User content and the organization built around it must be treated as personal information.
- The app must clearly communicate when AI is being asked to process a selected item or collection.
- AI assistance must be initiated by the user; the app must not automatically submit content to AI merely to organize it.
- Users must retain control over whether to use AI features and must be able to continue using the organizer without AI.
- Saved AI chat history must be treated as part of the user’s private knowledge and be available only within the user’s own app experience.
- The app must not expose a user’s saved items, folders, tags, collections, or chat history to other users by default.

### 13.5 Security

- Access to a user’s saved knowledge must be protected against unauthorized access.
- The product must protect user content and AI chat history during normal use and while information is being handled by the app.
- Sensitive user actions, including content deletion, must require deliberate user interaction and must not be triggered indirectly by an AI response.
- The product must not allow AI suggestions or generated text to bypass user confirmation requirements.

### 13.6 Accessibility

- The app must support Android accessibility features so that people using screen readers, larger text, magnification, or alternative interaction methods can complete core tasks.
- Text, controls, status messages, and confirmations must be legible and understandable at enlarged text sizes.
- Important information must not be conveyed only through color, imagery, or motion.
- Core tasks—capture, browse, organize, search, ask AI, review a suggestion, and confirm a destructive action—must be usable without relying solely on gestures that may be difficult for some users.

### 13.7 Compatibility and continuity

- Version 1.0 is designed for supported Android devices and must provide a coherent experience across common phone screen sizes and orientations.
- The app must handle temporary connectivity limitations gracefully, clearly distinguishing actions that are available immediately from those that need connectivity, including AI assistance.
- Users must not lose the ability to browse already available saved knowledge simply because an AI request or online feature is unavailable.

### 13.8 AI quality and transparency

- AI suggestions for folders, tags, related items, and possible duplicates must be presented as assistance, not as authoritative decisions.
- AI should provide suggestions relevant to the item or collection the user selected and avoid introducing unrelated content.
- AI conversations about an item or collection must make the selected context clear to the user.
- When AI cannot confidently help, the product should make that limitation clear rather than implying a false answer or automatic change.
- AI features must always preserve the central principle: the user approves organizational changes and decides what to do with AI output.

### 13.9 Scalability of the personal library

- The experience must remain understandable and navigable as users accumulate a substantial personal library of items, folders, tags, collections, and saved AI chats.
- Timeline browsing, manual organization, and search must continue to support effective retrieval as content grows.
- The app must avoid forcing users to reorganize their existing knowledge merely because their library becomes larger.

### 13.10 Storage awareness and graceful limits

- Pandora must make users aware when available device storage is becoming insufficient for saving additional items or completing a local backup.
- When storage is low, the app must explain the impact in plain language and identify a safe next action, such as freeing device space or trying again later.
- Low storage must not silently delete, overwrite, reduce the quality of, or otherwise alter existing saved items, organization, collections, or AI chat history.
- The app must preserve access to already saved knowledge as far as device conditions allow, even when it cannot accept additional captured content.

### 13.11 Data portability and single-device continuity

- The user must be able to initiate a portable backup of their Version 1.0 personal library without needing to create an account.
- The portable backup must preserve the user’s saved items and their meaningful context: folders, tags, multi-folder membership, collections, and saved AI chat history.
- Pandora must describe the purpose and contents of a backup clearly before the user creates it.
- Version 1.0 must clearly state that it does not automatically synchronize, merge, or share a user’s library across devices.
- Manual recovery or transfer through a user-initiated backup must not be represented as real-time multi-device sync.

### 13.12 Authentication and access control

- Version 1.0 must not require a Google account, Pandora account, or other sign-in for ordinary use of the organizer.
- Pandora must offer an optional in-app access lock through the device’s established authentication experience.
- The app must state plainly that local-only use means a user’s device access protections remain important to the privacy of their saved knowledge.
- Enabling or disabling the optional in-app access lock must require deliberate user action and clear confirmation of the resulting access experience.

## 14. Future scope

Future improvements, additional use cases, and later product versions will be discussed and defined separately. They are intentionally out of scope for Version 1.0 so this document remains faithful to the agreed first product direction.
