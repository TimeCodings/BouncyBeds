package dev.timecoding.bouncybeds.listener;

import dev.timecoding.bouncybeds.BouncyBeds;
import dev.timecoding.bouncybeds.config.ConfigHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BouncyBedListener implements Listener {

    private BouncyBeds plugin;
    private ConfigHandler configHandler;

    public BouncyBedListener(BouncyBeds plugin){
        this.plugin = plugin;
        this.configHandler = this.plugin.getConfigHandler();
    }

    private List<Player> isadded = new ArrayList<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        Player p = e.getPlayer();
        if(configHandler.getBoolean("Enabled")){
            Block b = p.getLocation().subtract(0, 0.1, 0).getBlock();
            if(configHandler.getBoolean("FallHigh.Enabled") && configHandler.getInteger("FallHigh.InBlocks") <= p.getFallDistance() || !configHandler.getBoolean("FallHigh.Enabled")) {
                if (configHandler.getBoolean("Delay.MustLeaveBed") && !isadded.contains(p) || !configHandler.getBoolean("Delay.MustLeaveBed")) {
                    if (scheduler.containsKey(p) || e.hasChangedBlock()) {
                        isadded.add(p);
                        startScheduler(p);
                        if (b != null && isBed(b.getType())) {
                            Integer high = configHandler.getInteger("JumpHigh");
                            p.setVelocity(p.getVelocity().setY(getJumpHigh(p)));
                        }
                    }
                }
                if (!isBed(b.getType()) && b.getType() != Material.AIR) {
                    isadded.remove(p);
                    jumphigh.remove(p);
                    scheduler.remove(p);
                }
            }
        }
    }
    private HashMap<Player, BukkitTask> scheduler = new HashMap<>();
    private HashMap<Player, Double> jumphigh = new HashMap<>();
    private List<Player> indelay = new ArrayList<>();

    public void startScheduler(Player p){
        Integer perticks = configHandler.getInteger("Decrease.PerTicks");
        if(!scheduler.containsKey(p) && configHandler.getBoolean("Decrease.Enabled") && !indelay.contains(p)){
            BukkitTask task;
            double high = configHandler.getInteger("JumpHigh")*0.1;
            task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                @Override
                public void run() {
                    if(!jumphigh.containsKey(p)){
                        jumphigh.put(p, high);
                    }else{
                        Double removehigh = jumphigh.get(p);
                        jumphigh.remove(p);
                        jumphigh.put(p, (removehigh-(high*0.1)));
                        if(jumphigh.get(p) <= 0){
                            p.sendMessage("3");
                            jumphigh.remove(p);
                            runDelay(p);
                            stopScheduler(p);
                        }
                    }
                }
            }, 0, perticks);
            scheduler.put(p, task);
        }
    }

    public void runDelay(Player p){
        if(configHandler.getBoolean("Delay.Enabled") && !indelay.contains(p)) {
            indelay.add(p);
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    indelay.remove(p);
                }
            }, configHandler.getInteger("Delay.DelayInTicks"));
        }
    }

    public Double getJumpHigh(Player p){
        Integer high = configHandler.getInteger("JumpHigh");
        if(jumphigh.containsKey(p)){
            return jumphigh.get(p);
        }else{
            return (high.doubleValue()*0.1);
        }
    }

    public void stopScheduler(Player p){
        if(scheduler.containsKey(p)){
            scheduler.get(p).cancel();
            scheduler.remove(p);
        }
    }

    public boolean isBed(Material m){
        return m.name().contains("BED");
    }

}