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
                        break;
                    default:
                        help(player);
                        break;
                }
            } else {
                help(player);
            }
        }
        System.out.println(dungeonKey.allParties + " < ALL PARTIES");
        return true;
    }

    private void createParty(Player player) {
        for (Party party : dungeonKey.allParties) {
            if (party.getMembers().containsKey(player)) {
                player.sendMessage(ChatColor.RED + "You're already in a party! Please leave your party with /dk leave to leave your current party.");
                return;
            }
        }
        Party newParty = new Party(player);
        dungeonKey.allParties.add(newParty);
        player.sendMessage(ChatColor.GREEN + "Successfully created a party.");
    }

    private void help(Player player) {
        player.sendMessage(
        "-----" + ChatColor.GOLD + "Dungeon Key Help" + ChatColor.WHITE + "-----" +
            ChatColor.GOLD + "\n/dk create " + ChatColor.WHITE + "- Create a party" +
            ChatColor.GOLD + "\n/dk invite <player> " + ChatColor.WHITE + "- Invite a player to your party" +
            ChatColor.GOLD + "\n/dk leave " + ChatColor.WHITE + "- Leave current party" +
            ChatColor.GOLD + "\n/dk disband " + ChatColor.WHITE + "- Disband current party" +
            ChatColor.GOLD + "\n/dk party " + ChatColor.WHITE + "- View current party" +
            ChatColor.GOLD + "\n/dk accept" + ChatColor.WHITE + "- Accept a party invite" +
            ChatColor.GOLD + "\n/dk reject" + ChatColor.WHITE + "- Reject a party invite"
        );
    }

    private void leaveParty(Player player) {
        for (Party party : dungeonKey.allParties) {
            if (party.getMembers().containsKey(player)) {
                party.members.remove(player);

                // Remove party from all parties list if empty/last member leaves
                if (party.members.size() == 0) {
                    dungeonKey.allParties.remove(party);
                }

                player.sendMessage(ChatColor.GREEN +"You have left your current party.");
                for (Player p : party.getMembers().keySet()) {
                    p.sendMessage(player.getDisplayName() + ChatColor.RED + " has left the party.");
                }
                return;
            } else {
                player.sendMessage(ChatColor.RED + "You are not currently in a party.");
            }
        }
    }

    private void acceptInvite(Player player) {
        for (Party party : dungeonKey.allParties) {
            if (party.getInvitedMembers().contains(player)) {
                party.members.put(player, false);
                party.invitedMembers.remove(player);
                player.sendMessage(ChatColor.GREEN + "You accepted the invite to join the party.");
                for (Player p : party.getMembers().keySet()) {
                    p.sendMessage(player.getDisplayName() + " has joined the party.");
                }
                return;
            } else {
                player.sendMessage(ChatColor.RED + "You don't have any pending invites.");
            }
        }
    }

    private void denyInvite(Player player) {
        for (Party party : dungeonKey.allParties) {
            if (party.getInvitedMembers().contains(player)) {
                party.invitedMembers.remove(player);
                player.sendMessage(ChatColor.RED + "You denied the invite to join the party.");
                return;
            }
        }
    }

    private void disbandParty(Player player) {
        for (Party party : dungeonKey.allParties) {
            if (party.getHost() == player) {
                player.sendMessage(ChatColor.RED + "You disbanded the party.");
                for (Player p : party.getMembers().keySet()) {
                    if (p != party.getHost()) {
                        p.sendMessage(ChatColor.RED + "Your party was disbanded.");
                    }
                }
                dungeonKey.allParties.remove(party);
                return;
            } else {
                player.sendMessage(ChatColor.RED + "You need to be the host of a party to disband it!");
            }
        }
    }

    private void getParty(Player player) {
        for (Party party : dungeonKey.allParties) {
            if (party.getMembers().containsKey(player)) {
                player.sendMessage(ChatColor.GOLD + "Party Host: " + party.getHost().getDisplayName());
                for (Player p : party.getMembers().keySet()) {
                    if (p != party.getHost()) {
                        player.sendMessage(ChatColor.GOLD + "\n - " + p.getDisplayName());
                    }
                }
                return;
            }
        }
        player.sendMessage(ChatColor.RED + "You are not currently in a party.");
    }

    private void inviteToParty(Player host, String inviteeName) {
        Player invitee = dungeonKey.getServer().getPlayerExact(inviteeName);
        if (invitee == null) {
            host.sendMessage(ChatColor.RED + "Player not found.");
        } else if (host == invitee) {
            host.sendMessage(ChatColor.RED + "You can't invite yourself to a party!");
        } else {
            if (dungeonKey.allParties.size() > 0) {
                for (Party party : dungeonKey.allParties) {
                    if (party.getMembers().containsKey(invitee)) {
                        host.sendMessage(ChatColor.RED + "That player is already in a party.");
                    } else if (party.getHost() == host) {
                        party.invitedMembers.add(invitee);
                        host.sendMessage(ChatColor.GREEN + "Invited " + inviteeName + ChatColor.GREEN + " to join your party.");
                        invitee.sendMessage(ChatColor.GREEN + "You have been invited to join " +
                                host.getDisplayName() + ChatColor.GREEN +  "'s party. " +
                                ChatColor.GREEN +  "\nType /dk accept to accept the invite or /dk deny to deny the invite.");
                    } else {
                        host.sendMessage(ChatColor.RED + "You are not the host of a party.");
                    }
                }
            } else {
                host.sendMessage(ChatColor.RED + "You are not the host of a party.");
            }
        }
    }
}
