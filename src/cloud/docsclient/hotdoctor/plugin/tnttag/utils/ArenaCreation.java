package cloud.docsclient.hotdoctor.plugin.tnttag.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.google.common.io.Files;

import cloud.docsclient.hotdoctor.plugin.tnttag.Main;
import cloud.docsclient.hotdoctor.plugin.tnttag.exceptions.ArenaException;
import cloud.docsclient.hotdoctor.plugin.tnttag.structure.Arena;

public class ArenaCreation {

    public ArenaCreation(Player player, String mapName, Main plugin) {
        this.player = player;
        this.mapName = mapName;
        this.plugin = plugin;
    }

    private Main plugin;

    private Player player;
    private Location spawnLocation;
    private Location prelobbyLocation;
    private String mapName;
    private int minPlayers = 0;
    private int maxPlayers = 0;
    private int roundTimeSeconds = 0;
    private int lobbyCountdown = 0;

    public boolean saveArena() throws IOException, ArenaException {
        if (spawnLocation != null && prelobbyLocation != null) {
            if (minPlayers == 0) {
                minPlayers = 16;
            }
            if (maxPlayers == 0) {
                maxPlayers = 32;
            }
            if (roundTimeSeconds == 0) {
                roundTimeSeconds = 55;
            }
            if (lobbyCountdown == 0) {
                lobbyCountdown = 30;
            }
            File worldDir = new File(Bukkit.getServer().getWorldContainer().getAbsolutePath(),
                    spawnLocation.getWorld().getName());
            File mapsDir = new File(plugin.getDataFolder(), "arenas");
            Files.copy(worldDir, mapsDir);
            File yml = new File(mapsDir, mapName + ".yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(yml);
            config.set("arena.mapName", mapName);
            config.set("arena.minplayers", minPlayers);
            config.set("arena.maxplayers", maxPlayers);
            config.set("arena.mapName", mapName);
            config.set("arena.preLobbyLoc", prelobbyLocation);
            config.set("arena.spawnLoc", spawnLocation);
            config.set("arena.initialRoundSeconds", roundTimeSeconds);
            config.set("arena.lobbyCountdown", lobbyCountdown);
            config.save(yml);
            plugin.getArenaManager().registerArena(mapName);
            return true;
        }
        return false;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public Location getPrelobbyLocation() {
        return prelobbyLocation;
    }

    public void setPrelobbyLocation(Location prelobbyLocation) {
        this.prelobbyLocation = prelobbyLocation;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getRoundTimeSeconds() {
        return roundTimeSeconds;
    }

    public void setRoundTimeSeconds(int roundTimeSeconds) {
        this.roundTimeSeconds = roundTimeSeconds;
    }

    public int getLobbyCountdown() {
        return lobbyCountdown;
    }

    public void setLobbyCountdown(int lobbyCountdown) {
        this.lobbyCountdown = lobbyCountdown;
    }
}
