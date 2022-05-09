package cloud.docsclient.hotdoctor.plugin.tnttag.managers;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import cloud.docsclient.hotdoctor.plugin.tnttag.Main;
import cloud.docsclient.hotdoctor.plugin.tnttag.information.management.Database;
import cloud.docsclient.hotdoctor.plugin.tnttag.utils.TNTPlayer;

public class StorageManager {
	private Main plugin;
	private Database storage;

	private HashMap<UUID, TNTPlayer> loadedPlayers = new HashMap<>();

	public StorageManager(Database storage, Main plugin) {
		this.plugin = plugin;
		this.storage = storage;
		autoSaver();
	}
	
	
	public boolean isLoaded(OfflinePlayer player) {
		return loadedPlayers.containsKey(player.getUniqueId());
	}
	
	public boolean isLoaded(Player player) {
		return loadedPlayers.containsKey(player.getUniqueId());
	}

	public TNTPlayer load(Player player) {
		return this.load(Bukkit.getOfflinePlayer(player.getUniqueId()));
	}

	public TNTPlayer load(OfflinePlayer player) {
		TNTPlayer TNT = isLoaded(player) ? loadedPlayers.get(player.getUniqueId()) : storage.load(player.getUniqueId());
		if(isLoaded(player)) loadedPlayers.put(player.getUniqueId(), TNT);
		return TNT;
	}

	public void save(Player player) {
		storage.save(player.getUniqueId());
	}

	public void save(OfflinePlayer player) {
		storage.save(player.getUniqueId());
	}
	private BukkitTask task;
	private void autoSaver() {
		task = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
			int ticks = 2;
			for(Player player : Bukkit.getOnlinePlayers()) {
				if(this.isLoaded(player)) {
					Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, ()->{
						this.save(player);
					}, ticks);
					ticks +=2;
				}
				
			}
		}, 6000, 6000);
	}
	
	public void disable() {
		task.cancel();
	}

}
