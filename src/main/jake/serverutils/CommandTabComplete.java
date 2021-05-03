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

        switch(command.getName()) {
            case "pvp":
                args.add("enable");
                args.add("disable");
                args.add("list");
                break;
            case "warp":
                for (Warp warp : ServerUtils.warps) {
                    if (warp.isAvailable(commandSender.getName())) {
                        args.add(warp.getName());
                    }
                }
                break;
            case "delwarp":
                for (Warp warp : ServerUtils.warps) {
                    if (warp.isOwned(commandSender.getName()) || warp.isPublic()) {
                        args.add(warp.getName());
                    }
                }
                break;
            case "sharewarp":
                if (strings.length == 1) {
                    args.add("add");
                    args.add("remove");
                } else if (strings.length == 2) {
                    for (Warp warp : ServerUtils.warps) {
                        if (warp.isOwned(commandSender.getName())) {
                            args.add(warp.getName());
                        }
                    }
                }else if(strings.length > 3){
                    if(strings[2].equals("add")) {
                        args.add("public");
                        for (Player p : commandSender.getServer().getOnlinePlayers()) {
                            args.add(p.getName());
                        }
                    }else if(strings[2].equals("remove")){
                        Warp warp = getWarpFromName(strings[3]);
                        if(warp != null && warp.isOwned(commandSender.getName())){
                            args.addAll(warp.getAccessables());
                        }
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

    private Warp getWarpFromName(String name) {
        for (Warp warp : ServerUtils.warps) {
            if (warp.getName().equals(name)) {
                return warp;
            }
        }
        return null;
    }
}
