import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;

public class RentalSystem {

    // =====================================================
    //                   SINGLETON PATTERN
    // =====================================================

    private static RentalSystem instance = null;

    private RentalSystem() {
        vehicles = new ArrayList<>();
        customers = new ArrayList<>();
        rentalHistory = new RentalHistory();
    }

    public static RentalSystem getInstance() {
        if (instance == null) {
            instance = new RentalSystem();
        }
        return instance;
    }

    // =====================================================
    //                     DATA LISTS
    // =====================================================

    private List<Vehicle> vehicles;
    private List<Customer> customers;
    private RentalHistory rentalHistory;

    // =====================================================
    //                       METHODS
    // =====================================================

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        saveVehicle(vehicle); // NEW
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        saveCustomer(customer); // NEW
    }

    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {

            vehicle.setStatus(Vehicle.VehicleStatus.Rented);

            RentalRecord rec = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(rec);

            saveRecord(rec); // NEW

            System.out.println("Vehicle rented to " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not available for renting.");
        }
    }

    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {

            vehicle.setStatus(Vehicle.VehicleStatus.Available);

            RentalRecord rec = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(rec);

            saveRecord(rec); // NEW

            System.out.println("Vehicle returned by " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not rented.");
        }
    }

    public void displayVehicles(Vehicle.VehicleStatus status) {

        if (status == null)
            System.out.println("\n=== All Vehicles ===");
        else
            System.out.println("\n=== " + status + " Vehicles ===");

        System.out.printf("|%-16s | %-12s | %-12s | %-12s | %-6s | %-18s |%n",
                " Type", "Plate", "Make", "Model", "Year", "Status");
        System.out.println("|--------------------------------------------------------------------------------------------|");

        boolean found = false;

        for (Vehicle v : vehicles) {
            if (status == null || v.getStatus() == status) {

                found = true;

                String type = (v instanceof Car) ? "Car"
                        : (v instanceof Minibus) ? "Minibus"
                        : (v instanceof PickupTruck) ? "Pickup Truck"
                        : "Unknown";

                System.out.printf("| %-15s | %-12s | %-12s | %-12s | %-6d | %-18s |%n",
                        type, v.getLicensePlate(), v.getMake(), v.getModel(), v.getYear(), v.getStatus());
            }
        }

        if (!found) {
            if (status == null)
                System.out.println("  No vehicles found.");
            else
                System.out.println("  No vehicles with Status: " + status);
        }

        System.out.println();
    }

    public void displayAllCustomers() {
        for (Customer c : customers)
            System.out.println("  " + c);
    }

    public void displayRentalHistory() {

        if (rentalHistory.getRentalHistory().isEmpty()) {
            System.out.println("  No rental history found.");
            return;
        }

        System.out.printf("|%-10s | %-12s | %-20s | %-12s | %-12s |%n",
                " Type", "Plate", "Customer", "Date", "Amount");
        System.out.println("|-------------------------------------------------------------------------------|");

        for (RentalRecord r : rentalHistory.getRentalHistory()) {
            System.out.printf("| %-9s | %-12s | %-20s | %-12s | $%-11.2f |%n",
                    r.getRecordType(),
                    r.getVehicle().getLicensePlate(),
                    r.getCustomer().getCustomerName(),
                    r.getRecordDate(),
                    r.getTotalAmount());
        }

        System.out.println();
    }

    // =====================================================
    //                SEARCH METHODS (FIXED)
    // =====================================================

    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles)
            if (v.getLicensePlate().equalsIgnoreCase(plate))
                return v;
        return null;
    }

    public Customer findCustomerById(int id) {   // ONLY ONE VERSION NOW
        for (Customer c : customers)
            if (c.getCustomerId() == id)
                return c;
        return null;
    }

    // =====================================================
    //                   FILE SAVE METHODS
    // =====================================================

    private void saveVehicle(Vehicle vehicle) {
        try (FileWriter writer = new FileWriter("vehicles.txt", true)) {

            String type;
            if (vehicle instanceof Car) type = "Car";
            else if (vehicle instanceof Minibus) type = "Minibus";
            else if (vehicle instanceof PickupTruck) type = "PickupTruck";
            else type = "Unknown";

            writer.write(
                    type + "," +
                    vehicle.getLicensePlate() + "," +
                    vehicle.getMake() + "," +
                    vehicle.getModel() + "," +
                    vehicle.getYear()
            );

         // Write subclass-specific info
            if (vehicle instanceof Car) {
                writer.write("," + ((Car) vehicle).getNumSeats());
            } 
            else if (vehicle instanceof Minibus) {
                writer.write("," + ((Minibus) vehicle).isAccessible());
            } 
            else if (vehicle instanceof PickupTruck) {
                PickupTruck p = (PickupTruck) vehicle;
                writer.write("," + p.getCargoSize() + "," + p.hasTrailer());
            }

            writer.write("\n");

        } catch (IOException e) {
            System.out.println("Error writing to vehicles.txt");
        }
    }

    private void saveCustomer(Customer customer) {
        try (FileWriter writer = new FileWriter("customers.txt", true)) {

            writer.write(
                    customer.getCustomerId() + "," +
                    customer.getCustomerName() + "\n"
            );

        } catch (IOException e) {
            System.out.println("Error writing to customers.txt");
        }
    }

    private void saveRecord(RentalRecord record) {
        try (FileWriter writer = new FileWriter("rental_records.txt", true)) {

            writer.write(
                    record.getRecordType() + "," +
                    record.getVehicle().getLicensePlate() + "," +
                    record.getCustomer().getCustomerId() + "," +
                    record.getRecordDate() + "," +
                    record.getTotalAmount() + "\n"
            );

        } catch (IOException e) {
            System.out.println("Error writing to rental_records.txt");
        }
    }
}
