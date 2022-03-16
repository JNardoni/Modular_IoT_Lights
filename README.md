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
2. Swtup modes for the wall to run
3. Change between modes
4. Change tuntime settings such as turning the lights on/off and changing brightness

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


## Arduino Server
