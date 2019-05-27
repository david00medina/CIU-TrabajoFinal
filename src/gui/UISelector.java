package gui;

public enum UISelector {
    NOW(-1),
    START(0),
    ONE(1),
    TWO(2),
    THREE(3),
    CHRONO(4),
    BACK(5),
    BACK_OVER(6),
    BACK_PRESSED(7),
    PAUSE(8),
    PAUSE_OVER(9),
    PAUSE_PRESSED(10),
    TITLE(11),
    CROWN(12),
    GOLD_MEDAL(13),
    SILVER_MEDAL(14),
    BRONZE_MEDAL(15);

    private int id;

    UISelector(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static UISelector getSelectorFromID(int id) {
        for (UISelector uiSelector :
                UISelector.values()) {
            if (uiSelector.getId() == id) return uiSelector;
        }
        return null;
    }
}
