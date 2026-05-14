# 🐄 Gokula-Health

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg?logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4CAF50.svg?logo=android)
![Room Database](https://img.shields.io/badge/Room-Offline%20First-yellow.svg)
![Dagger Hilt](https://img.shields.io/badge/Dagger%20Hilt-DI-red.svg)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-orange.svg)

**White Revolution 2.0 — Bringing precision technology to animal husbandry.**

Gokula-Health is a fully offline, modern Android application designed as a **Digital Health Passport for Dairy Cattle**. It empowers small-scale dairy farmers to transition from memory-based farming to data-driven farming, helping them track milk yields, automate vaccination schedules, monitor breeding cycles, and detect early signs of illness using AI-driven insights.

---

## 📸 Screenshots
*(Add your screenshots here by dragging and dropping images from your computer into the GitHub editor)*

<p float="left">
  <img src="https://via.placeholder.com/250x500.png?text=Home+Screen" width="200" />
  <img src="https://via.placeholder.com/250x500.png?text=Milk+Yield+Chart" width="200" /> 
  <img src="https://via.placeholder.com/250x500.png?text=Breeding+Tracker" width="200" />
  <img src="https://via.placeholder.com/250x500.png?text=Smart+Vaccinations" width="200" />
</p>

---

## ✨ Core Features

### 🥛 Milk Diary & Yield Analytics (Female Cattle)
* Log morning and evening milk yields daily.
* Interactive **7, 30, and 90-day yield graphs** to visualize production trends.
* Auto-calculates monthly averages and highest/lowest yield days.

### 🤖 GenAI Health Detection
* The app's background engine constantly monitors milk logs.
* Automatically detects a **3-day consecutive drop** in milk yield.
* Triggers an AI-generated alert suggesting possible causes (e.g., Mastitis, Heat Stress, Feed Quality) and recommends immediate actions.

### 💉 Smart Vaccination Tracker
* **Smart Suggestions:** Auto-recommends vaccines (FMD, HS, BQ) based on the cattle's specific breed, age, and gender.
* **Offline Push Notifications:** Uses Android's `AlarmManager` to send local push notifications at 8:00 AM on the day a vaccine is due—requiring zero internet connection.

### 🐄 Complete Breeding & Pregnancy Cycle
* Track the entire reproductive cycle: **Heat Observation → Insemination → Pregnancy Confirmation → Dry-Off → Calving**.
* Auto-calculates expected calving dates (adds 280 days) and dry-off dates.
* Visual progress bars for active pregnancies and lactation stage tracking.

### 🧬 Semen Log (Male Cattle)
* Dedicated tracking for bulls to log semen collection dates, batch numbers, volume, and motility.

### 🔍 Global Search & Health Notes
* Keep detailed text-based health observations for each animal.
* Global search engine with advanced filters (Sort by Age, Filter by Breed/Gender).

---

## 🛠️ Tech Stack & Architecture

Built with modern Android development standards and best practices:

* **UI:** Jetpack Compose (Declarative UI), Material Design 3.
* **Architecture:** MVVM (Model-View-ViewModel) with Clean Architecture principles.
* **Local Database:** Room (SQLite) with complex relational tables and safe Database Migrations.
* **Asynchronous Programming:** Kotlin Coroutines & StateFlow.
* **Dependency Injection:** Dagger-Hilt.
* **Background Tasks:** `AlarmManager` & `BroadcastReceiver` for offline scheduling.
* **Charts:** MPAndroidChart wrapped in Compose `AndroidView`.
* **Image Loading:** Coil (with secure internal storage caching).

---

## 🗄️ Database Schema Overview
The app relies on a robust, offline-first relational database:
1. `Cattle` - Core profile (Tag ID, Breed, Age, Gender, Photo).
2. `MilkLog` - Daily yield tracking.
3. `Vaccination` - History and upcoming scheduled doses.
4. `BreedingRecord` - Cycle tracking and auto-calculated dates.
5. `HealthNote` - Text-based veterinary observations and Semen Logs.

---

## 🚀 Getting Started

### Prerequisites
* Android Studio (Latest version recommended)
* Minimum SDK: API 24 (Android 7.0)
* Target SDK: API 34 (Android 14)

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/Gokula-Health.git
   ```
2. Open the project in Android Studio.
3. Sync the Gradle files.
4. Build and run the app on an emulator or physical device. (Note: The app includes a MockDataGenerator that automatically populates the database with sample cattle, milk logs, and active pregnancies on the first run for easy testing).

## 🔒 Permissions Used
* `CAMERA` - To capture cattle profile photos.
* `POST_NOTIFICATIONS` - For vaccination reminders (Android 13+).
* `SCHEDULE_EXACT_ALARM` - To ensure reminders fire exactly on the due date.
* `INTERNET` - For GenAI API suggestions.

## 👨‍💻 Author
[Your Name/MindMatrix]

LinkedIn: [Your LinkedIn Profile URL]
Portfolio: [Your Portfolio URL]

## 📄 License
This project is licensed under the MIT License.
