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

import java.util.List;

public class UseKeyListener implements Listener {
    private DungeonKey plugin;

    public UseKeyListener(DungeonKey dungeonKey) {
        plugin = dungeonKey;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        FileConfiguration config = plugin.getConfig();

        if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            List<String> itemLore = player.getInventory().getItemInMainHand().getItemMeta().getLore();
            System.out.println(itemLore);

            for (String key : config.getKeys(false)) {
                String name = HiddenStringUtils.extractHiddenString(itemLore.get(0));

                if (key.equals(name)) {
                    String world = config.getString(key + ".world");
                    Location location = new Location(
                            plugin.getServer().getWorld(world),
                            config.getDouble(key + ".coordinates.x"),
                            config.getDouble(key + ".coordinates.y"),
                            config.getDouble(key + ".coordinates.z")
                    );

                    System.out.println("DEBUG > " + key + name);
                    System.out.println("DEBUG > " + location);

                    if (itemLore.size() > 0 && HiddenStringUtils.hasHiddenString(itemLore.get(0))) {
                        player.teleport(location);
                    }
                }
            }
        }
    }
}
