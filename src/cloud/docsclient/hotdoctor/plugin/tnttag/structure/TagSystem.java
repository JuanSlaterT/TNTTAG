package cloud.docsclient.hotdoctor.plugin.tnttag.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cloud.docsclient.hotdoctor.plugin.tnttag.utils.TNTPlayer;

public class TagSystem {

	public TagSystem(Arena arena) {
		this.arena = arena;
	}

	private Arena arena;

	private List<TNTPlayer> taggers = new ArrayList<>();

	public boolean isTagger(TNTPlayer player) {
		return taggers.contains(player);
	}
	
	public List<TNTPlayer> getCurrentTaggers(){
		return taggers;
	}
	
	public void clear() {
		taggers.clear();
	}

	public List<TNTPlayer> getTaggersRandomly(int numberOfPlayers) {
		taggers = new ArrayList<>();
		List<TNTPlayer> alive = arena.getPlayingPlayers();
		Random random = new Random();
		for(int i = 0 ; i<numberOfPlayers; i++) {
			taggers.add(alive.get(random.nextInt(alive.size())));
		}
		return taggers;
	}
	
	

}
