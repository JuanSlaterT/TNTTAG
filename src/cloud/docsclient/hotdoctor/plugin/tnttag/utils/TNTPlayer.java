package cloud.docsclient.hotdoctor.plugin.tnttag.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import cloud.docsclient.hotdoctor.plugin.tnttag.Main;

public class TNTPlayer {

	private List<Player> hiddenPlayers = new ArrayList<>();

	public TNTPlayer(OfflinePlayer player) {
		this.player = player;
	}

	public TNTPlayer(UUID uuid) {
		this(Bukkit.getOfflinePlayer(uuid));
	}

	public TNTPlayer(Player player) {
		this(player.getUniqueId());
	}

	private OfflinePlayer player;
	private int wins = 0;
	private int gamesPlayed = 0;

	public OfflinePlayer getOfflinePlayer() {
		return player;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(player.getUniqueId());
	}

	public void hidePlayer(Main plugin, Player player)  {
		String v = NMSVersion.getVersion();
		if (v.equals("1.8") || v.equals("1.9") || v.equals("1.10") || v.equals("1.11") || v.equals("1.12")) {
			if(!hiddenPlayers.contains(player)) {
				this.getPlayer().hidePlayer(player);
			}
		}else {
			if(!hiddenPlayers.contains(player)) {
				this.getPlayer().hidePlayer(plugin, player);
			}
		}
	}
	
	public void showPlayer(Main plugin, Player player) {
		String v = NMSVersion.getVersion();
		if (v.equals("1.8") || v.equals("1.9") || v.equals("1.10") || v.equals("1.11") || v.equals("1.12")) {
			if(hiddenPlayers.contains(player)) {
				this.getPlayer().showPlayer(player);
			}
		}else {
			if(hiddenPlayers.contains(player)) {
				this.getPlayer().showPlayer(plugin, player);
			}
		}
	}
	
	public List<Player> getHiddenPlayers(){
		return hiddenPlayers;
	}

	public int getWins() {
		return wins;
	}

	public int getGamesPlayed() {
		return gamesPlayed;
	}

	public void setGamesPlayed(int gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

}
