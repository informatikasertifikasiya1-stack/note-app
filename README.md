<div align="center">

# 📝 Note App

**A clean and feature-rich note-taking application for Android**

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-50%25-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Java](https://img.shields.io/badge/Java-49%25-F89820?style=for-the-badge&logo=openjdk&logoColor=white)
![Build](https://img.shields.io/badge/Build-Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

</div>

---

## 📖 Overview

**Note App** is a modern Android application that allows users to create, manage, and organize their
personal notes with ease. Built with a combination of **Kotlin** and **Java**, it supports rich text
editing, custom note colors, image attachments, URL embedding, and note exporting — all stored
locally on your device.

---

## 📸 Screenshots

<div align="center">

|                Home Screen                |                  Note Editor                  |              Rich Text Editing              |
|:-----------------------------------------:|:---------------------------------------------:|:-------------------------------------------:|
| <img src="screen1_home.png" width="200"/> | <img src="screen5_new_note.png" width="200"/> | <img src="screen2_editor.png" width="200"/> |

|                Color Picker                 |               Export Options                |
|:-------------------------------------------:|:-------------------------------------------:|
| <img src="screen4_colors.png" width="200"/> | <img src="screen3_export.png" width="200"/> |

</div>

---

## ✨ Features

### 🗒️ Note Management

- **Create Notes** — Add notes with a title, subtitle, and full body text
- **Edit Notes** — Update any note at any time
- **Delete Notes** — Remove notes with a dedicated delete button
- **Auto Timestamp** — Every note shows its creation date and time
- **"NEW" Badge** — Recently created notes are highlighted with a NEW label

### ✍️ Rich Text Editor

- **Bold, Italic, Underline, Strikethrough** — Full inline text formatting toolbar
- **Bullet Lists** — Organize content with bullet points
- **Block Quotes** — Highlight important sections
- **Clear Formatting** — Reset text styling with one tap

### 🎨 Customization

- **7 Note Colors** — Choose from Black, Yellow, Purple, Blue, Pink, Green, and Brown themes per
  note
- **Color-coded Cards** — Note cards on the home screen reflect the selected color

### 🖼️ Media & Links

- **Add Image** — Embed images directly into notes
- **Add URL** — Attach web links to any note

### 📤 Export & Sharing

- **Export as Image** — Save your note as an image file
- **Export as TXT** — Export note content as a plain text file
- **Share** — Share notes directly via Android's share sheet

### 💾 Storage

- **Fully Offline** — All notes saved locally, no internet required
- **Persistent Storage** — Notes survive app restarts and reboots

---

## 🛠️ Tech Stack

### Languages

| Language   | Usage                                                 |
|------------|-------------------------------------------------------|
| **Kotlin** | ~50.9% — Modern Android logic, ViewModels, Extensions |
| **Java**   | ~49.1% — Core Android components and data layers      |

### Android & Jetpack

| Component                      | Purpose                                                   |
|--------------------------------|-----------------------------------------------------------|
| **Android SDK**                | Core Android platform APIs                                |
| **Room Database**              | Local SQLite abstraction for note persistence             |
| **RecyclerView**               | Staggered grid display of notes on home screen            |
| **ViewModel**                  | Lifecycle-aware UI data management                        |
| **LiveData**                   | Reactive UI updates                                       |
| **Material Design Components** | Consistent, modern UI elements                            |
| **Rich Text Editor**           | Inline formatting toolbar (Bold, Italic, Underline, etc.) |
| **BottomSheet Dialog**         | Color picker, export options, and actions menu            |

### Build Tools

| Tool                    | Purpose                                       |
|-------------------------|-----------------------------------------------|
| **Gradle (Kotlin DSL)** | Build automation via `build.gradle.kts`       |
| **Android Studio**      | Official IDE for development                  |
| **Gradle Wrapper**      | Consistent Gradle version across environments |

---

## 🏗️ Architecture

This project follows the **MVVM (Model-View-ViewModel)** architectural pattern:

```
┌─────────────────────────────────────────────┐
│                    UI Layer                  │
│         (Activities / Fragments / XML)       │
└─────────────────┬───────────────────────────┘
                  │ observes
┌─────────────────▼───────────────────────────┐
│               ViewModel Layer                │
│     (Manages UI state & business logic)      │
└─────────────────┬───────────────────────────┘
                  │ reads/writes
┌─────────────────▼───────────────────────────┐
│              Repository Layer                │
│       (Single source of truth for data)      │
└─────────────────┬───────────────────────────┘
                  │
┌─────────────────▼───────────────────────────┐
│              Room Database                   │
│         (Local SQLite persistence)           │
└─────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
note-app/
├── app/
│   └── src/
│       └── main/
│           ├── java/         # Java & Kotlin source files
│           │   ├── activities/
│           │   ├── viewmodel/
│           │   ├── repository/
│           │   ├── database/
│           │   └── model/
│           └── res/          # Layouts, drawables, strings
├── gradle/
├── build.gradle.kts          # Project-level build config
├── settings.gradle.kts
└── gradle.properties
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio **Hedgehog** or later
- Android SDK **21+**
- JDK **11** or higher

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/informatikasertifikasiya1-stack/note-app.git
   ```

2. **Open in Android Studio**
    - Launch Android Studio
    - Select **File → Open** and navigate to the cloned folder

3. **Sync Gradle**
    - Wait for Android Studio to sync and download all dependencies

4. **Run the App**
    - Connect an Android device or start an emulator
    - Click the **▶ Run** button or press `Shift + F10`

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

1. Fork the project
2. Create your feature branch: `git checkout -b feature/AmazingFeature`
3. Commit your changes: `git commit -m 'Add some AmazingFeature'`
4. Push to the branch: `git push origin feature/AmazingFeature`
5. Open a Pull Request

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

<div align="center">

Made with ❤️
by [informatikasertifikasiya1-stack](https://github.com/informatikasertifikasiya1-stack)

⭐ Star this repo if you found it helpful!

</div>
