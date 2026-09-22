# ContractorHub

Construction Business Management App — Sites, Labour, Materials, Bills, Payments,
Daily Diary, Profit/Loss, Estimates, Electrical/Plumbing/Civil calculators, Contacts.
Full roadmap: available in the project's original specification document (95 parts).

## Status: Phase 0-2 — Foundation + Dashboard

Phase 0-1 (foundation) + Phase 2 (Dashboard) are included in this build:

- Gradle project foundation + Version Catalog (`gradle/libs.versions.toml`)
- Theme (light + dark) — PART 9 color palette
- Bottom navigation (Home / Sites / Transactions / Reports / More) — Home now has
  the full Dashboard UI; the rest are still placeholder fragments (to be replaced in Phase 3+)
- Room database skeleton (compiles, but no real tables yet — those come in Phase 3+)
- **Dashboard (new, Phase 2):** Active Sites, Today's Income/Expense, Pending
  Receivable, Labour Payable, Low Stock warning, Insights, Quick Actions row,
  Site Status cards, Recent Transactions list — as per PART 14/71/81/82.
  Data currently comes from `DummyDashboardData.kt` (dummy). Once Site/Labour/
  Material repositories are built in Phase 3+, they'll be wired into
  `DashboardViewModel` — no UI changes will be needed (the contract is already
  fixed in the `DashboardSummary` model).
- Quick Action buttons currently just show a "coming soon" Toast — actual
  navigation will be wired up once the corresponding module is built (there's
  a TODO comment in the code).

**Not yet done:** Sites/Labour/Materials/Bills logic, OCR, camera, PDF export, backup,
fonts (Noto Sans Devanagari / Poppins — the actual .ttf files still need to be added, see below).

## Baseline versions

|                    |                            |
| ------------------ | -------------------------- |
| Android Studio     | Quail 3 — 2026.1.3 Patch 1 |
| AGP                | 9.3.0                      |
| Gradle             | 9.5.0                      |
| JDK                | 17                         |
| Kotlin             | 2.4.10                     |
| Compile/Target SDK | 37                         |
| Min SDK            | 26                         |
| UI                 | XML Views (no Compose)     |
| Architecture       | MVVM + Repository          |
| Database           | Room / SQLite              |
| Package            | com.contractorhub.app      |

⚠️ **Important:** The AGP/Gradle/Android Studio versions listed above may be
newer than my (AI) knowledge cutoff, so I wasn't able to verify that they
exist. If Gradle Sync in Android Studio suggests a different stable version,
update it **only in `gradle/libs.versions.toml`** — don't hardcode a version
anywhere else (PART 3).

## First-time setup steps

1. Open this folder in Android Studio ("Open" as an existing project).
2. The `gradlew` / `gradlew.bat` and `gradle-wrapper.jar` files are **not**
   included in this export (they're binary files that couldn't be generated
   here). If Android Studio prompts you, choose "Configure Gradle Wrapper" /
   "Recreate wrapper" — or, from the terminal (if Gradle is already installed
   on your system):

```
gradle wrapper --gradle-version 9.5.0
```

   This will use the already-provided `gradle-wrapper.properties` to generate
   the rest of the wrapper files.

3. Fonts: `app/src/main/res/font/` doesn't have the actual `.ttf` files yet.
   Download Noto Sans Devanagari and Poppins from their official source
   (Google Fonts) and place them in that same folder — they'll then be wired
   into `font_family.xml` and the theme (this is a later-phase task).
4. Sync → Build → Run:

```
./gradlew assembleDebug
```

   Only move on to the next phase (Phase 2 — Dashboard) once this succeeds
   (PART 60, PART 91).

## Known limitations (Phase 0-1)

- The launcher icon is currently a plain placeholder shape (the real logo —
  PART 12 — comes later).
- Of all 5 bottom-nav tabs, only Home has real UI; the rest show a "Coming
  soon" placeholder.
- The Room database only has 1 placeholder table — Site/Labour/Material/Bill
  entities haven't been added yet.
- The gradlew wrapper executable/jar isn't included (see above).

## Build order (remaining phases)

Sites+Clients → Labour+Attendance+Ledger → Materials+Stock →
Bills+Scanner → Expenses+Payments → Diary → Profit/Loss → Estimates →
Electrical → Plumbing → Civil calculators → Contacts → Photos/Documents →
Reports → Backup/Restore → Notifications → Polish → Testing → Release.

Standing rule: **don't move to the next phase until the current one builds successfully.**

## 👤 Author
**Sandeep Dhore**
[LinkedIn](https://www.linkedin.com/in/sandeep-dhore) · [GitHub](https://github.com/Sandip678)
