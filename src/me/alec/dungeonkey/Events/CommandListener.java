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
                    case "leave":
                        leaveParty(player);
                        break;
                    case "disband":
                        disbandParty(player);
                        break;
                    case "party":
                        getParty(player);
                        break;
                    case "accept":
                        acceptInvite(player);
                        break;
                    case "deny":
                        denyInvite(player);
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

    private void leaveParty(Player player) {
        for (Party party : dungeonKey.allParties) {
            if (party.getMembers().contains(player)) {
                party.members.remove(player);

                // Remove party from all parties list if empty/last member leaves
                if (party.members.size() == 0) {
                    dungeonKey.allParties.remove(party);
                }

                player.sendMessage("You have left your current party.");
                return;
            }
        }
    }

    private void acceptInvite(Player player) {
        for (Party party : dungeonKey.allParties) {
            if (party.getInvitedMembers().contains(player)) {
                party.members.add(player);
                party.invitedMembers.remove(player);
                player.sendMessage("You accepted the invite to join the party.");
                return;
            } else {
                player.sendMessage("You don't have any pending invites.");
            }
        }
    }

    private void denyInvite(Player player) {
        for (Party party : dungeonKey.allParties) {
            if (party.getInvitedMembers().contains(player)) {
                party.invitedMembers.remove(player);
                player.sendMessage("You denied the invite to join the party.");
                return;
            }
        }
    }

    private void disbandParty(Player player) {
        for (Party party : dungeonKey.allParties) {
            if (party.getMembers().contains(player)) {
                for (Player p : party.getMembers()) {
                    p.sendMessage("Your party was disbanded.");
                }
                dungeonKey.allParties.remove(party);
                player.sendMessage("You disbanded the party.");
                return;
            }
        }
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
