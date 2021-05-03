package main.jake.serverutils;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Events implements Listener {

    private ServerUtils plguin;

    public Events(ServerUtils plugin) {
        this.plguin = plugin;
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e){
        Entity en = e.getDamager();
        Entity p = e.getEntity();
        if(en instanceof Player && p instanceof Player){
            //Checks if either the attacker or the damage receiver has pvp disabled and if so cancels the damage
            if(ServerUtils.noPvp.containsKey(p.getName()) || ServerUtils.noPvp.containsKey(en.getName())){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent e){
        if(e.getBlock().getType() == Material.TNT) {
            World.Environment environment = e.getPlayer().getWorld().getEnvironment();
            //check if tnt is being placed in each dimension and checks if it is blocked by config
            //if so cancel the event
            if(environment == World.Environment.NORMAL){
                if (!plguin.getConfig().getBoolean("tntOverworld")) {
                    e.getPlayer().sendMessage("TNT is not enabled here");
                    e.setCancelled(true);
                }
            }else if(environment == World.Environment.NETHER){
                if (!plguin.getConfig().getBoolean("tntNether")) {
                    e.getPlayer().sendMessage("TNT is not enabled here");
                    e.setCancelled(true);
                }
            }else if(environment == World.Environment.THE_END){
                if (!plguin.getConfig().getBoolean("tntEnd")) {
                    e.getPlayer().sendMessage("TNT is not enabled here");
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent e){
        //Checks to see if a player has a pvp status and if they don't disable or enable pvp as a default set by config
        if(ServerUtils.noPvp.containsKey(e.getPlayer().getName()) && ServerUtils.pvp.containsKey(e.getPlayer().getName())){
            if(plguin.config.getBoolean("pvpDisableDefault")) {
                ServerUtils.noPvp.put(e.getPlayer().getName(), 0);
            }else{
                ServerUtils.pvp.put(e.getPlayer().getName(), 0);
            }
        }
    }

    int sleepers = 0;

    @EventHandler
    public void playerSleepEvent(PlayerBedEnterEvent e){
        sleepers++;
        if(e.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
            int sleepPercent = (sleepers / plguin.getServer().getOnlinePlayers().size()) * 100;
            if ((sleepers / plguin.getServer().getOnlinePlayers().size()) * 100 >= plguin.getConfig().getInt("totalSleeperPercent")) {
                e.getPlayer().getWorld().setTime(1000);
                e.getPlayer().getWorld().setWeatherDuration(0);
                e.getPlayer().getWorld().setThunderDuration(0);
                e.getPlayer().getWorld().setThundering(false);
                plguin.getServer().broadcastMessage("Good Morning!");
                sleepers = 0;
            } else {
                e.getPlayer().getServer().broadcastMessage(e.getPlayer().getName() + " is sleeping. (" + sleepPercent + "%/" + plguin.getConfig().getInt("totalSleeperPercent") + "%)");
            }
        }
    }

    @EventHandler
    public void playerAwakeEvent(PlayerBedLeaveEvent e){
        if(e.getPlayer().getWorld().getTime() >= 13000) {
            sleepers--;
            int sleepPercent = (sleepers / plguin.getServer().getOnlinePlayers().size()) * 100;
            e.getPlayer().getServer().broadcastMessage(e.getPlayer().getName() + " woke up. (" + sleepPercent + "%/" + plguin.getConfig().getInt("totalSleeperPercent") + "%)");
        }
    }

}
