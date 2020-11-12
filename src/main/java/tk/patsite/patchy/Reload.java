package tk.patsite.patchy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public final class Reload implements org.bukkit.command.CommandExecutor {

    Patchy plugin;

    public Reload(Patchy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("patchy.reload") || sender.isOp()) {
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.AQUA + "Reloaded config!");
            plugin.getLogfile().important("Player " + sender.getName() + " reloaded plugin.");
            plugin.getLogger().info(ChatColor.AQUA + "Player " + sender.getName() + " reloaded plugin.");
            if (plugin.getChecker().checkUpdate()) {
                plugin.getLogger().warning(ChatColor.DARK_RED + "Patchy is not updated!! Download new version from https://www.spigotmc.org/resources/85672/");
                plugin.getLogfile().warn("Patchy is not updated!! Download new version from https://www.spigotmc.org/resources/85672/");
            }
        } else {
            sender.sendMessage("You do not have permission!");
        }
        return true;
    }
}
