package me.alec.dungeonkey;

import me.alec.dungeonkey.Events.CommandListener;
import me.alec.dungeonkey.Events.MobDeathListener;
import me.alec.dungeonkey.Events.UseKeyListener;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class DungeonKey extends JavaPlugin {
    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        this.getServer().getPluginManager().registerEvents(new UseKeyListener(this), this);
        this.getServer().getPluginManager().registerEvents(new MobDeathListener(this), this);
        this.getCommand("dungeonkey").setExecutor(new CommandListener(this));

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "\n\nDungeonKey enabled\n\n");
    }

    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "\n\nDungeonKey disabled\n\n");
    }
}
