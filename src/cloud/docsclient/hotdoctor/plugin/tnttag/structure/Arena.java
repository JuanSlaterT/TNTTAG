package cloud.docsclient.hotdoctor.plugin.tnttag.structure;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import cloud.docsclient.hotdoctor.plugin.tnttag.Main;
import cloud.docsclient.hotdoctor.plugin.tnttag.events.ArenaChangeStatusEvent;
import cloud.docsclient.hotdoctor.plugin.tnttag.exceptions.ArenaException;
import cloud.docsclient.hotdoctor.plugin.tnttag.utils.GameStatus;
import cloud.docsclient.hotdoctor.plugin.tnttag.utils.TNTPlayer;

public class Arena {

    private Location prelobby;
    private Location spawn;
    private int max_players;
    private int min_players;
    private int initialLobbyCountdown;

    private GameStatus status = GameStatus.WAITING;

    private RoundSystem roundSystem;
    private TagSystem tagSystem;
    private String mapName;

    public Arena(Main plugin, String mapName, Location prelobby, Location spawn, int max_players, int min_players,
            int roundSeconds, int lobbycountdown) throws ArenaException {
        if (min_players <= 1 || max_players <= 1) {
            throw new ArenaException("Arena min/max players can not be less than or equals to 1.");
        }
        if (roundSeconds % 5 != 0) {
            throw new ArenaException("Arena RoundTime must be multiply of 5.");
        }
        this.prelobby = prelobby;
        this.spawn = spawn;
        this.min_players = min_players;
        this.max_players = max_players;
        this.plugin = plugin;
        initialLobbyCountdown = lobbycountdown;
        roundSystem = new RoundSystem(this, roundSeconds);
        tagSystem = new TagSystem(this);
        this.mapName = mapName;
    }

    private Main plugin;

    protected Main getPlugin() {
        return plugin;
    }

    private List<TNTPlayer> living = new ArrayList<>();
    private List<TNTPlayer> spectating = new ArrayList<>();

    public GameStatus getGameStatus() {
        return status;
    }

    public void changeGameStatus(GameStatus to) {
        GameStatus from = status;
        ArenaChangeStatusEvent event = new ArenaChangeStatusEvent(this, status, from);
        status = to;
        Bukkit.getPluginManager().callEvent(event);
        if ((from == GameStatus.WAITING || from == GameStatus.STARTING) && to == GameStatus.INGAME) {
            living.forEach((player) -> player.getPlayer().teleport(this.getSpawnArena()));
            roundSystem.start();
        } else if (from == GameStatus.INGAME && to == GameStatus.ENDING) {
            roundSystem.resetAll();
        }
    }

    public RoundSystem getRoundSystem() {
        return roundSystem;
    }

    public TagSystem getTagSystem() {
        return tagSystem;
    }

    public List<TNTPlayer> getPlayingPlayers() {
        return living;
    }

    public List<TNTPlayer> getSpectatingPlayers() {
        return spectating;
    }

    public Location getSpawnArena() {
        return spawn;
    }

    public Location getPreLobbyArena() {
        return prelobby;
    }

    public int getMinPlayers() {
        return min_players;
    }

    public int getMaxPlayers() {
        return max_players;
    }

    private BukkitTask ID;
    private int lobbyCountdown;

    public void startCountdown() {
        lobbyCountdown = initialLobbyCountdown;
        Sound finishing = Sound
                .valueOf(plugin.sendMessageWithPlaceholdersOf(null, "arena.sounds.starting.finishing_countdown"));
        Sound start = Sound.valueOf(plugin.sendMessageWithPlaceholdersOf(null, "arena.sounds.starting.start"));
        this.getPlayingPlayers().forEach((players) -> {
            Player player = players.getPlayer();

            player.sendMessage(plugin.sendMessageWithPlaceholdersOf(player, "arena.messages.starting"));
            if (start != null) {
                player.playSound(player.getLocation(), start, 2.0f, 2.0f);
            }
        });
        ID = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
            if (status == GameStatus.STARTING) {
                lobbyCountdown--;
                this.getPlayingPlayers().forEach((players) -> {
                    Player player = players.getPlayer();
                    player.sendMessage(plugin.sendMessageWithPlaceholdersOf(player, "arena.messages.starting"));
                    if (lobbyCountdown % 5 == 0) {
                        if (start != null) {
                            player.playSound(player.getLocation(), start, 2.0f, 2.0f);
                        }
                    } else if (lobbyCountdown < 5) {
                        if (finishing != null) {
                            player.playSound(player.getLocation(), finishing, 2.0f, 2.0f);
                        }
                    }
                });
            }
        }, 20, 20);
    }

    public int getCurrentLobbyCountdown() {
        return lobbyCountdown;
    }

    public int getLobbyCountdown() {
        return initialLobbyCountdown;
    }

    public void cancelCountdown() {

    }

}
