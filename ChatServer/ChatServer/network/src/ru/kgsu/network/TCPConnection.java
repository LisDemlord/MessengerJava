package ru.kgsu.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {

    private final Socket socket;
    private final Thread rxThread;
    private final TCPConnectionListener eventListener;
    private final BufferedReader in;
    private final BufferedWriter out;

    // Делегирование конструкторов, при вызове первого, следом вызывается второй

    public TCPConnection(TCPConnectionListener eventListener, String ipAddress, int port) throws IOException {
        this(eventListener, new Socket(ipAddress, port));
    }

    public TCPConnection(final TCPConnectionListener eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        // Потоки ввода и вывода (как матрешка)
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        // Анонимный класс (фишка JAVA), в котором переопределили метод run()
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnection(TCPConnection.this);
                    // Пока поток не закрыт, обрабатываем строку
                    while (!rxThread.isInterrupted()) {
                        eventListener.onStringInput(TCPConnection.this, in.readLine());
                    }
                } catch (IOException e) {
                    eventListener.onException(TCPConnection.this, e);
                } finally {
                    // При получении исключения закрываем соединение
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        rxThread.start();
    }

    // Срабатывает при отправке строки
    public synchronized void sendString(String value) {
        try {
            // В поток записываем строку + принудительно отправляем ее
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
            Disconnect();
        }
    }

    // Срабатывает при отключении соединения
    public synchronized void Disconnect() {
        // Закрываем поток + сокет
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
        }
    }

    // Переопределение метода класса toString()
    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ":" + socket.getPort();
    }
}
