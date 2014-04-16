package me.servercaster;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Patrick Beuks (killje) and Floris Huizinga (Flexo013)
 */
public class GroupSender {

    private int lineIndex = 0;
    private final JavaPlugin instance = ServerCaster.getInstance();
    private final String path;
    private final int totalMessages;
    private final ArrayList<ArrayList<String>> messages = new ArrayList<>();
    private final ArrayList<Player> players = new ArrayList<>();

    public GroupSender(String path) {
        totalMessages = instance.getConfig().getStringList("Messages").size();
        this.path = path;
    }

    public boolean addPlayer(Player player) {
        return players.add(player);
    }

    public void run() {
        if (totalMessages <= lineIndex) {
            lineIndex = 0;
        }
        if (totalMessages > messages.size()) {
            List<String> storedMessages = instance.getConfig().getStringList(path);
            String prefix = instance.getConfig().getString("Prefix");
            Builder builder = new Builder();
            if (!prefix.equals("")) {
                prefix = prefix + " ";
            }
            ArrayList<String> newMessage = new ArrayList<>();
            for (String string : storedMessages.get(lineIndex).split("&NEWLINE;")) {
                String properMessages = builder.getProperMessage(prefix + string);
                if (instance.getConfig().getBoolean("Debug")) {
                    instance.getLogger().info(properMessages);
                }
                newMessage.add(properMessages);
            }
            messages.add(newMessage);
        }
        sendmessage(messages.get(lineIndex));
        lineIndex++;
    }

    private void sendmessage(ArrayList<String> message) {
        for (Player player : players) {
            for (String string : message) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getPlayerListName() + " " + string);
            }
        }
    }

    public String getGroup() {
        return path;
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public void setLineIndex(int message) {
        lineIndex = message;
    }
}