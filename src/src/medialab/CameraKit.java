package medialab;

public class CameraKit extends Equipment {
    private final int lensCount;
    private final boolean hasTripod;

    public CameraKit(String id, String name, double baseDailyPrice, int lensCount, boolean hasTripod) {

        super(id, name, baseDailyPrice);
        this.hasTripod = hasTripod;
        this.lensCount = lensCount;
    }

    public int getLensCount() { return lensCount; }
    public boolean hasTripod() { return hasTripod; }

    @Override
    public double calculateDailyPrice() {
        double price = baseDailyPrice + (lensCount * 10.0);
        if (hasTripod) price += 15.0;
        return price;
    }

    @Override
    public String getDetails() {
        return String.format("Obiektywy: %d, statyw: %s",
                lensCount, hasTripod ? "TAK" : "NIE");
    }
}
