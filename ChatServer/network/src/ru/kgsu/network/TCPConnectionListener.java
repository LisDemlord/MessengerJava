package ru.kgsu.network;

import java.io.IOException;
import java.net.Socket;

public interface TCPConnectionListener {

    void onConnection(TCPConnection tcpConnection);
    void onStringInput(TCPConnection tcpConnection, String value);
    void onDisconnect(TCPConnection tcpConnection);
    void onException(TCPConnection tcpConnection, Exception e);
}
