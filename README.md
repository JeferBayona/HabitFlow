# ⚡ HabitFlow — Interactive Habit & Streak Dashboard + Android App

An interactive daily habit tracking and streak dashboard with dynamic momentum analytics, 1-click event templates (like Biking, Running, Gym, Meditation), custom habit creator, and a companion Android APK.

---

## 📱 Download Android APK

The pre-compiled, signed Android APK is available directly in this repo:

📥 **[Download HabitFlow.apk (33 KB)](./apk/HabitFlow.apk)**

* Minimum SDK: Android 8.0 (API 26)
* Target SDK: Android 14 (API 34)
* Architecture: Universal (all Android devices)

---

## ✨ Features

### 🚴 1-Click Event Templates (Starts Today)
* **Quick Templates Shelf**: Tap any preset habit card directly above the grid to add it with **1 click**:
  * 🚴 **Biking / Cycling** (Fitness)
  * 🏃 **Running / Jogging** (Fitness)
  * 🏋️ **Gym Strength Workout** (Fitness)
  * 💧 **Hydrate 2.5L Water** (Health)
  * 🧘 **15m Mindfulness Meditation** (Mindfulness)
  * 📚 **Read 25 Pages Book** (Learning)
  * 💻 **Practice Coding** (Learning)
  * 😴 **8 Hours Quality Sleep** (Health)
  * 📧 **Zero Inbox & Plan** (Productivity)
  * 💰 **Track Expenses** (Finance)
* **Templates Gallery Modal**: Browse 20+ categorized templates across Fitness, Health, Mindfulness, Productivity, Learning, and Finance.

### ✏️ Custom Habit Creation
* Tap **+ New Habit** to manually configure:
  * Title
  * **Start Date** (automatically initialized to the current date)
  * Category (Health, Fitness, Mind, Work, Growth, Finance)
  * 18 Phosphor Icons
  * 6 Accent Themes (Indigo, Emerald, Rose, Amber, Cyan, Purple)

### 📅 Current Date Start
* Whenever you start fresh or add a habit, the start date is set to the **current date**.
* Each habit row clearly displays its tracking start date (e.g. `Started: Today` or `Started: Oct 14, 2026`).

### 📊 Real-Time Analytics & Streak Tracking
* **KPI Metrics**: Today's % Completion, 7-Day Average, Top Active Streak 🔥, All-Time Best 🏆.
* **7-Day Interactive Matrix**: Check off habits with instant real-time sound effects and confetti celebrations on 100% completion.
* **Visual Charts**: Interactive Bar Chart & SVG Trend Curve switcher.
* **Streak Leaderboard**: Dual-layer comparison bars showing current active streak vs. all-time record.
* **14-Day Consistency Pulse**: Heatmap grid showing activity consistency.

---

## 🛠️ Repository Structure

```
HabitFlow/
├── index.html                   # Web dashboard (works in any browser & GitHub Pages)
├── apk/
│   └── HabitFlow.apk            # Pre-compiled and signed Android APK
├── android/                     # Standalone Android APK project
│   ├── AndroidManifest.xml
│   ├── build_apk.ps1            # Command-line build & signing script
│   ├── src/com/habitflow/app/   # Native Android Java source
│   ├── res/                     # Android app resources & icon
│   └── assets/                  # Embedded app assets
└── android-compose/             # Kotlin + Jetpack Compose project version
    ├── app/
    ├── build.gradle.kts
    └── settings.gradle.kts
```

---

## 🚀 Running Locally

### Web Version
Simply open `index.html` in any web browser.

### Android Build
To rebuild the APK from source using the Android SDK:
```powershell
cd android
powershell -ExecutionPolicy Bypass -File build_apk.ps1
```

---

## 📄 License
MIT License
