
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

//------------------PATTERN 2------------------
//Pattern 2: Solid Wall Fill
//  The entire wall maintains the same color, slowly transitioning between the colors in the palette

//---Initialize pattern 2--
//Gives the wall the same color based on the new modes color palette
void Pattern_2_init(int mode_num) {
 // PLAYABLE_MODES[mode_num]
  currentBlending = LINEARBLEND;
  count = 0;

  for (int i = 0; i < NUM_PANELS; i++) {
    FillPanel(i, PLAYABLE_MODES[mode_num].colorPalette[0]);    
  }
  FastLED.show();
  FastLED.delay(1000 / UPDATES_PER_SECOND);
}

void Pattern_2() {

  count++;
  
  if (count >= 150-(PLAYABLE_MODES[CURRENT_MODE].playspeed*10)) { //Determines the speed which the pattern changes
    count = 0;                                                    //Base speed: 150 = 3 seconds, 

    int randColor = random(PLAYABLE_MODES[CURRENT_MODE].colors); //picks a random color from the set colors

    for (int i = 0; i < NUM_PANELS; i++) {
      FillPanel(i, PLAYABLE_MODES[CURRENT_MODE].colorPalette[randColor]); //fills all the panels
    }    
    
  }  
}

//------------------PATTERN 3------------------
//Pattern 3: Fireworks
//  The wall stays black, as the night sky. Then, on occasion, one panel will light upbright, before dimming back down

//---Initialize pattern 3--
//The lights are turned off
void Pattern_3_init(int mode_num) {
 // PLAYABLE_MODES[mode_num]
  currentBlending = LINEARBLEND;
  count = 0; // time since last update, based on clock speed
  
  GLBL1 = random(2,35); //Stores the light being turned on
  GLBL2 = 0; //Stores the time left on the light

  for (int i = 0; i < NUM_PANELS; i++) {
    FillPanel(i, CRGB::Black);    
  }
  FastLED.show();
  FastLED.delay(1000 / UPDATES_PER_SECOND);
}

void Pattern_3() {

  count++;

  //Two settings -
  //If 1 > 0 and 2 = 0, all lights are off, 1 is counting down towared the next "Firework"
  //Else, a firework is currently going off. The light thats on is stored in 1, and the timer is stored in 2
   if (count >= 20-(PLAYABLE_MODES[CURRENT_MODE].playspeed*10)) { //Determines the speed which the pattern changes
    count = 0;                                                    

    //Firework is on - Continues to count down the blast duration
    if (GLBL2 > 0) {
      GLBL2--;

      //Timer is done - Turn off the light, start the timer for the next one
      if (GLBL2 == 0) {
        FillPanel(GLBL1, CRGB::Black); //fills the panel
        GLBL1 = random(2,35);
      }      
    }

    else { //Countdown the timer for the next firework
      GLBL1--;

      //If the timer goes off for the next firework,
      if (GLBL1 == 0) {
        
        int randColor = random(PLAYABLE_MODES[CURRENT_MODE].colors); //picks a random color from the set colors
        GLBL1 = random(NUM_PANELS); //picks a random panel
        GLBL2 = random(10,40); //Picks a random duration for the firework
        
        FillPanel(GLBL1, PLAYABLE_MODES[CURRENT_MODE].colorPalette[randColor]); //fills the panel
      }
    }   
  }  
}
