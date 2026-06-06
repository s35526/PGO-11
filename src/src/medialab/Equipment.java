package medialab;

public abstract class Equipment implements Displayable {
    private final String id;
    private final String name;
    protected final double baseDailyPrice;
    private boolean available;

    public Equipment(String id, String name, double baseDailyPrice) {
        this.id = id;
        this.name = name;
        this.baseDailyPrice = baseDailyPrice;
        this.available = true;
    }

    public String getId() {return id;}
    public String getName() {return name;}
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public abstract double calculateDailyPrice();
    public abstract String getDetails();

    @Override
    public String getDisplayText() {
        String status = available ? "DOSTEPNY" : "NIEDOSTEPNY";
        return String.format("[%s] %s | %s | Cena/dzien: %.2f PLN | %s | %s",
                id, name, getClass().getSimpleName(),
                calculateDailyPrice(), status, getDetails());
    }
}
