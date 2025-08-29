package dev.xernas.menulib.utils;

import dev.xernas.menulib.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Supplier;

public class MenuUtils {
	
	/**
	 * Set the name of an ItemBuilder
	 *
	 * @param itemBuilder The ItemBuilder to set the name of
	 * @param name        The name to set
	 * @return The ItemBuilder with the name set
	 */
	private static ItemBuilder itemBuilderSetName(ItemBuilder itemBuilder, String name) {
		ItemMeta itemMeta = itemBuilder.getItemMeta();
		itemMeta.setDisplayName(name);
		itemBuilder.setItemMeta(itemMeta);
		
		return itemBuilder;
	}
	
	/**
	 * Set an Item to be refreshed.
	 *
	 * @param player       The Player to whom the menu is opened
	 * @param menu         The Menu to which the item belongs
	 * @param slot         Slot of Item to be refreshed
	 * @param itemSupplier Supplier of Item to be refreshed
	 * @return The BukkitRunnable that will refresh the item
	 */
	public static BukkitRunnable runDynamicItem(Player player, Menu menu, int slot, Supplier<ItemBuilder> itemSupplier) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				try {
					if (! menu.getInventory().getHolder().equals(player.getOpenInventory().getTopInventory().getHolder())) {
						cancel();
						return;
					}
					
					ItemStack item = itemSupplier.get();
					player.getOpenInventory().getTopInventory().setItem(slot, item);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}
}
