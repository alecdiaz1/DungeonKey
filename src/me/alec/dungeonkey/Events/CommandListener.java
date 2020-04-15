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
                    createParty(player);
                } else if (args[0].equalsIgnoreCase("party")) {
                    getParty(player);
                } else if (args[0].equalsIgnoreCase("invite")) {
                    inviteToParty(player, args[1]);
                }
            }
        }
        System.out.println(dungeonKey.allParties + " < ALL PARTIES");
        return true;
    }

    private void createParty(Player player) {
        Party newParty = new Party(player);
        dungeonKey.allParties.add(newParty);
    }

    private void getParty(Player player) {
        for (Party party : dungeonKey.allParties) {
            if (party.getMembers().contains(player)) {
                player.sendMessage("Your party: " + party.getMembers().toString());
                System.out.println(party.getMembers());
            }
        }
    }

    private void inviteToParty(Player host, String inviteeName) {
        Player invitee = dungeonKey.getServer().getPlayerExact(inviteeName);
        System.out.println(invitee + " < invitee");
        if (invitee == null) {
            host.sendMessage("Player not found.");
        } else {
            host.sendMessage("Invited " + inviteeName + " to join your party.");
        }
    }
}
