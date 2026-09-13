---
name: Editorial Porcelain
colors:
  surface: '#faf8ff'
  surface-dim: '#d2d9f4'
  surface-bright: '#faf8ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f2f3ff'
  surface-container: '#eaedff'
  surface-container-high: '#e2e7ff'
  surface-container-highest: '#dae2fd'
  on-surface: '#131b2e'
  on-surface-variant: '#464555'
  inverse-surface: '#283044'
  inverse-on-surface: '#eef0ff'
  outline: '#777587'
  outline-variant: '#c7c4d8'
  surface-tint: '#4d44e3'
  primary: '#3525cd'
  on-primary: '#ffffff'
  primary-container: '#4f46e5'
  on-primary-container: '#dad7ff'
  inverse-primary: '#c3c0ff'
  secondary: '#9d4300'
  on-secondary: '#ffffff'
  secondary-container: '#fd761a'
  on-secondary-container: '#5c2400'
  tertiary: '#004c76'
  on-tertiary: '#ffffff'
  tertiary-container: '#00659a'
  on-tertiary-container: '#bedfff'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#e2dfff'
  primary-fixed-dim: '#c3c0ff'
  on-primary-fixed: '#0f0069'
  on-primary-fixed-variant: '#3323cc'
  secondary-fixed: '#ffdbca'
  secondary-fixed-dim: '#ffb690'
  on-secondary-fixed: '#341100'
  on-secondary-fixed-variant: '#783200'
  tertiary-fixed: '#cce5ff'
  tertiary-fixed-dim: '#93ccff'
  on-tertiary-fixed: '#001d31'
  on-tertiary-fixed-variant: '#004b73'
  background: '#faf8ff'
  on-background: '#131b2e'
  surface-variant: '#dae2fd'
typography:
  display-lg:
    fontFamily: Newsreader
    fontSize: 40px
    fontWeight: '400'
    lineHeight: 48px
    letterSpacing: -0.02em
  display-lg-mobile:
    fontFamily: Newsreader
    fontSize: 32px
    fontWeight: '400'
    lineHeight: 40px
    letterSpacing: -0.01em
  headline-lg:
    fontFamily: Newsreader
    fontSize: 28px
    fontWeight: '500'
    lineHeight: 36px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Newsreader
    fontSize: 22px
    fontWeight: '500'
    lineHeight: 30px
  headline-sm:
    fontFamily: Hanken Grotesk
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  body-lg:
    fontFamily: Hanken Grotesk
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 26px
  body-md:
    fontFamily: Hanken Grotesk
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 22px
  body-sm:
    fontFamily: Hanken Grotesk
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 18px
  label-lg:
    fontFamily: Hanken Grotesk
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.01em
  label-md:
    fontFamily: Hanken Grotesk
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.02em
  label-sm:
    fontFamily: Hanken Grotesk
    fontSize: 10px
    fontWeight: '700'
    lineHeight: 14px
    letterSpacing: 0.04em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-tablet: 1.5rem
  margin: 1.25rem
  margin-tablet: 2rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.25rem
  space-xl: 1.75rem
---

## Brand & Style

This design system establishes an authentic, tactile personal knowledge archive tailored specifically for modern Android. Rooted in the visual quietude of Japanese stationery and archival curation, it rejects ephemeral tech trends, neon glow states, and dark glassmorphism in favor of clarity, physical restraint, and effortless utility.

The core philosophy is strictly **"Organizer first, AI second."** The system approaches personal thoughts, documents, captured snapshots, and links as valuable artifacts. Artificial intelligence serves quietly in the background as a meticulous librarian rather than an intrusive centerpiece—surfacing semantic connections, auto-tagging, and providing structured reflections only when requested.

### Key Aesthetic Tenets
- **Porcelain & Crisp Sheets:** A layered canvas evoking heavyweight off-white cotton paper overlaid with pristine white note cards.
- **Editorial Utility:** Deep slate typographic hierarchy paired with functional pastels allows high-density thoughts to breathe without visual fatigue.
- **Calm Accents:** Color acts as taxonomy rather than decoration. Each functional pillar carries its own designated pastel wash and vibrant anchor tint.
- **Material Ergonomics:** Precision-engineered for thumb-reach ergonomics on Android, observing 48dp minimum touch boundaries, smooth spring transitions, and comfortable negative space.

## Colors

The palette is light-mode exclusive, tuned to provide maximum legibility, calm focus, and deliberate contextual signaling. Avoid harsh synthetic blacks; instead, rely on slate-infused charcoals.

### Surface System
- **Canvas Base (`#FBFBFA` / `#F8F9FA`):** Warm porcelain substrate that prevents eye strain during prolonged reading and capture sessions.
- **Elevated Surfaces (`#FFFFFF`):** High-grade pure white used for primary card nodes, modal bottom sheets, search surfaces, and drawer surfaces.
- **Structural Outlines (`#E2E8F0` / `#E5E7EB`):** Muted hairline borders defining edge boundaries without adding visual clutter.

### Semantic Functional Roles
- **Primary & Interactive (Periwinkle / Iris - `#4F46E5`):** Standard navigation states, selected tab bars, active control toggles, primary callouts, and default actions. Tint wash: `#EEF2FF`.
- **Capture & Quick Add (Warm Peach / Apricot - `#F97316`):** Action-oriented affordances, quick-add triggers, audio record badges, and high-priority flags. Tint wash: `#FFF7ED`.
- **Knowledge & Collections (Soft Sky Blue - `#0284C7`):** Folders, topic clusters, deep-link indices, and external citations. Tint wash: `#F0F9FF`.
- **Reflections & AI Assist (Lavender / Wisteria - `#7C3AED`):** Subtle secondary indicators, semantic tags, automated synthesis cards, and periodic digests. Must always remain subdued; never overpower manual user notes. Tint wash: `#F5F3FF`.

### Neutral & Typographic Spectrum
- **Text Primary (`#0F172A`):** Pure slate charcoal for body text, headers, and essential iconography (meeting AAA accessibility standards).
- **Text Secondary (`#334155`):** Sub-headings, active metadata, and contextual timestamps.
- **Text Tertiary / Muted (`#64748B`):** Inactive icons, placeholder hints, and subtle dividers.

## Typography

The typographic system pairs an authoritative, high-craft serif display face (`Newsreader`) with a contemporary, razor-sharp sans-serif workhorse (`Hanken Grotesk`). This tension mimics an editorial notebook: expressive, considered headlines complemented by neutral, legible content blocks.

### Typographic Principles
- **Editorial Headlines:** `Newsreader` is employed for screen titles, note subject lines, daily reflection prompts, and rich quote blocks. Its organic forms provide an intimate reading experience.
- **Functional Body & Metadata:** `Hanken Grotesk` guarantees high clarity across long-form capture, file hierarchies, and quick-scan card overviews.
- **Rhythmic Line Spacing:** Body copy enforces a 1.55–1.625 line-height ratio to prevent visual crowding when reviewing multi-paragraph notes on mobile viewports.
- **Case Formats:** Use sentence case globally. Avoid forced all-caps except on micro-metadata tags (`label-sm`).

## Layout & Spacing

Layouts adhere to an unhurried, anti-crowded cadence designed to foster contemplation and easy one-handed operation.

### Grid & Boundaries
- **Mobile (Phone):** Single-column dynamic stream flanked by `1.25rem` (20dp) to `1.5rem` (24dp) edge gutters. Cards span full width between margins. Touch targets strictly retain `48dp` bounding boxes minimum.
- **Tablet / Foldable (Unfolded):** Asymmetric 2-column layout (e.g., 38% directory tree/pinned collections, 62% active reading and note workspace) using `1.5rem` (24dp) gutters and `2rem` (32dp) outer margins.

### Component Spacing Behavior
- **Card Internals:** Internal card padding is locked between `1.0rem` (16dp) and `1.25rem` (20dp). Avoid nested borders that reduce internal padding below `12dp`.
- **Vertical Stack Rhythm:** Use `space-md` (16dp) for sibling cards in standard feeds, and `space-xl` (28dp) to delineate contextual sections (such as "Today's Reflections" from "Pinned Stacks").

## Elevation & Depth

Visual depth is achieved through tactile tonal layering, edge definition, and soft physical ambient occlusion rather than heavy drop shadows or flashy blurs.

### Surface Hierarchy
1. **Level 0 (Canvas Base):** Flat `#FBFBFA`. Never elevated, hosts full-screen background scrolling.
2. **Level 1 (Default Sheet & Cards):** Pure `#FFFFFF` resting atop Level 0. Outlined with a continuous 1px `#E2E8F0` hairline border and supported by an ultra-diffused, paper-like shadow: `0 1px 3px rgba(15, 23, 42, 0.04), 0 6px 16px rgba(15, 23, 42, 0.02)`.
3. **Level 2 (Active / Picked Cards & Menus):** Pure `#FFFFFF` elevated during long-press drag or popover states: `0 8px 24px rgba(15, 23, 42, 0.08)`, border soft slate `#CBD5E1`.
4. **Level 3 (Bottom Sheets & Dialogs):** Sheet surfaces resting at pure `#FFFFFF` with smooth 28dp top radiuses, anchored over a warm charcoal scrim (`rgba(15, 23, 42, 0.3)`).

### Strict Negative Rules
- No colored or glowing box-shadows.
- No backdrop blur filters on cards (keep sheet renders fast and crisp on native Android surfaces).
- Zero gradient fills on card surfaces.

## Shapes

The shape system blends the structure of physical stationery cards with the comfort of rounded native Android geometry.

### Rounding Scale
- **Pill / Continuous Capsule (`9999px`):** Reserved for taxonomy chips, status indicators, floating action buttons, search bars, and interactive pill triggers.
- **Containers & Sheet Cards (`16px` - `24px` / `rounded-2xl`):** All content cards, modal sheets, and quick-capture zones use a unified `16dp` to `24dp` continuous corner curvature (reminiscent of bound notebook corners).
- **Embedded Media & Inputs (`10px` - `12dp`):** Text input containers, preview thumbnails, audio waveform modules, and inline code/quote blocks.

## Components

### Buttons
- **Primary Action (Global / Submit):** Solid `#4F46E5` background, `#FFFFFF` typography, 48dp height, rounded to 24dp or fully pill-shaped. No outer shadows in resting state; subtle 0.98 scale compression on press.
- **Capture Trigger (Floating Action Button):** Prominent warm peach (`#F97316`), pure white icon, positioned within easy thumb reach. Accompanied by haptic tap confirmation.
- **Secondary / Ghost:** Transparent surface with a 1px border (`#E2E8F0`), active text in `#334155`. Hover/press fills surface with `#F8F9FA`.

### Cards (Knowledge Artifacts)
- **Structure:** Surface white (`#FFFFFF`), 1px solid border (`#E2E8F0`), `rounded-2xl` (16–20dp).
- **Internal Spacing:** Standardized at 20dp padding.
- **Top Row:** Topic indicator pill or type badge alongside contextual date (`body-sm` in `#64748B`).
- **Middle Section:** Card title in `headline-md` or `headline-sm` (`Newsreader` or `Hanken Grotesk`), followed by truncated excerpt in `body-md` (max 3 lines).
- **Bottom Row:** Metadata chips (e.g., reading time, linked notes count, audio length) rendered in their functional pastel washes.

### Chips & Taxonomy Pills
- **Style:** Compact capsule shape (height 32dp, horizontal padding 12dp).
- **Unselected:** Canvas tone `#F8F9FA`, text `#334155`, border 1px `#E2E8F0`.
- **Active Filter:** Soft periwinkle wash `#EEF2FF`, border `#C7D2FE`, text `#4F46E5`.
- **AI / Assistive Tag:** Subtle wisteria wash `#F5F3FF`, border `#DDD6FE`, text `#7C3AED`, accompanied by an understated sparkle icon.

### Form Inputs & Quick Capture Bars
- **Surface:** `#FFFFFF` resting inside `#FBFBFA` canvas.
- **Height & Border:** Minimum 52dp height, 1px `#CBD5E1` border, 12dp corner radius.
- **Focus State:** 1.5px `#4F46E5` outline with no outer fuzz or ring glow.
- **Placeholder:** `#64748B` in `body-md`.

### Selection Controls (Checkboxes & Radios)
- **Dimension:** 22dp geometric profile embedded in a 48dp touch container.
- **Off State:** 1.5px border `#94A3B8`, pure white core.
- **On State:** Solid fill (`#4F46E5`), crisp white check/inner dot, 4dp corner radius for checkboxes, full circle for radios.

### Bottom Sheet (Navigation & Metadata Inspectors)
- **Top Edge:** Rounded-t-3xl (28dp).
- **Drag Handle:** 36dp width, 4dp height, `#CBD5E1`, 8dp top margin.
- **Surface:** Pristine white with edge-to-edge content support and zero horizontal screen cutoff on mobile displays.