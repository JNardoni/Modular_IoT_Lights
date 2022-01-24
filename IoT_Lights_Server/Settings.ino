
//Changes settings with the device.
//Change to variables instead? Yeah probs
//First digit: Which setting being changed
//0: On/Off
    //Second digit, 1: on, 0: off
//1: Brightness
    //Second digit = brightness * 10 + a base brightness value so it cant turn off
//2: Current Mode
    //Second digit = the new mode
int changeSettings(String command) {

  if (command[0] == '0') {
    ON = getIntFromCommand(1,command);
  }
  else if (command[0] == '1') {    
    BRIGHTNESS = 10 + 10 * getIntFromCommand(1,command);
  }
  else if (command[0] == '2') {
    int newMode = getIntFromCommand(1,command);

      if (PLAYABLE_MODES[newMode].set == 1) { //If there a new node, initializes the new mode by changing the 
         CURRENT_MODE = newMode;              //active mode and calling the pattern that the mode implements
         switch (PLAYABLE_MODES[newMode].pattern) {
            case 1:
                Pattern_1_init(newMode);
                break;
         }
      }
  }

}
