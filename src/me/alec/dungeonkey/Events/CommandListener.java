package me.alec.dungeonkey.Events;

import me.alec.dungeonkey.DungeonKey;
import me.alec.dungeonkey.Models.Party;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CommandListener implements CommandExecutor {
    private final DungeonKey dungeonKey;

    public CommandListener(DungeonKey dungeonKey) {
        this.dungeonKey = dungeonKey;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            FileConfiguration config =  dungeonKey.getConfig();

            Player host = (Player) sender;

            host.sendMessage("command received");

            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("create")) {
                    Party newParty = new Party(host);
                    dungeonKey.allParties.add(newParty);

                    System.out.println(dungeonKey.allParties + " < ALL PARTIES");
                }
            }
        }
        return true;
    }
}
