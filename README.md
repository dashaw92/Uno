# Uno  
Motivation: The only online Uno game I could find that was decent costs a lot of money on Steam.  

### How to play  
Host:  
```
java me.daniel.uno.Server [port]
OR
java -jar uno.jar [port]
```  
The default port is `1312`.  
You must port forward unless you are all playing on the same network. (See [here](https://portforward.com/) for how)

Player:  
`telnet <ip> <port>` OR `nc <ip> <port>` or with PuTTY, using the `Raw` option.  
Once connected, enter a nickname. If no nickname is entered, a random  
nickname will be created.

During the game, you will be presented with four options on your turn: `PLAY`, `UNO`, `DRAW`, or `SKIP`.  

`PLAY`:  
This command will play the selected card if it is a legal move.  
Usage: `play suit type`  
Example: `play red eight` or `play wild draw four`

`UNO`:  
This command will cause you to win, if you have 1 card.  
Usage: `uno`

`DRAW`:  
This command draws a card from the drawing pile. It can only be used once per turn.  
Usage: `draw`

`SKIP`:  
This command skips your turn, moving on to the next player. You may only use it if you have drawn a card  
and have no legal move available.  
Usage: `skip`
