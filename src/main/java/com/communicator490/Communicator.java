package com.communicator490;

import com.communicator490.communication.Conversation;
import com.communicator490.communication.Message;
import com.communicator490.communication.Server;
import com.communicator490.controllers.conversationWindow.ConversationWindowController;
import com.communicator490.controllers.mainWindow.MainWindowController;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class Communicator {
    private Server server;
    private MainWindowController mainWindowController;
    private HashMap<String, ConversationWindowController> conversationWindowControllers = new HashMap<>();

    private static Communicator communicator = new Communicator();

    public static Communicator getInstance() {
        return communicator;
    }

    private Communicator() {
        try {
            this.server = new Server();
        } catch (SocketException e) {
            handleFatalError("Can't start server: " + e.getMessage());
        }
    }

    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    public int getPort() {
        return -1; // TODO
    }

    public int getInternalPort() {
        return server.getInternalPort();
    }

    public String getIp() {
        return "???"; // TODO
    }

    public String getInternalIp() {
        String ip = "???";

        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
        } catch (UnknownHostException | SocketException e) {
            handleWarning("Can't establish local IP: " + e.getMessage());
        }
        return ip;
    }

    public void stop() {
        server.stop();
    }

    public void openConversation(String ip, int port) {
        if (!conversationWindowControllers.containsKey(ip)) {
            Conversation conversation = null;
            ConversationWindowController conversationWindowController = null;
            try {
                conversation = new Conversation(InetAddress.getByName(ip), port);
                conversationWindowController = mainWindowController.openConversationWindow(conversation);
            } catch (UnknownHostException e) {
                mainWindowController.handleWarning("Couldn't open conversation: " + e.getMessage());
            }

            if (conversationWindowController != null) {
                conversationWindowControllers.put(ip, conversationWindowController);
            }
        } else {
            conversationWindowControllers.get(ip).open();
        }
    }

    public void receiveMessage(Message m) {
        InetAddress fromIP = m.getIp();
        openConversation(fromIP.getHostAddress(), m.getPort());
        conversationWindowControllers.get(fromIP.getHostAddress()).receiveMessage(m.getContent());
    }

    public void endConversation(ConversationWindowController windowController) {
        conversationWindowControllers.remove(windowController.getConversation().getForeignAddress());
    }

    public void sendMessage(Message message) throws IOException {
        server.sendMessage(message);
    }

    public void handleFatalError(String message) {
        for (Map.Entry<String, ConversationWindowController> entry : conversationWindowControllers.entrySet()) {
            entry.getValue().handleFatalError(message);
        }
        mainWindowController.handleFatalError(message);
        server.stop();
    }

    public void handleWarning(String message) {
        mainWindowController.handleWarning(message);
    }
}
