
# Java-ServerSocket

This project contains two Java applications for the communication between computers connected on a local area network. Implemented on native Java. No additional libraries required.

##
### Key Features (Client):
-   User-to-User Chat
-   Chat with a specific User which is currently Online
-   Group Chat (currently not available on the client side)
-   Set Server IP Manually


### Current supported commands (Client):
List of commands the user can type
|         Command    |  Function               |
|--------------------|-------------------------|
|`user: message`     | Write a message to user |
|`get time`          | Current Server Time     |
|`exit`              | disconnect from server  |
      
##
### Key Features (Server):
-   Send Message to all Users
-   Admin command console
-   Manage Group chats
-   Manage Group members
-   User Statistics
-   Traffic logging
-   Respond to a 'get current server time' request  
-   Kick all Clients on Server shutdown

### Current supported commands (Server):
List of commands the admin can type and run directly on the server
|       Command  |    Function               |
|----------------|---------------------------|
|`help`          | show help information     |
|`all: message`  | send message to all users |
|`kick user abc` | kick abc from the server  |
|`toggle log`    | log all commands          |
|`list users`    | list all users online     |
|`list groups`   | list all groups online    |
|`exit`          | shut down the server      |
##
### Launch Client app on Command Window:
     cd client/src
     make run  
     
### Launch Server App on Command Window:
     cd server/src
     make run

###
> Please note:
> Some features are only implemented on the server side and can be implemented in the future as well as new commands.
###
SocketServer made by Oliver Rascheja