/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpleServer;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.*;

/**
 * A server program which accepts requests from clients to
 * bounce back their strings.  When clients connect, a new thread is
 * started to handle an interactive dialog in which the client
 * sends in a string and the server thread sends back the
 * original string.
 *
 */
public class SimpleServer {

    /**
     * Application method to run the server runs in an infinite loop
     * listening on port 9001.  When a connection is requested, it
     * spawns a new thread to do the servicing and immediately returns
     * to listening.  The server keeps a unique client number for each
     * client that connects just to show interesting logging
     * messages.  
     */
    
    public static void main(String[] args) throws Exception {
        System.out.println("The simple server is running.");
        int clientNumber = 0;
        try (ServerSocket listener = new ServerSocket(6789)) {
            while (true) {
                new Responder(listener.accept(), clientNumber++).start();
            }
        }
    }

    /**
     * A private thread to handle clients' requests on a particular
     * socket.  The client terminates the dialogue by sending a single line
     * containing only a q.
     */
    public static class Responder extends Thread {
        private final Socket socket;
        private final int clientNumber;

        public Responder(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            log("New connection with client# " + clientNumber + " at " + socket);
        }

        /**
         * Services this thread's client by first sending the
         * client a welcome message then repeatedly reading strings
         * and sending back the version of the string.
         */
        @Override
        public void run() {
            try {

                // Decorate the streams so we can send characters
                // and not just bytes.  Ensure output is flushed
                // after every newline.
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                // Send a welcome message to the client.
                out.println("Hello, you are client #" + clientNumber + ".");
                out.println("Enter a line with only a q to quit.");

                // Get messages from the client, line by line; return them
                // unmodified
                while (true) {
                    String input = in.readLine();
                    if (input == null || input.equals("q")) {
                        break;
                    }
                    out.println(input);
                }
            } catch (IOException e) {
                log("Error handling client# " + clientNumber + ": " + e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    log("Couldn't close a socket, what's going on?");
                }
                log("Connection with client# " + clientNumber + " closed");
            }
        }

        /**
         * Logs a simple message.  In this case we simply write the
         * message to the server applications standard output.
         */
        private void log(String message) {
            System.out.println(message);
        }
    }
}