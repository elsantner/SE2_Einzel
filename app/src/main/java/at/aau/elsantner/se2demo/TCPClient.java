package at.aau.elsantner.se2demo;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import io.reactivex.Observable;

public class TCPClient {
    private PrintWriter bufferOut;
    private BufferedReader bufferIn;
    private final String url;
    private final int port;

    public TCPClient(String url, int port) {
        this.url = url;
        this.port = port;
    }

    public Observable<String> send(final String msg) {
        return Observable.fromCallable(() -> {
            try {
                String receivedString;
                // init server connection
                InetAddress serverIP = InetAddress.getByName(url);
                try (Socket socket = new Socket(serverIP, port)) {
                    // init in and out streams
                    bufferOut = new PrintWriter(
                            new BufferedWriter(
                                    new OutputStreamWriter(socket.getOutputStream())), true);
                    bufferIn = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
                    // send mnr to server and ...
                    bufferOut.println(msg);
                    // ... receive response from server
                    receivedString = bufferIn.readLine();
                }
                return receivedString;
            } catch (Exception ex) {
                Log.e("TCPClient", "Error", ex);
                return "Error: " + ex.getMessage();
            }
        });
    }
}
