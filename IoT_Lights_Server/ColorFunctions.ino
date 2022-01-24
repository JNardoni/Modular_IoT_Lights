
//Fill the led panels with lights
//Using the LEDS_PER_PANEL, sets up all the individual leds in the panel to the same color
//    INPUT: INT  number of the panel being turned on, CRGB color combination to set the panel
void FillPanel(int panelNum, CRGB Color) {

  for (int i = 0; i < LEDS_PER_PANEL; i++) {
    leds[i+panelNum*LEDS_PER_PANEL] = Color;
  }  
}

//Converts a command string to the int in the position you need
//  Input: INT position in the command string, STRING the command string which needs parsing
//  Output: INT value in that space in the string
int getIntFromCommand(int cmdPosition, String command) {
  int value = 0;
  
  char c = command[cmdPosition];  //Grab the char in the needed spot in the arry
  value = c - '0';    //Char to int
  return value;       //Return int
}
