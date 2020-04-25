package me.alec.dungeonkey.Items;

import me.alec.dungeonkey.DungeonKey;
import me.alec.dungeonkey.HiddenStringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ExitKeyCreator {
    private final DungeonKey dungeonKey;

    public ExitKeyCreator(DungeonKey dungeonKey) {
        this.dungeonKey = dungeonKey;
    }

    public ItemStack createKey(String keyName) {
        FileConfiguration config = dungeonKey.getConfig();

        Material material = Material.valueOf(config.getString( "exitKey.material"));
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String displayName = config.getString("exitKey.displayName");
        ArrayList<String> lore = new ArrayList<>();

        // Create hidden id
        lore.add(HiddenStringUtils.encodeString(keyName));

        // Description
        lore.add(config.getString("exitKey.item-lore"));

        // Set ItemMeta
        assert meta != null;
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);

        return item;
    }
}
