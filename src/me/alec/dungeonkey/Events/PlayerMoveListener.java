package me.alec.dungeonkey.Events;

import me.alec.dungeonkey.DungeonKey;
import me.alec.dungeonkey.Models.Party;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
    private final DungeonKey dungeonKey;

    public PlayerMoveListener(DungeonKey dungeonKey) {
        this.dungeonKey = dungeonKey;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        for (Party party : dungeonKey.tasks.keySet()) {
            if (party.getMembers().containsKey(player) && !party.inDungeon) {
                dungeonKey.tasks.get(party).cancel();
                dungeonKey.tasks.remove(party);
                for (Player p : party.getMembers().keySet()) {
                    p.sendMessage(ChatColor.RED + "Teleport cancelled, a party member moved.");
                }
            }
        }
    }
}
