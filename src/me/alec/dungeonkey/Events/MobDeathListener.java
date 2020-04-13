package me.alec.dungeonkey.Events;

import me.alec.dungeonkey.DungeonKey;
import me.alec.dungeonkey.Items.ItemCreator;
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

    public MobDeathListener(DungeonKey dungeonKey) {
        this.dungeonKey = dungeonKey;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity slainEntity = event.getEntity();

        if (slainEntity instanceof Monster) {
            System.out.println("DEBUG > killed" + slainEntity.getName());

            int dropChance = new Random().nextInt(100);
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

        String[] items = config.getKeys(false).toArray(new String[0]);
        return items[new Random().nextInt(items.length)];
    }
}
