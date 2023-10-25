package dev.xernas.menulib;

import com.google.errorprone.annotations.NoAllocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.NotNull;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public abstract class Menu implements InventoryHolder {

    @NotNull
    public abstract String getName();
    @NotNull
    public abstract Integer getSize();
    public abstract void onInventoryClick(InventoryClickEvent e);

    @NotNull
    public abstract Map<Integer, ItemStack> getContent();

    public void open(Player player) {
        Inventory inventory = getInventory();
        getContent().forEach(inventory::setItem);
        player.openInventory(inventory);
    }

    public Map<Integer, ItemStack> fill(Material material) {
        Map<Integer, ItemStack> map = new HashMap<>();
        for (int i = 0; i < getSize(); i++) {
            map.put(i, new ItemStack(material));
        }
        return map;
    }

    public boolean isItem(ItemStack item, String itemId) {
        PersistentDataContainer dataContainer = Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer();
        if (dataContainer.has(MenuLib.getItemIdKey(), PersistentDataType.STRING)) {
            return Objects.equals(dataContainer.get(MenuLib.getItemIdKey(), PersistentDataType.STRING), itemId);
        }
        return false;
    }

    public void back(UUID playerUUID) {
        Menu lastMenu = MenuLib.getLastMenu(playerUUID);
        Player player = Objects.requireNonNull(Bukkit.getPlayer(playerUUID));
        player.closeInventory();
        lastMenu.open(player);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return Bukkit.createInventory(this, getSize(), getName());
    }
}
