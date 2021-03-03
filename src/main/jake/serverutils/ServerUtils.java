package main.jake.serverutils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class ServerUtils extends JavaPlugin {

    public static HashMap<String, Integer> pvp = new LinkedHashMap<>();
    public static HashMap<String, Integer> noPvp = new LinkedHashMap<>();

    public HashMap<String[], Location> warps = new HashMap<>();

    PvPFileHandler pvpFiles = new PvPFileHandler();
    FileConfiguration config = this.getConfig();


    public void onEnable(){

        File dir = new File("plugins/ServerUtils");
        if(!dir.exists()){
            if(!dir.mkdir()){
                getServer().getConsoleSender().sendMessage("ServerUtils: Unable to create plugin directory");
            }
        }
        config.addDefault("pvpCooldownTimer", 1800);//60s*30m
        config.options().copyDefaults(true);
        saveConfig();

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Server Utils has been enabled");
        getServer().getPluginManager().registerEvents(new Events(), this);
        Commands commands = new Commands(this);

        for(String cmd : commands.commands){
            getCommand(cmd).setExecutor(commands);
        }

        noPvp = pvpFiles.readNoPvP();
        pvp = pvpFiles.readPvP();

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for(String p : pvp.keySet()){
                    pvp.replace(p, pvp.get(p) - 1);
                    if(pvp.get(p) < 0)
                        pvp.replace(p, 0);
                }
                for(String p : noPvp.keySet()){
                    noPvp.replace(p, noPvp.get(p) - 1);
                    if(noPvp.get(p) < 0)
                        noPvp.replace(p, 0);
                }
            }
        }, 0, 20);
    }

    public void onDisable(){
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "Server Utils has been disabled");
        pvpFiles.write();

    }




}
