package dev.xernas.menulib;

import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.xernas.menulib.utils.StaticSlots.removeRecurringIntegers;

public abstract class PaginatedMenu extends Menu {
    private int page = 0;
    private int numberOfPages;

    public PaginatedMenu(Player owner) {
        super(owner);
    }

    /**
     * @return The border's material
     * @see Material
     */
    @Nullable
    public abstract Material getBorderMaterial();

    @NotNull
    public abstract List<Integer> getStaticSlots();

    /**
     * @return ListStack of items that will be displayed in the paginated menu
     * @see ItemStack
     * @see List
     */
    @NotNull
    public abstract List<ItemStack> getItems();

    public abstract Map<Integer, ItemStack> getButtons();

    @Override
    @NotNull
    public final Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> map = new HashMap<>();
        for (Integer staticSlot : getStaticSlots()) {
            map.put(staticSlot, ItemUtils.createItem(" ", getBorderMaterial() == null ? Material.AIR : getBorderMaterial()));
        }
        List<Integer> staticSlots = removeRecurringIntegers(getStaticSlots());
        int maxItems = getInventorySize().getSize() - staticSlots.size();
        numberOfPages = (int) Math.ceil((double) getItems().size() / maxItems) - 1;

        // Pagination
        int index = 0;
        for (int i = 0; i < getInventory().getSize(); i++) {
            if (!staticSlots.contains(i)) {
                if (index + maxItems * page < getItems().size()) {
                    map.put(i, getItems().get(index + maxItems * page));
                    index++;
                }
            }
        }
        // Pagination

        if (getButtons() != null) {
            getButtons().forEach((integer, itemStack) -> {
                if (staticSlots.contains(integer)) {
                    map.put(integer, itemStack);
                }
            });
        }
        return map;
    }

    @Override
    public final @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    /**
     * Change the menu page
     * @param page Menu index
     */
    public final void setPage(int page) {
        this.page = page;
    }

    /**
     * @return Current menu page
     */
    public final int getPage() {
        return page;
    }

    /**
     * @return True if it's the last page of the menu
     */
    public final boolean isLastPage() {
        return page == numberOfPages;
    }
}
