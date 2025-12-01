import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;



public class VehicleRentalTest {

    //  TASK 2.1

    @Test
    public void testLicensePlate() {

        // VALID plates
        Vehicle car1 = new Car("Toyota", "Corolla", 2019, 5);
        assertDoesNotThrow(() -> car1.setLicensePlate("AAA100"));
        assertEquals("AAA100", car1.getLicensePlate());

        Vehicle car2 = new Car("Honda", "Civic", 2020, 5);
        assertDoesNotThrow(() -> car2.setLicensePlate("ABC567"));
        assertEquals("ABC567", car2.getLicensePlate());

        Vehicle car3 = new Car("Ford", "Focus", 2024, 5);
        assertDoesNotThrow(() -> car3.setLicensePlate("ZZZ999"));
        assertEquals("ZZZ999", car3.getLicensePlate());

        // INVALID plates
        Vehicle badCar = new Car("Test", "Car", 2000, 4);

        assertThrows(IllegalArgumentException.class, () -> badCar.setLicensePlate(""));
        assertThrows(IllegalArgumentException.class, () -> badCar.setLicensePlate(null));
        assertThrows(IllegalArgumentException.class, () -> badCar.setLicensePlate("AAA1000"));
        assertThrows(IllegalArgumentException.class, () -> badCar.setLicensePlate("ZZZ99"));  
    }
    
    // TASK 2.2
    
    @Test
    public void testRentAndReturnVehicle() {

        RentalSystem system = RentalSystem.getInstance();

        Vehicle testCar = new Car("Toyota", "Corolla", 2020, 5);
        testCar.setLicensePlate("AAA111");

        Customer testCustomer = new Customer(1, "George");

        system.addVehicle(testCar);
        system.addCustomer(testCustomer);

        assertEquals(Vehicle.VehicleStatus.Available, testCar.getStatus());

        boolean rentSuccess = system.rentVehicle(testCar, testCustomer, LocalDate.now(), 500);
        assertTrue(rentSuccess);
        assertEquals(Vehicle.VehicleStatus.Rented, testCar.getStatus());

        boolean rentFail = system.rentVehicle(testCar, testCustomer, LocalDate.now(), 500);
        assertFalse(rentFail);

        boolean returnSuccess = system.returnVehicle(testCar, testCustomer, LocalDate.now(), 0);
        assertTrue(returnSuccess);
        assertEquals(Vehicle.VehicleStatus.Available, testCar.getStatus());

        boolean returnFail = system.returnVehicle(testCar, testCustomer, LocalDate.now(), 0);
        assertFalse(returnFail);
    }
    
    // TASK 2.3
    
    @Test
    public void testSingletonRentalSystem() throws Exception {

        Constructor<RentalSystem> constructor =
                RentalSystem.class.getDeclaredConstructor();

        int modifiers = constructor.getModifiers();
        assertTrue(Modifier.isPrivate(modifiers),
                "RentalSystem constructor should be PRIVATE (Singleton)");

        RentalSystem instance = RentalSystem.getInstance();
        assertNotNull(instance);

        RentalSystem instance2 = RentalSystem.getInstance();
        assertSame(instance, instance2, "getInstance() should return the same Singleton instance");
    }

}