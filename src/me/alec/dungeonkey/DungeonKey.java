package me.alec.dungeonkey;

import me.alec.dungeonkey.Events.CommandListener;
import me.alec.dungeonkey.Events.DeathListener;
import me.alec.dungeonkey.Events.QuitListener;
import me.alec.dungeonkey.Events.UseKeyListener;
import me.alec.dungeonkey.Models.Party;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class DungeonKey extends JavaPlugin {
    public ArrayList<Party> allParties = new ArrayList<>();

    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        this.getServer().getPluginManager().registerEvents(new UseKeyListener(this), this);
        this.getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        this.getServer().getPluginManager().registerEvents(new QuitListener(this), this);
        this.getCommand("dungeonkey").setExecutor(new CommandListener(this));

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "\n\nDungeonKey enabled\n\n");
    }

    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "\n\nDungeonKey disabled\n\n");
    }
}
