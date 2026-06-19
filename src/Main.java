import exception.DriverNotFoundException;
import exception.NoDriverAvailableException;
import exception.RideNotFoundException;
import exception.RiderNotFoundException;
import model.Driver;
import model.FareReceipt;
import model.Ride;
import model.Rider;
import repository.DriverRepository;
import repository.RideRepository;
import repository.RiderRepository;
import service.DriverService;
import service.RideService;
import service.RiderService;
import strategy.DefaultFareStrategy;
import strategy.FareStrategy;
import strategy.LeastActiveDriverStrategy;
import strategy.NearestDriverStrategy;
import strategy.RideMatchingStrategy;

import java.util.List;
import java.util.Scanner;

/**
 * Entry point for the Ride Booking System.
 *
 * Responsibilities (SRP for Main):
 *  - Wire up all dependencies (manual DI / composition root).
 *  - Present the console menu to the user.
 *  - Delegate every action to the appropriate service.
 *  - Handle user-facing exceptions gracefully.
 *
 * Main.java contains ONLY menu + user-interaction logic.
 */
public class Main {

    // ─── Repositories ───────────────────────────────────────────────────────
    private static final RiderRepository  riderRepository  = new RiderRepository();
    private static final DriverRepository driverRepository = new DriverRepository();
    private static final RideRepository   rideRepository   = new RideRepository();

    // ─── Services ────────────────────────────────────────────────────────────
    private static final RiderService  riderService  = new RiderService(riderRepository);
    private static final DriverService driverService = new DriverService(driverRepository);

    // ─── Strategies (swappable via DI) ────────────────────────────────────────
    // Change these two lines to switch algorithms without touching any other code.
    private static final RideMatchingStrategy matchingStrategy = new NearestDriverStrategy();
    private static final FareStrategy         fareStrategy     = new DefaultFareStrategy();

    // ─── Core Service ─────────────────────────────────────────────────────────
    private static final RideService rideService = new RideService(
            rideRepository,
            riderService,
            driverService,
            matchingStrategy,
            fareStrategy
    );

    private static final Scanner scanner = new Scanner(System.in);

    // ═════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        printBanner();
        boolean running = true;

        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1  -> addRider();
                    case 2  -> addDriver();
                    case 3  -> viewRiders();
                    case 4  -> viewAllDrivers();
                    case 5  -> viewAvailableDrivers();
                    case 6  -> requestRide();
                    case 7  -> completeRide();
                    case 8  -> cancelRide();
                    case 9  -> viewAllRides();
                    case 10 -> generateFareReceipt();
                    case 11 -> {
                        System.out.println("\n👋 Thank you for using the Ride Booking System. Goodbye!");
                        running = false;
                    }
                    default -> System.out.println("⚠  Invalid choice. Please enter 1–11.");
                }
            } catch (RiderNotFoundException | DriverNotFoundException
                     | RideNotFoundException | NoDriverAvailableException e) {
                System.out.println("❌ Error: " + e.getMessage());
            } catch (IllegalStateException e) {
                System.out.println("⚠  State Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("❌ Unexpected error: " + e.getMessage());
            }
        }

        scanner.close();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  MENU ACTIONS
    // ═════════════════════════════════════════════════════════════════════════

    /** 1. Add Rider */
    private static void addRider() {
        System.out.println("\n─── Add Rider ───────────────────────────");
        String id       = readString("Enter Rider ID   : ");
        String name     = readString("Enter Rider Name : ");
        String location = readString("Enter Location   : ");
        riderService.registerRider(id, name, location);
    }

    /** 2. Add Driver */
    private static void addDriver() {
        System.out.println("\n─── Add Driver ──────────────────────────");
        String id       = readString("Enter Driver ID   : ");
        String name     = readString("Enter Driver Name : ");
        String location = readString("Enter Location    : ");
        driverService.registerDriver(id, name, location);
    }

    /** 3. View All Riders */
    private static void viewRiders() {
        System.out.println("\n─── All Riders ──────────────────────────");
        List<Rider> riders = riderService.getAllRiders();
        if (riders.isEmpty()) {
            System.out.println("  (no riders registered yet)");
        } else {
            riders.forEach(r -> System.out.println("  " + r));
        }
    }

    /** 4. View All Drivers */
    private static void viewAllDrivers() {
        System.out.println("\n─── All Drivers ─────────────────────────");
        List<Driver> drivers = driverService.getAllDrivers();
        if (drivers.isEmpty()) {
            System.out.println("  (no drivers registered yet)");
        } else {
            drivers.forEach(d -> System.out.println("  " + d));
        }
    }

    /** 5. View Available Drivers */
    private static void viewAvailableDrivers() {
        System.out.println("\n─── Available Drivers ───────────────────");
        List<Driver> drivers = driverService.getAvailableDrivers();
        if (drivers.isEmpty()) {
            System.out.println("  (no drivers currently available)");
        } else {
            drivers.forEach(d -> System.out.println("  " + d));
        }
    }

    /** 6. Request Ride */
    private static void requestRide() {
        System.out.println("\n─── Request Ride ────────────────────────");
        String riderId  = readString("Enter Rider ID         : ");
        double distance = readDouble("Enter Distance (km)    : ");
        Ride ride = rideService.requestRide(riderId, distance);
        System.out.println("  ✔ Ride successfully booked!");
        System.out.println("  " + ride);
    }

    /** 7. Complete Ride */
    private static void completeRide() {
        System.out.println("\n─── Complete Ride ───────────────────────");
        String rideId = readString("Enter Ride ID : ");
        Ride ride = rideService.completeRide(rideId);
        System.out.println("  ✔ " + ride);
    }

    /** 8. Cancel Ride */
    private static void cancelRide() {
        System.out.println("\n─── Cancel Ride ─────────────────────────");
        String rideId = readString("Enter Ride ID : ");
        Ride ride = rideService.cancelRide(rideId);
        System.out.println("  ✔ " + ride);
    }

    /** 9. View All Rides */
    private static void viewAllRides() {
        System.out.println("\n─── All Rides ───────────────────────────");
        List<Ride> rides = rideService.getAllRides();
        if (rides.isEmpty()) {
            System.out.println("  (no rides found)");
        } else {
            rides.forEach(r -> System.out.println("  " + r));
        }
    }

    /** 10. Generate Fare Receipt */
    private static void generateFareReceipt() {
        System.out.println("\n─── Fare Receipt ────────────────────────");
        String rideId = readString("Enter Ride ID : ");
        FareReceipt receipt = rideService.generateFareReceipt(rideId);
        System.out.println();
        System.out.println(receipt);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  UTILITY / DISPLAY HELPERS
    // ═════════════════════════════════════════════════════════════════════════

    private static void printBanner() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║     🚖 RIDE BOOKING SYSTEM v1.0        ║");
        System.out.println("║       (Uber / Ola Style — Java)        ║");
        System.out.println("╚════════════════════════════════════════╝");
    }

    private static void printMenu() {
        System.out.println("\n════════════════ MENU ═══════════════════");
        System.out.println("  1.  Add Rider");
        System.out.println("  2.  Add Driver");
        System.out.println("  3.  View All Riders");
        System.out.println("  4.  View All Drivers");
        System.out.println("  5.  View Available Drivers");
        System.out.println("  6.  Request Ride");
        System.out.println("  7.  Complete Ride");
        System.out.println("  8.  Cancel Ride");
        System.out.println("  9.  View All Rides");
        System.out.println("  10. Generate Fare Receipt");
        System.out.println("  11. Exit");
        System.out.println("═════════════════════════════════════════");
    }

    // ─── Safe input helpers ────────────────────────────────────────────────

    private static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("⚠  Please enter a valid integer.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(line);
                if (value <= 0) {
                    System.out.println("⚠  Distance must be greater than 0.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("⚠  Please enter a valid number.");
            }
        }
    }
}
