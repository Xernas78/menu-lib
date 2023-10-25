package dev.xernas.menulib;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public final class MenuLib implements Listener {

    private static NamespacedKey itemIdKey;
    private static final Map<Player, Menu> lastMenu = new HashMap<>();
    private static final Map<Menu, Map<ItemStack, Consumer<InventoryClickEvent>>> itemClickEvents = new HashMap<>();

    private MenuLib(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        itemIdKey = new NamespacedKey(plugin, "itemId");
    }

    public static void init(JavaPlugin plugin) {
        new MenuLib(plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof Menu menu) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {
                return;
            }
            itemClickEvents.forEach((menu1, itemStackConsumerMap) -> {
                if (menu1.equals(menu)) {
                    itemStackConsumerMap.forEach((itemStack, inventoryClickEventConsumer) -> {
                        if (itemStack.equals(e.getCurrentItem())) {
                            inventoryClickEventConsumer.accept(e);
                        }
                    });
                }
            });
            menu.onInventoryClick(e);
        }
    }

    public static void setItemClickEvent(Menu menu, ItemStack itemStack, Consumer<InventoryClickEvent> e) {
        Map<ItemStack, Consumer<InventoryClickEvent>> itemEvents = itemClickEvents.get(menu);
        if (itemEvents == null) {
            itemEvents = new HashMap<>();
        }
        itemEvents.put(itemStack, e);
        itemClickEvents.put(menu, itemEvents);
    }

    public static NamespacedKey getItemIdKey() {
        return itemIdKey;
    }

    public static void setLastMenu(Player player, Menu menu) {
        lastMenu.put(player, menu);
    }

    public static Menu getLastMenu(Player player) {
        return lastMenu.get(player);
    }
}
