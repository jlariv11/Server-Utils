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
            if(ServerUtils.noPvp.containsKey(p.getName()) || ServerUtils.noPvp.containsKey(en.getName())){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent e){
        if(e.getBlock().getType() == Material.TNT) {
            World.Environment environment = e.getPlayer().getWorld().getEnvironment();
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

}
