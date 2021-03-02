package main.jake.serverutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class PvPFileHandler {

    public void write(){
        File file = new File("plugins/ServerUtils/pvp.txt");
        File f = new File("plugins/ServerUtils/nopvp.txt");
        try {
            file.createNewFile();
            f.createNewFile();
            FileWriter writer = new FileWriter("plugins/ServerUtils/pvp.txt");
            for(String s : ServerUtils.pvp.keySet()){
                writer.write(s + "," + ServerUtils.pvp.get(s) + "\n");
            }
            writer.close();
            FileWriter w = new FileWriter("plugins/ServerUtils/nopvp.txt");
            for(String s : ServerUtils.noPvp.keySet()){
                w.write(s + "," + ServerUtils.noPvp.get(s));
            }
            w.close();
        } catch (IOException ignored) {
        }
    }

    public HashMap<String, Integer> readNoPvP(){
        HashMap<String, Integer> map = new LinkedHashMap<>();
        try {
            Scanner sc = new Scanner(new File("plugins/ServerUtils/nopvp.txt"));
            while(sc.hasNextLine()){
                String data = sc.nextLine();
                map.put(data.split(",")[0], Integer.parseInt(data.split(",")[1]));
            }
        } catch (FileNotFoundException ignored) {

        }
        return map;
    }

    public HashMap<String, Integer> readPvP(){
        HashMap<String, Integer> map = new LinkedHashMap<>();
        try {
            Scanner sc = new Scanner(new File("plugins/ServerUtils/pvp.txt"));
            while(sc.hasNextLine()){
                String data = sc.nextLine();
                map.put(data.split(",")[0], Integer.parseInt(data.split(",")[1]));
            }
        } catch (FileNotFoundException ignored) {

        }
        return map;
    }

}
