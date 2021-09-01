package main.jake.serverutils;

import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
        JSONObject obj = new JSONObject();
        for (String player : ServerUtils.pvp.keySet()) {
            JSONObject playerObj = new JSONObject();
            playerObj.put("cooldown", ServerUtils.pvp.get(player));
            playerObj.put("status", "pvp");
            obj.put(player, playerObj);
        }
        for (String player : ServerUtils.noPvp.keySet()) {
            JSONObject playerObj = new JSONObject();
            playerObj.put("cooldown", ServerUtils.noPvp.get(player));
            playerObj.put("status", "nopvp");
            obj.put(player, playerObj);
        }
        try {
            FileWriter writer = new FileWriter(PVP_FILE);
            writer.write(JsonFormatter.prettyPrintJSON(obj.toJSONString()));
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
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(jsonString);
            for(Object name : obj.keySet()){
                JSONObject playerObj = (JSONObject) parser.parse(obj.get(name).toString());
                if(pvpEnabled && playerObj.get("status").equals("pvp")) {
                    pvpMap.put((String) name, Integer.parseInt((String) playerObj.get("cooldown")));
                }else if(!pvpEnabled && playerObj.get("status").equals("nopvp")){
                    pvpMap.put((String) name, Integer.parseInt((String) playerObj.get("cooldown")));
                }
            }
        } catch (FileNotFoundException ignored) {

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return pvpMap;
    }

}
