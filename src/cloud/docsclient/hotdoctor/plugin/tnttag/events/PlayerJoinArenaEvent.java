package cloud.docsclient.hotdoctor.plugin.tnttag.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import cloud.docsclient.hotdoctor.plugin.tnttag.structure.Arena;
import cloud.docsclient.hotdoctor.plugin.tnttag.utils.TNTPlayer;

public class PlayerJoinArenaEvent extends Event implements Cancellable {
	private final static HandlerList HANDLERS = new HandlerList();
	private boolean cancel = false;
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return HANDLERS;
	}

	public PlayerJoinArenaEvent(TNTPlayer player, Arena arena) {
		this.arena = arena;
		this.player = player;
	}

	private Arena arena;
	private TNTPlayer player;

	public TNTPlayer getSpectatorPlayer() {
		return player;
	}

	public Arena getArena() {
		return arena;
	}

	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
		
	}

}
