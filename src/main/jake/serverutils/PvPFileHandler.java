package main.jake.serverutils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

public class PvPFileHandler {

    private final File PVP_FILE = new File("plugins/ServerUtils/pvp.json");

    @SuppressWarnings("unchecked")
    public void write() {
        JsonObject obj = new JsonObject();
        //JSONObject obj = new JSONObject();
        for (String player : ServerUtils.pvp.keySet()) {
            //JSONObject playerObj = new JSONObject();
            JsonObject playerObj = new JsonObject();
            playerObj.addProperty("cooldown", ServerUtils.pvp.get(player));
            playerObj.addProperty("status", "pvp");
            obj.add(player, playerObj);
        }
        for (String player : ServerUtils.noPvp.keySet()) {
            JsonObject playerObj = new JsonObject();
            playerObj.addProperty("cooldown", ServerUtils.noPvp.get(player));
            playerObj.addProperty("status", "nopvp");
            obj.add(player, playerObj);
        }
        try (FileWriter writer = new FileWriter(PVP_FILE)){
            if(!obj.isJsonNull()) {
                writer.write(JsonFormatter.prettyPrintJSON(obj.toString()));
            }else{
                System.out.println("Is Json Null");
            }

        } catch (IOException ignored) {
        }
    }


    //Reads the no pvp file and puts the player name and cooldown into a HashMap
    public HashMap<String, Integer> readPvP(boolean pvpEnabled){
        HashMap<String, Integer> pvpMap = new HashMap<>();
        try {
            Scanner scanner = new Scanner(PVP_FILE);
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNext()){
                sb.append(scanner.nextLine().replace(" ", ""));
            }
            String jsonString = sb.toString();
            if(jsonString.isEmpty()){
                return pvpMap;
            }
            JsonObject obj = JsonParser.parseString(jsonString).getAsJsonObject();
            for(String name : obj.keySet()){
                JsonObject playerObj = obj.getAsJsonObject(name);
                if(pvpEnabled && playerObj.get("status").getAsString().equals("pvp")) {
                    pvpMap.put(name, playerObj.get("cooldown").getAsInt());
                }else if(!pvpEnabled && playerObj.get("status").getAsString().equals("nopvp")){
                    pvpMap.put(name, playerObj.get("cooldown").getAsInt());
                }
            }
            scanner.close();
        } catch (FileNotFoundException ignored) {

        }
        return pvpMap;
    }

}
