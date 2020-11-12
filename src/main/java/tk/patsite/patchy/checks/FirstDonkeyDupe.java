package tk.patsite.patchy.checks;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalExitEvent;
import tk.patsite.patchy.Patchy;

public final class FirstDonkeyDupe implements Listener {

    Patchy plugin;

    public FirstDonkeyDupe(Patchy plugin) {
        this.plugin = plugin;
    }

    private void sendEvent(Location loc) {
        //add to metrics limited
        plugin.getMetric().addLine("exploit_limited", 1);

        plugin.getMetric().addDrilldown("patched_type", "Donkey / Mule dupe 1");

        plugin.getLogfile().info("Someone at " + loc.toString() + "failed to kill a donkey/mule right after teleported through nether portal.");
        plugin.getLogger().info(ChatColor.RED + "Someone at " + loc.toString() + "failed to kill a donkey/mule right after teleported through nether portal.");

    }

    @EventHandler
    public void DonkeyDupePatcherEnterOne(EntityPortalEnterEvent e) {
        //if the entity is dead
        Entity ent = e.getEntity();
        if (!ent.isDead())
            return;

        if (ent.getType() == EntityType.DONKEY || ent.getType() == EntityType.MULE) {
            ent.remove();
            sendEvent(ent.getLocation());
        }
    }

    @EventHandler
    public void DonkeyDupePatcherExitOne(EntityPortalExitEvent e) {
        //if the entity is dead
        Entity ent = e.getEntity();
        if (!ent.isDead())
            return;

        if (ent.getType() == EntityType.DONKEY || ent.getType() == EntityType.MULE) {
            ent.remove();
            sendEvent(ent.getLocation());
        }
    }
}
