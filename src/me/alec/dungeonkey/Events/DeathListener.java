package me.alec.dungeonkey.Events;

import me.alec.dungeonkey.DungeonKey;
import me.alec.dungeonkey.Items.KeyCreator;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

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

        if (slainEntity instanceof Monster) {

            int dropChance = random.nextInt(100);

            if (dropChance < config.getInt("globals.dropRate")) {
                String keyName = getRandomKey();

                ItemStack newDungeonKey =  new KeyCreator(dungeonKey).createKey(keyName);

                slainEntity.getLocation().getWorld().dropItem(
                        slainEntity.getLocation(),
                        newDungeonKey);
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