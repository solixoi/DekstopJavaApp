package by.server;

import by.server.models.entities.*;
import by.server.models.enums.RequestType;
import by.server.models.enums.ResponseStatus;
import by.server.models.tcp.Response;
import by.server.repositories.ProductRepository;
import by.server.service.*;
import by.server.utility.ClientThread;
import by.server.utility.JPAUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {
    private static final int PORT = 6666;
    private static ServerSocket serverSocket;
    private static ClientThread clientThread;
    private static Thread thread;
    private static List<Socket> currentSockets = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        JPAUtil.getEntityManagerFactory();

        serverSocket = new ServerSocket(PORT);
        while (true) {
            Iterator<Socket> iterator = currentSockets.iterator();
            while (iterator.hasNext()) {
                Socket socket = iterator.next();
                if (socket.isClosed()) {
                    iterator.remove();
                }
            }
            Socket socket = serverSocket.accept();
            currentSockets.add(socket);
            System.out.println("Клиент " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " подключен.");
            clientThread = new ClientThread(socket);
            thread = new Thread(clientThread);
            thread.start();
            System.out.flush();
        }
    }
}