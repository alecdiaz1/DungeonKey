package me.alec.dungeonkey.Events;

import me.alec.dungeonkey.DungeonKey;
import me.alec.dungeonkey.HiddenStringUtils;
import me.alec.dungeonkey.Models.Party;
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
                        if (key.equals(name)) {
                            if (canPlayerStartDungeon(player)) {
                                if (!dungeonInProgress(key)) {
                                    teleportPlayers(getHostParty(player), key);
                                    item.setAmount(0);
                                } else {
                                    player.sendMessage("Another party is currently in the dungeon.");
                                }

                                // Crashes if try to use inventory.remove()
                            }
                        }
                    }
                }

            }
        }
    }

    private boolean dungeonInProgress(String key) {
        for (Party party : dungeonKey.allParties) {
            if (party.dungeonName.equals(key) && party.inDungeon) {
                return true;
            }
        }
        return false;
    }

    private void teleportPlayers(Party party, String key) {
        FileConfiguration configOriginal = dungeonKey.getConfig();
        ConfigurationSection config = configOriginal.getConfigurationSection("keys");

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

        for (Player p : party.getMembers().keySet()) {
            p.teleport(location);
        }

        party.dungeonName = key;
        party.inDungeon = true;
    }

    private boolean canPlayerStartDungeon(Player player) {
        for (Party party : dungeonKey.allParties) {
            if (party.getHost() == player) {
                if (!party.inDungeon) {
                    return true;
                } else {
                    player.sendMessage("You are already in a dungeon!");
                }
            } else {
                player.sendMessage("You must be the host of a party to use the key!");
            }
        }
        return false;
    }

    private Party getHostParty(Player player) {
        for (Party party : dungeonKey.allParties) {
            if (party.getHost() == player) {
                return party;
            }
        }
        return null;
    }
}
