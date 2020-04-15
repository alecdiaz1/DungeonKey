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

            Player player = (Player) sender;

            player.sendMessage("command received");

            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("create")) {
                    Party newParty = new Party(player);
                    dungeonKey.allParties.add(newParty);
                } else if (args[0].equalsIgnoreCase("party")) {
                    for (Party party : dungeonKey.allParties) {
                        if (party.getMembers().contains(player)) {
                            player.sendMessage("Your party: " + party.getMembers().toString());
                            System.out.println(party.getMembers());
                        }
                    }
                } else if (args[0].equalsIgnoreCase("invite")) {
                    try {
                        Player invitee = dungeonKey.getServer().getPlayer(args[0]);
                    } catch(Exception e) {
                        player.sendMessage("Player not found.");
                    }
                }
            }
        }
        System.out.println(dungeonKey.allParties + " < ALL PARTIES");
        return true;
    }
}
