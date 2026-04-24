# AutoLog 🚗

An Android application for tracking vehicle maintenance schedules and service history — built with Java, Room, and MVVM architecture.

---

## Features

- **Garage Management** — Add and manage multiple vehicle profiles with make, model, year, VIN, mileage, and a photo
- **Maintenance Tracking** — Monitor 4 service types (oil changes, tire rotations, brake checks, wiper changes) by both mileage and date intervals
- **Smart Progress Dashboard** — Color-coded progress bars showing how close each service is to due, turning red when approaching or overdue
- **Service History** — Log and view past service records per vehicle, filtered by service type
- **Auto-Advancing Registration** — Registration due dates automatically roll over to the next cycle on expiration
- **Photo Support** — Add vehicle photos via camera or gallery with persistent URI permissions

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| Architecture | MVVM (ViewModel + LiveData) |
| Database | Room (SQLite) |
| Image Loading | Glide |
| UI Components | RecyclerView, Material Design, AlertDialog |
| Camera | FileProvider, ActivityResultLauncher |
| IDE | Android Studio |

---

## Architecture

AutoLog follows the **MVVM** pattern recommended by Google:

```
UI Layer (Activities)
    ↓ observes
ViewModel (AutoLogViewModel)
    ↓ queries
Repository / Room DAOs (AutoLogDao, ServiceRecordDao)
    ↓ reads/writes
Room Database (AutoLogDatabase)
    └── Entities: Car, ServiceRecord, Modification
```

- **Thread-safe singleton** pattern for database instantiation
- **LiveData** observers keep the UI in sync with the database in real time
- **Parcelable** Car entity for safe inter-Activity data passing
- **DateConverter** for Room-compatible date serialization

---

## Screens

| Screen | Description |
|---|---|
| **MainActivity** | Garage view — lists all vehicles with photo, make, model, and mileage |
| **DashboardActivity** | Per-vehicle dashboard with maintenance progress bars and service settings |
| **MaintenanceRecordsActivity** | Full service history log for a selected vehicle |

---

## Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 26+
- Java 11+

### Installation

```bash
git clone https://github.com/gmorillotech/AutoLog.git
```

1. Open the project in **Android Studio**
2. Let Gradle sync complete
3. Run on an emulator or physical device (API 26+)

---

## Project Structure

```
app/src/main/java/com/example/autolog/
├── data/
│   ├── AutoLogDatabase.java      # Room database singleton
│   ├── AutoLogDao.java           # Car + Modification DAOs
│   ├── ServiceRecordDao.java     # Service record queries
│   ├── Car.java                  # Parcelable Car entity
│   ├── ServiceRecord.java        # Service record entity
│   ├── Modification.java         # Modification entity
│   └── DateConverter.java        # Room type converter
├── CarAdapter.java               # RecyclerView adapter with Glide
├── AutoLogViewModel.java         # ViewModel for LiveData
├── MainActivity.java             # Garage screen
├── DashboardActivity.java        # Maintenance dashboard
└── MaintenanceRecordsActivity.java # Service history screen
```

---

## Author

**George Morillo**
- GitHub: [@gmorillotech](https://github.com/gmorillotech)
- LinkedIn: [linkedin.com/in/gmorillotech](https://linkedin.com/in/gmorillotech)
