package me.alec.dungeonkey.Events;

import me.alec.dungeonkey.DungeonKey;
import me.alec.dungeonkey.HiddenStringUtils;
import me.alec.dungeonkey.Models.Party;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ExitKeyListener implements Listener {
    private final DungeonKey dungeonKey;

    public ExitKeyListener(DungeonKey dungeonKey) {
        this.dungeonKey = dungeonKey;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Action action = event.getAction();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
                List<String> itemLore = item.getItemMeta().getLore();

                if (itemLore.size() > 0 && HiddenStringUtils.hasHiddenString(itemLore.get(0))) {
                    Party playerParty = getPlayerParty(player);
                    if (playerParty != null) {
                        if (playerParty.getHost() == player) {
                            String name = HiddenStringUtils.extractHiddenString(itemLore.get(0));
                            if (name.equals("exitKey")) {
                                teleportPlayers(playerParty, item);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean teleportPlayers(Party party, ItemStack item) {
        FileConfiguration config = dungeonKey.getConfig();

        // Check if any party in dungeon already, return false if so

        for (Player player: party.getMembers().keySet()) {
            player.sendMessage(ChatColor.GREEN + "Teleporting back to spawn in 5 seconds. Any movement will cancel this.");
        }

        dungeonKey.tasks.put(party, new BukkitRunnable() {
            @Override
            public void run() {
            for (Player p : party.getMembers().keySet()) {
                String world = config.getString( "exitKey.world");
                assert world != null;
                Location location = new Location(
                        // will this bug out?
                        dungeonKey.getServer().getWorld(world),
                        config.getDouble( "exitKey.coordinates.x"),
                        config.getDouble("exitKey.coordinates.y"),
                        config.getDouble("exitKey.coordinates.z"),
                        (float) config.getDouble("exitKey.coordinates.yaw"),
                        (float) config.getDouble("exitKey.coordinates.pitch")
                );

                p.teleport(location);
                p.sendMessage(ChatColor.RED + "Dungeon finished, party disbanded.");
                if (p.getInventory().contains(item)) {
                        p.getInventory().remove(item);
                }

                String resetConfigPath = "keys." + party.dungeonName + ".reset-block-coordinates.";
                Block block = dungeonKey.getServer().getWorld("builderworld").getBlockAt(
                        config.getInt(resetConfigPath + "x"),
                        config.getInt(resetConfigPath + "y"),
                        config.getInt(resetConfigPath + "z")
                );
                System.out.print(party.dungeonName + " < DUNGEON NAME");
                System.out.println(resetConfigPath + ".x" + " < RESET X");
                System.out.println(config.getInt(resetConfigPath + "x") + " < X RESET");
                System.out.println(block + " < BLOCK");
                block.setType(Material.REDSTONE_BLOCK);

                dungeonKey.allParties.remove(party);
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
