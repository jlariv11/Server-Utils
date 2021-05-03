package main.jake.serverutils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.jline.internal.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class WarpFileHandler {

    private final File warpFile = new File("plugins/ServerUtils/warps.txt");

    private ServerUtils plugin;

    public WarpFileHandler(ServerUtils plugin) {
        this.plugin = plugin;
    }

    //Writes the warp name, owner and location x, y, z to a file
    public void writeToFile(){
        try{
            if(!warpFile.exists()){
                warpFile.createNewFile();
            }
            FileWriter writer = new FileWriter(warpFile);
            for(Warp warp : ServerUtils.warps) {
                double x = warp.getLoc().getX();
                double y = warp.getLoc().getY();
                double z = warp.getLoc().getZ();
                writer.write("\n" + warp.getName() + "," + warp.getAccessables() + "," + x + "," + y + "," + z + "," + warp.getWorld().getName());
            }
            writer.close();
        }catch (IOException e){
            plugin.getServer().getConsoleSender().sendMessage("Failed to create warp file");
        }
    }

    //Read the warp file and add warp name, owner, and location to a list of the Warp class
    public List<Warp> readFromFile(){
        List<Warp> warpList = new ArrayList<>();
        try{
            Scanner scan = new Scanner(warpFile);
            while(scan.hasNext()){
                String l = scan.nextLine();
                String[] line = l.split(",");
                if(line.length < 2)
                    continue;
                List<String> names = new ArrayList<>();
                for(int i = 1; i < line.length; i++){
                    if(line[i].contains("]")){
                        String ln = line[i].replace("]", "").replace("[", "");
                        ln = ln.replace(" ", "");
                        names.add(ln);
                        break;
                    }
                    names.add(line[i].replace("[", "").replace(" ", ""));
                }
                double x = Double.parseDouble(line[names.size() + 1]);
                double y = Double.parseDouble(line[names.size() + 2]);
                double z = Double.parseDouble(line[names.size() + 3]);
                warpList.add(new Warp(line[0], names, new Location(plugin.getServer().getWorld(line[names.size() + 4]), x, y, z)));
            }
        } catch (FileNotFoundException ignored) {
            //If there is no file, no warps were saved so just return an empty list
        }

        return warpList;
    }



}
