package cloud.docsclient.hotdoctor.plugin.tnttag.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import cloud.docsclient.hotdoctor.plugin.tnttag.structure.Arena;
import cloud.docsclient.hotdoctor.plugin.tnttag.utils.TNTPlayer;

public class PlayerQuitArenaEvent extends Event {
	private final static HandlerList HANDLERS = new HandlerList();

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return HANDLERS;
	}

	public PlayerQuitArenaEvent(TNTPlayer player, Arena arena) {
		this.arena = arena;
		this.player = player;
	}

	private Arena arena;
	private TNTPlayer player;

	public TNTPlayer getTNTPlayer() {
		return player;
	}

	public Arena getArena() {
		return arena;
	}
}
