package dev.xernas.menulib.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.xernas.menulib.MenuLib;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.UUID;

/**
 * Utility class for performing operations on {@link ItemStack}.
 * Provides methods for creating items, checking item identifiers, and creating player skulls.
 */
public class ItemUtils {
    
    /**
     * Creates an {@link ItemStack} with the specified display name and material.
     *
     * @param name     the display name of the item
     * @param material the material type of the item
     * @return the created ItemStack with the given name and material
     */
    public static ItemStack createItem(String name, Material material) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }
    
    /**
     * Checks if the provided {@link ItemStack} has the specified item ID stored in its
     * {@link PersistentDataContainer}.
     *
     * @param item   The {@link ItemStack} to be checked for the specified item ID.
     * @param itemId The item ID to be compared against the value stored in the persistent data container.
     * @return {@code true} if the item has a matching item ID; {@code false} otherwise.
     */
    public static boolean isItem(ItemStack item, String itemId) {
        PersistentDataContainer dataContainer = Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer();
        if (dataContainer.has(MenuLib.getItemIdKey(), PersistentDataType.STRING)) {
            return Objects.equals(dataContainer.get(MenuLib.getItemIdKey(), PersistentDataType.STRING), itemId);
        }
        return false;
    }
    
    /**
     * Creates a player skull item for the specified player UUID.
     *
     * @param playerUUID the UUID of the player whose skull is to be created
     * @return an {@link ItemStack} representing the player's skull
     */
    public static ItemStack getPlayerSkull(UUID playerUUID) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        if (skullMeta != null) {
            PlayerProfile profile = Bukkit.createProfile(playerUUID);
            skullMeta.setPlayerProfile(profile);
            skull.setItemMeta(skullMeta);
        }
        return skull;
    }
    
    /**
     * Compare deux {@link ItemStack} pour vérifier s'ils sont similaires.
     * Deux items sont considérés similaires s'ils ont le même type, la même quantité,
     * et les mêmes métadonnées (nom, lore, etc.). Permet de ne pas vérifier le component TooltipDisplay.
     *
     * @param item1 le premier item à comparer
     * @param item2 le second item à comparer
     * @return true si les items sont similaires, false sinon
     */
    @SuppressWarnings("UnstableApiUsage")
    public static boolean isSimilar(ItemStack item1, ItemStack item2) {
        if (item1 == null || item2 == null) return false;
        if (item1.getType() != item2.getType()) return false;
        if (item1.getAmount() != item2.getAmount()) return false;
        if (item1.hasItemMeta() != item2.hasItemMeta()) return false;
        if (item1.hasItemMeta() && item2.hasItemMeta()) {
            if (! Objects.equals(item1.getItemMeta().displayName(), item2.getItemMeta().displayName())) return false;
            if (! Objects.equals(item1.getItemMeta().lore(), item2.getItemMeta().lore())) return false;
            if (! Objects.equals(item1.getItemMeta().getPersistentDataContainer(), item2.getItemMeta().getPersistentDataContainer())) {
                return false;
            }
            if (! Objects.equals(item1.getData(DataComponentTypes.ENCHANTMENTS), item2.getData(DataComponentTypes.ENCHANTMENTS))) {
                return false;
            }
        }
        
        return true;
    }
}