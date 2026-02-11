package com.example.chat;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Minimal TCP chat client with two threads:
 *  - Reader thread: blocks on socket input and prints server messages.
 *  - Main thread: reads from STDIN and writes to the socket.
 */
public class ChatClient {
    public static void main(String[] args) {
        Config cfg = Config.fromArgs(args);
        if (cfg == null) {
            printUsageAndExit();
        }

        try (Socket socket = new Socket(cfg.host, cfg.port)) {
            System.out.printf("Connected to %s:%d as '%s'%n", cfg.host, cfg.port, cfg.username);

            BufferedReader serverIn = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter serverOut = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            BufferedReader stdin = new BufferedReader(
                    new InputStreamReader(System.in, StandardCharsets.UTF_8));

            // Announce name to server (safe if server ignores)
            sendLine(serverOut, "/name " + cfg.username);

            AtomicBoolean running = new AtomicBoolean(true);

            Thread reader = new Thread(() -> {
                try {
                    String line;
                    while ((line = serverIn.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    if (running.get()) {
                        System.err.println("Connection closed by server: " + e.getMessage());
                    }
                } finally {
                    running.set(false);
                }
            }, "server-reader");
            reader.setDaemon(true);
            reader.start();

            String input;
            while (running.get() && (input = stdin.readLine()) != null) {
                if (input.equalsIgnoreCase("/quit")) {
                    sendLine(serverOut, "/quit");
                    break;
                }
                if (!input.startsWith("/")) {
                    sendLine(serverOut, "[" + cfg.username + "] " + input);
                } else {
                    sendLine(serverOut, input);
                }
            }
            running.set(false);
        } catch (IOException e) {
            System.err.println("Unable to connect: " + e.getMessage());
            System.exit(2);
        }
    }

    private static void sendLine(BufferedWriter out, String line) throws IOException {
        out.write(line);
        out.write("\n");
        out.flush();
    }

    private static void printUsageAndExit() {
        System.out.println("Usage: java -jar chat-client-1.0.0.jar --host <host> --port <port> --name <username>");
        System.exit(1);
    }

    private record Config(String host, int port, String username) {
        static Config fromArgs(String[] args) {
            String host = null, name = null;
            Integer port = null;
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "--host" -> host = (i + 1 < args.length) ? args[++i] : null;
                    case "--port" -> {
                        if (i + 1 < args.length) {
                            try { port = Integer.parseInt(args[++i]); } catch (NumberFormatException ignored) {}
                        }
                    }
                    case "--name" -> name = (i + 1 < args.length) ? args[++i] : null;
                }
            }
            if (host == null || port == null || name == null || name.isBlank()) return null;
            return new Config(host, port, name);
        }
    }
}
