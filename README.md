# Java Socket Server

A lightweight, multi-threaded **Java Socket Server & Client** application for communication over a local area network.  
Built entirely with **native Java (no external libraries required)**.  

This project demonstrates my skills in **network programming, multithreading, and client/server architecture**.  

![Java](https://img.shields.io/badge/Java-8+-blue.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)
![Build](https://img.shields.io/badge/build-Maven-success.svg)
---

## Key Features

### Client
- User-to-User Chat  
- Private messaging with a specific user  
- Group Chat support (server-side implemented, client-side planned) 
- Manual configuration of server IP  

### Server
- Broadcast messages to all connected users  
- Admin command console with interactive commands  
- Manage group chats and group members  
- User statistics and traffic logging  
- Responds to client requests 
- Kick all clients on server shutdown  
---

## Supported Commands

### Client

| Command           | Function                      |
|-------------------|-------------------------------|
| `user: message`   | Send a message to `user`      |
| `get time`        | Request current server time   |
| `exit`            | Disconnect from server        |

### Server

| Command            | Function                        |
|--------------------|---------------------------------|
| `help`             | Show help information           |
| `all: message`     | Send message to all users       |
| `kick user abc`    | Kick user `abc` from the server |
| `toggle log`       | Enable/disable traffic logging  |
| `list users`       | List all online users           |
| `list groups`      | List all active groups          |
| `exit`             | Shut down the server            |

---

## Getting Started

### Run the Client

```bash
cd client/src
make run
```

### Run the Server

```bash
cd server/src
make run
```

---

### Example Usage

Start the server:
```bash
java -cp server/src Server
```

Connect a client:
```bash
java -cp client/src Client
```

When the client is running, Send a message to another user (or use any other command listed above):
```bash
alice: Hello Bob!
```

Request the current server time:
```bash
get time
```

---

### Architecture Overview

Multiple clients connect to a single SocketServer within the same local area network.
```text
[Client A] ----\
[Client B] ----- [ SocketServer ] ----> Command Processing
[Client C] ----/
```

---

### Motivation

This project is a showcase of my expertise in:
- Java programming
- Networking with sockets
- Multithreaded server design
- Command parsing & protocol design
- Clean separation of client/server logic

It demonstrates my ability to build scalable, maintainable backend systems in Java.
Skills demonstrated: Java, Socket Programming, Multithreading, Client/Server Design, Protocol Design

---

### License

MIT License – free for personal and commercial use.

---

### Author

Author: Oliver Rascheja
Website: https://oliver.rascheja.com
