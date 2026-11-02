# LifeOS – An Offline Personal Life Management System

**Project-Based Learning (PBL) | Phase III – Final Implementation**

LifeOS is an offline-first Android application designed to bring essential personal management activities together in one place. It provides a unified platform for managing tasks, schedules, finances, notes, journal entries and sensitive personal information.

The application is developed using native Android technologies and stores its core application data locally on the device, allowing users to access its primary features without an internet connection.

---

## Project Information

| Detail | Information |
|---|---|
| Project Name | LifeOS |
| Project Title | An Offline Personal Life Management System |
| Project Type | Android Application |
| Institution | Graphic Era (Deemed to be University), Dehradun |
| Programme | Bachelor of Computer Applications (BCA) |
| Semester | 5th Semester |
| Academic Session | 2026–27 |
| Team Name | Spartans |
| Team ID | IJP-V-2026-T022 |
| Project Mentor | Dr. K. C. Purohit |

## Core Modules

LifeOS consists of six integrated management modules.

### 1. Task Manager
- Create, edit and delete tasks.
- Set task priorities and categories.
- Assign due dates.
- Track task completion and status.

### 2. Schedule Manager
- Create and manage events.
- Set event dates and timings.
- Add event descriptions and locations.
- Schedule reminders and receive notifications.

### 3. Finance Manager
- Record income and expenses.
- Categorise financial transactions.
- Edit and delete transactions.
- View total income, expenses and current balance.

### 4. Knowledge Hub
- Create and manage personal notes.
- Organise notes using categories.
- Edit and delete existing notes.
- Pin important notes for easier access.

### 5. Journal & Memories
- Maintain personal journal entries.
- Record daily experiences and thoughts.
- Select moods for journal entries.
- Mark important entries as memories.

### 6. Privacy Corner – Secure Vault
- Protect private records using a PIN.
- Store sensitive information such as usernames and passwords.
- Encrypt stored passwords and private notes.
- View, edit and delete saved records.

## Supporting Features

- Centralised dashboard for accessing all modules.
- User profile management.
- Profile photograph support.
- Light mode, dark mode and system-default appearance.
- Local database persistence.
- Event reminders and notifications.
- Integrated navigation across modules.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java | Application development and business logic |
| XML | User interface layouts |
| Android SDK | Android application framework |
| Android Studio | Development environment |
| Room | Database abstraction and persistence |
| SQLite | Local relational database |
| Git | Version control |
| GitHub | Source code repository |

## System Architecture

LifeOS follows a layered Android application architecture.

**Application Flow:**

User → Android Interface (Java + XML) → Activities and ViewModels → DAOs and Repositories → Room Persistence Layer → SQLite Local Database

The application uses Room to manage structured local data storage. Its database contains seven primary entities:

- Profile
- Task
- Event
- FinanceTransaction
- Note
- JournalEntry
- VaultItem

The architecture separates the user interface, application logic and data persistence responsibilities.

## Offline Functionality

LifeOS follows an offline-first approach. Its primary management features use locally stored data, and the application does not require a backend server or cloud connection for normal operation.

User records are persisted in the device's local SQLite database through Room.

## Installation and Execution

### Option 1: Install the APK

1. Obtain the LifeOS APK from the project team.
2. Transfer the APK to an Android device.
3. Allow installation from the relevant source if Android requests permission.
4. Install and launch LifeOS.
5. Create a profile and configure the Secure Vault PIN when prompted.

### Option 2: Run Through Android Studio

1. Clone or download this repository.
2. Open the project in Android Studio.
3. Allow Gradle synchronisation to complete.
4. Connect an Android device with USB debugging enabled or start an Android emulator.
5. Run the application using Android Studio.

**Clone the repository:**

```bash
git clone https://github.com/RudyNothing/LifeOS.git
```

## Testing and Validation

The final application was tested on an Android device.

The following areas were validated:

- Creation, editing, deletion and retrieval of records across modules.
- Local database persistence after closing and reopening the application.
- Secure Vault PIN creation and verification.
- Encrypted record storage and retrieval.
- Event reminder scheduling and notification flow.
- Navigation and integration across all six modules.
- Interface behaviour, input validation and theme compatibility.

The final implementation was also packaged into a debug APK for internal distribution and demonstration.

## Team Members

| Member | Role |
|---|---|
| Daivik Mishra | Team Lead |
| Kanchan | Developer |
| Shreya Mittal | Developer / Documentation |
| Rudraksh Bhatia | Developer / Database |

The project was developed through collaborative planning, implementation, integration, testing and documentation.

## Current Limitations

- Application data is stored locally on the device.
- Cloud synchronisation is not currently implemented.
- Cross-device data transfer is not currently available.

## Future Scope

Potential improvements include:

- Secure backup and restore functionality.
- Optional encrypted cloud synchronisation.
- Global search across application modules.
- Additional dashboard analytics.
- Further enhancements to personal data management.

## References

- [Android Developers Documentation](https://developer.android.com/)
- [AndroidX Room Documentation](https://developer.android.com/training/data-storage/room)
- [Java Documentation](https://docs.oracle.com/en/java/)
- [SQLite Documentation](https://www.sqlite.org/docs.html)
- [Material Design Guidelines](https://m3.material.io/)

---

## Repository

**GitHub:** https://github.com/RudyNothing/LifeOS

**LifeOS – One application. Six modules. Your life, organised.**