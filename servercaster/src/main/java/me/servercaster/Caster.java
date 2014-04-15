package me.servercaster;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Patrick Beuks (killje) and Floris Huizinga (Flexo013)
 */
public class Caster implements Runnable, CommandExecutor {

    private int lineIndex = 0;
    private int totalMessages;
    private final JavaPlugin instance = ServerCaster.getInstance();
    private final Builder converter = new Builder();
    private final ArrayList<ArrayList<String>> messages = new ArrayList<>();

    public Caster() {
        totalMessages = instance.getConfig().getStringList("Messages").size();
        init();
    }

    private void init() {
        int intervalInTicks = getIntervalInTicks();
        instance.getServer().getScheduler().scheduleSyncRepeatingTask(instance, this, intervalInTicks, intervalInTicks);
    }

    private void init(int message) {
        cast(message);
        init();
    }

    @Override
    public void run() {
        if (totalMessages <= lineIndex) {
            lineIndex = 0;
        }
        if (totalMessages > messages.size()) {
            List<String> storedMessages = instance.getConfig().getStringList("Messages");
            String prefix = instance.getConfig().getString("Prefix");
            if (!prefix.equals("")) {
                prefix = prefix + " ";
            }
            ArrayList<String> newMessage = new ArrayList<>();
            for (String string : storedMessages.get(lineIndex).split("&NEWLINE;")) {
                String properMessages = converter.getProperMessage(prefix + string);
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
        for (Player player : instance.getServer().getOnlinePlayers()) {
            for (String string : message) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getPlayerListName() + " " + string);
            }
        }
    }

    private void cast(int message) {
        if (message == -1) {
            run();
        } else {
            lineIndex = message;
            run();
        }
    }

    private int getIntervalInTicks() {
        int interval = instance.getConfig().getInt("Interval");
        if (!instance.getConfig().getBoolean("InSeconds")) {
            interval *= 60;
        }
        return 20 * interval;
    }

    private void reset() {
        instance.reloadConfig();
        instance.getServer().getScheduler().cancelTasks(instance);
        totalMessages = instance.getConfig().getStringList("Messages").size();
        lineIndex = 0;
        messages.clear();
        init();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("reloadservercast")) {
            reset();
            sender.sendMessage(ChatColor.GREEN + "Servercast messages have been reloaded");
            return true;
        } else if (cmd.getName().equalsIgnoreCase("cast")) {
            instance.getServer().getScheduler().cancelTasks(instance);
            int message = 0;
            if (args.length > 1) {
                return false;
            }
            if (args.length == 1) {
                message = Integer.parseInt(args[0]);
                if (message <= 0) {
                    sender.sendMessage(ChatColor.RED + "the number needs to be greater then zero");
                    return false;
                }
            }
            message--;
            init(message);
            return true;
        }
        return false;
    }

}
