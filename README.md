# RealTime Chat App 🔨🔥

A real-time chat application built using **Jetpack Compose**, **Firebase**, **MVVM + Clean Architecture**, and **Hilt**. This app supports messaging, authentication, offline caching, and media upload.

## ✨ Features

- 📱 Modern UI with Jetpack Compose
- 🔐 Firebase Authentication
- 💬 Real-time messaging using Firestore
- 🖼️ Image message support (Firebase Storage)
- 📆 Offline message support with Room
- 🔀 MVVM + Clean Architecture
- 🥠 Unit testing with JUnit & Mockito

---

## 🧱 Tech Stack

| Layer        | Libraries Used |
|-------------|----------------|
| UI          | Jetpack Compose, Material3 |
| Architecture| MVVM, Clean Architecture |
| DI          | Hilt |
| Database    | Room |
| Realtime DB | Firebase Firestore |
| Auth        | Firebase Auth |
| Media       | Firebase Storage |
| Image Load  | Coil |
| Testing     | JUnit, Mockito |

---

## 📁 Project Structure

```
com.example.realtimechat
├── data                # Remote & Local data sources
│   ├── model
│   ├── repository
│   ├── source.local
│   └── source.remote
├── domain             # Business logic
│   ├── model
│   ├── repository
│   └── usecase
├── presentation       # UI & ViewModels
│   ├── ui
│   └── viewmodel
├── di                 # Hilt modules
└── utils              # Utility classes
```

---

## 🔧 Setup & Run

1. Clone the repo
   ```bash
   git clone https://github.com/your-username/RealTimeChat.git
   cd RealTimeChat
   ```

2. Set up Firebase project:
   - Add your `google-services.json` to `app/`
   - Enable Authentication (Email/Password)
   - Set up Firestore and Storage

3. Run the project
   ```bash
   ./gradlew assembleDebug
   ```

4. Run tests
   ```bash
   ./gradlew testDebugUnitTest
   ```

---

## ✅ Testing

- Uses **JUnit** for unit testing
- **Mockito** for mocking use cases and repositories
- ViewModel and UseCase layers are tested

---

## 🧠 Coming Soon

- Group chat support
- Message reactions
- Push notifications (FCM)
- Dark mode

---

## 💬 Contributing

Contributions, issues and feature requests are welcome!

---

## 👨‍💻 Developed by

**Ayush Rana**  
📧 ayush.rana1401@gmail.com

