package cloud.docsclient.hotdoctor.plugin.tnttag.structure.commands;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import cloud.docsclient.hotdoctor.plugin.tnttag.Main;
import cloud.docsclient.hotdoctor.plugin.tnttag.exceptions.ArenaException;
import cloud.docsclient.hotdoctor.plugin.tnttag.utils.ArenaCreation;

public class AdminCommands implements TabExecutor {
    public AdminCommands(Main plugin) {
        this.plugin = plugin;
    }

    private Main plugin;

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // TODO Auto-generated method stub
        return null;
    }

    public HashMap<Player, ArenaCreation> creators = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command comando, String label, String[] args) {
        if (!(sender instanceof Player || sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', "&cError: &fAdmin Commands are only avaible ingame."));
            return true;
        }
        Player player = (Player) sender;
        if (player.hasPermission("tnttag.admin")) {
            if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))
                    || (args.length == 1 && args[0].equalsIgnoreCase("1"))
                    || (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("1"))) {
                player.sendMessage("");
                player.sendMessage("");
                plugin.sendCenteredMessage(player, "&e&lTAG PLUGIN");
                player.sendMessage("&cAliases: &b/tagadmin&7, &b/tagcreator");
                player.sendMessage("");
                player.sendMessage(plugin.amazingText("&e/tagadmin create &9<MapName>", player));
                player.sendMessage(plugin
                        .amazingText("&e/tagadmin setPreLobby &7- &bSets Location as PreLobby of the Map", player));
                player.sendMessage(plugin.amazingText(
                        "&e/tagadmin setSpawn &7- &bSets Location as Main Spawn/Deathmatch Spawn of the Map", player));
                player.sendMessage(plugin.amazingText(
                        "&e/tagadmin setMinPlayers <Number> &7- &bSets Number as Min Players of the Map", player));
                player.sendMessage(plugin.amazingText(
                        "&e/tagadmin setMaxPlayers <Nunber> &7- &bSets Number as Max Players of the Map", player));
                player.sendMessage(plugin.amazingText(
                        "&e/tagadmin setLobbyCountdown <Number> &7- &bSets Number as Lobby Countdown of the Map",
                        player));
                player.sendMessage(plugin.amazingText(
                        "&e/tagadmin setRoundTimeSeconds <Number> &7- &bSets Number as Initial Round Time Seconds of the Map",
                        player));
                player.sendMessage(
                        plugin.amazingText("&e/tagadmin save &7- &bSaves the arena that were being edited.", player));
                player.sendMessage("");
                player.sendMessage("");
                return true;
            } else if (args[0].equalsIgnoreCase("create")) {
                if (args.length == 2) {
                    if (exists(args[1])) {
                        player.sendMessage(plugin.amazingText(
                                "&c&lERROR (!): &f&lThis arena is already registered or is being created by another player.",
                                player));
                    } else {
                        if (!creators.containsKey(player)) {
                            ArenaCreation ac = new ArenaCreation(player, args[1], plugin);
                            creators.put(player, ac);
                            player.sendMessage(plugin.amazingText(
                                    "&a&lSUCCESS (!): &f&lYou are now editing the arena &b" + args[1] + "&f&l.",
                                    player));
                            sendMissingFields(player);
                        } else {
                            ArenaCreation ac = creators.get(player);
                            player.sendMessage(
                                    plugin.amazingText("&c&lERROR (!): &f&lYou are already creating an arena named &b"
                                            + ac.getMapName() + "&f&l.", player));
                        }
                    }
                } else {
                    player.sendMessage(plugin.amazingText("&c&lERROR (!): &f&lYou must specify an map name.", player));
                }
                return true;
            } else if (args[0].equalsIgnoreCase("setprelobby")) {
                if (creators.containsKey(player)) {
                    ArenaCreation ac = creators.get(player);
                    ac.setPrelobbyLocation(player.getLocation());
                    player.sendMessage(plugin
                            .amazingText("&a&lSUCCESS (!): &f&lYou setted the prelobby location for the mapname &b"
                                    + args[1] + "&f&l.", player));
                    sendMissingFields(player);
                } else {
                    player.sendMessage(plugin.amazingText("&c&lERROR (!): &f&lYou are not creating an arena.", player));
                }
                return true;
            } else if (args[0].equalsIgnoreCase("setspawn")) {
                if (creators.containsKey(player)) {
                    ArenaCreation ac = creators.get(player);
                    ac.setSpawnLocation(player.getLocation());
                    player.sendMessage(plugin.amazingText(
                            "&a&lSUCCESS (!): &f&lYou setted the spawn location for the mapname &b" + args[1] + "&f&l.",
                            player));
                    sendMissingFields(player);
                } else {
                    player.sendMessage(plugin.amazingText("&c&lERROR (!): &f&lYou are not creating an arena.", player));
                }
                return true;
            } else if (args[0].equalsIgnoreCase("setminplayers")) {
                if (args.length == 2) {
                    if (creators.containsKey(player)) {
                        ArenaCreation ac = creators.get(player);
                        String n = args[1];
                        if (isNumber(n)) {
                            ac.setMinPlayers(Integer.parseInt(n));
                            player.sendMessage(plugin.amazingText(
                                    "&a&lSUCCESS (!): &f&lYou setted the Min Amount of Players for the mapname &b"
                                            + args[1] + "&f&l.",
                                    player));
                            sendMissingFields(player);
                        } else {
                            player.sendMessage(plugin.amazingText(
                                    "&c&lERROR (!): &f&lYou must set a number value as argument.", player));
                        }
                    } else {
                        player.sendMessage(
                                plugin.amazingText("&c&lERROR (!): &f&lYou are not creating an arena.", player));
                    }
                } else {
                    player.sendMessage(plugin
                            .amazingText("&c&lERROR (!): &f&lYou must specify a number value as argument", player));
                }
                return true;
            } else if (args[0].equalsIgnoreCase("setmaxplayers")) {
                if (args.length == 2) {
                    if (creators.containsKey(player)) {
                        ArenaCreation ac = creators.get(player);
                        String n = args[1];
                        if (isNumber(n)) {
                            ac.setMaxPlayers(Integer.parseInt(n));
                            player.sendMessage(plugin.amazingText(
                                    "&a&lSUCCESS (!): &f&lYou setted the Max Amount of Players for the mapname &b"
                                            + args[1] + "&f&l.",
                                    player));
                            sendMissingFields(player);
                        } else {
                            player.sendMessage(plugin.amazingText(
                                    "&c&lERROR (!): &f&lYou must set a number value as argument.", player));
                        }
                    } else {
                        player.sendMessage(
                                plugin.amazingText("&c&lERROR (!): &f&lYou are not creating an arena.", player));
                    }
                } else {
                    player.sendMessage(plugin
                            .amazingText("&c&lERROR (!): &f&lYou must specify a number value as argument", player));
                }
                return true;
            } else if (args[0].equalsIgnoreCase("setLobbyCountdown")) {
                if (args.length == 2) {
                    if (creators.containsKey(player)) {
                        ArenaCreation ac = creators.get(player);
                        String n = args[1];
                        if (isNumber(n)) {
                            ac.setLobbyCountdown(Integer.parseInt(n));
                            player.sendMessage(plugin.amazingText(
                                    "&a&lSUCCESS (!): &f&lYou setted the Lobbyb Countdown for the mapname &b"
                                            + ac.getMapName() + "&f&l.",
                                    player));
                            sendMissingFields(player);
                        } else {
                            player.sendMessage(plugin.amazingText(
                                    "&c&lERROR (!): &f&lYou must set a number value as argument.", player));
                        }
                    } else {
                        player.sendMessage(
                                plugin.amazingText("&c&lERROR (!): &f&lYou are not creating an arena.", player));
                    }
                } else {
                    player.sendMessage(plugin
                            .amazingText("&c&lERROR (!): &f&lYou must specify a number value as argument", player));
                }
                return true;
            } else if (args[0].equalsIgnoreCase("setRoundTimeSeconds")) {
                if (args.length == 2) {
                    if (creators.containsKey(player)) {
                        ArenaCreation ac = creators.get(player);
                        String n = args[1];
                        if (isNumber(n)) {
                            ac.setRoundTimeSeconds(Integer.parseInt(n));
                            player.sendMessage(plugin.amazingText(
                                    "&a&lSUCCESS (!): &f&lYou setted the Lobbyb Countdown for the mapname &b"
                                            + ac.getMapName() + "&f&l.",
                                    player));
                            sendMissingFields(player);
                        } else {
                            player.sendMessage(plugin.amazingText(
                                    "&c&lERROR (!): &f&lYou must set a number value as argument.", player));
                        }
                    } else {
                        player.sendMessage(
                                plugin.amazingText("&c&lERROR (!): &f&lYou are not creating an arena.", player));
                    }
                } else {
                    player.sendMessage(plugin
                            .amazingText("&c&lERROR (!): &f&lYou must specify a number value as argument", player));
                }
                return true;
            } else if (args[0].equalsIgnoreCase("save")) {
                if (creators.containsKey(player)) {
                    ArenaCreation ac = creators.get(player);
                    try {
                        if (ac.saveArena()) {
                            player.sendMessage(
                                    plugin.amazingText("&a&lSUCCESS (!): &f&lArena creation of the mapname &b"
                                            + ac.getMapName() + "&f&lcreated correctly.", player));
                            creators.remove(player);
                        }
                    } catch (IOException | ArenaException e) {
                        player.sendMessage(plugin.amazingText(
                                "&c&lERROR (!): &f&lError while creating arena, please check console.", player));
                        player.sendMessage(plugin.amazingText("&7&lINFO (?): &f&lYou still in edit mode.", player));
                        e.printStackTrace();
                    }
                } else {
                    player.sendMessage(plugin.amazingText("&c&lERROR (!): &f&lYou are not creating an arena.", player));
                }
            } else if (args[0].equalsIgnoreCase("cancel")) {
                if (creators.containsKey(player)) {
                    ArenaCreation ac = creators.get(player);
                    player.sendMessage(
                            plugin.amazingText("&a&lSUCCESS (!): &f&lYou left the editing mode of the mapname &b"
                                    + ac.getMapName() + " &f&lcorrectly.", player));
                    player.sendMessage(plugin
                            .amazingText("&7&lINFO (?): &f&lAll the arena data information were removed.", player));
                    creators.remove(player);
                } else {
                    player.sendMessage(plugin.amazingText("&c&lERROR (!): &f&lYou are not creating an arena.", player));
                }
            }
        }
        // TODO Auto-generated method stub
        return false;
    }

    private boolean isNumber(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void sendMissingFields(Player player) {
        if (creators.containsKey(player)) {
            ArenaCreation ac = creators.get(player);
            player.sendMessage("");
            player.sendMessage(plugin
                    .amazingText("&eFor complete arena creation, please execute the following commands:", player));
            player.sendMessage("");
            int i = 0;
            if (ac.getPrelobbyLocation() == null) {
                i++;
                player.sendMessage(plugin.amazingText(
                        "&a" + i + ". &b/tagadmin setPreLobby &7- &bSets Location as PreLobby of the Map &c(IMPORTANT)",
                        player));
            }
            if (ac.getSpawnLocation() == null) {
                i++;
                player.sendMessage(plugin.amazingText("&a" + i
                        + ". &b/tagadmin setSpawn &7- &bSets Location as Main Spawn/Deathmatch Spawn of the Map &c(IMPORTANT)",
                        player));
            }
            if (ac.getMinPlayers() == 0) {
                i++;
                player.sendMessage(plugin.amazingText(
                        "&a" + i + ". &b/tagadmin setMinPlayers <Number> &7- &bSets Number as Min Players of the Map",
                        player));
            }
            if (ac.getMaxPlayers() == 0) {
                i++;
                player.sendMessage(plugin.amazingText(
                        "&a" + i + ". &b/tagadmin setMaxPlayers <Number> &7- &bSets Number as Max Players of the Map",
                        player));
            }
            if (ac.getRoundTimeSeconds() == 0) {
                i++;
                player.sendMessage(plugin.amazingText("&a" + i
                        + ". &b/tagadmin setRoundTimeSeconds <Number> &7- &bSets Number as Initial Round Time Seconds of the Map",
                        player));
            }
            if (ac.getLobbyCountdown() == 0) {
                i++;
                player.sendMessage(plugin.amazingText("&a" + i
                        + ". &b/tagadmin setLobbyCountdown <Number> &7- &bSets Number as Lobby Countdown of the Map",
                        player));
            }
            if (ac.getPrelobbyLocation() != null && ac.getSpawnLocation() != null) {
                i++;
                player.sendMessage(plugin.amazingText(
                        "&a" + i + ". &b/tagadmin save &7- &bSaves the arena that were being edited.", player));
            }
            player.sendMessage("");
            i++;
            player.sendMessage(
                    plugin.amazingText("&a" + i + ". &c/tagadmin cancel &7- &bLeave the edit mode.", player));
            player.sendMessage("");
        }
    }

    private boolean exists(String mapName) {
        if (plugin.getArenaManager().getLoadedMapNames().contains(mapName)) {
            return true;
        }
        for (ArenaCreation creating : creators.values()) {
            if (creating.getMapName().equalsIgnoreCase(mapName)) {
                return true;
            }
        }
        return false;
    }

}
