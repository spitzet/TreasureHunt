//***********************************************// 
 	Treasure Hunt 2014 v9 Readme 
//***********************************************// 
By Thi Cao and Travis Spitze 
  
How to run: 
Navigate to the bin folder in command prompt and type the following two commands with your ip address and desired port number: java network.Server (port) 
java a2.Starter (ip address) (port) 
Gamepad: 
This program was tested with an XBOX 360 For Windows. Since SAGE could not get the controller name automatically, it had to be manually hard coded into the code. It should be noted that the variable gpname is hard coded, and the game will not run without the correct controller name in gpname. 
  
Instructions: 
First, pick whether you want a male or female avatar. The male avatar is "drunk mode", with more difficult controls. Once you have decided the difficulty of your game, you can move your character onto any treasure (any rotating shape) except for the treasure box (cube in the middle) in the middle to receive points. Once points are received, the treasure box will grow and the treasure will disappear. Each player can see their score on a HUD that is displayed at the bottom left of their screen. Also, each player has their own set of treasures to ensure that each player has an equal opportunity to maximize their score, and their own treasure box to keep track of how much treasure that they have collected. 
  
Scripting: 
Players can modify the js file called "PlayerName" inside the scripting folder before OR while the program is running to dynamically change their name. We begin with our uni­gender character named "Pat" by default. 
  
Controls for keyboard: 
A/D/W/S: strafe left/right/forward/backward. 
-Left-arrow / Right-arrow: rotate the camera left or right. 
-Up-arrow / Down-arrow: zoom the camera in or out. 
NUMPAD4/NUMPAD6: rotate the camera left or right as well as the direction that the avatar is facing. 
J/K: Toggle animations. 
ESC: exit the game. 
  
Controls for gamepad (God Mode!): 
-X-axis: strafe camera left and right in the same manner as the A and S keys. 
-Y-axis: strafe camera forward and backward in the same manner as the W and D keys. ­RX­axis: rotate camera left or right, the same as the Left/Right arrow keys. 
-RY-axis: zoom the camera in or out, the same as the Up/Down arrow keys. 
-A/B: Toggle animations. 
-Z-axis: rotate the camera left or right as well as the direction that the avatar is facing, the same as the NUMPAD4/NUMPAD6 keys. 
  
Hardware Requirements: 
This game is now set by default to windowed mode. The full screen function does not display properly on some monitors. To change back to full screen mode, the FULL_SCREEN_MODE variable can be changed to true in the createDisplaySystem() function. 
  
Skybox, Terrain, and Networking. 
After you pick your character, you will notice that a skybox has been added to the game, as well as terrain. This means you must navigate around and over hills in order to collect your treasure. Also, in this version of Treasure Hunt 2014, multiple players can play together over a network. Any action that a player performs (such as moving, or performing their animations) can be seen by other players Additionally, the movement of npcs are synced across each client. 
  
External Models, Skinning: 
Unlike previous versions of the game, the player's avatar is no longer a simple shape. You can choose whether to control a male character (drunk mode) or a female character. Also, skins have been added to these characters. Your character and character skin can be seen by other players inside the game's network. 
  
Sound: 
This version of TreasureHunt2014 incorporates 3D sound into the game. The treasure box in the middle plays a mysterious trumpet sound as you gather your treasure. As you move around the treasure box you can hear the direction from which the sound is coming as well as how far away it is. 
  
NPCs/AI: 
NPCs with intelligent AI have been added to the game. If you get too close to the five large cubes 
(which will have models in later versions of this game), they will begin to run away from you! Once they are far enough away from you, they will once again feel safe and go about their business. These cube critters have been added purely for aesthetic reasons in this version of the game, and may be incorporated into gameplay in later versions. 
  
Contributions: 
Thi Cao: skybox, terrain, external female model and her skinning, sound. 
Travis Spitze: readme, scripting, external male model and his skinning. Thi and Travis: networking, NPCs/AI, and animation. 
  
Extra Credit: 
Each character has two animations to choose from: the marching animation and the dancing animation. The controls for each of these animations are listed above. 
  
 
 
