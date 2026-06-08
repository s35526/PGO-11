package medialab;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static ReservationService service;

    public static void main(String[] args) {
        service = new ReservationService(
                createStudents(),
                createEquipment(),
                new LoyaltyDiscountPolicy()
        );

        System.out.println("  SYSTEM REZERWACJI SPRZETU - MediaLab");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> listStudents();
                case "2" -> listEquipment();
                case "3" -> createReservation();
                case "4" -> returnEquipment();
                case "5" -> showActiveReservations();
                case "6" -> service.printReport();
                case "0" -> {
                    System.out.println("Do widzenia!");
                    running = false;
                }
                default -> System.out.println("Nieznana opcja. Wybierz liczbe z menu.");
            }
        }
    }

    private static void printMenu() {
        System.out.print("""

                --------------------------------------------
                 1. Lista studentow
                 2. Lista sprzetu (dostepnosc i ceny)
                 3. Utworz rezerwacje
                 4. Zwroc sprzet
                 5. Aktywne rezerwacje
                 6. Raport zakonczonych rezerwacji
                 0. Zakoncz
                --------------------------------------------
                Wybor: """);
    }

    private static void listStudents() {
        System.out.println("\n--- LISTA STUDENTOW ---");
        service.getStudents().forEach(System.out::println);
    }

    private static void listEquipment() {
        System.out.println("\n--- LISTA SPRZETU ---");
        service.getAllEquipment().forEach(e -> System.out.println(e.getDisplayText()));
    }

    private static void createReservation() {
        System.out.print("Podaj id studenta: ");
        String studentId = scanner.nextLine().trim();
        System.out.print("Podaj id sprzetu: ");
        String equipmentId = scanner.nextLine().trim();
        System.out.print("Podaj liczbe dni (1-14): ");
        String daysInput = scanner.nextLine().trim();

        int days;
        try {
            days = Integer.parseInt(daysInput);
        } catch (NumberFormatException e) {
            System.out.println("Blad: podano nieprawidlowa liczbe dni.");
            return;
        }

        Reservation r = service.createReservation(studentId, equipmentId, days);
        if (r != null) {
            double cost = r.calculateTotalCost(new LoyaltyDiscountPolicy());
            System.out.printf("%nUtworzono rezerwacje %s.%n", r.getId());
            System.out.printf("Sprzet: %s%n", r.getEquipment().getName());
            System.out.printf("Koszt: %.2f PLN%n", cost);
            System.out.printf("Status: %s%n", r.getStatus());
        }
    }

    private static void returnEquipment() {
        System.out.print("Podaj id rezerwacji: ");
        String reservationId = scanner.nextLine().trim();
        service.returnEquipment(reservationId);
    }

    private static void showActiveReservations() {
        System.out.println("\n--- AKTYWNE REZERWACJE ---");
        List<Reservation> active = service.getActiveReservations();
        if (active.isEmpty()) {
            System.out.println("Brak aktywnych rezerwacji.");
        } else {
            active.forEach(r -> System.out.println(r.getDisplayText()));
        }
    }

    private static List<Student> createStudents() {
        List<Student> list = new ArrayList<>();
        list.add(new Student("S001", "Anna Kowalska",   "12c", 120));
        list.add(new Student("S002", "Marek Nowak",     "12c",  40));
        list.add(new Student("S003", "Julia Zielinska", "13a",   0));
        return list;
    }

    private static List<Equipment> createEquipment() {
        List<Equipment> list = new ArrayList<>();
        list.add(new LaptopSet ("E001", "Lenovo ThinkPad Lab", 80,  32, true));
        list.add(new LaptopSet ("E002", "Dell XPS Demo",       100, 16, false));
        list.add(new CameraKit ("E003", "Sony Content Kit",    90,  3,  true));
        list.add(new CameraKit ("E004", "Canon Interview Kit", 70,  1,  true));
        return list;
    }
}