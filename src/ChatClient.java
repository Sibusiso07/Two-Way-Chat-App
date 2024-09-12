import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost"; // Server address to connect to
    private static final int PORT = 12345; // Port number of the server

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT); // Attempt to establish a connection to the server
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Input stream to receive messages from the server
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // Output stream to send messages to the server
             Scanner scanner = new Scanner(System.in)) { // Scanner to read input from the user

            System.out.println("Connected to the server. Start chatting...");

            // Thread to handle receiving messages from the server
            Thread receiveMessages = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) { // Continuously read messages from the server
                        System.out.println(message); // Print the received message
                    }
                } catch (IOException e) {
                    System.out.println("Connection lost. Please check if the server is still running."); // Friendly message if connection is lost
                }
            });

            receiveMessages.start(); // Start the thread to listen for incoming messages

            // Main thread to handle sending messages to the server
            while (true) {
                String message = scanner.nextLine(); // Read message from the user
                out.println(message); // Send the message to the server
            }
        } catch (IOException e) {
            // Friendly message if the server is not available
            System.out.println("Unable to connect to the server. Please ensure the server is running and try again.");
        }
    }
}
