package cloud.docsclient.hotdoctor.plugin.tnttag.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import cloud.docsclient.hotdoctor.plugin.tnttag.structure.Arena;
import cloud.docsclient.hotdoctor.plugin.tnttag.utils.GameStatus;

public class ArenaChangeStatusEvent extends Event {
	private final static HandlerList HANDLERS = new HandlerList();

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return HANDLERS;
	}
	
	public Arena arena;
	public GameStatus from;
	public GameStatus to;
	
	public ArenaChangeStatusEvent(Arena arena, GameStatus from, GameStatus to) {
		this.arena = arena;
		this.to = to;
		this.from = from;
	}
	
	public Arena getArena() {
		return arena;
	}
	
	public GameStatus getFromGameStatus() {
		return from;
	}
	
	public GameStatus getToGameStatus() {
		return to;
	}
}
