# Ride Booking System — Java Console Application
## (Uber / Ola Style | OOP + SOLID + Clean Architecture + LLD)

---

## 1. Complete Folder Structure

```
RideBookingSystem/
└── src/
    ├── Main.java
    ├── model/
    │   ├── Rider.java
    │   ├── Driver.java
    │   ├── Ride.java
    │   ├── FareReceipt.java
    │   ├── RideStatus.java          (enum)
    │   └── VehicleType.java         (enum)
    ├── service/
    │   ├── RiderService.java
    │   ├── DriverService.java
    │   └── RideService.java
    ├── strategy/
    │   ├── RideMatchingStrategy.java (interface)
    │   ├── NearestDriverStrategy.java
    │   ├── LeastActiveDriverStrategy.java
    │   ├── FareStrategy.java         (interface)
    │   ├── DefaultFareStrategy.java
    │   └── PeakHourFareStrategy.java
    ├── repository/
    │   ├── RiderRepository.java
    │   ├── DriverRepository.java
    │   └── RideRepository.java
    └── exception/
        ├── RiderNotFoundException.java
        ├── DriverNotFoundException.java
        ├── RideNotFoundException.java
        └── NoDriverAvailableException.java
```

---

## 2. How to Compile and Run

```bash
# From the RideBookingSystem/ directory:

# Compile all Java files into the out/ directory
javac -d out -sourcepath src $(find src -name "*.java")

# Run the application
java -cp out Main
```

---

## 3. Sample Execution Output

```
╔════════════════════════════════════════╗
║     🚖 RIDE BOOKING SYSTEM v1.0        ║
║       (Uber / Ola Style — Java)        ║
╚════════════════════════════════════════╝

════════════════ MENU ═══════════════════
  1.  Add Rider
  2.  Add Driver
  ...
  11. Exit
═════════════════════════════════════════
Enter your choice: 1

─── Add Rider ───────────────────────────
Enter Rider ID   : R001
Enter Rider Name : Alice
Enter Location   : Koramangala
✔ Rider registered successfully: Rider[id=R001, name=Alice, location=Koramangala]

Enter your choice: 2
─── Add Driver ──────────────────────────
Enter Driver ID   : D001
Enter Driver Name : Raj
Enter Location    : Koramangala
✔ Driver registered successfully: Driver[id=D001, name=Raj, location=Koramangala, available=true, ridesCompleted=0]

Enter your choice: 6
─── Request Ride ────────────────────────
Enter Rider ID         : R001
Enter Distance (km)    : 12.5
  → Ride created with status REQUESTED (ID: RIDE-824812DB)
  → Driver assigned: Raj | Status: ASSIGNED
  ✔ Ride successfully booked!
  Ride[id=RIDE-824812DB, rider=Alice, driver=Raj, distance=12.5 km, status=ASSIGNED]

Enter your choice: 7
─── Complete Ride ───────────────────────
Enter Ride ID : RIDE-824812DB
  → Ride RIDE-824812DB COMPLETED. Driver Raj is now available.

Enter your choice: 10
─── Fare Receipt ────────────────────────
Enter Ride ID : RIDE-824812DB

╔══════════════════════════════════════╗
║         FARE RECEIPT                 ║
╠══════════════════════════════════════╣
║ Ride ID   : RIDE-824812DB            ║
║ Amount    : Rs. 155.00               ║
║ Generated : 2026-06-18 02:41:54      ║
╚══════════════════════════════════════╝
  (Base: Rs.30 + 12.5 km × Rs.10/km = Rs.155)
```

---

## 4. SOLID Principles — How Each Is Implemented

### S — Single Responsibility Principle (SRP)
Every class has exactly one reason to change:

| Class | Single Responsibility |
|---|---|
| `Rider`, `Driver`, `Ride`, `FareReceipt` | Hold domain data only — no logic |
| `RiderRepository` | CRUD for riders in memory |
| `DriverRepository` | CRUD for drivers in memory |
| `RideRepository` | CRUD for rides in memory |
| `RiderService` | Rider business operations (register, find) |
| `DriverService` | Driver business operations (register, availability) |
| `RideService` | Ride lifecycle orchestration |
| `NearestDriverStrategy` | The nearest-driver algorithm only |
| `DefaultFareStrategy` | The flat-rate fare calculation only |
| `Main` | Menu display and user input only |

### O — Open/Closed Principle (OCP)
The system is **open for extension, closed for modification**.

- To add a **new matching algorithm** (e.g., `RatingBasedDriverStrategy`), just implement `RideMatchingStrategy` and inject it — no existing class is touched.
- To add a **new pricing model** (e.g., `SurgeFareStrategy`), just implement `FareStrategy` — `RideService` never changes.
- Swapping strategies in `Main.java` is a single one-line change:
  ```java
  // Before
  private static final RideMatchingStrategy matchingStrategy = new NearestDriverStrategy();
  // After (zero other changes needed)
  private static final RideMatchingStrategy matchingStrategy = new LeastActiveDriverStrategy();
  ```

### L — Liskov Substitution Principle (LSP)
Any concrete strategy is fully substitutable for its interface:

- `NearestDriverStrategy` and `LeastActiveDriverStrategy` are **completely interchangeable** as `RideMatchingStrategy` — `RideService` behaves correctly with either.
- `DefaultFareStrategy` and `PeakHourFareStrategy` are **completely interchangeable** as `FareStrategy` — both accept a `Ride` and return a `double` with no surprises.

### I — Interface Segregation Principle (ISP)
All interfaces are small and focused — no fat interfaces:

- `RideMatchingStrategy` has exactly **one method**: `findDriver(Rider, List<Driver>)`
- `FareStrategy` has exactly **one method**: `calculateFare(Ride)`

Implementations are never forced to implement methods they don't need.

### D — Dependency Inversion Principle (DIP)
High-level modules depend on **abstractions**, not concretions:

- `RideService` depends on `RideMatchingStrategy` (interface), `FareStrategy` (interface), and `RideRepository` — never on `NearestDriverStrategy` or `DefaultFareStrategy` directly.
- All services receive their dependencies through **constructor injection** (see section 7).

---

## 5. Strategy Pattern — How It Is Used

The **Strategy Pattern** allows the algorithm to be selected at runtime without altering the classes that use it.

### Ride Matching Strategy

```
«interface»
RideMatchingStrategy
  + findDriver(Rider, List<Driver>): Driver
        ▲                  ▲
        │                  │
NearestDriverStrategy   LeastActiveDriverStrategy
  (closest location)    (fewest rides done)
```

`RideService.requestRide()` calls:
```java
Driver selectedDriver = matchingStrategy.findDriver(rider, availableDrivers);
```
It has zero knowledge of whether it's talking to `NearestDriverStrategy` or `LeastActiveDriverStrategy`.

### Fare Strategy

```
«interface»
FareStrategy
  + calculateFare(Ride): double
        ▲                  ▲
        │                  │
DefaultFareStrategy    PeakHourFareStrategy
 Rs.30 + Rs.10/km      same × 1.5 at peak hours
```

`RideService.generateFareReceipt()` calls:
```java
double fare = fareStrategy.calculateFare(ride);
```
Again, zero knowledge of the concrete implementation.

---

## 6. Dependency Injection — How It Is Implemented

**Constructor Injection** is used throughout. No `new` of a concrete dependency inside a service.

```java
// RideService — all dependencies injected at construction time
public RideService(RideRepository       rideRepository,
                   RiderService         riderService,
                   DriverService        driverService,
                   RideMatchingStrategy matchingStrategy,   // abstraction
                   FareStrategy         fareStrategy) {     // abstraction
    this.rideRepository   = rideRepository;
    this.matchingStrategy = matchingStrategy;
    this.fareStrategy     = fareStrategy;
    // ...
}
```

The **Composition Root** (where everything is wired together) lives entirely in `Main.java`:

```java
// Repositories
RiderRepository  riderRepository  = new RiderRepository();
DriverRepository driverRepository = new DriverRepository();
RideRepository   rideRepository   = new RideRepository();

// Services
RiderService  riderService  = new RiderService(riderRepository);
DriverService driverService = new DriverService(driverRepository);

// Strategies — swap these to change behaviour system-wide
RideMatchingStrategy matchingStrategy = new NearestDriverStrategy();
FareStrategy         fareStrategy     = new DefaultFareStrategy();

// Core service — receives all deps via constructor
RideService rideService = new RideService(
    rideRepository, riderService, driverService,
    matchingStrategy, fareStrategy
);
```

Benefits:
- Testable (mock any dependency)
- Swappable (change strategy in one place)
- No hidden dependencies (`new` never appears inside a service class)

---

## 7. Additional Design Principles Applied

| Principle | Where Applied |
|---|---|
| **DRY** | Input reading helpers (`readString`, `readInt`, `readDouble`) avoid duplicating Scanner logic across all menu options |
| **KISS** | In-memory `HashMap` repositories; no frameworks, no XML, no annotations |
| **YAGNI** | Only the features explicitly required are built (no persistence, no auth, no notifications) |
| **Law of Demeter** | Services call methods only on their direct dependencies — no chains like `ride.getDriver().getVehicle().getPlate()` |
| **Encapsulation** | All model fields are `private` with public getters/setters only where needed |

---

## 8. Exception Handling Strategy

| Exception | Trigger |
|---|---|
| `RiderNotFoundException` | `RiderService.getRiderById()` when ID not in repo |
| `DriverNotFoundException` | `DriverService.getDriverById()` when ID not in repo |
| `RideNotFoundException` | `RideService.getRideById()` when ID not in repo |
| `NoDriverAvailableException` | `RideService.requestRide()` when available driver list is empty |
| `IllegalStateException` | Completing/cancelling a ride in the wrong state |

All exceptions are caught in `Main.java`'s menu loop and displayed as user-friendly messages — the application never crashes.
