import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.Scanner;

public class RentalSystem {

    // SINGLETON
    private static RentalSystem instance = null;

    private RentalSystem() {
        vehicles = new ArrayList<>();
        customers = new ArrayList<>();
        rentalHistory = new RentalHistory();
        loadData(); // load saved data
    }

    public static RentalSystem getInstance() {
        if (instance == null) {
            instance = new RentalSystem();
        }
        return instance;
    }

    private List<Vehicle> vehicles;
    private List<Customer> customers;
    private RentalHistory rentalHistory;

    public boolean addVehicle(Vehicle vehicle) {
        if (findVehicleByPlate(vehicle.getLicensePlate()) != null) {
            System.out.println("Error: A vehicle with this license plate already exists.");
            return false;
        }

        vehicles.add(vehicle);
        saveVehicle(vehicle);
        return true;
    }

    public boolean addCustomer(Customer customer) {
        if (findCustomerById(customer.getCustomerId()) != null) {
            System.out.println("Error: A customer with this ID already exists.");
            return false;
        }

        customers.add(customer);
        saveCustomer(customer);
        return true;
    }

    // RENT & RETURN
    
    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {

            vehicle.setStatus(Vehicle.VehicleStatus.Rented);

            RentalRecord rec = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(rec);

            saveRecord(rec);

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

            saveRecord(rec);

            System.out.println("Vehicle returned by " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not rented.");
        }
    }

    // DISPLAY METHODS
    
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

    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles)
            if (v.getLicensePlate().equalsIgnoreCase(plate))
                return v;
        return null;
    }

    public Customer findCustomerById(int id) {
        for (Customer c : customers)
            if (c.getCustomerId() == id)
                return c;
        return null;
    }

    // SAVE METHODS

    private void saveVehicle(Vehicle vehicle) {
        try (FileWriter writer = new FileWriter("vehicles.txt", true)) {

            String type;
            if (vehicle instanceof Car) type = "Car";
            else if (vehicle instanceof Minibus) type = "Minibus";
            else if (vehicle instanceof PickupTruck) type = "PickupTruck";
            else type = "Unknown";

            writer.write(type + "," +
                         vehicle.getLicensePlate() + "," +
                         vehicle.getMake() + "," +
                         vehicle.getModel() + "," +
                         vehicle.getYear());

            if (vehicle instanceof Car) {
                writer.write("," + ((Car) vehicle).getNumSeats());
            } else if (vehicle instanceof Minibus) {
                writer.write("," + ((Minibus) vehicle).isAccessible());
            } else if (vehicle instanceof PickupTruck) {
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
            writer.write(customer.getCustomerId() + "," +
                         customer.getCustomerName() + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to customers.txt");
        }
    }

    private void saveRecord(RentalRecord record) {
        try (FileWriter writer = new FileWriter("rental_records.txt", true)) {
            writer.write(record.getRecordType() + "," +
                         record.getVehicle().getLicensePlate() + "," +
                         record.getCustomer().getCustomerId() + "," +
                         record.getRecordDate() + "," +
                         record.getTotalAmount() + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to rental_records.txt");
        }
    }

    // LOAD METHODS
    
    private void loadData() {
        loadVehicles();
        loadCustomers();
        loadRentalRecords();
    }

    private void loadVehicles() {
        File file = new File("vehicles.txt");
        if (!file.exists()) return;

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");

                if (parts.length < 5) continue;

                String type  = parts[0];
                String plate = parts[1];
                String make  = parts[2];
                String model = parts[3];
                int year     = Integer.parseInt(parts[4]);

                Vehicle v = null;

                if ("Car".equalsIgnoreCase(type) && parts.length >= 6) {
                    int seats = Integer.parseInt(parts[5]);
                    v = new Car(make, model, year, seats);
                }
                else if ("Minibus".equalsIgnoreCase(type) && parts.length >= 6) {
                    boolean accessible = Boolean.parseBoolean(parts[5]);
                    v = new Minibus(make, model, year, accessible);
                }
                else if ("PickupTruck".equalsIgnoreCase(type) && parts.length >= 7) {
                    double cargoSize = Double.parseDouble(parts[5]);
                    boolean hasTrailer = Boolean.parseBoolean(parts[6]);
                    v = new PickupTruck(make, model, year, cargoSize, hasTrailer);
                }

                if (v != null) {
                    v.setLicensePlate(plate);
                    vehicles.add(v);
                }
            }

        } catch (Exception e) {
            System.out.println("Error loading vehicles: " + e.getMessage());
        }
    }

    private void loadCustomers() {
        File file = new File("customers.txt");
        if (!file.exists()) return;

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", 2);
                if (parts.length < 1) continue;

                int id = Integer.parseInt(parts[0]);
                String name = (parts.length > 1) ? parts[1] : "";

                customers.add(new Customer(id, name));
            }

        } catch (Exception e) {
            System.out.println("Error loading customers: " + e.getMessage());
        }
    }

    private void loadRentalRecords() {
        File file = new File("rental_records.txt");
        if (!file.exists()) return;

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 5) continue;

                String recordType = parts[0];
                String plate      = parts[1];
                int customerId    = Integer.parseInt(parts[2]);
                LocalDate date    = LocalDate.parse(parts[3]);
                double amount     = Double.parseDouble(parts[4]);

                Vehicle v = findVehicleByPlate(plate);
                Customer c = findCustomerById(customerId);

                if (v == null || c == null) continue;

                RentalRecord rec = new RentalRecord(v, c, date, amount, recordType);
                rentalHistory.addRecord(rec);

                if ("RENT".equalsIgnoreCase(recordType)) {
                    v.setStatus(Vehicle.VehicleStatus.Rented);
                } else if ("RETURN".equalsIgnoreCase(recordType)) {
                    v.setStatus(Vehicle.VehicleStatus.Available);
                }
            }

        } catch (Exception e) {
            System.out.println("Error loading rental records: " + e.getMessage());
        }
    }

}
