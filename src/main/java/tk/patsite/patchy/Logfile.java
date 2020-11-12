package tk.patsite.patchy;

import org.bukkit.plugin.Plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings({"unused"})
public final class Logfile {
    final File file;
    final File dataFolder;
    final Plugin plug;

    public Logfile(Plugin plugin, String name) {
        dataFolder = plugin.getDataFolder();
        plug = plugin;
        file = new File(plugin.getDataFolder(), name);

        //generate file
        if (!file.exists()) {
            async(() -> {
                try {
                    //noinspection ResultOfMethodCallIgnored
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void print(String string) {
        async(() -> {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
                bw.append(string).append("\r\n");
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void info(String string) {
        StringBuilder ret = new StringBuilder();

        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        ret.append("(");
        ret.append(date.format(LocalDateTime.now()));
        ret.append(")");
        ret.append(" [INFO] ");
        ret.append(string);

        print(ret.toString());

    }

    public void warn(String string) {
        StringBuilder ret = new StringBuilder();

        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        ret.append("(");
        ret.append(date.format(LocalDateTime.now()));
        ret.append(")");
        ret.append(" [!WARNING!] ");
        ret.append(string);

        print(ret.toString());
    }

    public void error(String string) {
        StringBuilder ret = new StringBuilder();

        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        ret.append("(");
        ret.append(date.format(LocalDateTime.now()));
        ret.append(")");
        ret.append(" -----[!ERROR!]----- ");
        ret.append(string);

        print(ret.toString());
    }

    public void important(String string) {
        StringBuilder ret = new StringBuilder();

        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        ret.append("(");
        ret.append(date.format(LocalDateTime.now()));
        ret.append(")");
        ret.append(" -----[IMPORTANT]----- ");
        ret.append(string);

        print(ret.toString());
    }


    public void clean() {
        //noinspection ResultOfMethodCallIgnored
        async(file::delete);
        async(() -> {
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void async(Runnable lambda) {
        plug.getServer().getScheduler().runTaskAsynchronously(plug, lambda);
    }
}
