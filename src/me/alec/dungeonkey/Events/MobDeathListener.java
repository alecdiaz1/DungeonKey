package me.alec.dungeonkey.Events;

import me.alec.dungeonkey.DungeonKey;
import me.alec.dungeonkey.Items.ItemCreator;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class MobDeathListener implements Listener {
    private final DungeonKey dungeonKey;
    private Random random = new Random();

    public MobDeathListener(DungeonKey dungeonKey) {
        this.dungeonKey = dungeonKey;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity slainEntity = event.getEntity();

        if (slainEntity instanceof Monster) {
            System.out.println("DEBUG > killed " + slainEntity.getName());

            int dropChance = random.nextInt(100);
            if (dropChance < 50) {
                String keyName = getRandomKey();

                ItemStack newDungeonKey =  new ItemCreator(dungeonKey).createKey(keyName);

                slainEntity.getLocation().getWorld().dropItem(
                        slainEntity.getLocation(),
                        newDungeonKey);
            }
        }
    }

    public String getRandomKey() {
        FileConfiguration config = dungeonKey.getConfig();

        String[] keyNames = config.getKeys(false).toArray(new String[0]);

        double totalWeight = 0.0d;

        for (String key : keyNames) {
            totalWeight += config.getDouble(key + ".dropRate");
        }

        int randomIndex = -1;
        double randomNum = Math.random() * totalWeight;

        for (String key : config.getKeys(false)) {
            System.out.println("DROP RATE");
            System.out.println(config.get(key + ".dropRate"));

            randomNum -= config.getDouble(key + ".dropRate");
            if (randomNum <= 0.0d) {
                randomIndex = ArrayUtils.indexOf(keyNames, key);
                break;
            }
        }

        System.out.println(keyNames[randomIndex]);
        return keyNames[randomIndex];
    }
}
