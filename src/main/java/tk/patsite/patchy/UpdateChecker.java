package tk.patsite.patchy;

import org.bukkit.Bukkit;
import org.bukkit.util.Consumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public final class UpdateChecker {

    private final int resourceId;
    Patchy plugin;

    public UpdateChecker(Patchy plugin, int resourceId) {
        this.resourceId = resourceId;
        this.plugin = plugin;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                plugin.getLogfile().info("Cannot look for updates: " + exception.getMessage());
            }
        });
    }

    public boolean checkUpdate() {
        AtomicBoolean ret = new AtomicBoolean(false);
        getVersion(ver -> {
            if (plugin.getDescription().getVersion().equalsIgnoreCase(ver)) {
                ret.set(true);
            }
        });
        return ret.get();
    }
}