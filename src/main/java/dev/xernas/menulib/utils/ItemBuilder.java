package dev.xernas.menulib.utils;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.MenuLib;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

public class ItemBuilder {

    private final ItemStack item;
    private final Menu itemMenu;

    public ItemBuilder(Menu itemMenu, Material material) {
        item = new ItemStack(material);
        this.itemMenu = itemMenu;
    }

    public ItemBuilder setDisplayName(String name) {
        ItemMeta im = Objects.requireNonNull(item.getItemMeta());
        im.setDisplayName(name);
        item.setItemMeta(im);
        return this;
    }

    public ItemBuilder setLore(String... lines) {
        ItemMeta im = Objects.requireNonNull(item.getItemMeta());
        im.setLore(Arrays.stream(lines).toList());
        item.setItemMeta(im);
        return this;
    }

    public ItemBuilder setItemId(String itemId) {
        ItemMeta im = Objects.requireNonNull(item.getItemMeta());
        PersistentDataContainer dataContainer = im.getPersistentDataContainer();
        dataContainer.set(MenuLib.getItemIdKey(), PersistentDataType.STRING, itemId);
        item.setItemMeta(im);
        return this;
    }

    public ItemBuilder setOnClick(Consumer<InventoryClickEvent> e) {
        MenuLib.setItemClickEvent(itemMenu, item, e);
        return this;
    }

    public ItemBuilder setNextMenu(Menu menu) {
        Consumer<InventoryClickEvent> clickEventConsumer = inventoryClickEvent -> {
            Player player = (Player) inventoryClickEvent.getWhoClicked();
            player.closeInventory();
            menu.open(player);
        };
        MenuLib.setItemClickEvent(itemMenu, item, clickEventConsumer);
        return this;
    }

    public ItemBuilder setItemFlags(ItemFlag... itemFlags) {
        ItemMeta im = Objects.requireNonNull(item.getItemMeta());
        im.getItemFlags().forEach(im::removeItemFlags);
        im.addItemFlags(itemFlags);
        item.setItemMeta(im);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta im = Objects.requireNonNull(item.getItemMeta());
        im.setUnbreakable(unbreakable);
        item.setItemMeta(im);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        ItemMeta im = Objects.requireNonNull(item.getItemMeta());
        im.addEnchant(enchantment, level, ignoreLevelRestriction);
        item.setItemMeta(im);
        return this;
    }
    public ItemBuilder removeEnchant(Enchantment enchantment) {
        ItemMeta im = Objects.requireNonNull(item.getItemMeta());
        im.removeEnchant(enchantment);
        item.setItemMeta(im);
        return this;
    }

    public ItemStack build() {
        return item;
    }

}
