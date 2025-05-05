package dev.xernas.menulib.utils;

public enum InventorySize {

    SMALLEST(9),
    SMALL(18),
    NORMAL(27),
    LARGE(36),
    LARGER(45),
    LARGEST(54);

    private final int size;
    
    /**
     * Constructs an InventorySize enum instance with the specified size.
     *
     * @param size the size of the inventory associated with this enum instance
     */
    InventorySize(int size) {
        this.size = size;
    }
    
    /**
     * Retrieves the size value associated with this enum instance.
     *
     * @return the size value of the inventory for this enum instance
     */
    public int getSize() {
        return size;
    }
}
