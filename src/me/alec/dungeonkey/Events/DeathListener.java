package me.alec.dungeonkey.Events;

import me.alec.dungeonkey.DungeonKey;
import me.alec.dungeonkey.Items.ExitKeyCreator;
import me.alec.dungeonkey.Items.KeyCreator;
import me.alec.dungeonkey.Models.Party;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class DeathListener implements Listener {
    private final DungeonKey dungeonKey;
    private Random random = new Random();

    public DeathListener(DungeonKey dungeonKey) {
        this.dungeonKey = dungeonKey;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        FileConfiguration config = dungeonKey.getConfig();

        Entity slainEntity = event.getEntity();
        String sanitizedName = slainEntity.getName().replaceAll("\\s|[^a-zA-Z0-9]","").toLowerCase();

        if (config.getStringList("bosses").contains(sanitizedName)) {
            processBossDeath(slainEntity);
        } else if (slainEntity instanceof Monster) {
            processMonsterDeath(slainEntity);
        } else if (slainEntity instanceof Player) {
            processPlayerDeath(slainEntity);
        }
    }

    private void processBossDeath(Entity slainEntity) {
        System.out.println("BOSS DIED");
        ItemStack newExitKey = new ExitKeyCreator(dungeonKey).createKey("exitKey");

        slainEntity.getLocation().getWorld().dropItem(
                slainEntity.getLocation(),
                newExitKey);
    }

    private void processMonsterDeath(Entity slainEntity) {
        FileConfiguration config = dungeonKey.getConfig();
        int dropChance = random.nextInt(100);

        if (dropChance < config.getInt("globals.dropRate")) {
            String keyName = getRandomKey();

            ItemStack newDungeonKey =  new KeyCreator(dungeonKey).createKey(keyName);

            slainEntity.getLocation().getWorld().dropItem(
                    slainEntity.getLocation(),
                    newDungeonKey);
        }
    }

    private void processPlayerDeath(Entity player) {
        for (Party party : dungeonKey.allParties) {
            if (party.inDungeon) {
                if (party.getMembers().containsKey(player)) {
                    party.getMembers().put((Player) player, true);
                }

                Set<Boolean> deathBools = new HashSet<>(party.getMembers().values());
                if (deathBools.size() <= 1) {
                    for (Player p : party.getMembers().keySet()) {
                        FileConfiguration config = dungeonKey.getConfig();

                        p.sendMessage(ChatColor.RED + "Your entire party was slain. Party was disbanded.");
                        String resetConfigPath = "keys." + party.dungeonName + ".reset-block-coordinates.";
                        Block block = dungeonKey.getServer().getWorld("builderworld").getBlockAt(
                                config.getInt(resetConfigPath + "x"),
                                config.getInt(resetConfigPath + "y"),
                                config.getInt(resetConfigPath + "z")
                        );

                        block.setType(Material.REDSTONE_BLOCK);
                        dungeonKey.allParties.remove(party);
                    }
                }
            }
        }
    }

    public String getRandomKey() {
        FileConfiguration configOriginal = dungeonKey.getConfig();
        ConfigurationSection config = configOriginal.getConfigurationSection("keys");

        String[] keyNames = config.getKeys(false).toArray(new String[0]);

        double totalWeight = 0.0d;

        for (String key : keyNames) {
            totalWeight += config.getDouble(key + ".dropRate");
        }

        int randomIndex = -1;
        double randomNum = Math.random() * totalWeight;

        for (String key : config.getKeys(false)) {
            randomNum -= config.getDouble(key + ".dropRate");
            if (randomNum <= 0.0d) {
                randomIndex = ArrayUtils.indexOf(keyNames, key);
                break;
            }
        }

        return keyNames[randomIndex];
    }
}
