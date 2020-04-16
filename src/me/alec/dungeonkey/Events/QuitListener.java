package me.alec.dungeonkey.Events;

import me.alec.dungeonkey.DungeonKey;
import me.alec.dungeonkey.Models.Party;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {
    private final DungeonKey dungeonKey;

    public QuitListener(DungeonKey dungeonKey) {
        this.dungeonKey = dungeonKey;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (Party party : dungeonKey.allParties) {
            if (party.getMembers().containsKey(player)) {
                party.members.remove(player);
                for (Player p : party.getMembers().keySet()) {
                    p.sendMessage(player.getDisplayName() + ChatColor.RED + " disconnected and left the party.");
                }

                if (party.getMembers().size() < 1) {
                    dungeonKey.allParties.remove(party);
                }
            }
        }
    }
}
