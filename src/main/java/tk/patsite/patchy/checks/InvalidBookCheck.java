package tk.patsite.patchy.checks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import tk.patsite.patchy.Patchy;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class InvalidBookCheck implements org.bukkit.event.Listener {
    List<String> strEmpty = new ArrayList<>(1);

    {
        strEmpty.add("");
    }

    Patchy plugin;

    public InvalidBookCheck(Patchy plugin) {
        this.plugin = plugin;
    }


    private BookMeta patch(BookMeta metaData, final Player player) {
        int maxPages = plugin.getConfig().getInt("maxpages", 50);
        //check for max book pages
        if (metaData.getPageCount() > maxPages) {
            player.sendMessage(ChatColor.RED + " You can not write more than " + maxPages + ". Book text cut off to " + maxPages + " pages.");

            //reset pages to max
            metaData.setPages(metaData.getPages().subList(0, maxPages));

            //add to metrics limited

            plugin.getMetric().addLine("action_limited", 1);
            plugin.getMetric().addDrilldown("patched_type", "Invalid Book / MaxPage");

            plugin.getLogfile().info("Player " + player + " failed to save a book with over " + maxPages + " pages.");
            plugin.getLogger().info(ChatColor.RED + "Player " + player + " failed to save a book with over " + maxPages + " pages.");


        }

        //check if book contains weird character used to fill data
        int pages = metaData.getPageCount();
        //loop through pages
        for (int i = 1; i <= pages; i++) {
            String page = metaData.getPage(i);
            //check
            if (incorrectCharset(page)) {
                //incorrect charset
                //clear pages
                metaData.setPages(strEmpty);

                //send message
                player.sendMessage(ChatColor.RED + "That book has an invalid format. Removing pages.");

                //add to metrics
                plugin.getMetric().addLine("action_limited", 1);
                plugin.getMetric().addDrilldown("patched_type", "Invalid Book / Invalid Charset");
                //log
                //!!

                plugin.getLogfile().info("Player " + player + " failed to use an invalid character in a book.");
                plugin.getLogger().info(ChatColor.RED + "Player " + player + " failed to use an invalid character in a book.");
            }
        }
        return metaData;
    }


    @EventHandler
    public void patchBookMove(InventoryMoveItemEvent e) {
        ItemStack item = e.getItem();
        if (item.getType() != Material.WRITTEN_BOOK)
            return;
        if (e.getSource().getViewers().get(0) == null)
            return;
        if (item.getItemMeta() == null)
            return;

        item.setItemMeta(patch((BookMeta) item.getItemMeta(), (Player) e.getSource().getViewers().get(0)));
    }


    @EventHandler
    public void patchBookSign(PlayerEditBookEvent e) {
        e.setNewBookMeta(patch(e.getNewBookMeta(), e.getPlayer()));
    }

    private Boolean incorrectCharset(String text) {
        return !StandardCharsets.ISO_8859_1.newEncoder().canEncode(text);
    }
}
