package tk.patsite.patchy.checks;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;
import tk.patsite.patchy.Patchy;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class InvalidBookCheck implements org.bukkit.event.Listener {
    List<String> strEmpty = new ArrayList<>();

    Patchy plugin;

    public InvalidBookCheck(Patchy plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void patchBookSign(PlayerEditBookEvent e) {
        BookMeta metaData = e.getNewBookMeta();

        int maxPages = plugin.getConfig().getInt("maxpages", 50);
        //check for max book pages
        if (metaData.getPageCount() > maxPages) {
            e.getPlayer().sendMessage(ChatColor.RED + " You can not write more than " + maxPages + ". Book text cut off to " + maxPages + " pages.");

            //reset pages to max
            List<String> pageList = new ArrayList<>();

            for (int i = 1; i <= maxPages; i++) {
                pageList.add(metaData.getPage(i));
            }

            metaData.setPages(pageList);

            //add to metrics limited

            plugin.getMetric().addLine("action_limited", 1);
            plugin.getMetric().addDrilldown("patched_type", "Invalid Book / MaxPage");

            e.setNewBookMeta(metaData);

            plugin.getLogfile().info("Player " + e.getPlayer().getName() + " failed to save a book with over " + maxPages + " pages.");
            plugin.getLogger().info(ChatColor.RED + "Player " + e.getPlayer().getName() + " failed to save a book with over " + maxPages + " pages.");


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
                BookMeta meta = e.getNewBookMeta();
                meta.setPages(strEmpty);
                e.setNewBookMeta(meta);

                //send message
                e.getPlayer().sendMessage(ChatColor.RED + "That book has an invalid format. Removing pages.");

                //add to metrics
                plugin.getMetric().addLine("action_limited", 1);
                plugin.getMetric().addDrilldown("patched_type", "Invalid Book / Invalid Charset");
                //log
                //!!

                plugin.getLogfile().info("Player " + e.getPlayer().getName() + " failed to use an invalid character in a book.");
                plugin.getLogger().info(ChatColor.RED + "Player " + e.getPlayer().getName() + " failed to use an invalid character in a book.");
            }
        }
    }

    private Boolean incorrectCharset(String text) {
        return !StandardCharsets.ISO_8859_1.newEncoder().canEncode(text);
    }
}
