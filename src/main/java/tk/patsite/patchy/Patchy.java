package tk.patsite.patchy;

import org.bukkit.ChatColor;
import tk.patsite.patchy.checks.FirstDonkeyDupe;
import tk.patsite.patchy.checks.InvalidBookCheck;

import java.io.*;
import java.util.Objects;

public final class Patchy extends org.bukkit.plugin.java.JavaPlugin {

    final double configVersion = 1.0;

    Metric metric;
    Logfile logfile;
    UpdateChecker checker;

    public Metric getMetric() {
        return metric;
    }

    public tk.patsite.patchy.Logfile getLogfile() {
        return logfile;
    }

    public UpdateChecker getChecker() {
        return checker;
    }

    /*
    Please go to InvalidBookCheck line 62
    and complete logging for all
    info
     */


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

        if (getConfig().getDouble("v") != configVersion) {
            getLogger().info(ChatColor.RED + "Invalid Config! Refreshing default config..");
            //get the file
            File file = new File(getDataFolder(), "config.yml");

            //delete old file asynchronously

            //noinspection ResultOfMethodCallIgnored
            getServer().getScheduler().runTaskAsynchronously(this, file::delete);

            //create new file
            getServer().getScheduler().runTaskAsynchronously(this, () -> {
                try {
                    //noinspection ResultOfMethodCallIgnored
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            //set contents of file
            getServer().getScheduler().runTaskAsynchronously(this, () -> {
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
            });
        }


        getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            if (checker.checkUpdate()) {
                getLogger().info(ChatColor.DARK_RED + "Patchy is not updated!! Download new version from https://www.spigotmc.org/resources/85672/");
                logfile.warn("Patchy is not updated!! Download new version from https://www.spigotmc.org/resources/85672/");
            }
        });

        getServer().getPluginManager().registerEvents(new InvalidBookCheck(this), this);
        getServer().getPluginManager().registerEvents(new FirstDonkeyDupe(this), this);

        Objects.requireNonNull(getCommand("patchyreload")).setExecutor(new Reload(this));

        getLogger().info("Enabled Patchy v1.0");
    }

    @Override
    public void onDisable() {
        // to be safe
        saveConfig();
    }
}
