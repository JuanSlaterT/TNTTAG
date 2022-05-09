package cloud.docsclient.hotdoctor.plugin.tnttag;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import cloud.docsclient.hotdoctor.plugin.tnttag.managers.ArenaManager;
import cloud.docsclient.hotdoctor.plugin.tnttag.managers.PlayerManager;
import cloud.docsclient.hotdoctor.plugin.tnttag.managers.StorageManager;
import cloud.docsclient.hotdoctor.plugin.tnttag.structure.Arena;
import cloud.docsclient.hotdoctor.plugin.tnttag.utils.DefaultFontInfo;
import cloud.docsclient.hotdoctor.plugin.tnttag.utils.TNTPlayer;
import me.clip.placeholderapi.PlaceholderAPI;

public class Main extends JavaPlugin {

	private File statsFolder;
	private StorageManager storageManager;
	private PlayerManager playerManager;
	private ArenaManager arenaManager;

	public void onEnable() {
		File file = new File(this.getDataFolder() + File.separator + "player-stats");
		if (!file.exists())
			file.mkdirs();
		statsFolder = file;
		playerManager = new PlayerManager(this);
		arenaManager = new ArenaManager(this);
	}

	public ArenaManager getArenaManager() {
		return arenaManager;
	}

	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	public StorageManager getStorageManager() {
		return storageManager;
	}

	public File getStatsFolder() {
		return statsFolder;
	}

	public String sendMessageWithPlaceholdersOf(Player player, String path) {
		String text = "";
		if (this.getConfig().isList(path)) {
			for (String message : this.getConfig().getStringList(path)) {
				player.sendMessage(amazingText(message, player));
			}
		} else {
			String message = this.getConfig().getString(path);
			text = amazingText(message, player);
		}

		if (player != null) {
			TNTPlayer tnt = this.getPlayerManager().getTNTPlayer(player);
			Arena arena = this.getPlayerManager().getPlayingArena(tnt);
			text.replace("<player>", player.getName());
			if (arena != null) {
				text.replace("<min>", "" + arena.getMinPlayers());
				text.replace("<max>", "" + arena.getMaxPlayers());
				text.replace("<num>", "" + arena.getPlayingPlayers().size());
				text.replace("<seconds>", "" + arena.getCurrentLobbyCountdown());
			}
		}
		return text;
	}
	
    public void sendCenteredMessage(CommandSender player, String message) {
        if (message == null || message.equals("")) {
            player.sendMessage("");
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        message = this.amazingText(message, player);
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;
        char[] charArray;
        for (int length = (charArray = message.toCharArray()).length, i = 0; i < length; ++i) {
            final char c = charArray[i];
            if (c == '�') {
                previousCode = true;
            }
            else if (previousCode) {
                previousCode = false;
                isBold = (c == 'l' || c == 'L');
            }
            else {
                final DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += (isBold ? dFI.getBoldLength() : dFI.getLength());
                ++messagePxSize;
            }
        }
        final int halvedMessageSize = messagePxSize / 2;
        final int toCompensate = 154 - halvedMessageSize;
        final int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        final StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        player.sendMessage(String.valueOf(sb.toString()) + message);
        return;
    }
    
    public String amazingText(String message, CommandSender player) {
        if(player instanceof Player) {
            message = PlaceholderAPI.setPlaceholders((Player) player, message);
        }
        if(Bukkit.getBukkitVersion().contains("1.16") || Bukkit.getBukkitVersion().contains("1.17") || Bukkit.getBukkitVersion().contains("1.18")) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {
                String color = message.substring(matcher.start(), matcher.end());
                message = message.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
                matcher = pattern.matcher(message);
            }
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        
        
        return message;
    }

}
