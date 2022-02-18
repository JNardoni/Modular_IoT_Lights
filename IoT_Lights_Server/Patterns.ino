
//------------------PATTERN 1------------------
//Pattern 1: Generic blend
//  Every few seconds, a random panel is picked, and its color is changed to a random
//  color in the palette

//---Initialize pattern 1--
//Gives each panel a cover based  on the new modes color palette
void Pattern_1_init(int mode_num) {
 // PLAYABLE_MODES[mode_num]
  currentBlending = LINEARBLEND;
  count = 0;

  for (int i = 0; i < NUM_PANELS; i++) {
    FillPanel(i, PLAYABLE_MODES[mode_num].colorPalette[i%16]);    
  }
  FastLED.show();
  FastLED.delay(1000 / UPDATES_PER_SECOND);
}

void Pattern_1() {

  count++;
  
  if (count >= 150-(PLAYABLE_MODES[CURRENT_MODE].playspeed*10)) { //Determines the speed which the pattern changes
    count = 0;                                                    //Base speed: 150 = 3 seconds, 
    int randPanel = random(NUM_PANELS); //picks a random color
    int randColor = random(PLAYABLE_MODES[CURRENT_MODE].colors); //picks a random color from the set colors

    FillPanel(randPanel, PLAYABLE_MODES[CURRENT_MODE].colorPalette[randColor]); //fills the panel
    
  }  
}
