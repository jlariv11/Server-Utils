package main.jake.serverutils;

import net.minecraft.server.v1_16_R3.BlockSign;
import net.minecraft.server.v1_16_R3.Blocks;
import net.minecraft.server.v1_16_R3.TileEntitySign;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Events implements Listener {

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

}
