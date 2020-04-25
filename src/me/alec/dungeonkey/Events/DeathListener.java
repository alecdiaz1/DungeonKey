package me.alec.dungeonkey.Events;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.mobs.MobManager;
import io.lumine.xikage.mythicmobs.mobs.MobRegistry;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import io.lumine.xikage.mythicmobs.mobs.MythicMobStack;
import io.lumine.xikage.mythicmobs.mobs.entities.MythicEntity;
import io.lumine.xikage.mythicmobs.mobs.entities.MythicEntityType;
import io.lumine.xikage.mythicmobs.util.MythicUtil;
import me.alec.dungeonkey.DungeonKey;
import me.alec.dungeonkey.Items.ExitKeyCreator;
import me.alec.dungeonkey.Items.KeyCreator;
import me.alec.dungeonkey.Models.Party;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
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
        System.out.println(config.getStringList("bosses"));

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
            if (party.getMembers().containsKey(player)) {
                party.getMembers().put((Player) player, true);
//                player.sendMessage(((Player) player).getDisplayName() + " died.");
            }

            Set<Boolean> deathBools = new HashSet<>(party.getMembers().values());
            if (deathBools.size() <= 1) {
                party.inDungeon = false;
                party.dungeonName = "";
                for (Player p : party.getMembers().keySet()) {
                    p.sendMessage(ChatColor.RED + "Your entire party was slain.");
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
