package dev.xernas.menulib.utils;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.MenuLib;
import dev.xernas.menulib.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ItemBuilder extends ItemStack {
    private final Menu itemMenu;
    private ItemMeta meta;


    public ItemBuilder(Menu itemMenu, Material material) {
        this(itemMenu, material, null);
    }

    public ItemBuilder(Menu itemMenu, ItemStack item) {
        this(itemMenu, item, null);
    }

    public ItemBuilder(Menu itemMenu, Material material, Consumer<ItemMeta> itemMeta) {
        this(itemMenu, new ItemStack(material), itemMeta);
    }

    public ItemBuilder(Menu itemMenu, ItemStack item, Consumer<ItemMeta> itemMeta) {
        super(item);
        this.itemMenu = itemMenu;
        meta = getItemMeta();
        if (itemMeta != null) {
            itemMeta.accept(meta);
        }
        setItemMeta(meta);
    }

    public ItemBuilder setItemId(String itemId) {
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(MenuLib.getItemIdKey(), PersistentDataType.STRING, itemId.toLowerCase());
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setOnClick(Consumer<InventoryClickEvent> e) {
        MenuLib.setItemClickEvent(itemMenu, this, e);
        return this;
    }

    public ItemBuilder setNextMenu(Menu menu) {
        Consumer<InventoryClickEvent> clickEventConsumer = inventoryClickEvent -> {
            Player player = (Player) inventoryClickEvent.getWhoClicked();
            MenuLib.setLastMenu(player, itemMenu);
            menu.open();
        };
        setOnClick(clickEventConsumer);
        return this;
    }

    public ItemBuilder setCloseButton() {
        Consumer<InventoryClickEvent> clickEventConsumer = inventoryClickEvent -> itemMenu.getOwner().closeInventory();
        setOnClick(clickEventConsumer);
        return this;
    }

    public ItemBuilder setBackButton() {
        Consumer<InventoryClickEvent> clickEventConsumer = inventoryClickEvent -> itemMenu.back();
        setOnClick(clickEventConsumer);
        return this;
    }

    public ItemBuilder setNextPageButton() {
        Consumer<InventoryClickEvent> clickEventConsumer = inventoryClickEvent -> {
            if (itemMenu instanceof PaginatedMenu menu) {
                menu.setPage(menu.isLastPage() ? menu.getPage() : menu.getPage() + 1);
                menu.open();
            }
        };
        setOnClick(clickEventConsumer);
        return this;
    }

    public ItemBuilder setPreviousPageButton() {
        Consumer<InventoryClickEvent> clickEventConsumer = inventoryClickEvent -> {
            if (itemMenu instanceof PaginatedMenu menu) {
                menu.setPage(menu.getPage() == 0 ? 0 : menu.getPage() - 1);
                menu.open();
            }
        };
        setOnClick(clickEventConsumer);
        return this;
    }

    public ItemBuilder setItemFlags(ItemFlag... itemFlags) {
        meta.getItemFlags().forEach(meta::removeItemFlags);
        meta.addItemFlags(itemFlags);
        setItemMeta(meta);
        return this;
    }

    @Override
    public final boolean setItemMeta(@Nullable ItemMeta itemMeta) {
        meta = itemMeta;
        return super.setItemMeta(itemMeta);
    }
}
