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
                String action = args[0].toLowerCase();
                switch(action) {
                    case "create":
                        createParty(player);
                        break;
                    case "party":
                        getParty(player);
                        break;
                    case "invite":
                        if (args.length == 2) {
                            inviteToParty(player, args[1]);
                        } else {
                            player.sendMessage("Please specify a player to invite.");
                        }
                }
            }
        }
        System.out.println(dungeonKey.allParties + " < ALL PARTIES");
        return true;
    }

    private void createParty(Player player) {
        for (Party party : dungeonKey.allParties) {
            if (party.getMembers().contains(player)) {
                player.sendMessage("You're already in a party! Please leave your party with /dk leave to leave your current party.");
                return;
            }
        }
        Party newParty = new Party(player);
        dungeonKey.allParties.add(newParty);
    }

    private void getParty(Player player) {
        for (Party party : dungeonKey.allParties) {
            if (party.getMembers().contains(player)) {
                player.sendMessage("Your party: " + party.getMembers().toString());
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
            invitee.sendMessage("You have been invited to join " + host.getDisplayName() + "'s party. " +
                    "\nType /dk accept to accept the invite or /dk deny to deny the invite.");
        }
    }
}
