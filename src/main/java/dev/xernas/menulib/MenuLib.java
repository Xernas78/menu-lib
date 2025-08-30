package dev.xernas.menulib;

import dev.xernas.menulib.utils.ItemBuilder;
import dev.xernas.menulib.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Consumer;

/**
 * {@code MenuLib} is a utility class designed to handle custom menus in a Bukkit/Spigot plugin environment.
 * It provides functionality for managing menu interactions, associating click events with specific items,
 * and handling player-menu associations.
 * <p>
 * This class is intended for use in creating interactive menus within Minecraft plugins,
 * allowing developers to define custom behavior for item clicks within menus.
 * <p>
 * The {@code MenuLib} class implements the {@link Listener} interface to handle inventory-related events.
 */
public final class MenuLib implements Listener {
    private static final Map<Player, Deque<Menu>> menuHistory = new HashMap<>();
    
    private static NamespacedKey itemIdKey;
    
    private static JavaPlugin plugin;
    
    /**
     * Constructs a new {@code MenuLib} instance and registers it as an event listener.
     * Also initializes the {@link NamespacedKey} used for item identification.
     *
     * @param plugin The {@link JavaPlugin} instance used to register events and create the {@link NamespacedKey}
     */
    private MenuLib(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        itemIdKey = new NamespacedKey(plugin, "itemId");
        MenuLib.plugin = plugin;
    }
    
    /**
     * Initializes the {@code MenuLib} library for the given plugin.
     * This method sets up necessary event handling and utilities
     * required for managing custom menus.
     *
     * @param plugin The {@link JavaPlugin} instance representing the plugin
     *               that integrates the {@code MenuLib} library. This is used
     *               to register event listeners and initialize key functionality.
     */
    public static void init(JavaPlugin plugin) {
        new MenuLib(plugin);
    }
    
    /**
     * Associates a click event handler with a specific item in a given menu.
     * When a player clicks on the specified {@link ItemStack} in the menu,
     * the provided {@link Consumer} is executed to handle the {@link InventoryClickEvent}.
     *
     * @param menu      The {@link Menu} in which the click event will be associated.
     * @param itemStack The {@link ItemStack} that will trigger the event when clicked.
     * @param e         A {@link Consumer} of {@link InventoryClickEvent} representing the event handler
     *                  to be executed when the {@link ItemStack} is clicked within the menu.
     */
    public static void setItemClickEvent(Menu menu, ItemStack itemStack, Consumer<InventoryClickEvent> e) {
        menu.getItemClickEvents().put(new ItemBuilder(menu, itemStack), e);
    }
    
    /**
     * Clears the menu history for a specific player.
     * This method removes all recorded menus from the player's history,
     * effectively resetting their navigation state.
     *
     * @param player The {@link Player} whose menu history is to be cleared.
     */
    public static void clearHistory(Player player) {
        menuHistory.remove(player);
    }
    
    /**
     * Pushes a new menu onto the player's menu history stack.
     * This allows tracking of the player's navigation through different menus.
     *
     * @param player The {@link Player} whose menu history is to be updated.
     * @param menu   The {@link Menu} to be added to the player's menu history.
     */
    public static void pushMenu(Player player, Menu menu) {
        menuHistory.computeIfAbsent(player, k -> new ArrayDeque<>()).push(menu);
    }
    
    /**
     * Retrieves the current menu from the player's menu history without removing it.
     * If there is no current menu, it returns {@code null}.
     *
     * @param player The {@link Player} whose menu history is to be checked.
     * @return The current {@link Menu} if available, otherwise {@code null}.
     */
    public static Menu getCurrentLastMenu(Player player) {
        Deque<Menu> history = menuHistory.get(player);
        
        if (history == null || history.isEmpty()) {
            return null;
        }
        
        return history.peek();
    }
    
    /**
     * Retrieves the last menu from the player's menu history without removing it.
     * If there is no previous menu, it returns {@code null}.
     *
     * @param player The {@link Player} whose menu history is to be checked.
     * @return The previous {@link Menu} if available, otherwise {@code null}.
     */
    public static Menu getLastMenu(Player player) {
        Deque<Menu> history = menuHistory.get(player);
        
        if (history == null || history.isEmpty() || history.size() < 2) {
            return null;
        }
        
        Iterator<Menu> iterator = history.iterator();
        
        iterator.next();
        
        return iterator.next();
    }
    
    /**
     * Pops the current menu from the player's menu history and returns the previous menu.
     * If there is no previous menu, it returns {@code null}.
     *
     * @param player The {@link Player} whose menu history is to be modified.
     * @return The previous {@link Menu} if available, otherwise {@code null}.
     */
    public static Menu popAndGetPreviousMenu(Player player) {
        Deque<Menu> history = menuHistory.get(player);
        if (history == null || history.size() < 2) return null;
        history.pop();
        return history.peek();
    }
    
    /**
     * Checks if the player has a previous menu in their menu history.
     *
     * @param player The {@link Player} whose menu history is to be checked.
     * @return {@code true} if the player has a previous menu, {@code false} otherwise.
     */
    public static boolean hasPreviousMenu(Player player) {
        Deque<Menu> history = menuHistory.get(player);
        return history != null && history.size() > 1;
    }
    
    /**
     * Retrieves the {@link NamespacedKey} used for item identification within the {@code MenuLib} library.
     *
     * @return The {@link NamespacedKey} instance used to uniquely identify items managed by the {@code MenuLib} library.
     */
    public static NamespacedKey getItemIdKey() {
        return itemIdKey;
    }
    
    /**
     * Retrieves the {@link JavaPlugin} instance associated with the {@code MenuLib} library.
     *
     * @return The {@link JavaPlugin} instance used to initialize the {@code MenuLib} library.
     */
    public static JavaPlugin getPlugin() {
        return plugin;
    }
    
    /**
     * Handles click events in an inventory associated with a {@link Menu}.
     * This method ensures that clicks within the menu's inventory are canceled,
     * and delegates further handling to the menu's implementation of {@code onInventoryClick}.
     * Additionally, it triggers any registered item-specific click event handlers.
     *
     * @param e The {@link InventoryClickEvent} representing the inventory interaction
     *          triggered by the player. Contains information about the clicked
     *          inventory, the clicked item, and other event details.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (! (e.getInventory().getHolder() instanceof Menu menu)) {
            return;
        }
        
        if (e.getCurrentItem() == null) {
            return;
        }
        
        if (menu.getTakableSlot().contains(e.getSlot())) {
            return;
        }
        
        e.setCancelled(true);
        menu.onInventoryClick(e);
        
        ItemBuilder itemClicked = menu.getContent().get(e.getSlot());
        
        if (itemClicked != null && itemClicked.isBackButton()) {
            Player player = (Player) e.getWhoClicked();
            Menu previous = MenuLib.popAndGetPreviousMenu(player);
            if (previous != null) {
                previous.open();
            }
            return;
        }
        
        try {
            Map<ItemBuilder, Consumer<InventoryClickEvent>> itemClickEvents = menu.getItemClickEvents();
            if (itemClickEvents.isEmpty()) {
                return;
            }
            
            for (Map.Entry<ItemBuilder, Consumer<InventoryClickEvent>> entry : itemClickEvents.entrySet()) {
                if (ItemUtils.isSimilar(entry.getKey(), e.getCurrentItem())) {
                    entry.getValue().accept(e);
                }
            }
        } catch (Exception ex) {
            plugin.getSLF4JLogger().error("An error occurred while handling a click event in a menu: {}", ex.getMessage(), ex);
        }
    }
    
    /**
     * Handles the event that occurs when a player closes an inventory associated with a {@link Menu}.
     */
    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder(false) instanceof PaginatedMenu menu) {
            menu.onClose(e);
        }
        
        if (e.getInventory().getHolder(false) instanceof Menu menu) {
            menu.onClose(e);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (! (e.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof Menu)) {
                    Player player = (Player) e.getPlayer();
                    MenuLib.clearHistory(player);
                }
            }, 1L);
        }
    }
}