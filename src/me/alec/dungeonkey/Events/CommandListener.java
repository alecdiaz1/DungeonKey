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
            if (party.getMembers().containsKey(player)) {
                player.sendMessage("You're already in a party! Please leave your party with /dk leave to leave your current party.");
                return;
            }
        }
        Party newParty = new Party(player);
        dungeonKey.allParties.add(newParty);
        player.sendMessage("Successfully created a party.");
    }

    private void leaveParty(Player player) {
        for (Party party : dungeonKey.allParties) {
            if (party.getMembers().containsKey(player)) {
                party.members.remove(player);

                // Remove party from all parties list if empty/last member leaves
                if (party.members.size() == 0) {
                    dungeonKey.allParties.remove(party);
                }

                player.sendMessage("You have left your current party.");
                for (Player p : party.getMembers().keySet()) {
                    p.sendMessage(player.getDisplayName() + " has left the party.");
                }
                return;
            } else {
                player.sendMessage("You are not currently in a party.");
            }
        }
    }

    private void acceptInvite(Player player) {
        for (Party party : dungeonKey.allParties) {
            if (party.getInvitedMembers().contains(player)) {
                party.members.put(player, false);
                party.invitedMembers.remove(player);
                player.sendMessage("You accepted the invite to join the party.");
                for (Player p : party.getMembers().keySet()) {
                    p.sendMessage(player.getDisplayName() + " has joined the party.");
                }
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
            if (party.getHost() == player) {
                player.sendMessage("You disbanded the party.");
                for (Player p : party.getMembers().keySet()) {
                    p.sendMessage("Your party was disbanded.");
                }
                dungeonKey.allParties.remove(party);
                return;
            } else {
                player.sendMessage("You need to be the host of a party to disband it!");
            }
        }
    }

    private void getParty(Player player) {
        for (Party party : dungeonKey.allParties) {
            if (party.getMembers().containsKey(player)) {
                player.sendMessage("Your party: " + party.getMembers().toString());
                return;
            }
        }
        player.sendMessage("You are not currently in a party.");
    }

    private void inviteToParty(Player host, String inviteeName) {
        Player invitee = dungeonKey.getServer().getPlayerExact(inviteeName);
        System.out.println(invitee + " < invitee");
        if (invitee == null) {
            host.sendMessage("Player not found.");
        } else if (host == invitee) {
            host.sendMessage("You can't invite yourself to a party!");
        } else {
            if (dungeonKey.allParties.size() > 0) {
                for (Party party : dungeonKey.allParties) {
                    if (party.getHost() == host) {
                        party.invitedMembers.add(invitee);
                        host.sendMessage("Invited " + inviteeName + " to join your party.");
                        invitee.sendMessage("You have been invited to join " + host.getDisplayName() + "'s party. " +
                                "\nType /dk accept to accept the invite or /dk deny to deny the invite.");
                    } else {
                        host.sendMessage("You are not the host of a party.");
                    }
                }
            } else {
                host.sendMessage("You are not the host of a party.");
            }
        }
    }
}
