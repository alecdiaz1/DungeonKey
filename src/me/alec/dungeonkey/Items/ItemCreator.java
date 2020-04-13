package me.alec.dungeonkey.Items;

import me.alec.dungeonkey.DungeonKey;
import me.alec.dungeonkey.HiddenStringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ItemCreator {
    private final DungeonKey dungeonKey;

    public ItemCreator(DungeonKey dungeonKey) {
        this.dungeonKey = dungeonKey;
    }

    public ItemStack createKey(String keyName) {
        FileConfiguration config = dungeonKey.getConfig();

        ItemStack item = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta meta = item.getItemMeta();

        String displayName = config.getString(keyName + ".displayName");
        ArrayList<String> lore = new ArrayList<>();

        // Create hidden id
        lore.add(HiddenStringUtils.encodeString(keyName));

        // Description
        lore.add(config.getString(keyName + ".item-lore"));

        // Set ItemMeta
        assert meta != null;
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);

        return item;
    }
}
