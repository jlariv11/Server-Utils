package main.jake.serverutils;

import org.bukkit.Location;
import org.bukkit.World;

public class Warp {

    private String name;
    private String owner;
    private Location loc;
    private World world;

    public Warp(String name, String owner, Location loc) {
        this.name = name;
        this.owner = owner;
        this.loc = loc;
        this.world = loc.getWorld();
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public Location getLoc() {
        return loc;
    }

    public World getWorld() {
        return world;
    }

    public boolean isPublic(){
        return owner.equals("public");
    }

    public boolean isAvailable(String playerName){
        return isPublic() || isOwned(playerName);
    }

    public boolean isOwned(String playerName){
        return getOwner().equals(playerName);
    }

}
