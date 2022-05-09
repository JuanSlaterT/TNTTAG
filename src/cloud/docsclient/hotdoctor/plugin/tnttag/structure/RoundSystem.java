package cloud.docsclient.hotdoctor.plugin.tnttag.structure;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import cloud.docsclient.hotdoctor.plugin.tnttag.events.ArenaRoundFinishEvent;
import cloud.docsclient.hotdoctor.plugin.tnttag.events.ArenaRoundStartEvent;

public class RoundSystem {
	private int round = 1;
	private int roundTime;
	private int initialRoundTime;

	public RoundSystem(Arena arena, int roundTime) {
		initialRoundTime = roundTime;
		this.roundTime = roundTime;
		this.arena = arena;
	}

	private Arena arena;

	public int getRoundTime() {
		return roundTime;
	}

	private BukkitTask task;

	public void start() {
		if(task == null) {
			int toRest = round * 5;
			int rest = initialRoundTime - toRest;
			roundTime = round == 1 ? initialRoundTime : rest < 15 ? 15 : rest;
			ArenaRoundStartEvent e = new ArenaRoundStartEvent(arena);
			Bukkit.getPluginManager().callEvent(e);
			task = Bukkit.getScheduler().runTaskTimer(arena.getPlugin(), () -> {
				roundTime--;
				if (roundTime == 0) {
					ArenaRoundFinishEvent event = new ArenaRoundFinishEvent(arena);
					Bukkit.getPluginManager().callEvent(event);
					pause();
				}
			}, 20, 20);
		}

	}
	protected void pause() {
		if (task != null) {
			task.cancel();
			task = null;
		}
		round++;
	}
	protected void resetAll() {
		pause();
		roundTime = initialRoundTime;
		round = 1;
	}
}
