package cloud.docsclient.hotdoctor.plugin.tnttag.managers;

import java.util.HashMap;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import cloud.docsclient.hotdoctor.plugin.tnttag.Main;
import cloud.docsclient.hotdoctor.plugin.tnttag.exceptions.PlayerStatusException;
import cloud.docsclient.hotdoctor.plugin.tnttag.structure.Arena;
import cloud.docsclient.hotdoctor.plugin.tnttag.utils.TNTPlayer;

public class PlayerManager {

	public PlayerManager(Main plugin) {
		this.plugin = plugin;
	}

	private Main plugin;
	private HashMap<TNTPlayer, Arena> playingArena = new HashMap<>();

	public void setPlaying(TNTPlayer player, Arena arena) {
		if (arena == null && playingArena.containsKey(player)) {
			playingArena.remove(player);
		} else if (!playingArena.containsKey(player) && arena != null) {
			playingArena.put(player, arena);
		} else {
			throw new PlayerStatusException("Not able to update Playing Status.");
		}
	}
	
	public Arena getPlayingArena(TNTPlayer player) {
		if(isPlaying(player)) {
			return playingArena.get(player);
		}
		return null;
	}

	public boolean isPlaying(TNTPlayer player) {
		return playingArena.containsKey(player);
	}

	public TNTPlayer getTNTPlayer(Player player) {
		return plugin.getStorageManager().load(player);
	}

	public TNTPlayer getTNTPlayer(OfflinePlayer player) {
		return plugin.getStorageManager().load(player);
	}
}
