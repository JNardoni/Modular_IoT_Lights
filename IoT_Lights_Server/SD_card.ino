/*  Loads the modes, which are being stored as plan text 
 *  from the SD card.
 *  Eagerly stores the data in the Mode struct, so while the lights 
 *  run can easily switch between modes
 *  ToDO - Possibly switch to a lazy loading? Would need to be done 
 *  if many more modes are creatable
*/
void loadModes() {

  String line;
  int modeNum = 0;

  modeFile = SD.open("modelist.txt", FILE_READ);

  //check whether the file opens correctly
  if (modeFile) {  //If it opens, load the saved modes
    while (modeFile.available()) { //Loops through each mode
      line = modeFile.readStringUntil('\n'); //gets the individual line. First line says Mode 0
      
      line = modeFile.readStringUntil('\n'); //gets the second line for the mode, the pattern         
      PLAYABLE_MODES[modeNum].pattern = line.toInt(); //set pattern
      
      line = modeFile.readStringUntil('\n'); //gets the third line for the mode, the playspeed        
      PLAYABLE_MODES[modeNum].playspeed = line.toInt(); //set pattern      

      line = modeFile.readStringUntil('\n'); //gets the fourth line for the mode, the vairous colors being used      
      PLAYABLE_MODES[modeNum].colors = line.toInt(); //set pattern     

      //For loop cycles through each color assigned to that individual mode
      for (int i = 0; i < PLAYABLE_MODES[modeNum].colors; i++) {
        line = modeFile.readStringUntil('\n');  //Reads the line
        PLAYABLE_MODES[modeNum].colorPalette[i] = line.toInt();  //Saves the color
      }

      //lastly assigns the needed function pointer. Not saved in memory so just loads it off the pattern
      assignFunction(modeNum);
      modeNum++;
    }
  }
  else { //if not, prints an error
    Serial.println("Could not open the mode file");

    //Make the file in case it doesn't exist
    SD.open("modelist.txt", FILE_WRITE);
  }  
  modeFile.close();
}

//Saves the mode into memory
//Writes the information into a text file on the SD card

//Params: int modeToSave: int the number of the mode being saved
//return: none
void saveModes(int modeToSave) {

  modeFile = SD.open("modelist.txt", FILE_WRITE);

  //Checks if the file was properly loaded
  if (modeFile) {
    modeFile.println("Mode " + modeToSave);
    modeFile.println(PLAYABLE_MODES[modeToSave].pattern);  //Stores the pattern num
    modeFile.println(PLAYABLE_MODES[modeToSave].playspeed); //stores the speed
    modeFile.println(PLAYABLE_MODES[modeToSave].colors);   //stores the number of colors saved

    //For each color thats been assigned to a mode, prints it to the file on its own line
    for (int i = 0; i < PLAYABLE_MODES[modeToSave].colors; i++) {
      modeFile.println(PLAYABLE_MODES[modeToSave].colorPalette[i]);
    }
  }
  else { //if the file cant be opened
    Serial.println("Error opening file");
  }

  modeFile.close();
}

/*
 * Saves the number of panels to the card.
 * Done when the panels are initialized, or when new panel setups are added
 * 
 * Params: number of panels
 * returns: none
 * 
 */
void savePanels(int numPanels) {

    //Remove the current iteration of the file - To be replaced with a new panel setup
    SD.remove("panel_layout.txt");

    modeFile = SD.open("panel_layout.txt", FILE_WRITE);

    //Makes sure the file opened properly
    if (modeFile) {
      modeFile.println(numPanels); //Write to file
    }
    else {
      Serial.println("Error opening file");
    }
    modeFile.close();
}

/*
 * Loads the panels which have been saved to the card.
 * Done in setup, when the arduino is turned on
 * 
 * Params: none
 * returns: number of panels
 * 
 */
int loadPanels() {

    modeFile = SD.open("panel_layout.txt", FILE_READ);
    String line;
    //Check if the file exists
    if (modeFile) {
      line = modeFile.readStringUntil('\n');
      modeFile.close();
      return line.toInt();
    }
    //If file doesnt exist, no panels setup, 0 panels
    else {
      return 0;
    }
}
