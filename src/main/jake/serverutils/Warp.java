package main.jake.serverutils;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Arrays;
import java.util.List;

public class Warp {

    private String name;
    private List<String> accessibles;
    private Location loc;
    private World world;

    public Warp(String name, List<String> accessibles, Location loc) {
        this.name = name;
        this.accessibles = accessibles;
        this.loc = loc;
        this.world = loc.getWorld();
    }

    public String getName() {
        return name;
    }

    public List<String> getAccessables() {
        return accessibles;
    }

    public Location getLoc() {
        return loc;
    }

    public World getWorld() {
        return world;
    }

    public boolean isPublic(){
        return accessibles.get(0).equals("public");
    }

    public boolean isAvailable(String playerName){
        return isPublic() || hasAccess(playerName);
    }

    public boolean isOwned(String playerName){
        return getAccessables().get(0).equals(playerName);
    }

    public boolean hasAccess(String playerName){
        for(String s : getAccessables()){
            if(s.equals(playerName)){
                return true;
            }
        }
        return false;
    }

}
