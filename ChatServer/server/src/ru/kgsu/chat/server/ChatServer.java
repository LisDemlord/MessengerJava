package ru.kgsu.chat.server;

import ru.kgsu.network.TCPConnection;
import ru.kgsu.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener {

    public static void main(String[] args) {
        new ChatServer();
    }

    // Коллекция для хранения всех подключений
    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer(){
        System.out.println("Server is running...");
        // Для создания экземпляра ServerSocket + автоматического высвобождения ресурсов
        try (ServerSocket serverSocket = new ServerSocket(8999)) {
            // Бесконечный цикл для получения новых соединений
            while (true) {
                try {
                    // Создание нового TCP соединения. Используется this, так как класс ChatServer
                    // Реализует интерфейс TCPConnectionListener. .accept() возвращает ipAdress + port.
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("TCPConnection exception " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Реализование методов интерфейса TCPConnectionListener,
    // так как все методы интерфейса должны быть реализованы

    // Срабатывает при новом подключении
    @Override
    public synchronized void onConnection(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendMessageToAll("Client connected " + tcpConnection);
    }

    // Срабатывает при получении строки
    @Override
    public synchronized void onStringInput(TCPConnection tcpConnection, String value) {
        sendMessageToAll(value);
    }

    // Срабатывает при отключении соединения
    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendMessageToAll("Client disconnected " + tcpConnection);
    }

    // Срабатывает при получении исключения
    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception " + e);
    }

    // Метод отправляет сообщения всем соединениям
    private void sendMessageToAll(String value) {
        System.out.println(value);
        final int count = connections.size();
        for (int i = 0; i < count; i++) {
            connections.get(i).sendString(value);
        }
    }
}
