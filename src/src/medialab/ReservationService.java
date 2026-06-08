package medialab;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReservationService {
    private final List<Student> students;
    private final List<Equipment> equipmentList;
    private final List<Reservation> reservations;
    private final DiscountPolicy discountPolicy;
    private int reservationCounter = 1;

    public ReservationService(List<Student> students,
                              List<Equipment> equipmentList,
                              DiscountPolicy discountPolicy) {
        this.students = students;
        this.equipmentList = equipmentList;
        this.reservations = new ArrayList<>();
        this.discountPolicy = discountPolicy;
    }

    public Reservation createReservation(String studentId, String equipmentId, int days) {
        Student student = findStudentById(studentId).orElse(null);
        if (student == null) {
            System.out.println("Blad: student o ID '" + studentId + "' nie istnieje.");
            return null;
        }

        Equipment equipment = findEquipmentById(equipmentId).orElse(null);
        if (equipment == null) {
            System.out.println("Blad: sprzet o ID '" + equipmentId + "' nie istnieje.");
            return null;
        }

        if (!equipment.isAvailable()) {
            System.out.println("Blad: sprzet " + equipmentId + " nie jest dostepny.");
            return null;
        }

        if (days < 1 || days > 14) {
            System.out.println("Blad: liczba dni musi byc z zakresu 1-14.");
            return null;
        }

        String reservationId = String.format("R%03d", reservationCounter++);
        Reservation reservation = new Reservation(reservationId, student, equipment, days);
        equipment.setAvailable(false);
        reservations.add(reservation);
        return reservation;
    }

    public void returnEquipment(String reservationId) {
        Reservation reservation = findReservationById(reservationId).orElse(null);
        if (reservation == null) {
            System.out.println("Blad: rezerwacja o ID '" + reservationId + "' nie istnieje.");
            return;
        }
        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            System.out.println("Blad: rezerwacja " + reservationId
                    + " nie ma statusu ACTIVE (aktualny: " + reservation.getStatus() + ").");
            return;
        }

        reservation.setStatus(ReservationStatus.RETURNED);
        reservation.getEquipment().setAvailable(true);

        double totalCost = reservation.calculateTotalCost(discountPolicy);
        int pointsEarned = (int) (totalCost / 10.0);
        reservation.getStudent().addLoyaltyPoints(pointsEarned);

        System.out.printf("Zwrocono sprzet. Student %s otrzymal %d punktow lojalnosciowych.%n",
                reservation.getStudent().getFullName(), pointsEarned);
    }

    public List<Reservation> getActiveReservations() {
        return reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    public List<Reservation> getReturnedReservations() {
        return reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.RETURNED)
                .collect(Collectors.toList());
    }

    public void printReport() {
        List<Reservation> returned = getReturnedReservations();

        System.out.println("\n===== RAPORT ZAKONCZONYCH REZERWACJI =====");
        if (returned.isEmpty()) {
            System.out.println("Brak zakonczonych rezerwacji.");
        } else {
            for (Reservation r : returned) {
                double cost = r.calculateTotalCost(discountPolicy);
                System.out.printf("  %s | Koszt: %.2f PLN%n", r.getDisplayText(), cost);
            }
        }

        double totalRevenue = returned.stream()
                .mapToDouble(r -> r.calculateTotalCost(discountPolicy))
                .sum();
        System.out.printf("%nLaczny przychod: %.2f PLN%n", totalRevenue);

        students.stream()
                .max(Comparator.comparingInt(Student::getLoyaltyPoints))
                .ifPresent(s -> System.out.printf(
                        "Student z najwieksza liczba punktow: %s (%d pkt)%n",
                        s.getFullName(), s.getLoyaltyPoints()));
    }

    public List<Student> getStudents() { return students; }
    public List<Equipment> getAllEquipment() { return equipmentList; }

    private Optional<Student> findStudentById(String id) {
        return students.stream().filter(s -> s.getId().equalsIgnoreCase(id)).findFirst();
    }

    private Optional<Equipment> findEquipmentById(String id) {
        return equipmentList.stream().filter(e -> e.getId().equalsIgnoreCase(id)).findFirst();
    }

    private Optional<Reservation> findReservationById(String id) {
        return reservations.stream().filter(r -> r.getId().equalsIgnoreCase(id)).findFirst();
    }
}