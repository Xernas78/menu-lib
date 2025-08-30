package dev.xernas.menulib;

import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Represents an abstract Menu framework for managing custom player inventories.
 * A menu is tied to a specific player and provides methods for customization,
 * handling inventory interactions, and managing permissions.
 */
public abstract class Menu implements InventoryHolder {
    
    private final Object2ObjectMap<ItemBuilder, Consumer<InventoryClickEvent>> itemClickEvents = new Object2ObjectOpenHashMap<>();
    
    private final Player owner;
    
    /**
     * Constructs a new Menu for the specified player.
     *
     * @param owner The {@link Player} who owns the menu
     */
    protected Menu(Player owner) {
        this.owner = owner;
    }
    
    
    /**
     * Retrieves the name of the menu.
     *
     * @return A non-null {@link String} representing the name of the menu
     */
    @NotNull
    public abstract String getName();
    
    /**
     * Retrieves the textures of the menu.<br><br>
     *
     * WARNING: This method requires PlaceholderAPI and ItemsAdder to be installed on the server.
     *
     * @return A {@link String} representing the texture of the menu
     */
    public abstract @Nullable String getTexture();
    
    /**
     * Retrieves the size of the inventory for the menu.
     *
     * @return An {@link InventorySize} constant representing the size of the inventory.
     */
    @NotNull
    public abstract InventorySize getInventorySize();
    
    public String getPermission() {
        return null;
    }
    
    public Component getNoPermissionMessage() {
        return Component.text("§cVous n'avez pas la permission d'ouvrir ce menu.");
    }
    
    /**
     * Handles the event that occurs when a player interacts with the menu's inventory.
     * This method is called whenever an {@link InventoryClickEvent} is triggered for a menu
     * controlled by this class. Subclasses should implement the logic to respond
     * to clicks in the inventory, such as handling button functionality or item interactions.
     *
     * @param e The {@link InventoryClickEvent} containing details about the click action,
     *          including the player who clicked, the clicked inventory slot, and other relevant event data.
     */
    public abstract void onInventoryClick(InventoryClickEvent e);
    
    /**
     * Handles the event that occurs when a player closes the menu's inventory.
     * This method is called whenever an {@link InventoryCloseEvent} is triggered for a menu
     * controlled by this class. Subclasses
     * should implement the logic to respond to the inventory being closed,
     * such as saving data or cleaning up resources.
     *
     * @param event The {@link InventoryCloseEvent} containing details about the close action,
     */
    public abstract void onClose(InventoryCloseEvent event);
    
    /**
     * Retrieves the content of this menu as a mapping between inventory slot indexes and {@link ItemStack}s.
     * Each entry in the map represents an item stored in a specific slot of the menu's inventory.
     *
     * @return A non-null {@link Map} where the key is an integer representing an inventory slot index,
     * and the value is the {@link ItemStack} present in that slot.
     */
    @NotNull
    public abstract Map<Integer, ItemBuilder> getContent();
    
    /**
     * Retrieves a list of inventory slot indices that can be taken from the menu.
     * These slots are typically used for items that can be moved or removed by the player.
     *
     * @return A non-null list of integers representing the takable inventory slot indices.
     */
    public abstract List<Integer> getTakableSlot();
    
    /**
     * Opens the menu for the owner player. If the menu specifies a required permission,
     * the method checks if the owner has the necessary permission. If not, a "no permission"
     * message is sent to the owner and the menu does not open.
     * <p>
     * The inventory for the menu is created using {@link #getInventory()} and populated
     * with items from {@link #getContent()}. The populated inventory is then opened for
     * the owner player.
     */
    public final void open() {
        try {
            if (getPermission() != null && ! getPermission().isEmpty()) {
                if (! owner.hasPermission(getPermission())) {
                    owner.sendMessage(getNoPermissionMessage());
                    return;
                }
            }
            
            Menu current = MenuLib.getCurrentLastMenu(owner);
            if (current != this) {
                MenuLib.pushMenu(owner, this);
            }
            
            Inventory inventory = getInventory();
            
            getContent().forEach((slot, item) -> {
                setItem(owner, inventory, slot, item);
            });
            
            owner.openInventory(inventory);
        } catch (Exception e) {
            
            owner.closeInventory();
            e.printStackTrace();
        }
    }
    
    /**
     * Sets an item in the specified inventory at the given slot.
     * If the item is a "back button" and there is no previous menu for the player,
     * the method does nothing.
     * If the item is a "back button", it customizes the item's
     * display name and lore to indicate that it will return to the previous menu.
     *
     * @param player    The {@link Player} for whom the item is being set
     * @param inventory The {@link Inventory} where the item will be placed
     * @param slot      The inventory slot index where the item should be set
     * @param item      The {@link ItemBuilder} representing the item to be placed in the inventory
     */
    public final void setItem(Player player, Inventory inventory, int slot, ItemBuilder item) {
        if (item.isBackButton() && ! MenuLib.hasPreviousMenu(player)) return;
        
        if (item.isBackButton()) {
            item = new ItemBuilder(this, item, itemMeta -> {
                itemMeta.displayName(Component.text("§aRetour"));
                itemMeta.lore(List.of(
                        Component.text("§7Vous allez retourner au §a" +
                                (MenuLib.getLastMenu(player) != null ? MenuLib.getLastMenu(player).getName() : "Menu Précédent") + "§7."),
                        Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
                ));
            }, true);
        }
        
        inventory.setItem(slot, item);
    }
    
    /**
     * Fills the entire inventory with items made from the specified material.
     * Each slot in the inventory is populated with an {@link ItemStack} that uses the
     * provided material and has a blank name.
     *
     * @param material The {@link Material} to use for creating {@link ItemStack}s to fill the inventory.
     * @return A {@link Map} where the key represents the inventory slot index, and the value is the {@link ItemStack} placed
     * in that slot.
     */
    public final Map<Integer, ItemBuilder> fill(Material material) {
        Map<Integer, ItemBuilder> map = new HashMap<>();
        for (int i = 0; i < getInventorySize().getSize(); i++) {
            ItemBuilder filler = new ItemBuilder(this, material, itemMeta -> itemMeta.displayName(Component.text(" "))).hideTooltip(true);
            map.put(i, filler);
        }
        return map;
    }
    
    /**
     * Checks if the given {@link ItemStack} is associated with the specified item ID.
     *
     * @param item   The {@link ItemStack} to be checked
     * @param itemId The ID of the item to compare against
     * @return {@code true} if the item matches the specified ID, otherwise {@code false}
     */
    public final boolean isItem(ItemStack item, String itemId) {
        PersistentDataContainer dataContainer = Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer();
        if (dataContainer.has(MenuLib.getItemIdKey(), PersistentDataType.STRING)) {
            return Objects.equals(dataContainer.get(MenuLib.getItemIdKey(), PersistentDataType.STRING), itemId);
        }
        return false;
    }
    
    /**
     * Creates and returns the inventory associated with this menu.
     * The inventory is created with the size specified by {@link #getInventorySize()}
     * and named using {@link #getName()}. The menu itself is set as the inventory holder.
     *
     * @return The inventory object for this menu
     */
    @NotNull
    @Override
    public final Inventory getInventory() {
        boolean pluginStatus = getPluginStatus("ItemsAdder") && getPluginStatus("PlaceholderAPI");
        String title = pluginStatus && getTexture() != null && ! getTexture().isEmpty()
                ? getTexture()
                : getName();
        return Bukkit.createInventory(this, getInventorySize().getSize(), Component.text(title));
    }
    
    /**
     * Retrieves the mapping of {@link ItemBuilder} instances to their associated click event handlers.
     * This map contains items that have specific actions defined for when they are clicked
     * in the inventory. Each entry in the map consists of an {@link ItemBuilder} as the key
     * and a {@link Consumer} that handles {@link InventoryClickEvent}s as the value.
     *
     * @return An {@link Object2ObjectMap} where the key is an {@link ItemBuilder} and the value is a {@link Consumer}
     * that processes {@link InventoryClickEvent}s for that item.
     */
    public Object2ObjectMap<ItemBuilder, Consumer<InventoryClickEvent>> getItemClickEvents() {
        return itemClickEvents;
    }
    
    /**
     * Retrieves the player who owns this menu.
     *
     * @return The {@link Player} who is the owner of this menu
     */
    public Player getOwner() {
        return owner;
    }
    
    private boolean getPluginStatus(String name) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        return plugin != null && plugin.isEnabled();
    }
}