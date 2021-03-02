package main.jake.serverutils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class WarpFileHandler {

    private final String fileName = "plugins/ServerUtils/warps.txt";
    public HashMap<String[], Location> warps;

    private ServerUtils plugin;

    public WarpFileHandler(ServerUtils plugin) {
        this.plugin = plugin;
        warps = plugin.warps;
    }

    public void writeToFile(String name, String owner, Location loc, World world, boolean append){
        try{
            File warpFile = new File(fileName);
            if(!warpFile.exists()){
                //warpFile.mkdir();
                warpFile.createNewFile();
            }
            FileWriter writer = new FileWriter(warpFile, append);
            if(loc == null && world == null && !append){
                writer.write("");
                writer.close();
                return;
            }
            double x = loc.getX();
            double y = loc.getY();
            double z = loc.getZ();
            float pitch = loc.getPitch();
            float yaw = loc.getYaw();
            writer.write("\n" + name + "," + owner + "," + x + "," + y + "," + z + "," + pitch + "," + yaw + "," + world.getName());
            writer.close();
        }catch (IOException ignored){

        }
    }

    public Location readFromFile(String name, Player requester){
        try{
            File warpFile = new File(fileName);
            if(!warpFile.exists()){
                warpFile.createNewFile();
                return null;
            }
            Scanner scan = new Scanner(warpFile);
            while(scan.hasNext()){
                String[] line = scan.nextLine().split(",");
                if(line.length < 2)
                    continue;
                if(name.equals(line[0]) && requester.getName().equals(line[1]) || line[1].equals("public") && name.equals(line[0])) {
                    double x = Double.parseDouble(line[2]);
                    double y = Double.parseDouble(line[3]);
                    double z = Double.parseDouble(line[4]);
                    float pitch = Float.parseFloat(line[5]);
                    float yaw = Float.parseFloat(line[6]);
                    return new Location(requester.getServer().getWorld(line[7]), x, y, z, requester.getLocation().getYaw(), requester.getLocation().getPitch());
                }
            }
        } catch (IOException ignored) {
        }

        return null;
    }

    public boolean hasWarp(String name, String requester){
        try{
            File warpFile = new File(fileName);
            if(!warpFile.exists()){
                //warpFile.mkdir();
                warpFile.createNewFile();
                return false;
            }
            Scanner scan = new Scanner(warpFile);
            while(scan.hasNext()){
                String[] line = scan.nextLine().split(",");
                if(line.length < 2)
                    continue;
                if(name.equals(line[0]) && requester.equals(line[1]) || name.equals(line[0]) && line[1].equals("public")) {
                  return true;
                }
            }
        } catch (IOException ignored) {
        }

        return false;
    }

    public List<String> getAllWarps(Player requester){
        List<String> warps = new ArrayList<>();
        try{
            File warpFile = new File(fileName);
            if(!warpFile.exists()){
                //warpFile.mkdir();
                warpFile.createNewFile();
            }
            Scanner scan = new Scanner(warpFile);
            while(scan.hasNext()){
                String[] line = scan.nextLine().split(",");
                if(line.length < 2)
                    continue;
                if(requester.getName().equals(line[1]) || line[1].equals("public")) {
                    warps.add(line[0]);
                }
            }
        } catch (IOException ignored) {
        }
        return warps;
    }

    public void removeWarp(String name){
        List<String[]> warps = new ArrayList<>();
        try{
            File warpFile = new File(fileName);
            if(!warpFile.exists()){
                //warpFile.mkdir();
                warpFile.createNewFile();
            }
            Scanner scan = new Scanner(warpFile);
            while(scan.hasNext()){
                String[] line = scan.nextLine().split(",");
                if(line.length < 2)
                    continue;
                warps.add(line);
            }
        } catch (IOException ignored) {
        }
        warps.removeIf(w -> w[0].equals(name));

        if(warps.size() == 0){
            writeToFile("", "", null, null, false);
        }

        for(String[] w : warps){
            double x = Double.parseDouble(w[2]);
            double y = Double.parseDouble(w[3]);
            double z = Double.parseDouble(w[4]);
            float pitch = Float.parseFloat(w[5]);
            float yaw = Float.parseFloat(w[6]);
            writeToFile(w[0], w[1], new Location(null, x, y, z, yaw, pitch), plugin.getServer().getWorld(w[7]),false);
        }

    }



}
