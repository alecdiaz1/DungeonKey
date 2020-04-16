package me.alec.dungeonkey.Events;

import me.alec.dungeonkey.DungeonKey;
import me.alec.dungeonkey.HiddenStringUtils;
import me.alec.dungeonkey.Models.Party;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
                                        teleportPlayers(playerParty, key, item);
                                    } else {
                                        player.sendMessage(ChatColor.RED + "You are already in a dungeon!");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "You must be the host of a party to do start a dungeon!");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "You must be the host of a party to do start a dungeon!");
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

    private boolean teleportPlayers(Party party, String key, ItemStack item) {
        FileConfiguration configOriginal = dungeonKey.getConfig();
        ConfigurationSection config = configOriginal.getConfigurationSection("keys");

        // Check if any party in dungeon already, return false if so

        for (Player player: party.getMembers().keySet()) {
            player.sendMessage(ChatColor.GREEN + "Teleporting to the dungeon in 5 seconds. Any movement will cancel this.");
        }

        dungeonKey.tasks.put(party, new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : party.getMembers().keySet()) {
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

                    for (Party dungeonParty : dungeonKey.allParties) {
                        if (dungeonParty.getDungeonName().equals(key)) {
                            party.getHost().sendMessage(ChatColor.RED + "Another party is currently in the dungeon.");
                            this.cancel();
                            dungeonKey.tasks.remove(party);
                        }
                    }

                    party.dungeonName = key;
                    party.inDungeon = true;
                    p.teleport(location);
                    if (p.getInventory().contains(item)) {
                        p.getInventory().remove(item);
                    }
                }
            }
        }.runTaskLater(dungeonKey, 100)); // Divide by 20 to get seconds
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
