import java.io.*;
import java.net.*;

public class ChatServer {
    private static final int PORT = 12345; // Port on which the server will listen

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) { // Create a server socket that listens on the specified port
            System.out.println("Server started. Waiting for clients to connect...");

            // Accept connections from two clients
            Socket client1 = serverSocket.accept();
            System.out.println("Client 1 connected.");
            Socket client2 = serverSocket.accept();
            System.out.println("Client 2 connected.");

            // Create handlers for both clients, passing each client's socket and the other client's socket for communication
            ClientHandler handler1 = new ClientHandler(client1, client2, "Client 1");
            ClientHandler handler2 = new ClientHandler(client2, client1, "Client 2");

            // Start the threads for both handlers to handle messages concurrently
            new Thread(handler1).start();
            new Thread(handler2).start();
        } catch (IOException e) {
            e.printStackTrace(); // Handle exceptions related to the server socket
        }
    }
}

// Handles communication between two clients
class ClientHandler implements Runnable {
    private Socket clientSocket; // Socket for this client
    private Socket otherClientSocket; // Socket for the other client
    private BufferedReader in; // Input stream to receive messages from this client
    private PrintWriter out; // Output stream to send messages to the other client
    private String clientName; // Name of the client ("Client 1" or "Client 2")

    // Constructor to initialize client handler with sockets and client name
    public ClientHandler(Socket clientSocket, Socket otherClientSocket, String clientName) {
        this.clientSocket = clientSocket;
        this.otherClientSocket = otherClientSocket;
        this.clientName = clientName; // Set the client name
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // Initialize input stream
            out = new PrintWriter(otherClientSocket.getOutputStream(), true); // Initialize output stream
        } catch (IOException e) {
            e.printStackTrace(); // Handle exceptions during stream initialization
        }
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) { // Read messages from this client
                System.out.println(clientName + ": " + message); // Print the received message with the client's name
                out.println(clientName + ": " + message); // Send the message to the other client with the client's name
            }
        } catch (IOException e) {
            System.out.println(clientName + " disconnected."); // Log that the client has disconnected
        } finally {
            // Close resources when the client disconnects
            try {
                if (in != null) in.close(); // Close input stream if it was opened
                if (out != null) out.close(); // Close output stream if it was opened
                if (clientSocket != null) clientSocket.close(); // Close this client's socket
                if (otherClientSocket != null) otherClientSocket.close(); // Close the other client's socket
            } catch (IOException e) {
                e.printStackTrace(); // Handle exceptions during resource closing
            }
        }
    }
}
