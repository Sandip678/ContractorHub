# ContractorHub

Construction Business Management App — Sites, Labour, Materials, Bills, Payments,
Daily Diary, Profit/Loss, Estimates, Electrical/Plumbing/Civil calculators, Contacts.
पूर्ण roadmap: प्रोजेक्टच्या मूळ specification document मध्ये (95 parts).

## Status: Phase 0-2 — Foundation + Dashboard

Phase 0-1 (foundation) + Phase 2 (Dashboard) आता या build मध्ये आहेत:
- Gradle project foundation + Version Catalog (`gradle/libs.versions.toml`)
- Theme (light + dark) — PART 9 color palette
- Bottom navigation (Home / Sites / Transactions / Reports / More) — Home वर आता
  पूर्ण Dashboard UI, बाकीचे अजूनही placeholder fragments (Phase 3+ मध्ये replace होतील)
- Room database चा सांगाडा (compile होतो, अजून खरी tables नाहीत — त्या Phase 3+ मध्ये)
- **Dashboard (नवीन, Phase 2):** Active Sites, Today's Income/Expense, Pending
  Receivable, Labour Payable, Low Stock warning, Insights, Quick Actions row,
  Site Status cards, Recent Transactions list — PART 14/71/81/82 प्रमाणे.
  डेटा सध्या `DummyDashboardData.kt` मधून येतो (dummy). Phase 3+ मध्ये Site/Labour/
  Material repositories तयार झाल्यावर `DashboardViewModel` मध्ये तेच जोडायचे —
  UI ला हात लावावा लागणार नाही (contract आधीच `DashboardSummary` model मध्ये फिक्स आहे).
- Quick Action buttons सध्या फक्त "coming soon" Toast दाखवतात — प्रत्यक्ष module
  तयार झाल्यावर navigation जोडायचं (कोड मध्ये TODO comment आहे).

**अजून नाही:** Sites/Labour/Materials/Bills logic, OCR, camera, PDF export, backup,
fonts (Noto Sans Devanagari / Poppins — actual .ttf files टाकायचे राहिले आहेत, खाली बघ).

## Baseline versions

| | |
|---|---|
| Android Studio | Quail 3 — 2026.1.3 Patch 1 |
| AGP | 9.3.0 |
| Gradle | 9.5.0 |
| JDK | 17 |
| Kotlin | 2.4.10 |
| Compile/Target SDK | 37 |
| Min SDK | 26 |
| UI | XML Views (no Compose) |
| Architecture | MVVM + Repository |
| Database | Room / SQLite |
| Package | com.contractorhub.app |

⚠️ **महत्त्वाचे:** वरील AGP/Gradle/Android Studio versions माझ्या (AI) knowledge cutoff
नंतरच्या असू शकतात, त्यामुळे मी त्या exist करतात हे verify करू शकलो नाही. Android
Studio मध्ये project उघडल्यावर Gradle Sync ने वेगळी stable version सुचवली, तर
**फक्त `gradle/libs.versions.toml` मध्ये** ती अपडेट कर — बाकी कुठेही version हार्डकोड
करू नकोस (PART 3).

## पहिल्यांदा उघडताना काय करावं लागेल

1. हा folder Android Studio मध्ये "Open" कर (existing project म्हणून).
2. `gradlew` / `gradlew.bat` आणि `gradle-wrapper.jar` या फाईल्स या export मध्ये
   **नाहीत** (binary files असल्यामुळे इथे तयार करता आल्या नाहीत). Android Studio
   ने विचारल्यास "Configure Gradle Wrapper" / "Recreate wrapper" निवड — किंवा
   टर्मिनल मध्ये (जर सिस्टीमवर आधीच Gradle असेल तर):
   ```
   gradle wrapper --gradle-version 9.5.0
   ```
   यामुळे `gradle-wrapper.properties` (आधीच दिलेली) वापरून बाकी wrapper फाईल्स तयार
   होतील.
3. Fonts: `app/src/main/res/font/` मध्ये अजून प्रत्यक्ष `.ttf` फाईल्स नाहीत.
   Noto Sans Devanagari आणि Poppins अधिकृत source वरून (Google Fonts) डाउनलोड करून
   त्याच folder मध्ये टाक — मग `font_family.xml` आणि theme मध्ये जोडू (हे पुढच्या
   phase चं काम).
4. Sync → Build → Run:
   ```
   ./gradlew assembleDebug
   ```
   यशस्वी झाल्यावरच पुढचा phase (Phase 2 — Dashboard) सुरू करायचा (PART 60, PART 91).

## Known limitations (Phase 0-1)

- Launcher icon सध्या साधा placeholder shape आहे (खरा logo — PART 12 — नंतर).
- सर्व 5 bottom-nav tabs पैकी फक्त Home ला खरा UI आहे; बाकी सगळे "Coming soon"
  placeholder दाखवतात.
- Room database मध्ये फक्त 1 placeholder table आहे — Site/Labour/Material/Bill
  entities अजून जोडलेले नाहीत.
- gradlew wrapper executable/jar included नाही (वर बघ).

## Build order (उरलेले सगळे phases)

Sites+Clients → Labour+Attendance+Ledger → Materials+Stock →
Bills+Scanner → Expenses+Payments → Diary → Profit/Loss → Estimates →
Electrical → Plumbing → Civil calculators → Contacts → Photos/Documents →
Reports → Backup/Restore → Notifications → Polish → Testing → Release.

नियम कायम: **प्रत्येक phase नंतर build यशस्वी झाल्याशिवाय पुढे जायचं नाही.**
