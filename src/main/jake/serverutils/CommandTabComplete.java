package main.jake.serverutils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandTabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {

        List<String> args = new ArrayList<>();

        switch(command.getName()){
            case "pvp":
                args.add("enable");
                args.add("disable");
                args.add("list");
                break;
            case "warp":
            case "delwarp":
                for(Warp warp : ServerUtils.warps){
                    if(warp.isAvailable(commandSender.getName())){
                        args.add(warp.getName());
                    }
                }
                break;
            case "xpshare":
                for(Player p : commandSender.getServer().getOnlinePlayers()){
                    args.add(p.getName());
                }
                break;
            case "tpa":
                args.add("confirm");
                args.add("deny");
                for(Player p : commandSender.getServer().getOnlinePlayers()){
                    args.add(p.getName());
                }
                break;
        }


        return args;
    }
}
