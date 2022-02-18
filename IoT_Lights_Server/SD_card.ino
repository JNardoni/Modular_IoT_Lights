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

      line = modeFile.readStringUntil('\n'); //gets the third line for the mode, the playspeed        
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
  else { //if not, prints an erro
    Serial.println("error opening test.txt");
  }  
}

//Saves the mode into memory
//Writes the information into a text file on the SD card

//Paramst: int modeToSave: int the number of the mode being saved
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

  
}
