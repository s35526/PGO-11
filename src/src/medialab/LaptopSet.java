package medialab;

public class LaptopSet extends Equipment {
    private final int ramGb;
    private final boolean hasDockingStation;

    public LaptopSet(String id, String name, double baseDailyPrice, int ramGb, boolean hasDockingStation) {
        super(id, name, baseDailyPrice);
        this.ramGb = ramGb;
        this.hasDockingStation = hasDockingStation;
    }

    public int getRamGb() {
        return ramGb;
    }

    public boolean hasDockingStation() {
        return hasDockingStation;
    }

    @Override
    public double calculateDailyPrice() {
        double price = baseDailyPrice;
        if (hasDockingStation) price += 15.00;
        if (ramGb >= 32)  price += 25.0;
        return price;
    }
    @Override
    public String getDetails() {
        return String.format ("RAM: %d GB, stacja dokujaca: %s",
                ramGb, hasDockingStation ? "TAK" : "NIE");
    }
}
