package main.jake.serverutils;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.atomic.AtomicInteger;

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
    public void onEntitySpawn(EntitySpawnEvent e){
        if(e.getEntity() instanceof TNTPrimed) {
            World.Environment environment = e.getEntity().getWorld().getEnvironment();
            //check if tnt is being spawned in each dimension and checks if it is blocked by config
            //if so cancel the event
            if(environment == World.Environment.NORMAL){
                if (!plguin.getConfig().getBoolean("tntOverworld")) {
                    e.setCancelled(true);
                }
            }else if(environment == World.Environment.NETHER){
                if (!plguin.getConfig().getBoolean("tntNether")) {
                    e.setCancelled(true);
                }
            }else if(environment == World.Environment.THE_END){
                if (!plguin.getConfig().getBoolean("tntEnd")) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent e){
        //Checks to see if a player has a pvp status and if they don't disable or enable pvp as a default set by config
        if(!ServerUtils.noPvp.containsKey(e.getPlayer().getName()) && !ServerUtils.pvp.containsKey(e.getPlayer().getName())){
            if(plguin.config.getBoolean("pvpDisableDefault")) {
                ServerUtils.noPvp.put(e.getPlayer().getName(), 0);
            }else{
                ServerUtils.pvp.put(e.getPlayer().getName(), 0);
            }
        }
    }


    @EventHandler
    public void playerSleepEvent(PlayerBedEnterEvent e){
        if(e.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK){
            int maxSleepPercent = plguin.getConfig().getInt("totalSleeperPercent");
            int maxToSleep = (int)Math.ceil(plguin.getServer().getOnlinePlayers().size() * ((double) maxSleepPercent / 100));
            AtomicInteger sleeping = new AtomicInteger();
            sleeping.getAndIncrement();
            plguin.getServer().getOnlinePlayers().forEach(player -> {
                if(((sleeping.get() / plguin.getServer().getOnlinePlayers().size()) * 100) >= maxSleepPercent){
                    e.getPlayer().getWorld().setTime(1000);
                    e.getPlayer().getWorld().setWeatherDuration(0);
                    e.getPlayer().getWorld().setThunderDuration(0);
                    e.getPlayer().getWorld().setThundering(false);
                    plguin.getServer().broadcastMessage("Good Morning!");
                }
            });
            plguin.getServer().broadcastMessage(String.format("%s is sleeping. (%d/%d) players to skip the night.", e.getPlayer().getName(), sleeping.get(), maxToSleep));
        }
    }


}
