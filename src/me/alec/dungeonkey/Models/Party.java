package me.alec.dungeonkey.Models;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Party  {
    public Player host;
    public String dungeonName;
    public ArrayList<Player> members;

    public Party(Player player) {
        this.host = player;
    }

    public Player getHost() {
        return host;
    }

    public void setHost(Player host) {
        this.host = host;
    }

    public String getDungeonName() {
        return dungeonName;
    }

    public void setDungeonName(String dungeonName) {
        this.dungeonName = dungeonName;
    }

    public ArrayList<Player> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<Player> members) {
        this.members = members;
    }
}
