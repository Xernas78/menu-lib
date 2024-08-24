package dev.xernas.menulib.utils;

/**
 * List of possible Menu Size from 9 to 54
 */
public enum InventorySize {

    SMALLEST(9),
    SMALL(18),
    NORMAL(27),
    LARGE(36),
    LARGER(45),
    LARGEST(54);

    private final int size;

    InventorySize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
