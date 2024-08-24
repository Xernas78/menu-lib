package dev.xernas.menulib;

import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class Menu implements InventoryHolder {

    private final Player owner;

    public Menu(Player owner) {
        this.owner = owner;
    }

    /**
     * Return the Menu name
     * @return Inventory name
     */
    @NotNull
    public abstract String getName();

    /**
     * Return the menu size
     * @return Inventory Size
     * @see InventorySize
     */
    @NotNull
    public abstract InventorySize getInventorySize();

    public String getPermission() {
        return null;
    }

    public String getNoPermissionMessage() {
        return "";
    }

    /**
     Trigger when the player click on a slot in the menu
     @param e The InventoryClickEvent
     @see InventoryClickEvent
     */
    public abstract void onInventoryClick(InventoryClickEvent e);

    @NotNull
    public abstract Map<Integer, ItemStack> getContent();

    /**
     * Open the menu for the menu's owner
    */
    public final void open() {
        if (getPermission() != null && !getPermission().isEmpty()) {
            if (!owner.hasPermission(getPermission())) {
                owner.sendMessage(getNoPermissionMessage());
                return;
            }
        }
        Inventory inventory = getInventory();
        getContent().forEach(inventory::setItem);
        owner.openInventory(inventory);
    }

    public final Map<Integer, ItemStack> fill(Material material) {
        Map<Integer, ItemStack> map = new HashMap<>();
        for (int i = 0; i < getInventorySize().getSize(); i++) {
            ItemStack filler = ItemUtils.createItem(" ", material);
            map.put(i, filler);
        }
        return map;
    }

    public final boolean isItem(ItemStack item, String itemId) {
        PersistentDataContainer dataContainer = Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer();
        if (dataContainer.has(MenuLib.getItemIdKey(), PersistentDataType.STRING)) {
            return Objects.equals(dataContainer.get(MenuLib.getItemIdKey(), PersistentDataType.STRING), itemId);
        }
        return false;
    }

    /**
     * Open the last menu opened by the owner
     */
    public final void back() {
        Menu lastMenu = MenuLib.getLastMenu(owner);
        lastMenu.open();
    }

    /**
     * @return Menu
     * @see Inventory
     */
    @NotNull
    @Override
    public final Inventory getInventory() {
        return Bukkit.createInventory(this, getInventorySize().getSize(), getName());
    }

    /**
     * @return Menu owner
     */
    public final Player getOwner() {
        return owner;
    }
}
