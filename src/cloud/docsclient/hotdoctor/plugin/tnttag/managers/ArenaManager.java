package cloud.docsclient.hotdoctor.plugin.tnttag.managers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.Files;
import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.InvalidWorldException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldAlreadyExistsException;
import com.grinderwolf.swm.api.exceptions.WorldLoadedException;
import com.grinderwolf.swm.api.exceptions.WorldTooBigException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;

import cloud.docsclient.hotdoctor.plugin.tnttag.Main;
import cloud.docsclient.hotdoctor.plugin.tnttag.events.PlayerJoinArenaEvent;
import cloud.docsclient.hotdoctor.plugin.tnttag.events.PlayerQuitArenaEvent;
import cloud.docsclient.hotdoctor.plugin.tnttag.exceptions.ArenaException;
import cloud.docsclient.hotdoctor.plugin.tnttag.structure.Arena;
import cloud.docsclient.hotdoctor.plugin.tnttag.utils.GameStatus;
import cloud.docsclient.hotdoctor.plugin.tnttag.utils.TNTPlayer;

public class ArenaManager {

    private HashMap<String, Set<Arena>> arenas = new HashMap<>();
    private Main plugin;
    private Location lobby; // TODO POR DECLARAR EN CONSTRUCTOR
    SlimePlugin swm = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
    Random random = java.util.concurrent.ThreadLocalRandom.current();

    public Set<String> getLoadedMapNames() {
        return arenas.keySet();
    }
    

    public ArenaManager(Main plugin) {
        this.plugin = plugin;
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (String mapName : arenas.keySet()) {
                Set<Arena> arenaList = arenas.get(mapName);
                List<Arena> avaibleArenas = arenaList.stream()
                        .filter((arenaMap) -> arenaMap.getGameStatus() == GameStatus.WAITING
                                && arenaMap.getPlayingPlayers().isEmpty())
                        .collect(Collectors.toList());
                if (avaibleArenas.size() > 2) {
                    int numberToDelete = (int) (avaibleArenas.size() * 0.5);
                    for (int i = 0; i < numberToDelete; i++) {
                        Arena arena = avaibleArenas.get(i);
                        SlimeLoader loader = swm.getLoader("file");
                        try {
                            loader.deleteWorld(arena.getSpawnArena().getWorld().getName());
                        } catch (UnknownWorldException | IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        arenaList.remove(arena);
                        continue;
                    }
                }

            }
        }, 20 * 300, 20 * 300);
    }

    public boolean registerArena(String arenaName) {
        File mapList = new File(plugin.getDataFolder(), "arenas");
        if (mapList.isDirectory() && mapList.list().length != 0
                && !arenas.keySet().contains(arenaName)) {
            for (String cnf : mapList.list()) {
                if (cnf.equals(arenaName + ".yml")) {
                    try {
                        Arena arena = createArenaBasedOn(arenaName);
                        Arena arena2 = createArenaBasedOn(arenaName);
                        Arena arena3 = createArenaBasedOn(arenaName);
                        Set<Arena> arenaList = new HashSet<Arena>();
                        arenaList.add(arena);
                        arenaList.add(arena2);
                        arenaList.add(arena3);
                        arenas.put(arenaName, arenaList);
                    } catch (IOException | ArenaException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
        }
        return false;
    }

    public Location getLobbyLocation() {
        return lobby;
    }

    public void joinRandomArena(TNTPlayer player) {
        List<String> maps = arenas.keySet().stream().collect(Collectors.toList());
        joinRandomArenaByName(player, maps.get(random.nextInt(maps.size())));
    }

    public void joinRandomArenaByName(TNTPlayer player, String name) {
        if (arenas.containsKey(name)) {
            Set<Arena> arenaList = arenas.get(name);
            List<Arena> busyArenas = arenaList.stream().filter((arenas) -> arenas.getGameStatus() == GameStatus.INGAME
                    || arenas.getGameStatus() == GameStatus.STARTING).collect(Collectors.toList());
            if (busyArenas.size() >= arenaList.size() - 1) {
                try {
                    Arena arena = createArenaBasedOn(name);
                    if (arena != null) {
                        arenaList.add(arena);
                        arenas.put(name, arenaList);
                    }
                } catch (IOException | ArenaException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            List<Arena> startingArenas = arenaList.stream()
                    .filter((arenas) -> arenas.getGameStatus() == GameStatus.STARTING
                            && arenas.getPlayingPlayers().size() < arenas.getMaxPlayers())
                    .collect(Collectors.toList());
            if (startingArenas.isEmpty()) {
                List<Arena> waiting = arenaList.stream()
                        .filter((arenas) -> arenas.getGameStatus() == GameStatus.STARTING).collect(Collectors.toList());
                for (Arena arenas : waiting) {
                    if (arenas.getPlayingPlayers().size() > 0) {
                        joinArena(player, arenas);
                        return;
                    }
                }
                joinArena(player, waiting.get(random.nextInt(waiting.size())));
                return;
            } else {
                joinArena(player, startingArenas.get(random.nextInt(startingArenas.size())));
            }

        }
    }

    public Arena createArenaBasedOn(String mapName) throws IOException, ArenaException {
        File worldDir = new File(plugin.getDataFolder() + File.separator + "arenas", mapName);
        File yml = new File(plugin.getDataFolder() + File.separator + "arenas", mapName + ".yml");
        if (worldDir.exists() && worldDir.isDirectory()) {
            if (yml.exists()) {
                UUID uuidMap = UUID.randomUUID();
                File worldToUse = new File(plugin.getDataFolder() + File.separator + "arenas", uuidMap.toString());
                Files.copy(worldDir, worldToUse);
                SlimeLoader loader = swm.getLoader("file");
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    try {
                        swm.importWorld(new File(plugin.getDataFolder() + File.separator + "arenas"),
                                uuidMap.toString(), loader);
                    } catch (WorldAlreadyExistsException | InvalidWorldException | WorldLoadedException
                            | WorldTooBigException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                });
                YamlConfiguration arenaSettings = YamlConfiguration.loadConfiguration(yml);
                String name = arenaSettings.getString("arena.mapName");
                int minplayers = arenaSettings.getInt("arena.minplayers");
                int maxplayers = arenaSettings.getInt("arena.maxplayers");
                Location prelobbyLoc = (Location) arenaSettings.get("arena.preLobbyLoc", Location.class);
                Location spawnLoc = (Location) arenaSettings.get("arena.spawnLoc", Location.class);
                if (prelobbyLoc.getWorld().equals(spawnLoc.getWorld())) { // se reemplaza el world (que seria el nombre
                                                                          // del mapa) por el nuevo generado
                                                                          // automaticamente (UUID Map)
                    prelobbyLoc.setWorld(Bukkit.getWorld(uuidMap)); // se cambia el world del prelobby en caso de que
                                                                    // ambos sean el mismo mundo
                }
                spawnLoc.setWorld(Bukkit.getWorld(uuidMap));
                int initialRoundSeconds = arenaSettings.getInt("arena.initialRoundSeconds");
                int lobbyCountdown = arenaSettings.getInt("arena.lobbyCountdown");
                try {
                    return new Arena(plugin, name, prelobbyLoc, spawnLoc, maxplayers, minplayers, initialRoundSeconds,
                            lobbyCountdown);
                } catch (ArenaException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else {
                throw new ArenaException("Plugin was not able to get arena configuration file");
            }
        } else {
            throw new ArenaException("Plugin was not able to get arena world in arena's folder.");
        }
        return null;
    }

    public void joinArena(TNTPlayer player, Arena arena) {
        if (!plugin.getPlayerManager().isPlaying(player)) {
            if (arena == null) {
                player.getPlayer().sendMessage(
                        plugin.sendMessageWithPlaceholdersOf(player.getPlayer(), "arena.messages.invalid-arena"));
                return;
            }
            PlayerJoinArenaEvent event = new PlayerJoinArenaEvent(player, arena);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                if (arena.getGameStatus() == GameStatus.WAITING || arena.getGameStatus() == GameStatus.STARTING) {
                    plugin.getPlayerManager().setPlaying(player, arena);
                    arena.getPlayingPlayers().add(player);
                    player.getPlayer().teleport(arena.getPreLobbyArena());
                    if (arena.getGameStatus() == GameStatus.WAITING
                            && arena.getMinPlayers() == arena.getPlayingPlayers().size()) {
                        arena.changeGameStatus(GameStatus.STARTING);
                        arena.startCountdown();
                    }
                    arena.getPlayingPlayers().forEach((players) -> players.getPlayer().sendMessage(
                            plugin.sendMessageWithPlaceholdersOf(player.getPlayer(), "arena.messages.join")));
                }
            }
        } else {
            player.getPlayer().sendMessage(
                    plugin.sendMessageWithPlaceholdersOf(player.getPlayer(), "arena.messages.already-playing"));
        }
    }

    public void leaveArena(TNTPlayer player, Arena arena, boolean isJoinningOther) {
        if (plugin.getPlayerManager().isPlaying(player)) {
            if (arena != null) {
                if (plugin.getPlayerManager().getPlayingArena(player) == arena) {
                    if (arena.getPlayingPlayers().contains(player)) {
                        if (arena.getGameStatus() != GameStatus.ENDING) {
                            arena.getPlayingPlayers().forEach((players) -> {
                                players.getPlayer().sendMessage(plugin.sendMessageWithPlaceholdersOf(player.getPlayer(),
                                        "arena.messages.quit"));
                            });
                        }

                        arena.getPlayingPlayers().remove(player);
                    } else if (arena.getSpectatingPlayers().contains(player)) {
                        arena.getSpectatingPlayers().remove(player);
                    }
                    if (arena.getGameStatus() != GameStatus.ENDING) {
                        arena.getSpectatingPlayers().forEach((players) -> {
                            players.getPlayer().sendMessage(
                                    plugin.sendMessageWithPlaceholdersOf(player.getPlayer(), "arena.messages.quit"));
                        });
                    }
                    if (!isJoinningOther) {
                        player.getPlayer().teleport(lobby);
                    }
                    plugin.getPlayerManager().setPlaying(player, null);
                    PlayerQuitArenaEvent event = new PlayerQuitArenaEvent(player, arena);
                    Bukkit.getPluginManager().callEvent(event);

                }
            } else {
                player.getPlayer().sendMessage(
                        plugin.sendMessageWithPlaceholdersOf(player.getPlayer(), "arena.messages.invalid-arena"));
            }
        } else {
            player.getPlayer().sendMessage(
                    plugin.sendMessageWithPlaceholdersOf(player.getPlayer(), "arena.messages.not-playing"));
        }
    }

}
