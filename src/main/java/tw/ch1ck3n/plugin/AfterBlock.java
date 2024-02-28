package tw.ch1ck3n.plugin;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class AfterBlock extends JavaPlugin implements Listener {

    private String block;

    private long block_alive_tick, item_alive_tick;

    private boolean block_drop_item, item_can_pickup;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        getServer().getPluginManager().registerEvents(this, this);

        block = config.getString("block");
        block_alive_tick = config.getLong("block_alive_tick");
        block_drop_item = config.getBoolean("block_drop_item");
        item_alive_tick = config.getLong("item_alive_tick");
        item_can_pickup = config.getBoolean("item_can_pickup");
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getBlock().getType().toString().equalsIgnoreCase(block)) {
            new BukkitRunnable() {
                public void run() {
                    if (!block_drop_item) e.getBlock().setType(Material.AIR);
                    else e.getBlock().breakNaturally();
                }
            }.runTaskLater(this, block_alive_tick);
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e) {
        if (e.getEntityType() == EntityType.DROPPED_ITEM &&
                ((Item) e.getEntity()).getItemStack().getType().name().equalsIgnoreCase(block)) {
            if (!item_can_pickup) ((Item) e.getEntity()).setPickupDelay(Integer.MAX_VALUE);
            new BukkitRunnable() {
                public void run() {
                    e.getEntity().remove();
                }
            }.runTaskLater(this, item_alive_tick);
        }
    }
}
