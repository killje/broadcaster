package me.servercaster.extension;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.servercaster.core.ServerCaster;
import me.servercaster.core.event.CastListener;
import me.servercaster.core.event.PreCastEvent;
import me.servercaster.core.event.PreCastPlayerEvent;
import net.amoebaman.util.Reflection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Patrick Beuks (killje) and Floris Huizinga (Flexo013)
 */
public class ServerExtension extends JavaPlugin implements CastListener {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        ServerCaster.addMessageListener(this, this);
    }

    @Override
    public void castHandler(PreCastEvent e) {
        ArrayList<String> messages = e.getMessages();
        ArrayList<String> newMessages = new ArrayList<>();
        for (String string : messages) {
            Random rand = new Random();
            Player[] onlinePlayers = this.getServer().getOnlinePlayers();
            if (onlinePlayers.length != 0) {
                string = string.replaceAll("(?i)%RDMPLAYER%", onlinePlayers[rand.nextInt(onlinePlayers.length)].getName());
            }
            getLogger().info(string);
            string = string.replaceAll("(?i)%SLOTS%", this.getServer().getMaxPlayers() + "");
            getLogger().info(string);
            string = string.replaceAll("(?i)%PLAYERS%", onlinePlayers.length + "");
            if (string.contains("(?i)%ONLINEPLAYERS%")) {
                String players = "";
                for (Player player : onlinePlayers) {
                    players += player.getDisplayName() + ", ";
                }
                players = players.substring(0, players.length() - 2);
                string = string.replaceAll("(?i)%ONLINEPLAYERS%", players);
            }
            List<String> staff = getConfig().getStringList("Staff");
            if (string.contains("(?i)%LISTALLSTAFF%")) {
                String allStaff = "";
                for (String staffName : staff) {
                    allStaff += staffName + ", ";
                }
                allStaff = allStaff.substring(0, allStaff.length() - 2);
                string = string.replaceAll("(?i)%LISTALLSTAFF%", allStaff);
            }
            if (string.contains("(?i)%ONLINESTAFF%")) {
                String onlineStaff = "";
                for (String staffName : staff) {
                    if (getServer().getPlayer(staffName).isOnline()) {
                        onlineStaff += staffName + ", ";
                    }
                }
                onlineStaff = onlineStaff.substring(0, onlineStaff.length() - 2);
                string = string.replaceAll("(?i)%ONLINESTAFF%", onlineStaff);
            }
            newMessages.add(string);
        }
        e.setMessages(newMessages);
    }

    @Override
    public void castPlayerHandler(PreCastPlayerEvent e) {
        ArrayList<String> messages = e.getMessages();
        ArrayList<String> newMessages = new ArrayList<>();
        for (String string : messages) {
            string = string.replaceAll("(?i)%PLAYER%", e.getPlayer().getName());
            string = string.replaceAll("(?i)%PING%", getPing(e.getPlayer()) + "");
            newMessages.add(string);
        }
        e.setMessages(newMessages);
    }

    private int getPing(Player p) {
        return 1;
//        Class<?> cp = Reflection.getOBCClass("entity.CraftPlayer").cast(p).getClass();
//        int returnvalue = -1;
//        Object ep;
//        try {
//            ep = Reflection.getMethod(cp, "getHandler").invoke(null);
//            returnvalue = Reflection.getField(ep.getClass(), "ping").getInt(ep);
//        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
//            Logger.getLogger(ServerExtension.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return returnvalue;
    }
}
