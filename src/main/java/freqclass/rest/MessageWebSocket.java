/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package freqclass.rest;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;


/**
 * A simple WebSocket handler that echos back messages and allows sending of strings.
 * @author Keith
 */
@WebSocket
public class MessageWebSocket {
    
    private final static Logger LOGGER = Logger.getLogger(MessageWebSocket.class.getName());
    
    // Store sessions if you want to, for example, broadcast a message to all users
    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();

    @OnWebSocketConnect
    public void connected(Session session) {
        sessions.add(session);
        LOGGER.info("Connected session: " + session.getRemoteAddress());
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        sessions.remove(session);
        LOGGER.info("Closed session: " + session.getRemoteAddress());
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        System.out.println("Got: " + message);   // Print message
        session.getRemote().sendString(message); // and send it back
    }
    
    @OnWebSocketError
    public void error(Session session, Throwable t) {
        LOGGER.warning("Error in session: " + session.getRemoteAddress());
    }
    
    public void sendMessage(String message) {
        sessions.parallelStream().forEach((s) -> {
            try {
                s.getRemote().sendString(message);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Error sending message to session: " + s.getRemoteAddress(), ex);
            }
        });
    }
}
