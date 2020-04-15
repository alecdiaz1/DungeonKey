package me.alec.dungeonkey.Events;

import me.alec.dungeonkey.DungeonKey;
import me.alec.dungeonkey.HiddenStringUtils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class UseKeyListener implements Listener {
    private final DungeonKey dungeonKey;

    public UseKeyListener(DungeonKey dungeonKey) {
        this.dungeonKey = dungeonKey;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        FileConfiguration configOriginal = dungeonKey.getConfig();
        ConfigurationSection config = configOriginal.getConfigurationSection("keys");

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Action action = event.getAction();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
                List<String> itemLore = item.getItemMeta().getLore();

                if (itemLore.size() > 0 && HiddenStringUtils.hasHiddenString(itemLore.get(0))) {
                    for (String key : config.getKeys(false)) {

                        // Get hidden key name
                        String name = HiddenStringUtils.extractHiddenString(itemLore.get(0));
                        System.out.println(name);
                        if (key.equals(name)) {

                            // Build location object
                            String world = config.getString(key + ".world");
                            assert world != null;
                            Location location = new Location(
                                    dungeonKey.getServer().getWorld(world),
                                    config.getDouble(key + ".coordinates.x"),
                                    config.getDouble(key + ".coordinates.y"),
                                    config.getDouble(key + ".coordinates.z"),
                                    (float) config.getDouble(key + ".coordinates.yaw"),
                                    (float) config.getDouble(key + ".coordinates.pitch")
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
}
