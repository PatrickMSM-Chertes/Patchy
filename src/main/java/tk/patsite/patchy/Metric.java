package tk.patsite.patchy;


import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public final class Metric {
    private final tk.patsite.patchy.Metrics metricVar;
    Plugin plugin;

    public Metric(Plugin plugin, int id) {
        this.plugin = plugin;
        metricVar = new Metrics(plugin, id);
    }

    public final void addLine(String chartId, int amount) {
        metricVar.addCustomChart(new Metrics.SingleLineChart(chartId, () -> amount));
    }

    public final void addDrilldown(String chartId, String what) {
        metricVar.addCustomChart(new Metrics.DrilldownPie(chartId, () -> {
            Map<String, Map<String, Integer>> map = new HashMap<>(1);
            Map<String, Integer> entry = new HashMap<>(1);
            entry.put(what, 1);
            map.put(what, entry);
            return map;
        }));
    }
}
