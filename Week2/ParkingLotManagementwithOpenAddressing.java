import java.util.*;

class ParkingSpot {

    String licensePlate;
    long entryTime;
    String status; // EMPTY, OCCUPIED, DELETED

    public ParkingSpot() {
        status = "EMPTY";
    }
}

class ParkingLot {

    private ParkingSpot[] table;
    private int capacity;
    private int occupied;
    private int totalProbes;

    public ParkingLot(int capacity) {
        this.capacity = capacity;
        this.table = new ParkingSpot[capacity];

        for (int i = 0; i < capacity; i++) {
            table[i] = new ParkingSpot();
        }

        occupied = 0;
        totalProbes = 0;
    }

    // Hash function
    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % capacity;
    }

    // Park vehicle using linear probing
    public void parkVehicle(String licensePlate) {

        int index = hash(licensePlate);
        int probes = 0;

        while (!table[index].status.equals("EMPTY")) {

            index = (index + 1) % capacity;
            probes++;

            if (probes >= capacity) {
                System.out.println("Parking full");
                return;
            }
        }

        table[index].licensePlate = licensePlate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].status = "OCCUPIED";

        occupied++;
        totalProbes += probes;

        System.out.println("Assigned spot #" + index + " (" + probes + " probes)");
    }

    // Exit vehicle
    public void exitVehicle(String licensePlate) {

        int index = hash(licensePlate);
        int probes = 0;

        while (!table[index].status.equals("EMPTY")) {

            if (table[index].status.equals("OCCUPIED") &&
                table[index].licensePlate.equals(licensePlate)) {

                long exitTime = System.currentTimeMillis();
                long durationMillis = exitTime - table[index].entryTime;

                double hours = durationMillis / (1000.0 * 60 * 60);
                double fee = hours * 5; // $5 per hour

                table[index].status = "DELETED";
                occupied--;

                System.out.println("Spot #" + index +
                        " freed, Duration: " +
                        String.format("%.2f", hours) +
                        " hours, Fee: $" +
                        String.format("%.2f", fee));

                return;
            }

            index = (index + 1) % capacity;
            probes++;

            if (probes >= capacity) break;
        }

        System.out.println("Vehicle not found");
    }

    // Find nearest available spot
    public int findNearestAvailable() {

        for (int i = 0; i < capacity; i++) {

            if (table[i].status.equals("EMPTY")) {
                return i;
            }
        }

        return -1;
    }

    // Generate statistics
    public void getStatistics() {

        double occupancyRate = (occupied * 100.0) / capacity;
        double avgProbes = occupied == 0 ? 0 : (double) totalProbes / occupied;

        System.out.println("Occupancy: " +
                String.format("%.2f", occupancyRate) + "%");

        System.out.println("Average Probes: " +
                String.format("%.2f", avgProbes));

        System.out.println("Peak Hour: 2-3 PM (simulated)");
    }
}

public class ParkingLotManagementwithOpenAddressing {

    public static void main(String[] args) throws Exception {

        ParkingLot lot = new ParkingLot(500);

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        Thread.sleep(2000);

        lot.exitVehicle("ABC-1234");

        lot.getStatistics();
    }
}