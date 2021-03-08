package main.jake.serverutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class PvPFileHandler {

    private final File PVP_FILE = new File("plugins/ServerUtils/pvp.txt");
    private final File NO_PVP_FILE = new File("plugins/ServerUtils/nopvp.txt");

    public void write(){
        //Writes the names of players and their switch cooldown to their corresponding pvp status to files
        try {
            PVP_FILE.createNewFile();
            NO_PVP_FILE.createNewFile();
            FileWriter writer = new FileWriter(PVP_FILE);
            for(String s : ServerUtils.pvp.keySet()){
                writer.write(s + "," + ServerUtils.pvp.get(s) + "\n");
            }
            writer.close();
            FileWriter w = new FileWriter(NO_PVP_FILE);
            for(String s : ServerUtils.noPvp.keySet()){
                w.write(s + "," + ServerUtils.noPvp.get(s));
            }
            w.close();
        } catch (IOException ignored) {
        }
    }

    //Reads the no pvp file and puts the player name and cooldown into a HashMap
    public HashMap<String, Integer> readNoPvP(){
        HashMap<String, Integer> map = new LinkedHashMap<>();
        try {
            Scanner sc = new Scanner(NO_PVP_FILE);
            while(sc.hasNextLine()){
                String data = sc.nextLine();
                map.put(data.split(",")[0], Integer.parseInt(data.split(",")[1]));
            }
        } catch (FileNotFoundException ignored) {
            //If the file does not exist then there were no entries into the no pvp status
        }
        return map;
    }

    //Reads the pvp file and puts the player name and cooldown into a HashMap
    public HashMap<String, Integer> readPvP(){
        HashMap<String, Integer> map = new LinkedHashMap<>();
        try {
            Scanner sc = new Scanner(PVP_FILE);
            while(sc.hasNextLine()){
                String data = sc.nextLine();
                map.put(data.split(",")[0], Integer.parseInt(data.split(",")[1]));
            }
        } catch (FileNotFoundException ignored) {
            //If the file does not exist then there were no entries into the pvp status
        }
        return map;
    }

}
