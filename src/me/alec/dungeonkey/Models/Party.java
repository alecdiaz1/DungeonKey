package me.alec.dungeonkey.Models;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class Party  {
    // TODO: make variables private, use only getters/setters
    public Player host;
    public String dungeonName;
    public HashMap<Player, Boolean> members;
    public ArrayList<Player> invitedMembers;

    public Party(Player player) {
        this.host = player;
        this.members = new HashMap<>();
        this.invitedMembers = new ArrayList<>();
        members.put(player, false);
    }

    public Player getHost() {
        return this.host;
    }

    public void setHost(Player host) {
        this.host = host;
    }

    public String getDungeonName() {
        return this.dungeonName;
    }

    public void setDungeonName(String dungeonName) {
        this.dungeonName = dungeonName;
    }

    public HashMap<Player, Boolean> getMembers() {
        return this.members;
    }

    public void setMembers(HashMap<Player, Boolean> members) {
        this.members = members;
    }

    public ArrayList<Player> getInvitedMembers() {
        return this.invitedMembers;
    }

    public void setInvitedMembers(ArrayList<Player> invitedMembers) {
        this.invitedMembers = invitedMembers;
    }
}
