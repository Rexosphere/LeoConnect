<p align="center">
  <img src="assets/app_icon.png" width="200" alt="LeoConnect App Icon"/>
</p>

# LeoConnect 🦁

**Uniting Leo Clubs, Amplifying Impact.**

[![Download App](https://img.shields.io/badge/Download-APK-blue?style=for-the-badge&logo=android)](https://github.com/Rexosphere/LeoConnect/releases/latest)
[![Platform](https://img.shields.io/badge/Platform-Android%20|%20iOS%20|%20Desktop-green?style=for-the-badge)](.)
[![Built with KMP](https://img.shields.io/badge/Built%20with-Kotlin%20Multiplatform-7F52FF?style=for-the-badge&logo=kotlin)](https://kotlinlang.org/docs/multiplatform.html)

LeoConnect is the premier social platform dedicated to Leo Club members across Sri Lanka. Built with **Kotlin Multiplatform (KMP)**, it delivers a seamless experience on Android, iOS, and Desktop from a single codebase.

---

## 🚀 Tech Stack

### Frontend (Mobile & Desktop)
- **Kotlin Multiplatform (KMP)** - Single codebase for Android, iOS, Desktop
- **Jetpack Compose** - Modern declarative UI framework
- **Material3** - Google's latest design system with dynamic theming
- **Voyager** - Type-safe navigation
- **Kamel** - Async image loading
- **Koin** - Dependency injection
- **Haze** - Glassmorphism blur effects

### Backend (Edge-Deployed)
- **Cloudflare Workers** - Serverless edge computing
- **D1 Database** - SQLite at the edge
- **R2 Storage** - Object storage for images
- **Firebase Auth** - Google OAuth authentication
- **FCM** - Push notifications

---

## 📱 Screenshots

<p float="left">
  <img src="screenshots/LoginScreen.jpg" width="200" />
  <img src="screenshots/Feed.jpg" width="200" /> 
  <img src="screenshots/PostScreen.jpg" width="200" />
  <img src="screenshots/ClubsScreen.jpg" width="200" />
</p>

<p float="left">
  <img src="screenshots/DistrictInfoScreen.jpg" width="200" />
  <img src="screenshots/ProfileScreen.jpg" width="200" />
  <img src="screenshots/ProfileEditScreen.jpg" width="200" />
  <img src="screenshots/SettingsScreen.jpg" width="200" />
</p>

---

## ✨ Features

### Social Networking
- **Social Feed** - Personalized posts from clubs and Leos you follow
- **Post Creation** - Share text and images with your community
- **Likes & Comments** - Engage with posts from others
- **Share Posts** - Spread inspiring content

### Discovery
- **Club Discovery** - Find and explore clubs by district
- **User Search** - Find other Leo members
- **District Exploration** - Browse all districts and their clubs

### Community
- **User Profiles** - Showcase your Leo journey
- **Following System** - Follow users and clubs
- **Direct Messaging** - Private conversations with other Leos

### Notifications
- **Push Notifications** - Real-time alerts for:
  - New followers
  - Post likes
  - Comments on your posts
  - New messages
  - Posts from people you follow

### Security
- **Google OAuth** - Secure authentication
- **Input Validation** - Protected against malicious input
- **JWT Verification** - Secure API access

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    LeoConnect Frontend                       │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │   Android   │  │     iOS     │  │   Desktop   │         │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘         │
│         └─────────────────┼────────────────┘                │
│                    Kotlin Multiplatform                      │
│                    Jetpack Compose UI                        │
└───────────────────────────┬─────────────────────────────────┘
                            │ HTTPS
┌───────────────────────────┴─────────────────────────────────┐
│                 Cloudflare Workers (Edge)                    │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │  REST API   │  │   D1 (SQL)  │  │  R2 (Files) │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
└─────────────────────────────────────────────────────────────┘
```

---

## 🛠️ API Endpoints

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/auth/google` | POST | ✓ | Authenticate with Google |
| `/feed` | GET | ✓ | Get home feed |
| `/posts` | POST | ✓ | Create a post |
| `/posts/:id/like` | POST | ✓ | Like/unlike a post |
| `/posts/:id/comments` | GET/POST | ✓ | Get/add comments |
| `/posts/:id/share` | POST | ✓ | Share a post |
| `/comments/:id/like` | POST | ✓ | Like/unlike a comment |
| `/users/:id` | GET | ✓ | Get user profile |
| `/users/:id/follow` | POST/DELETE | ✓ | Follow/unfollow user |
| `/clubs` | GET | - | List clubs by district |
| `/clubs/:id/follow` | POST | ✓ | Follow a club |
| `/districts` | GET | - | List all districts |
| `/search` | GET | - | Search users, clubs, posts |
| `/messages` | GET/POST | ✓ | Messaging |
| `/notifications` | GET | ✓ | Get notifications |

---

## 📦 Project Structure

```
LeoConnect/
├── composeApp/
│   └── src/
│       ├── commonMain/          # Shared KMP code
│       │   └── kotlin/
│       │       └── com/rexosphere/leoconnect/
│       │           ├── data/          # Repositories, APIs
│       │           ├── domain/        # Models, Use cases
│       │           ├── presentation/  # Screens, ViewModels
│       │           ├── di/            # Koin modules
│       │           └── ui/theme/      # Material3 theming
│       ├── androidMain/         # Android-specific
│       └── iosMain/             # iOS-specific
├── iosApp/                      # iOS app wrapper
└── assets/                      # App icons, resources

LeoConnect_Backend/
├── src/
│   ├── index.ts                 # Main API routes
│   ├── auth.ts                  # Firebase auth
│   ├── models.ts                # Data models
│   ├── helpers.ts               # Utility functions
│   └── notifications.ts         # FCM push notifications
├── migrations/                  # D1 database migrations
└── test/                        # Unit tests
```

---

## 🚀 Getting Started

### Prerequisites
- JDK 17+
- Android Studio Hedgehog+ (for Android)
- Xcode 15+ (for iOS)
- Node.js 18+ (for backend)

### Running the App

```bash
# Clone the repository
git clone https://github.com/Rexosphere/LeoConnect.git
cd LeoConnect

# Android
./gradlew :composeApp:assembleDebug

# Desktop
./gradlew :composeApp:run

# iOS (requires Mac)
open iosApp/LeoConnect.xcodeproj
```

### Running the Backend

```bash
cd LeoConnect_Backend
npm install
npm run dev
```

---

## 🤝 Why LeoConnect?

### 🌍 Connect Across Sri Lanka
Break down geographical barriers. Discover clubs from other districts, follow their activities, and build a network of changemakers.

### 📢 Share Your Journey
Share updates, photos, and stories from your service projects. Inspire others with the incredible work happening in the Leo community.

### 📅 Stay Updated
Never miss an event. Get real-time notifications for posts, messages, and follows.

### 📱 Cross-Platform
One codebase, three platforms. Enjoy a beautiful, modern experience on Android, iOS, and Desktop.

---

## 📄 License

This project is developed for the **AlgoArena Competition** by the Leo Club of University of Sri Jayewardenepura.

---

## 👥 Team

**Rexosphere** - Development Team

---

*Built with ❤️ for the Leo Community*
