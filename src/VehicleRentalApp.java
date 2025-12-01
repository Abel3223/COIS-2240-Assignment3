import java.util.Scanner;
import java.time.LocalDate;

public class VehicleRentalApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RentalSystem rentalSystem = RentalSystem.getInstance();

        while (true) {
            System.out.println("\n1: Add Vehicle\n" +
                               "2: Add Customer\n" +
                               "3: Rent Vehicle\n" +
                               "4: Return Vehicle\n" +
                               "5: Display Available Vehicles\n" +
                               "6: Show Rental History\n" +
                               "0: Exit\n");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {

                //ADD VEHICLE
       
                case 1:
                    System.out.println("  1: Car\n" +
                                       "  2: Minibus\n" +
                                       "  3: Pickup Truck");
                    int type = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter license plate: ");
                    String plate = scanner.nextLine().toUpperCase();

                    System.out.print("Enter make: ");
                    String make = scanner.nextLine();

                    System.out.print("Enter model: ");
                    String model = scanner.nextLine();

                    System.out.print("Enter year: ");
                    int year = scanner.nextInt();
                    scanner.nextLine();

                    Vehicle vehicle = null;

                    if (type == 1) {
                        System.out.print("Enter number of seats: ");
                        int seats = scanner.nextInt();
                        scanner.nextLine();
                        vehicle = new Car(make, model, year, seats);

                    } else if (type == 2) {
                        System.out.print("Is accessible? (true/false): ");
                        boolean accessible = scanner.nextBoolean();
                        scanner.nextLine();
                        vehicle = new Minibus(make, model, year, accessible);

                    } else if (type == 3) {
                        System.out.print("Enter cargo size: ");
                        double cargoSize = scanner.nextDouble();
                        scanner.nextLine();
                        System.out.print("Has trailer? (true/false): ");
                        boolean hasTrailer = scanner.nextBoolean();
                        scanner.nextLine();
                        vehicle = new PickupTruck(make, model, year, cargoSize, hasTrailer);

                    } else {
                        System.out.println("Invalid vehicle type. Vehicle not added.");
                        break;
                    }

                    try {
                        vehicle.setLicensePlate(plate);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                        break; // do not add vehicle
                    }


                    if (rentalSystem.addVehicle(vehicle)) {
                        System.out.println("Vehicle added successfully.");
                    } else {
                        System.out.println("Vehicle NOT added — duplicate license plate.");
                    }

                    break;

                //ADD CUSTOMER
                    
                case 2:
                    System.out.print("Enter customer ID: ");
                    int cid = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter name: ");
                    String cname = scanner.nextLine();

                    if (rentalSystem.addCustomer(new Customer(cid, cname))) {
                        System.out.println("Customer added successfully.");
                    } else {
                        System.out.println("Customer NOT added — duplicate ID.");
                    }

                    break;
                    
                //RENT VEHICLE
                    
                case 3:
                    rentalSystem.displayVehicles(Vehicle.VehicleStatus.Available);

                    System.out.print("Enter license plate: ");
                    String rentPlate = scanner.nextLine().toUpperCase();

                    System.out.println("Registered Customers:");
                    rentalSystem.displayAllCustomers();

                    System.out.print("Enter customer ID: ");
                    int rentId = scanner.nextInt();

                    System.out.print("Enter rental amount: ");
                    double rentAmount = scanner.nextDouble();
                    scanner.nextLine();

                    Vehicle rentVehicle = rentalSystem.findVehicleByPlate(rentPlate);
                    Customer rentCustomer = rentalSystem.findCustomerById(rentId);

                    if (rentVehicle == null || rentCustomer == null) {
                        System.out.println("Vehicle or customer not found.");
                        break;
                    }

                    rentalSystem.rentVehicle(rentVehicle, rentCustomer, LocalDate.now(), rentAmount);
                    break;

                //RETURN VEHICLE
           
                case 4:
                    rentalSystem.displayVehicles(Vehicle.VehicleStatus.Rented);

                    System.out.print("Enter license plate: ");
                    String returnPlate = scanner.nextLine().toUpperCase();

                    System.out.println("Registered Customers:");
                    rentalSystem.displayAllCustomers();

                    System.out.print("Enter customer ID: ");
                    int returnId = scanner.nextInt();

                    System.out.print("Enter return fees: ");
                    double fees = scanner.nextDouble();
                    scanner.nextLine();

                    Vehicle returnVehicle = rentalSystem.findVehicleByPlate(returnPlate);
                    Customer returnCustomer = rentalSystem.findCustomerById(returnId);

                    if (returnVehicle == null || returnCustomer == null) {
                        System.out.println("Vehicle or customer not found.");
                        break;
                    }

                    rentalSystem.returnVehicle(returnVehicle, returnCustomer, LocalDate.now(), fees);
                    break;

                //DISPLAY AVAILABLE VEHICLES
         
                case 5:
                    rentalSystem.displayVehicles(Vehicle.VehicleStatus.Available);
                    break;

                case 6:
                    rentalSystem.displayRentalHistory();
                    break;

                case 0:
                    scanner.close();
                    System.exit(0);
            }
        }
    }
}
