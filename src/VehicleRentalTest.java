import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
        assertThrows(IllegalArgumentException.class, () -> badCar.setLicensePlate("AAA1000")); // too long
        assertThrows(IllegalArgumentException.class, () -> badCar.setLicensePlate("ZZZ99"));   // too short
    }
}
