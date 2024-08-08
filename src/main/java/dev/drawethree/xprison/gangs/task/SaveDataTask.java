package dev.drawethree.xprison.gangs.task;

import dev.drawethree.xprison.gangs.XPrisonGangs;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Task;

import java.util.concurrent.TimeUnit;

public final class SaveDataTask implements Runnable {

    private final XPrisonGangs plugin;
    private Task task;

    public SaveDataTask(XPrisonGangs plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.plugin.getGangsManager().saveDataOnDisable();
    }

    public void start() {
        stop();
        final int interval = this.plugin.getConfig().getSaveDataInterval();
        if (interval < 1) {
            return;
        }
        this.task = Schedulers.async().runRepeating(this, 30, TimeUnit.SECONDS, interval, TimeUnit.MINUTES);
    }

    public void stop() {
        if (task != null) {
            task.stop();
        }
    }
}
