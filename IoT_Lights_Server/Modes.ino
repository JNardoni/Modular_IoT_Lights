//Mode: A mode is a set playable structure for the light panels. It consists of,
//1. A pattern. Patterns are premade, and consist of various ways that the panels change their color
//        It can consist of each panel smoothly changing lights, a rainbow of lights moving up/down the wall, etc
//2. A playspeed. This determines how fast the pattern on the lights is updating its position
//3. A color palatte. Color palattes define what colors are being used for the panels in the current pattern
/*
struct panelModes{
  uint8_t set = 0;
  uint8_t playspeed;  
  uint8_t pattern;  
  void  (*pattern_func)();

  CRGB colorPalette[16];  
};

struct panelModes PLAYABLE_MODES[MAX_MODES];
*/

//Adds, updates, or deletes a playable mode for the light panels
//Called from the android app when the user adds a mod
//Params: a large string, each character representing the way the panels are setup
//First character: Integer, 0/1, determing if the mode is being added or deleted
//Second character: Integer, referring to the pattern the user selected
//Third character: Integer, refers to the speed the user has chosen
//Chars 4-56: Ascii characters. Their value is converted to an integer, and used to
//make up the colors of the color palette
//Three Characters make up each color in the palette, 1 for R, 1 G, 1 for B. Can be from
//decimal 33-118 ( '!' to 'v' on the ascii table). Their values are converted to 0-255, with a granularity
//of 3 (Values 0,3,6...255).  Since LEDs arre imperfect , losing a few potential spots isnt a big deal
int addMode(String command) {

  //First character : whether its adding or deleting a mode. If 0, mode in character 2 
  //is being deleted
  if (command[0] == '0') {
    PLAYABLE_MODES[command[1]].set = 0;
    return 1;
  }
  //Checks if modes are available to use. If all 10 modes are set, returns 0
  if (MODES_SET >= MAX_MODES) {
    return 0;
  }

  //2nd spot in the command string: which slot to write to. This is used in case a previous mode is being
  //updated as opposed to starting from scratch. If being rewritten, just writes over the old mode entirely

  int mode_slot = getIntFromCommand(1,command);

  PLAYABLE_MODES[mode_slot].set = 1;  //Adds it to the known set of modes
  PLAYABLE_MODES[mode_slot].pattern = getIntFromCommand(2,command)+1; //Adds the pattern. Sends 0-9, but modes start at 1. Offets by 1
  PLAYABLE_MODES[mode_slot].playspeed = getIntFromCommand(3,command);  //Adds the playspeed

  //For each color in the color palette, grabs the next three spaces of the command string
  //Auto converts them from char to integer, and performs the needed modification to range the entire 0-255 spectrum.
  //Since the chars start at 33 (!), each val is subtracted by 33 so 33 = 0
  //The string spaces begin at 4 and stretch to 55
  int j = 0;
  while (command[4+j*3] != NULL) {
    PLAYABLE_MODES[mode_slot].colorPalette[j] = CRGB((command[4+j*3]-33)*3,(command[5+j*3]-33)*3,(command[6+j*3]-33)*3);
    j++;
  }
  //Assigns the number of colors being used in the palette
  PLAYABLE_MODES[mode_slot].colors = j;

  //assigns a function to be assigned by its associated pattern
  assignFunction(mode_slot);

  //Saves the mode into memory. Passes the position of the mode
  saveModes(mode_slot);
  
  return 1;  
}


void assignFunction(int modeNum) {
  //Switch statement to assign the mode storage struct a pointer to the pattern 
  //This allows the main Loop to continuously call the function without a block of switchs/if statements  
  switch(PLAYABLE_MODES[modeNum].pattern) {
    case 1:
      PLAYABLE_MODES[modeNum].pattern_func = Pattern_1;
  }
}
