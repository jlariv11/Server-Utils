package main.jake.serverutils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class WarpFileHandler {

    private final String fileName = "plugins/ServerUtils/warps.txt";

    private ServerUtils plugin;

    public WarpFileHandler(ServerUtils plugin) {
        this.plugin = plugin;
    }

    public void writeToFile(){
        try{
            File warpFile = new File(fileName);
            if(!warpFile.exists()){
                //warpFile.mkdir();
                warpFile.createNewFile();
            }
            FileWriter writer = new FileWriter(warpFile);
            for(Warp warp : ServerUtils.warps) {
//                if (warp.loc == null && warp.world == null && !append) {
//                    writer.write("");
//                    writer.close();
//                    return;
//                }
                double x = warp.getLoc().getX();
                double y = warp.getLoc().getY();
                double z = warp.getLoc().getZ();
                float pitch = warp.getLoc().getPitch();
                float yaw = warp.getLoc().getYaw();
                writer.write("\n" + warp.getName() + "," + warp.getOwner() + "," + x + "," + y + "," + z + "," + pitch + "," + yaw + "," + warp.getWorld().getName());
            }
            writer.close();
        }catch (IOException ignored){

        }
    }

    public List<Warp> readFromFile(){
        List<Warp> warpList = new ArrayList<>();
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
                double x = Double.parseDouble(line[2]);
                double y = Double.parseDouble(line[3]);
                double z = Double.parseDouble(line[4]);
                float pitch = Float.parseFloat(line[5]);
                float yaw = Float.parseFloat(line[6]);
                warpList.add(new Warp(line[0], line[1], new Location(plugin.getServer().getWorld(line[7]), x, y, z, yaw, pitch)));
            }
        } catch (IOException ignored) {
        }

        return warpList;
    }



}
