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

import java.util.HashMap;
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
                            Party playerParty = getPlayerParty(player);
                            if (playerParty != null) {
                                if (playerParty.getHost() == player) {
                                    if (!playerParty.inDungeon) {
                                        if (teleportPlayers(playerParty, key)) {
                                            item.setAmount(0);
                                            playerParty.dungeonName = key;
                                            playerParty.inDungeon = true;
                                        } else {
                                            player.sendMessage("Another party is currently in the dungeon.");
                                        }
                                        // Crashes if try to use inventory.remove()
                                    } else {
                                        player.sendMessage("You are already in a dungeon!");
                                    }
                                } else {
                                    player.sendMessage("You must be the host of a party to do start a dungeon!");
                                }
                            } else {
                                player.sendMessage("You must be the host of a party to do start a dungeon!");
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

    private boolean teleportPlayers(Party party, String key) {
        FileConfiguration configOriginal = dungeonKey.getConfig();
        ConfigurationSection config = configOriginal.getConfigurationSection("keys");

        // Check if any party in dungeon already, return false if so
        for (Party p : dungeonKey.allParties) {
            if (p.getDungeonName().equals(key)) {
                return false;
            }
        }

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

        return true;
    }

    private Party getPlayerParty(Player player) {
        for (Party party : dungeonKey.allParties) {
            if (party.getMembers().containsKey(player)) {
                return party;
            }
        }
        return null;
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
