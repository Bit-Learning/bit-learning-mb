# Bit Learning

An Android e-learning platform built with Kotlin and Jetpack Compose, featuring course management, video playback, community forums, and an integrated Python code compiler.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin 2.0.21 |
| UI Framework | Jetpack Compose + Material 3 |
| Architecture | Clean Architecture (core → data → domain → features) |
| DI | Hilt (Dagger) |
| Networking | Retrofit 2 + OkHttp 4 |
| Serialization | Gson + Jackson |
| Local Storage | Jetpack DataStore |
| Navigation | Jetpack Navigation Compose |
| Video Player | Media3 / ExoPlayer |
| Image Loading | Coil 3 |
| QR Scanning | ML Kit Barcode |
| Payments | Stripe Android SDK |
| Auth | Google Sign-In, GitHub OAuth2, JWT |
| Python Runtime | KtxPy (embedded CPython) |
| Code Editor | Sora Editor + TextMate |
| Terminal | Termux terminal-emulator |
| Logging | Timber |
| Linting | Spotless + ktlint |
| CI/CD | Codemagic |

## Project Structure

```
bitlearning/
├── app/                          # Main application module
│   └── src/main/java/com/app/bitlearning/
│       ├── BitLearningApp.kt     # Application class (Hilt entry, Python init)
│       ├── MainActivity.kt       # Single Activity, deep link handling
│       ├── Navigation.kt         # NavHost + route definitions
│       ├── core/
│       │   ├── common/           # Shared Compose components, theme, typography
│       │   ├── database/         # Room database (prepared for local caching)
│       │   ├── log/              # Logging utilities
│       │   ├── network/          # Retrofit ApiService, OkHttp config, AuthInterceptor
│       │   └── preferences/      # DataStore-backed app preferences
│       ├── data/
│       │   ├── mapper/           # DTO ↔ Domain model mappers
│       │   └── repository/       # Repository implementations
│       ├── domain/
│       │   ├── model/            # Domain entities (User, Course, Lecture, etc.)
│       │   └── repository/       # Repository interfaces (contracts)
│       └── features/
│           ├── auth/             # Login, Register, Google/GitHub OAuth2
│           ├── onboarding/       # First-launch onboarding flow
│           ├── splash/           # Splash screen with auth state check
│           ├── home/             # Home dashboard
│           ├── courses/          # Course catalog browsing
│           ├── coursedetail/      # Course detail + section/lecture list
│           ├── player/           # Video player (ExoPlayer)
│           ├── search/           # Course search
│           ├── profile/          # User profile, edit, certificates
│           ├── history/          # Learning history
│           ├── forum/            # Community forum (posts, comments, likes)
│           ├── notification/     # Notifications
│           ├── notificationsetting/ # Notification preferences
│           ├── payment/          # Payment settings (Stripe)
│           └── support/          # Help & support
├── ktxpy-module/                 # Embedded Python compiler module
│   ├── app/                      # KtxPy library (Sora editor, terminal, Python runtime)
│   │   ├── arch_arm32/           # Python 7z assets for ARM 32-bit
│   │   ├── arch_arm64-v8a/       # Python 7z assets for ARM 64-bit
│   │   ├── arch_x86/             # Python 7z assets for x86
│   │   └── arch_x86_64/          # Python 7z assets for x86_64
│   └── libp7zip/                 # Native 7zip decompression library
├── gradle/
│   └── libs.versions.toml        # Version catalog
├── codemagic.yaml                # CI/CD pipeline config
└── build.gradle.kts              # Root build script (Spotless, Hilt, KSP)
```

## System Architecture

### Clean Architecture Layers

```
┌─────────────────────────────────────────────────┐
│                  features/                       │
│  (UI screens + ViewModels per feature module)    │
├─────────────────────────────────────────────────┤
│                  domain/                         │
│  (Repository interfaces, domain models, enums)   │
├─────────────────────────────────────────────────┤
│                  data/                           │
│  (Repository impls, DTO mappers, API delegation) │
├─────────────────────────────────────────────────┤
│                  core/                           │
│  (Network, DataStore, Theme, Shared Components)  │
└─────────────────────────────────────────────────┘
```

- **features/** — Each feature is a self-contained module with its own `ui/` (Composables + ViewModel). Dependencies flow inward only.
- **domain/** — Pure Kotlin. Defines repository contracts and domain entities (`User`, `Course`, `Lecture`, `ForumPost`, etc.). No Android framework dependencies.
- **data/** — Implements domain repository interfaces. Contains `AuthRepositoryDelegator` for switching between mock and real backends, plus DTO-to-domain mappers.
- **core/** — Cross-cutting concerns: Retrofit/OkHttp networking with JWT `AuthInterceptor`, `AppPreferences` (DataStore), Material 3 theme, and reusable Compose components.

### Navigation

Single-Activity architecture using Jetpack Navigation Compose. All routes are defined in `Navigation.kt` via the `Routes` object. Supports Android App Links deep linking (`https://bit-learning.lch.id.vn/open-mobile?screen=...&id=...`).

Navigation flow:
```
Splash → Onboarding → Auth → Home ─┬─ Courses → CourseDetail → Player
                                    ├─ Search
                                    ├─ Profile → EditProfile / Certificates / History
                                    ├─ Forum
                                    ├─ Notifications
                                    └─ Python Compiler (KtxPy)
```

### Networking

- **Retrofit 2** with `GsonConverterFactory` for REST API communication
- **OkHttp 4** with `HttpLoggingInterceptor` and custom `AuthInterceptor` (attaches `Bearer` JWT token)
- **Cookie Jar** (`JavaNetCookieJar`) for automatic HttpOnly refresh-token handling
- All API responses wrapped in `ApiWrapper<T>` envelope: `{ status, message, data?, page?, error? }`
- Backend base URL: `https://bit-api-dev.lch.id.vn/api/`

### Authentication

| Method | Flow |
|--------|------|
| Email/Password | POST `/auth/login` → JWT access token stored in DataStore |
| Google Sign-In | Android SDK ID Token → POST `/auth/oauth2/google/mobile` |
| GitHub OAuth2 | Authorization code → POST `/auth/oauth2/github` |

### Python Compiler Integration

The app embeds [KtxPy](https://github.com/PsiCodes/KtxPy), a Python runtime for Android:
- Initialized in `BitLearningApp.onCreate()` (crash handler, file manager, Timber logging)
- Launched as a separate Activity (`github.psicodes.ktxpy.activities.HomeActivity`)
- Includes Sora code editor with TextMate syntax highlighting
- Termux terminal emulator for interactive Python sessions
- Architecture-specific Python 7z assets decompressed via libp7zip at first run

### Dependency Injection

Hilt (Dagger) with KSP annotation processing. `@HiltAndroidApp` on `BitLearningApp`, `@AndroidEntryPoint` on `MainActivity`. Network module provides singleton `OkHttpClient`, `Retrofit`, and `BitLearningApiService` instances.

## Build Variants

The project uses product flavors for CPU architecture targeting:

| Flavor | Architecture | Use Case |
|--------|-------------|----------|
| `arch_arm64` | arm64-v8a | Physical devices (most modern phones) |
| `arch_x86_64` | x86_64 | Android Emulator |

### Recommended Build Variants

**Emulator:**
```
:app                → arch_x86_64Debug
:ktxpy-module:app   → arch_x86_64Debug
:ktxpy-module:libp7zip → debug
```

**Physical device (ARM64):**
```
:app                → arch_arm64Debug
:ktxpy-module:app   → arch_arm64Debug
:ktxpy-module:libp7zip → debug
```

## Getting Started

### Prerequisites

- Android Studio Ladybug or later
- JDK 11+
- Android SDK 36 (compile & target)
- Min SDK 24

### Setup

1. Clone the repository:
   ```bash
   git clone <repository-url>
   ```

2. Create `keystore.properties` in the project root (for release builds):
   ```properties
   storeFile=path/to/your.keystore
   storePassword=your_store_password
   keyAlias=your_key_alias
   keyPassword=your_key_password
   ```

3. Open in Android Studio and sync Gradle.

4. Select the appropriate build variant for your target device (see Build Variants above).

5. Run the app.

## CI/CD

Automated builds via **Codemagic**:
- Runs on `mac_mini_m2` instances
- Builds release AAB with auto-incremented version code from Google Play
- Publishes to Google Play internal track as draft
- Gradle caching enabled for faster builds

## API Endpoints

The app communicates with a Spring Boot backend. Key endpoint groups:

| Group | Endpoints |
|-------|-----------|
| Auth | `/auth/login`, `/auth/register`, `/auth/logout`, `/auth/oauth2/*` |
| Courses | `/courses`, `/courses/{id}`, `/courses/grade/{grade}`, `/courses/my-courses` |
| Enrollment | `/enrollments/enroll/{courseId}`, `/enrollments/my-courses`, `/enrollments/courses/{courseId}/access` |
| Learning | `/learning/progress/lectures/{lectureId}/complete` |
| Users | `/users/profile`, `/users/{id}/avatar`, `/users/{id}/cover` |
| Onboarding | `/onboarding/pages` |
| Forum | `/posts`, `/posts/{id}`, `/comments`, `/posts/{postId}/like`, `/posts/{postId}/media` |

## Localization

The app supports:
- English (default)
- Vietnamese (`values-vi/`)

## License

Copyright © 2026 Bit Learning. All rights reserved.
Proprietary and confidential — see license headers in source files.