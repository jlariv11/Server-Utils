package main.jake.serverutils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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

    public final String[] commands = {pvp, setWarp, warp, list, delWarp, xpShare, tpa};
    public HashMap<Player, Player> tpaList = new HashMap<>();
    private final ServerUtils plugin;
    private final WarpFileHandler fileHandler;
    private final int pvpCooldown;

    public Commands(ServerUtils plugin) {
        this.plugin = plugin;
        this.fileHandler = new WarpFileHandler(plugin);
        pvpCooldown = plugin.getConfig().getInt("pvpCooldownTimer");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        switch (command.getName()) {
            case pvp:
                if (args.length < 1) {
                    return false;
                } else {
                    if (args[0].equals("enable") || args[0].equals("disable")) {
                        Player player = null;
                        if (args.length == 2) {
                            player = plugin.getServer().getPlayer(args[1]);
                            if (player == null) {
                                commandSender.sendMessage("Player does not exist");
                                return true;
                            }
                            if (!commandSender.isOp()) {
                                commandSender.sendMessage("You can not change the pvp status of another player");
                                return true;
                            }
                        }
                        if (commandSender instanceof Player && player == null) {
                            player = (Player) commandSender;
                        }
                        if (player == null) {
                            commandSender.sendMessage("An error occurred issuing this command");
                            return true;
                        }
                        if (args[0].equals("enable")) {
                            if (ServerUtils.noPvp.containsKey(player.getName()) && ServerUtils.noPvp.get(player.getName()) > 0) {
                                commandSender.sendMessage("Cannot enable PvP must wait " + ((ServerUtils.noPvp.get(player.getName()) / 60) + " minutes"));
                                return true;
                            }

                            ServerUtils.pvp.put(player.getName(), pvpCooldown);
                            ServerUtils.noPvp.remove(player.getName());
                            commandSender.sendMessage("PvP Enabled!");
                        } else if (args[0].equals("disable")) {
                            if (ServerUtils.pvp.containsKey(player.getName()) && ServerUtils.pvp.get(player.getName()) > 0) {
                                commandSender.sendMessage("Cannot disable PvP must wait " + ((ServerUtils.pvp.get(player.getName()) / 60) + " minutes"));
                                return true;
                            }

                            ServerUtils.noPvp.put(player.getName(), pvpCooldown);
                            ServerUtils.pvp.remove(player.getName());
                            commandSender.sendMessage("PvP Disabled!");
                        }
                        return true;
                    } else if (args[0].equals("list")) {
                        commandSender.sendMessage(ChatColor.RED + "PvP enabled players: ");
                        for (String player : ServerUtils.pvp.keySet()) {
                            commandSender.sendMessage(player);
                        }
                        commandSender.sendMessage(ChatColor.GREEN + "PvP disabled players: ");
                        for (String player : ServerUtils.noPvp.keySet()) {
                            commandSender.sendMessage(player);
                        }
                        return true;
                    }
                }
            case setWarp:
                if (args.length == 0) {
                    return false;
                }
                if (commandSender instanceof Player) {
                    String name = "";
                    if (args.length == 1) {
                        name = commandSender.getName();
                    } else if (args.length == 2 && args[1].equals("true")) {
                        name = "public";
                    }
                    if (getWarpFromName(args[0]) == null) {
                        commandSender.sendMessage("Warp " + args[0] + " set!");
                        ServerUtils.warps.add(new Warp(args[0], name, ((Player) commandSender).getLocation()));
                    } else {
                        commandSender.sendMessage("This warp already exists");
                    }
                } else {
                    commandSender.sendMessage("Only players can use this command");
                }
                return true;
            case warp:
                if (commandSender instanceof Player) {
                    if (args.length == 1) {
                        Warp warp = getWarpFromName(args[0]);
                        if (warp != null) {
                            if (warp.getOwner().equals("public") || warp.getOwner().equals(commandSender.getName())) {
                                Location toTeleport = warp.getLoc();
                                toTeleport.setPitch(((Player) commandSender).getLocation().getPitch());
                                toTeleport.setYaw(((Player) commandSender).getLocation().getYaw());
                                ((Player) commandSender).teleport(toTeleport);
                            }
                        } else {
                            commandSender.sendMessage("Warp does not exist!");
                        }
                    } else {
                        return false;
                    }

                }
                return true;
            case list:
                if (commandSender instanceof Player) {
                    if (ServerUtils.warps.size() == 0) {
                        commandSender.sendMessage("No warps available");
                    }
                    for (Warp warp : ServerUtils.warps) {
                        commandSender.sendMessage(warp.getName());
                    }
                } else {
                    commandSender.sendMessage("Only player can use this command");
                }
                return true;
            case delWarp:
                if (commandSender instanceof Player) {
                    if (args.length == 1) {
                        Warp warp = getWarpFromName(args[0]);
                        if (warp != null) {
                            if (warp.getOwner().equals("public") || warp.getOwner().equals(commandSender.getName()))
                                ServerUtils.warps.remove(warp);
                            commandSender.sendMessage("Warp " + args[0] + " deleted!");
                        } else {
                            commandSender.sendMessage("Warp does not exist!");
                        }
                    } else {
                        return false;
                    }
                }
                return true;
            case xpShare:
                if (commandSender instanceof Player) {
                    if (args.length == 2) {
                        Player receiver = commandSender.getServer().getPlayer(args[0]);
                        if (receiver != null) {
                            try {
                                int amount = Integer.parseInt(args[1]);
                                if (((Player) commandSender).getLevel() >= amount) {
                                    ((Player) commandSender).setLevel(((Player) commandSender).getLevel() - amount);
                                    receiver.setLevel(receiver.getLevel() + amount);
                                    commandSender.sendMessage("Experience Sent");
                                    receiver.sendMessage("Experience Received");
                                } else {
                                    commandSender.sendMessage("Insufficient Experience");
                                }
                                return true;
                            } catch (NumberFormatException e) {
                                commandSender.sendMessage("Enter a valid integer");
                                return true;
                            }
                        }
                    }
                }
                return false;
            case tpa:
                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage("Only a player may use this command");
                    return true;
                }
                if (args.length == 0)
                    return false;
                Player target = commandSender.getServer().getPlayer(args[0]);
                if (target != null) {
                    target.sendMessage(commandSender.getName() + " wants to teleport to you. /tpa confirm or /tpa deny");
                    if (tpaList.replace(target, (Player) commandSender) == null) {
                        tpaList.put(target, (Player) commandSender);
                    }
                } else if (args[0].equals("confirm")) {
                    if (tpaList.containsKey(commandSender)) {
                        tpaList.get(commandSender).teleport(((Player) commandSender).getLocation());
                        tpaList.remove(commandSender);
                        commandSender.sendMessage("Request confirmed!");
                    } else {
                        commandSender.sendMessage("You don't have any active tpa requests!");
                    }
                } else if (args[0].equals("deny")) {
                    if (tpaList.containsKey(commandSender)) {
                        tpaList.remove(commandSender);
                        commandSender.sendMessage("Request denied!");
                    } else {
                        commandSender.sendMessage("You don't have any active tpa requests!");
                    }
                } else {
                    return false;
                }
                return true;
            case "test":
                return true;
        }
        return true;
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
