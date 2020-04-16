package me.alec.dungeonkey;

import me.alec.dungeonkey.Events.*;
import me.alec.dungeonkey.Models.Party;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;

public class DungeonKey extends JavaPlugin {
    public ArrayList<Party> allParties = new ArrayList<>();
    public HashMap<Party, BukkitTask> tasks = new HashMap<>();

    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        this.getServer().getPluginManager().registerEvents(new UseKeyListener(this), this);
        this.getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        this.getServer().getPluginManager().registerEvents(new QuitListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        this.getCommand("dungeonkey").setExecutor(new CommandListener(this));

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "\n\nDungeonKey enabled\n\n");
    }

    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "\n\nDungeonKey disabled\n\n");
    }
}
