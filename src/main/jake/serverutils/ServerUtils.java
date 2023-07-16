package main.jake.serverutils;


import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class ServerUtils extends JavaPlugin {

    public static HashMap<String, Integer> pvp = new LinkedHashMap<>();
    public static HashMap<String, Integer> noPvp = new LinkedHashMap<>();

    public static List<Warp> warps = new ArrayList<>();

    PvPFileHandler pvpFiles = new PvPFileHandler();
    WarpFileHandler warpFile = new WarpFileHandler(this);
    FileConfiguration config = this.getConfig();


    @Override
    public void onEnable(){

        File dir = new File("plugins/ServerUtils");
        if(!dir.exists()){
            if(!dir.mkdir()){
                getServer().getConsoleSender().sendMessage("ServerUtils: Unable to create plugin directory");
            }
        }

        //Config Settings

        config.addDefault("pvpCooldownTimer", 1800);//60s*30m
        config.addDefault("pvpDisableDefault", true);
        config.addDefault("tntOverworld", false);
        config.addDefault("tntNether", true);
        config.addDefault("tntEnd", true);
        config.addDefault("totalSleeperPercent", 50);
        config.options().copyDefaults(true);
        saveConfig();




        getServer().getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "Server Utils has been enabled");
        getServer().getPluginManager().registerEvents(new Events(this), this);
        Commands commands = new Commands(this);

        //Command Registry
        for(String cmd : commands.commands){
            PluginCommand command = getCommand(cmd);
            if(command != null){
                command.setExecutor(commands);
                command.setTabCompleter(new CommandTabComplete());
            }
        }

        //Read files for saved data
        noPvp = pvpFiles.readPvP(false);
        pvp = pvpFiles.readPvP(true);
        warps = warpFile.readJsonFile();


        //Controls the cooldown for switching pvp status counts down 1 per second
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

        //Save data to files
        pvpFiles.write();
        warpFile.writeJsonFile();

    }




}
