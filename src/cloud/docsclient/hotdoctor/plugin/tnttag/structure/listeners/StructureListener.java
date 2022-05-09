package cloud.docsclient.hotdoctor.plugin.tnttag.structure.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import cloud.docsclient.hotdoctor.plugin.tnttag.Main;
import cloud.docsclient.hotdoctor.plugin.tnttag.events.ArenaChangeStatusEvent;
import cloud.docsclient.hotdoctor.plugin.tnttag.events.ArenaRoundFinishEvent;
import cloud.docsclient.hotdoctor.plugin.tnttag.events.ArenaRoundStartEvent;
import cloud.docsclient.hotdoctor.plugin.tnttag.events.PlayerQuitArenaEvent;
import cloud.docsclient.hotdoctor.plugin.tnttag.events.SpectatorJoinEvent;
import cloud.docsclient.hotdoctor.plugin.tnttag.managers.PlayerManager;
import cloud.docsclient.hotdoctor.plugin.tnttag.structure.Arena;
import cloud.docsclient.hotdoctor.plugin.tnttag.utils.GameStatus;
import cloud.docsclient.hotdoctor.plugin.tnttag.utils.TNTPlayer;

public class StructureListener implements Listener {

    public StructureListener(Main plugin) {
        this.plugin = plugin;
    }

    private Main plugin;
    private final PotionEffect cachedEffect = new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 80000, 0);

    @EventHandler
    public void build(BlockPlaceEvent e) {
        TNTPlayer player = plugin.getPlayerManager().getTNTPlayer(e.getPlayer());
        PlayerManager manager = plugin.getPlayerManager();
        if (manager.isPlaying(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void brik(BlockBreakEvent e) {
        TNTPlayer player = plugin.getPlayerManager().getTNTPlayer(e.getPlayer());
        PlayerManager manager = plugin.getPlayerManager();
        if (manager.isPlaying(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void quit(PlayerQuitArenaEvent e) {
        Player player = e.getTNTPlayer().getPlayer();
        player.getInventory().clear();
        player.getEquipment().setHelmet(null);
        player.getEquipment().setChestplate(null);
        player.getEquipment().setLeggings(null);
        player.getEquipment().setBoots(null);
        player.getActivePotionEffects().clear();
    }

    @EventHandler
    public void spectator(SpectatorJoinEvent e) {
        Player player = e.getSpectatorPlayer().getPlayer();
        Arena arena = e.getArena();
        if (!player.getWorld().equals(arena.getSpawnArena().getWorld())) {
            player.teleport(arena.getSpawnArena());
        }
        player.getInventory().clear();
        player.getEquipment().setHelmet(null);
        player.getEquipment().setChestplate(null);
        player.getEquipment().setLeggings(null);
        player.getEquipment().setBoots(null);
        player.addPotionEffect(cachedEffect);
        if (plugin.getConfig().getBoolean("arena.configurations.hidePlayers")) {
            arena.getPlayingPlayers().forEach((living) -> living.hidePlayer(plugin, player));

        }

    }

    @EventHandler
    public void end(ArenaChangeStatusEvent e) {
        if (e.getToGameStatus() == GameStatus.INGAME) {
            PlayerManager manager = plugin.getPlayerManager();
            for (TNTPlayer player : e.getArena().getPlayingPlayers()) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    TNTPlayer other = manager.getTNTPlayer(p);
                    if (manager.isPlaying(player) && manager.isPlaying(other)
                            && manager.getPlayingArena(player) == manager.getPlayingArena(other)) {
                        // NOTHING
                    } else {
                        if (plugin.getConfig().getBoolean("arena.configurations.hidePlayers")) {
                            player.hidePlayer(plugin, p);
                        }
                    }
                }
            }

        } else if (e.getToGameStatus() == GameStatus.ENDING) {
            Arena arena = e.getArena();
            if (arena.getPlayingPlayers().size() == 1) {
                TNTPlayer player = arena.getPlayingPlayers().get(0);
                player.setWins(player.getWins() + 1);
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                arena.getSpectatingPlayers()
                        .forEach((players) -> plugin.getArenaManager().leaveArena(players, arena, false));
                arena.getPlayingPlayers()
                        .forEach((players) -> plugin.getArenaManager().leaveArena(players, arena, false));
            }, 20 * 10);

        }
    }

    @EventHandler
    public void startRound(ArenaRoundStartEvent e) {
        Arena arena = e.getArena();
        int players = arena.getPlayingPlayers().size();
        if (players >= 30) {
            e.getArena().getTagSystem().getTaggersRandomly(7);
        } else if (players >= 25) {
            arena.getTagSystem().getTaggersRandomly(6);
        } else if (players >= 20) {
            arena.getTagSystem().getTaggersRandomly(5);
        } else if (players >= 15) {
            arena.getTagSystem().getTaggersRandomly(4);
        } else if (players >= 10) {
            arena.getTagSystem().getTaggersRandomly(3);
        } else if (players >= 7) {
            arena.getTagSystem().getTaggersRandomly(2);
        } else if (players <= 6) {
            arena.getTagSystem().getTaggersRandomly(1);
            arena.getPlayingPlayers().forEach((player) -> player.getPlayer().teleport(arena.getSpawnArena()));
            arena.getSpectatingPlayers().forEach((player) -> player.getPlayer().teleport(arena.getSpawnArena()));
        }

    }

    @EventHandler
    public void finishRound(ArenaRoundFinishEvent e) {
        // TODO Matar a los taggers al acabar
        // TODO Matar a los alrededor de los taggers al acabar (PERK SYSTEM)
        Arena arena = e.getArena();
        if (arena.getPlayingPlayers().size() <= 1) {
            arena.changeGameStatus(GameStatus.ENDING);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, () -> arena.getRoundSystem().start(), 8 * 20);
        }
    }

}
