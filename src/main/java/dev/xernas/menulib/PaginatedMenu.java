package dev.xernas.menulib;

import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import dev.xernas.menulib.utils.ItemUtils;
import dev.xernas.menulib.utils.StaticSlots;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An abstract class representing a paginated menu for a player.
 * This class extends the {@link Menu} class and provides the functionality
 * to paginate items within an inventory-like user interface.
 * PaginatedMenu is designed to manage a collection of items across multiple
 * pages and offers methods to retrieve, update, and navigate through these pages.
 */
public abstract class PaginatedMenu extends Menu {
    
    private int page = 0;
    private int numberOfPages;
    
    /**
     * Constructs a new PaginatedMenu for the specified player.
     * A PaginatedMenu is an extension of the {@link Menu} class
     * that provides support for pagination of items to be displayed
     * within the menu.
     *
     * @param owner The {@link Player} who owns the paginated menu
     */
    public PaginatedMenu(Player owner) {
        super(owner);
    }
    
    /**
     * Retrieves the material to be used as the border material for the menu.
     * The border material is used to visually differentiate specific slots
     * within the menu, typically serving as a decorative or structural element.
     *
     * @return The {@link Material} that represents the border material, or {@code null}
     * if no border material has been specified.
     */
    @Nullable
    public abstract Material getBorderMaterial();
    
    /**
     * Retrieves a list of static inventory slot indices that should remain constant
     * across all pages of the menu. These {@link StaticSlots} are typically used for items or UI
     * elements that do not change during pagination, such as borders or fixed buttons.
     *
     * @return A non-null list of integers representing the static inventory slot indices.
     */
    @NotNull
    public abstract List<Integer> getStaticSlots();
    
    /**
     * Retrieves the list of items to be displayed in the menu.
     *
     * @return A non-null {@link List} of {@link ItemStack} instances representing the items
     * available for pagination in the menu.
     */
    public abstract List<ItemStack> getItems();
    
    /**
     * Retrieves a mapping of button slots to their corresponding {@link ItemBuilder} instances
     * for the current menu. Each entry in the map represents a specific button within the menu,
     * where the key is the slot index and the value is the item displayed as the button.
     *
     * @return A {@link Map} where keys are slot indices (integers) and values are the
     * {@link ItemBuilder} objects representing the buttons in the inventory.
     */
    public abstract Map<Integer, ItemBuilder> getButtons();
    
    /**
     * Retrieves the contents of the current page in the paginated menu.
     * The method generates a mapping of slot indices to {@link ItemBuilder} instances,
     * including static slots, dynamic items for the current page, and any additional buttons.
     * <p>
     * The static slots always contain either the specified border material or {@link Material#AIR}
     * if no border material has been defined. Dynamic slots are populated with items based on
     * pagination logic, and buttons are placed in the static slots if applicable.
     *
     * @return A non-null {@link Map} where keys are slot indices (integers) and values are
     * {@link ItemBuilder} objects representing the items displayed in the menu for the current page.
     */
    @Override
    public final @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> map = new HashMap<>();
        for (Integer staticSlot : getStaticSlots()) {
            map.put(staticSlot, new ItemBuilder(this, ItemUtils.createItem(Component.text(" "), getBorderMaterial() == null ? Material.AIR : getBorderMaterial())));
        }
        List<Integer> staticSlots = StaticSlots.removeRecurringIntegers(getStaticSlots(), getInventorySize().getSize());
        int maxItems = getInventorySize().getSize() - staticSlots.size();
        numberOfPages = (int) Math.ceil((double) getSizeOfItems() / maxItems) - 1;
        
        // Check if the page is out of bounds
        int index = 0;
        for (int i = 0; i < getInventory().getSize(); i++) {
            if (!staticSlots.contains(i)) {
                if (index + maxItems * page < getItems().size()) {
                    map.put(i, new ItemBuilder(this, getItems().get(index + maxItems * page)));
                    index++;
                }
            }
        }
        
        if (getButtons() != null) {
            getButtons().forEach((integer, itemBuilder) -> {
                if (staticSlots.contains(integer)) {
                    map.put(integer, new ItemBuilder(this, itemBuilder, itemBuilder.isBackButton()));
                }
            });
        }
        return map;
    }
    
    /**
     * Retrieves the size of the inventory for the paginated menu.
     * The inventory size determines the number of slots available
     * for displaying items and additional UI elements.
     *
     * @return An {@link InventorySize} enumeration value representing
     * the inventory's size, which in this implementation is
     * {@link InventorySize#LARGEST}.
     */
    @Override
    public abstract @NotNull InventorySize getInventorySize();
    
    /**
     * Retrieves the total number of items available for pagination in the menu.
     * This method is used to calculate the number of pages required to display
     * all items based on the inventory size and static slots.
     *
     * @return The total count of items (as an integer) that can be paginated
     * within the menu.
     */
    public abstract int getSizeOfItems();
    
    /**
     * Determines whether the current page is the last page in the paginated menu.
     *
     * @return {@code true} if the current page is the last page, {@code false} otherwise.
     */
    public final boolean isLastPage() {
        return page == numberOfPages;
    }
    
    /**
     * Retrieves the current page number of the paginated menu.
     *
     * @return The current page number as an integer, starting from 0.
     */
    public int getPage() {
        return page;
    }
    
    /**
     * Advances the menu to the next page, if available.
     * If the current page is the last page, this method does nothing.
     *
     * @param page The page number to set as the current page.
     */
    public void setPage(int page) {
        this.page = page;
    }
    
    /**
     * Retrieves the total number of pages in the paginated menu.
     * This value is calculated based on the total number of items
     * and the inventory size, taking into account static slots.
     *
     * @return The total number of pages as an integer.
     */
    public int getNumberOfPages() {
        return numberOfPages;
    }
}
