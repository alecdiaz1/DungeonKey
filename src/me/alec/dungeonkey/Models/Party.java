package me.alec.dungeonkey.Models;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Party  {
    public Player host;
    public String dungeonName;
    public ArrayList<Player> members;
    public ArrayList<Player> invitedMembers;

    public Party(Player player) {
        this.host = player;
        this.members = new ArrayList<>();
        this.invitedMembers = new ArrayList<>();
        members.add(player);
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

    public ArrayList<Player> getMembers() {
        return this.members;
    }

    public void setMembers(ArrayList<Player> members) {
        this.members = members;
    }

    public ArrayList<Player> getInvitedMembers() {
        return this.invitedMembers;
    }

    public void setInvitedMembers(ArrayList<Player> invitedMembers) {
        this.invitedMembers = invitedMembers;
    }
}
