package tk.patsite.patchy;

import org.bukkit.ChatColor;
import tk.patsite.patchy.checks.FirstDonkeyDupe;
import tk.patsite.patchy.checks.InvalidBookCheck;

import java.io.*;
import java.util.Objects;

public final class Patchy extends org.bukkit.plugin.java.JavaPlugin {

    private Metric metric;
    private Logfile logfile;
    private UpdateChecker checker;

    public Metric getMetric() {
        return metric;
    }

    public tk.patsite.patchy.Logfile getLogfile() {
        return logfile;
    }

    public UpdateChecker getChecker() {
        return checker;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        metric = new Metric(this, 9304);
        logfile = new Logfile(this, "log.txt");
        checker = new UpdateChecker(this, 85672);

        logfile.info("Plugin started up!");

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        // check if outdated config

        double configVersion = 1.2;
        if (getConfig().getDouble("v") != configVersion) {
            getLogger().info(ChatColor.RED + "Invalid Config! Refreshing default config..");
            //get the file
            File file = new File(getDataFolder(), "config.yml");

            //delete old file asynchronously

            //noinspection ResultOfMethodCallIgnored
            file.delete();

            //create new file
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //set contents of file
            try {
                InputStream initialStream = getResource("config.yml");
                if (initialStream == null)
                    return;
                byte[] buffer = new byte[initialStream.available()];
                //noinspection ResultOfMethodCallIgnored
                initialStream.read(buffer);

                OutputStream outStream = new FileOutputStream(file);
                outStream.write(buffer);

                outStream.close();
                initialStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            reloadConfig();
        }

        getServer().getPluginManager().registerEvents(new InvalidBookCheck(this), this);
        getServer().getPluginManager().registerEvents(new FirstDonkeyDupe(this), this);

        Objects.requireNonNull(getCommand("patchyreload")).setExecutor(new Reload(this));

        getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            if (checker.checkUpdate()) {
                getLogger().info(ChatColor.DARK_RED + "Patchy is not updated!! Download new version from https://www.spigotmc.org/resources/85672/");
                logfile.warn("Patchy is not updated!! Download new version from https://www.spigotmc.org/resources/85672/");
            }
        });

        getLogger().info("Enabled Patchy v1.0");
    }

    @Override
    public void onDisable() {
        // to be safe
        saveConfig();
    }
}
