package tk.patsite.patchy.checks;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalExitEvent;
import tk.patsite.patchy.Patchy;

import java.util.HashMap;
import java.util.UUID;

public final class FirstDonkeyDupe implements Listener {

    Patchy plugin;

    private final HashMap<UUID, Integer> susDonkeys = new HashMap<>();

    public FirstDonkeyDupe(Patchy plugin) {
        this.plugin = plugin;
    }


    private String buildLocString(Location loc) {
        return new StringBuilder()
                .append("X:").append(loc.getX())
                .append(" Y:").append(loc.getY())
                .append(" Z:").append(loc.getZ())
                .append(" World:").append(loc.getWorld().getName())
                .toString();
    }

    private void sendEvent(Location loc) {
        //add to metrics limited
        plugin.getMetric().addLine("exploit_limited", 1);

        plugin.getMetric().addDrilldown("patched_type", "Donkey / Mule dupe 1");

        plugin.getLogfile().info("Someone at " + buildLocString(loc) + " failed to do a donkey dupe type 1.");
        plugin.getLogger().info(ChatColor.RED + "Someone at " + buildLocString(loc) + " failed to do a donkey dupe type 1.");

    }


    @EventHandler
    public void DonkeyDupePatcherEnterOne(EntityPortalEnterEvent e) {
        Entity ent = e.getEntity();

        if (ent.getType() == EntityType.DONKEY || ent.getType() == EntityType.MULE || ent.getType() == EntityType.LLAMA) {
            susDonkeys.putIfAbsent(ent.getUniqueId(), plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> susDonkeys.remove(ent.getUniqueId()), plugin.getConfig().getInt("removeDonkeyChecksAfter", 60) * 60 * 20L));
        }
    }

    @EventHandler
    public void DonkeyDupePatcherExitOne(EntityPortalExitEvent e) {
        Entity ent = e.getEntity();

        if (ent.getType() == EntityType.DONKEY || ent.getType() == EntityType.MULE || ent.getType() == EntityType.LLAMA) {
            susDonkeys.putIfAbsent(ent.getUniqueId(), plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> susDonkeys.remove(ent.getUniqueId()), plugin.getConfig().getInt("removeDonkeyChecksAfter", 60) * 60 * 20L));
        }
    }

    @EventHandler
    public void DonkeyDupePatcherOne(EntityDeathEvent e) {
        Entity ent = e.getEntity();

        if (!(ent.getType() == EntityType.DONKEY || ent.getType() == EntityType.MULE || ent.getType() == EntityType.LLAMA))
            return;

        if (susDonkeys.containsKey(ent.getUniqueId())) {
            ent.remove();
            sendEvent(ent.getLocation());
            susDonkeys.remove(ent.getUniqueId());
        }
    }
}
