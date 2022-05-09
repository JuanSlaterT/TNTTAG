package cloud.docsclient.hotdoctor.plugin.tnttag.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import cloud.docsclient.hotdoctor.plugin.tnttag.structure.Arena;

public class ArenaRoundStartEvent extends Event {
	private final static HandlerList HANDLERS = new HandlerList();

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return HANDLERS;
	}

	public ArenaRoundStartEvent(Arena arena) {
		this.arena = arena;
	}

	private Arena arena;

	public Arena getArena() {
		return arena;
	}
}