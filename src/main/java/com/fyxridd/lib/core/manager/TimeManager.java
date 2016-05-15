package com.fyxridd.lib.core.manager;

import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.api.event.TimeEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class TimeManager implements Listener{
	private class Check implements Runnable {
		private long pre;
		private int sum;//毫秒
		public void run() {
			long now = System.currentTimeMillis();
			if (pre == 0) pre = now;
			int past = (int) (now - pre);
			pre = now;
			sum += past;
			if (sum >= 1000) {
				sum = 0;
				//发出事件
				Bukkit.getPluginManager().callEvent(new TimeEvent());
			}
		}
	}

	public TimeManager() {
        //计时器
		Bukkit.getScheduler().scheduleSyncRepeatingTask(CorePlugin.instance, new Check(), 1, 1);
	}
}
