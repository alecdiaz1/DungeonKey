package me.alec.dungeonkey.Events;

import me.alec.dungeonkey.DungeonKey;
import me.alec.dungeonkey.Items.KeyCreator;
import me.alec.dungeonkey.Models.Party;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class DeathListener implements Listener {
    private final DungeonKey dungeonKey;
    private Random random = new Random();

    public DeathListener(DungeonKey dungeonKey) {
        this.dungeonKey = dungeonKey;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity slainEntity = event.getEntity();

        if (slainEntity instanceof Monster) {
            processMonsterDeath(slainEntity);
        } else if (slainEntity instanceof Player) {
            processPlayerDeath(slainEntity);
        }
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
            if (party.getMembers().containsKey(player)) {
                party.getMembers().put((Player) player, true);
            }

            Set<Boolean> deathBools = new HashSet<>(party.getMembers().values());
            if (deathBools.size() <= 1) {
                party.inDungeon = false;
                party.dungeonName = "";
                for (Player p : party.getMembers().keySet()) {
                    p.sendMessage("Your entire party was slain.");
                    party.getMembers().put(p, false);
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
