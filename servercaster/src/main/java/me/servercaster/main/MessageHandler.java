package me.servercaster.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.servercaster.main.event.PreCastPlayerEvent;
import me.servercaster.main.event.PreCastEvent;
import me.servercaster.main.event.CastListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Patrick Beuks (killje) and Floris Huizinga (Flexo013)
 */
public class MessageHandler {

    private final List _listeners = new ArrayList();
    private ArrayList<String> messages;
    private ArrayList<String> old;

    public synchronized void addEventListener(CastListener listener) {
        _listeners.add(listener);
    }

    public synchronized void removeEventListener(CastListener listener) {
        _listeners.remove(listener);
    }

    public void sendMessages(ArrayList<Player> players, ArrayList<String> messages) {
        this.messages = messages;
        this.old = messages;
        if (firePreServer(players)) {
            for (Player player : players) {
                if (firePrePlayer(player)) {
                    for (String string : this.messages) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + string);
                    }
                }
                this.messages = this.old;
            }
        }
    }

    private synchronized boolean firePreServer(ArrayList<Player> players) {
        PreCastEvent event = new PreCastEvent(messages, players, this);
        Iterator i = _listeners.iterator();
        while (i.hasNext()) {
            ((CastListener) i.next()).castHandler(event);
            if (event.isCancelled()) {
                return false;
            }
        }
        this.messages = event.getMessages();
        return true;
    }

    private synchronized boolean firePrePlayer(Player player) {
        PreCastPlayerEvent event = new PreCastPlayerEvent(messages, player, this);
        Iterator i = _listeners.iterator();
        while (i.hasNext()) {
            ((CastListener) i.next()).castPlayerHandler(event);
            if (event.isCancelled()) {
                return false;
            }
        }
        this.messages = event.getMessages();
        return true;
    }

}
