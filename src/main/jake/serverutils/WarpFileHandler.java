package main.jake.serverutils;


import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static main.jake.serverutils.ServerUtils.warps;

public class WarpFileHandler {

    private final File warpFile = new File("plugins/ServerUtils/warps.json");
    private final File legacyWarpFile = new File("plugins/ServerUtils/warps.txt");

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
            for(Warp warp : warps) {
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
            Scanner scan = new Scanner(legacyWarpFile);
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
        legacyWarpFile.delete();
        return warpList;
    }


    @SuppressWarnings("unchecked")
    public void writeJsonFile(){
        JSONObject tst = new JSONObject();
        for(Warp w : warps){
            JSONObject obj = new JSONObject();
            obj.put("owner", w.getAccessables().get(0));
            JSONArray arr = new JSONArray();
            arr.addAll(w.getAccessables());
            obj.put("access", arr);
            obj.put("x", w.getLoc().getX());
            obj.put("y", w.getLoc().getY());
            obj.put("z", w.getLoc().getZ());
            obj.put("world", w.getLoc().getWorld().getName());
            tst.put(w.getName(), obj);
        }
        try{
            FileWriter writer = new FileWriter(warpFile, false);

            writer.write(JsonFormatter.prettyPrintJSON(tst.toJSONString()));
            writer.close();
        }catch (IOException ignored){

        }
    }

    public List<Warp> readJsonFile(){
        List<Warp> warps = new ArrayList<>();
        if(legacyWarpFile.exists()){
            return readFromFile();
        }

        try {
            Scanner scanner = new Scanner(warpFile);
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNext()){
                sb.append(scanner.nextLine().replace(" ", ""));
            }
            String jsonString = sb.toString();
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(jsonString);
            for(Object name : obj.keySet()){
                JSONObject obj2 = (JSONObject) parser.parse(obj.get(name).toString());
                Warp warp = new Warp((String)name, (List<String>) obj2.get("access"),
                        new Location(plugin.getServer().getWorld((String) obj2.get("world")), (double)obj2.get("x"), (double)obj2.get("y"), (double)obj2.get("z")));
                warps.add(warp);
            }


        } catch (FileNotFoundException ignored) {

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return warps;
    }




}
