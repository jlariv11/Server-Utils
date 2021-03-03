package main.jake.serverutils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;

public class Commands implements CommandExecutor, Listener {

    public final String pvp = "pvp";
    public final String setWarp = "setwarp";
    public final String warp = "warp";
    public final String list = "warps";
    public final String delWarp = "delwarp";
    public final String xpShare = "xpshare";
    public final String tpa = "tpa";

    public final String[] commands = {pvp, setWarp, warp, list, delWarp, xpShare, "test", tpa};

    private ServerUtils plugin;
    private WarpFileHandler fileHandler;
    private int pvpCooldown;

    public HashMap<Player, Player> tpaList = new HashMap<>();

    public Commands(ServerUtils plugin) {
        this.plugin = plugin;
        this.fileHandler = new WarpFileHandler(plugin);
        pvpCooldown = plugin.getConfig().getInt("pvpCooldownTimer");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        switch (command.getName()){
            case pvp:
                if(args.length < 1){
                    commandSender.sendMessage("Invalid Syntax: Usage: pvp enable/disable [player]");
                    return false;
                }else{
                   if(args[0].equals("enable")){
                       if(args.length == 2){
                           Player player = commandSender.getServer().getPlayer(args[1]);
                           if(player == null){
                               commandSender.sendMessage("Invalid Syntax: Player does not exist");
                               return false;
                           }
                           if(!commandSender.isOp()){
                               commandSender.sendMessage("You can not change the pvp status of another player");
                               return true;
                           }

                           if(ServerUtils.noPvp.containsKey(player.getName()) && ServerUtils.noPvp.get(player.getName()) > 0){
                               commandSender.sendMessage("Cannot enable PvP must wait " + ((ServerUtils.noPvp.get(player.getName()) / 60) + " minutes"));
                               return true;
                           }

                           ServerUtils.pvp.put(player.getName(), pvpCooldown);
                           ServerUtils.noPvp.remove(player.getName());
                           commandSender.sendMessage("PvP Enabled!");
                           return true;

                       }else{
                           if(commandSender instanceof Player) {
                               if(ServerUtils.noPvp.containsKey(commandSender.getName()) && ServerUtils.noPvp.get(commandSender.getName()) > 0){
                                   commandSender.sendMessage("Cannot enable PvP must wait " + ((ServerUtils.noPvp.get(commandSender.getName()) / 60) + " minutes"));
                                   return true;
                               }

                               ServerUtils.pvp.put(commandSender.getName(), pvpCooldown);
                               ServerUtils.noPvp.remove(commandSender.getName());
                               commandSender.sendMessage("PvP Enabled!");
                               return true;
                           }else{
                               commandSender.sendMessage("Invalid Syntax: No player specified");
                               return false;
                           }
                       }
                   }else if(args[0].equals("disable")){
                       if(args.length == 2){
                           Player player = commandSender.getServer().getPlayer(args[1]);
                           if(player == null){
                               commandSender.sendMessage("Invalid Syntax: Player does not exist");
                               return false;
                           }
                           if(!commandSender.isOp()){
                               commandSender.sendMessage("You can not change the pvp status of another player");
                               return true;
                           }
                           if(ServerUtils.pvp.containsKey(player.getName()) && ServerUtils.pvp.get(player.getName()) > 0){
                               commandSender.sendMessage("Cannot disable PvP must wait " + ((ServerUtils.pvp.get(player.getName()) / 60) + " minutes"));
                               return true;
                           }

                           ServerUtils.noPvp.put(player.getName(), pvpCooldown);
                           ServerUtils.pvp.remove(player.getName());
                           commandSender.sendMessage("PvP Disabled!");
                           return true;
                       }else{
                           if(commandSender instanceof Player) {
                               if(ServerUtils.pvp.containsKey(commandSender.getName()) && ServerUtils.pvp.get(commandSender.getName()) > 0){
                                   commandSender.sendMessage("Cannot disable PvP must wait " + ((ServerUtils.pvp.get(commandSender.getName()) / 60) + " minutes"));
                                   return true;
                               }

                               ServerUtils.noPvp.put(commandSender.getName(), pvpCooldown);
                               ServerUtils.pvp.remove(commandSender.getName());
                               commandSender.sendMessage("PvP Disabled!");
                               return true;
                           }else{
                               commandSender.sendMessage("Invalid Syntax: No player specified");
                               return false;
                           }
                       }
                   }
                }
            case setWarp:
                if(commandSender instanceof Player){
                    if(args.length == 1){
                        if(!fileHandler.hasWarp(args[0], commandSender.getName())) {
                            commandSender.sendMessage("Warp " + args[0] + " set!");
                            fileHandler.writeToFile(args[0], commandSender.getName(), ((Player) commandSender).getLocation(), ((Player) commandSender).getWorld(), true);
                            return true;
                        }
                    }else if(args.length == 2 && args[1].equals("true")){
                        if(!fileHandler.hasWarp(args[0], "public")){
                            commandSender.sendMessage("Public warp " + args[0] + " set!");
                            fileHandler.writeToFile(args[0], "public", ((Player) commandSender).getLocation(), ((Player) commandSender).getWorld(), true);
                            return true;
                        }
                    }
                }
                return false;
            case warp:
                if(commandSender instanceof Player){
                    if(args.length == 1){
                        if(fileHandler.hasWarp(args[0], commandSender.getName())){
                            ((Player) commandSender).teleport(fileHandler.readFromFile(args[0], (Player) commandSender));
                            return true;
                        }else if(fileHandler.hasWarp("public", commandSender.getName())){
                            ((Player) commandSender).teleport(fileHandler.readFromFile(args[0], (Player) commandSender));
                            return true;
                        }else{
                            commandSender.sendMessage("Warp does not exist!");
                            return true;
                        }
                    }

                }
                return false;
            case list:
                if(commandSender instanceof Player){
                    if(fileHandler.getAllWarps((Player) commandSender).size() == 0){
                        commandSender.sendMessage("No warps available");
                        return true;
                    }
                    for(String w : fileHandler.getAllWarps((Player) commandSender)){
                     commandSender.sendMessage(w);
                    }
                    return true;
                }
            case delWarp:
                if(commandSender instanceof Player){
                    if(args.length == 1){
                        if(fileHandler.hasWarp(args[0], commandSender.getName())){
                            fileHandler.removeWarp(args[0]);
                            commandSender.sendMessage("Warp " + args[0] + " deleted!");
                            return true;
                        }
                    }
                }
            case xpShare:
                if(commandSender instanceof Player){
                    if(args.length == 2){
                        Player receiver = commandSender.getServer().getPlayer(args[0]);
                        if(receiver != null){
                            try{
                                int amount = Integer.parseInt(args[1]);
                                if(((Player) commandSender).getLevel() >= amount){
                                    ((Player) commandSender).setLevel(((Player) commandSender).getLevel() - amount);
                                    receiver.setLevel(receiver.getLevel() + amount);
                                    commandSender.sendMessage("Experience Sent");
                                    receiver.sendMessage("Experience Received");

                                }else{
                                    commandSender.sendMessage("Insufficient Experience");
                                }
                                return true;
                            }catch (NumberFormatException e){
                                commandSender.sendMessage("Enter a valid integer");
                                return false;
                            }
                        }
                    }
                }
                return false;
            case tpa:
                if(!(commandSender instanceof Player)) {
                    commandSender.sendMessage("Only a player may use this command");
                    return false;
                }
                if(args.length == 0)
                    return false;
                Player target = commandSender.getServer().getPlayer(args[0]);
                if(target != null){
                    target.sendMessage(commandSender.getName() + " wants to teleport to you. /tpa confirm or /tpa deny");
                    if(tpaList.replace(target, (Player) commandSender) == null){
                        tpaList.put(target, (Player) commandSender);
                    }
                }else if(args[0].equals("confirm")){
                    if(tpaList.containsKey(commandSender)){
                        tpaList.get(commandSender).teleport(((Player) commandSender).getLocation());
                        tpaList.remove(commandSender);
                        commandSender.sendMessage("Request confirmed!");
                    }else{
                        commandSender.sendMessage("You don't have any active tpa requests!");
                    }
                }else if(args[0].equals("deny")) {
                    if (tpaList.containsKey(commandSender)) {
                        tpaList.remove(commandSender);
                        commandSender.sendMessage("Request denied!");
                    } else {
                        commandSender.sendMessage("You don't have any active tpa requests!");
                    }
                }else{
                    return false;
                }
                return true;
            case "test":
                return true;
        }
        return true;
    }
}
