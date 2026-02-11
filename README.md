Overview

This project is a simple multi-client chat application built using the TCP protocol.
It allows multiple clients to connect to a server and communicate in real time through message broadcasting.

The goal of this project was to consolidate knowledge of network programming, concurrency, and blocking I/O handling using Java.


Features:

- Multi-client support (concurrent connections)
- Username selection on connection
- Message broadcasting to all connected clients
- /quit command to leave the chat
- Thread-based concurrency (server and client)
- Maven build configuration


Architecture

Server:

- Uses ServerSocket to accept incoming TCP connections
- Spawns a new thread for each connected client
- Maintains a list of active clients
- Broadcasts incoming messages to all connected clients
- Handles client disconnection

Client:

- Connects to the server via Socket
- Uses one thread to send messages
- Uses one thread to listen for incoming messages
- Handles blocking I/O through concurrency


Technologies Used

- Java
- TCP Sockets
- Multithreading
- Maven


How to Build:

Make sure you have Java and Maven installed.

"mvn clean package"

This will generate the executable JAR file inside the target/ directory.



How to Run:
Start the Server

"java -jar target/<server-jar-name>.jar"

Start the Client
"java -jar target/<client-jar-name>.jar"

When prompted, enter your username and start chatting.




Testing with Netcat:

You can use Netcat to simulate clients or servers during development.




To simulate a client:

"nc <host> <port>"

To simulate a server:

"nc -l <port>"


Learning Objectives

- Understand TCP communication
- Work with blocking I/O
- Use threads to handle concurrency
- Manage multiple client connections
- Package Java applications with Maven
