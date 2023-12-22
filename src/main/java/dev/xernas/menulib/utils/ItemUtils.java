package dev.xernas.menulib.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {

    public static ItemStack createItem(String name, Material material) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

}
