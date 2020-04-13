package me.alec.dungeonkey.Events;

import me.alec.dungeonkey.DungeonKey;
import me.alec.dungeonkey.HiddenStringUtils;
import me.alec.dungeonkey.Items.ItemCreator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class UseKeyListener implements Listener {
    private DungeonKey plugin;

    public UseKeyListener(DungeonKey dungeonKey) {
        plugin = dungeonKey;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        FileConfiguration config = plugin.getConfig();

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Action action = event.getAction();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            // TODO: handle null pointer exception checking lore
            List<String> itemLore = item.getItemMeta().getLore();

            if (itemLore.size() > 0 && HiddenStringUtils.hasHiddenString(itemLore.get(0))) {
                for (String key : config.getKeys(false)) {

                    // Get hidden key name
                    String name = HiddenStringUtils.extractHiddenString(itemLore.get(0));
                    if (key.equals(name)) {

                        // Build location object
                        String world = config.getString(key + ".world");
                        assert world != null;
                        Location location = new Location(
                                plugin.getServer().getWorld(world),
                                config.getDouble(key + ".coordinates.x"),
                                config.getDouble(key + ".coordinates.y"),
                                config.getDouble(key + ".coordinates.z")
                        );
                        player.teleport(location);

                        // Crashes if try to use inventory.remove()
                        item.setAmount(0);
                    }
                }
            }
        }
    }
}
