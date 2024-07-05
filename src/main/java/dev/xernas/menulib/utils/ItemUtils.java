package dev.xernas.menulib.utils;

import dev.xernas.menulib.MenuLib;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.UUID;

public class ItemUtils {

    public static ItemStack createItem(String name, Material material) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static boolean isItem(ItemStack item, String itemId) {
        PersistentDataContainer dataContainer = Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer();
        if (dataContainer.has(MenuLib.getItemIdKey(), PersistentDataType.STRING)) {
            return Objects.equals(dataContainer.get(MenuLib.getItemIdKey(), PersistentDataType.STRING), itemId);
        }
        return false;
    }

    public static ItemStack getPlayerSkull(UUID playerUUID) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);

        if (skullMeta != null) {
            skullMeta.setOwningPlayer(player);
            skull.setItemMeta(skullMeta);
        }

        return skull;
    }

}
