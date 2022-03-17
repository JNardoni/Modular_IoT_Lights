# Modular_IoT_Lights

This is a modular IoT light system. The system is composed of a series of modular 3D printed light panels. 
Each panel contains a cut string of individually addressable LED pixel lights. The smaller strips are wired
together to form one large strip, which is controlled directly by an arduino microprocessor. The arduino contains
patterns, which are played out as decided by the user. The user creates modes, or predefined setups of patterns, on 
the Android App. The app makes RESTful http calls to the arduino, telling it to play patterns as defined by the user.


## Android App

The Android app serves as the main user interface, allowing the user to control the lights on the wall.
In the app, the user can:

1. Setup their wall of lights
2. Setup modes for the wall to run
3. Change between modes
4. Change tuntime settings such as turning the lights on/off and changing brightness

### Panels

In order for the panels to turn on, the Arduino must know how many panels are connected to it. This means the user must
tell it how many panels there are through this app. While current modes don't take much into account in terms of positioning, 
future modes may. As such, defining panels is a little more complicated than simply clicking on how many panels there are. 

### Adding Panels

The add panels is selected in the options menu of the main screen (the three dots in the upper right corner). When selected, a 
dark screen will open with one light blue triangle in the middle, and 3 gray triangles around it. 

#### Active Panels

Blue triangles indicate that it is active - that panel is part of your wall of lights. When active panels are clicked, they are marked
inactive.

#### Inactive Panels

Gray triangles indicate that they are inactive - they are near active panels, but they themselves are more placekeepers, waiting to be activated
themselves. When gray triangles are clicked they are activated - they turn blue, and create more inactive trianelges around them. 

Below is my wall of light panels - I have 10 panels, represented by the 10 blue triangles in my current setup.

<img src="https://github.com/JNardoni/Modular_IoT_Lights/blob/main/Sample%20Images/Add%20Panels.png" title="Adding Modes UI" width = "400" height="908">

### Modes

The lights are not directly controlled. Instead, modes are set up for which the panel of lights run. 
The user decides a few things when creating a mode

1. The pattern for the lights to change

      Patterns are defined by the arduino and determine how the panels transition between colors. Each
      mode uses one of the predefined patterns. 

2. The speed at which the pattern occurs

      Each pattern has the panel colors changing in some way. These transitions can happen faster or slower
      at the users descretion.
   
3. The colors which the pattern uses.

      Each pattern can have up to 8 individual colors, but can have as few as 1. Each panel will light up
      with one of these colors, and transition between them differently.

4. A name for the mode

      The name is used in app only to represent the mode.

#### Adding Modes

On the screen, each of the 4 necessities for a mode can be selected. Each color added to the mode is represented on the Color Bar. 
In the example, the colors are bright, fun colors like light blue, green, pink, etc. More colors can be added by clicking on the Add Color button,
and already set colors can be changed and removed by clicking on its piece of the color bar.

<img src="https://github.com/JNardoni/Modular_IoT_Lights/blob/main/Sample%20Images/Add%20Modes.png" title="Adding Modes UI" width = "400" height="908">

#### Mode List

The main screen of the app consists of a title bar and a list of modes. The title bar has three parts.

1. A green triangle/red square. This is used to turn the light panels on/off
2. A + button, which is used to add more modes
3. An options button. This can be used to change settings, such as the layout of panels and brightness.

A very simple (still Work in progress) version of the main screen is shown below, with a few modes ive set up.

<img src="https://github.com/JNardoni/Modular_IoT_Lights/blob/main/Sample%20Images/List%20of%20Modes.png" title="Adding Modes UI" width = "400" height="908">

## Arduino Server

From a user perspective, very little is to be said about the arduino server. It must be connected to the same wifi as the app is connected to, 
and plugged into the lights. A few portions of the app do need to be changed, depending on user layout.

1. The connection information. This includes both the ssid and password, to your local wifi connection.
2. LED_PIN: The LED pin. This can be set to whichever digital pin you want to plug it into. 
3. LEDS_PER_PANEL: The number of leds per panel. Each of my panels use 11, but more or less can be uesed as seen fit.
4. Save modes. I have mine set up so modes are saved to an SD card, but internal memory is another option, Id also recommend setting up the OTA updates as well
5. LED type and color order. While i use WS2812B, other kinds of individually addressable led strips can be used. This should be changed to represent the users' lights


