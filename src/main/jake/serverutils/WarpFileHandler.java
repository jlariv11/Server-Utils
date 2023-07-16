package main.jake.serverutils;




import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Location;

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
        JsonObject tst = new JsonObject();
        //JSONObject tst = new JSONObject();
        for(Warp w : warps){
            JsonObject obj = new JsonObject();
            //JSONObject obj = new JSONObject();
            obj.addProperty("owner", w.getAccessables().get(0));
            JsonArray arr = new JsonArray();
            //JSONArray arr = new JSONArray();
            //arr.addAll(w.getAccessables());
            for(String access : w.getAccessables()){
                arr.add(access);
            }
            obj.add("access", arr);
            obj.addProperty("x", w.getLoc().getX());
            obj.addProperty("y", w.getLoc().getY());
            obj.addProperty("z", w.getLoc().getZ());
            obj.addProperty("world", w.getLoc().getWorld().getName());
            tst.add(w.getName(), obj);
        }
        try{
            FileWriter writer = new FileWriter(warpFile, false);
            if(!tst.isJsonNull()) {
                writer.write(JsonFormatter.prettyPrintJSON(tst.toString()));
            }else{
                System.out.println("Warp is Json Null");
            }
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
            JsonObject obj = JsonParser.parseString(jsonString).getAsJsonObject();
            for(String name : obj.keySet()){
                JsonObject obj2 = obj.getAsJsonObject(name);
                Warp warp = new Warp(name, obj2.get("access").getAsJsonArray().iterator(),
                        new Location(plugin.getServer().getWorld(obj2.get("world").getAsString()), obj2.get("x").getAsDouble(), obj2.get("y").getAsDouble(), obj2.get("z").getAsDouble()));
                warps.add(warp);
            }


        } catch (FileNotFoundException ignored) {

        }

        return warps;
    }




}
