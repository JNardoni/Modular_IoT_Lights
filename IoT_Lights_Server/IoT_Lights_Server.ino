#include <WiFi101.h>
//#include <WiFiClient.h>
//#include <WiFiServer.h>
//#include <WiFiSSLClient.h>
//#include <WiFiUdp.h>
//#include <SPI.h>
#include <WiFi101OTA.h>
#include <aREST.h>
#include <SD.h>


#include <FastLED.h>
#include <time.h>
#include <stdlib.h>

//Network Setup

char ssid[] = "Secrets";
char pass[] = "Secrets";
int status = WL_IDLE_STATUS;
WiFiServer server(80);

aREST rest = aREST();
//Definitions for led strip setup
#define LED_PIN         0
#define LEDS_PER_PANEL  11

#define LED_TYPE    WS2812B
#define COLOR_ORDER GRB
#define UPDATES_PER_SECOND 50

uint8_t NUM_PANELS = 0;
int NUM_LEDS = NUM_PANELS*LEDS_PER_PANEL;

uint8_t count = 0;
uint8_t GLBL1 = 0;
uint8_t GLBL2 = 0;

//Definitions for modes and status

#define MAX_MODES   10

uint8_t ON = 1;
uint8_t BRIGHTNESS = 60;
uint8_t CURRENT_MODE = 0;
uint8_t MODES_SET = 0;


//SD card and file setup
const int chipSelect = 7;
File modeFile;

//Mode: A mode is a set playable structure for the light panels. It consists of,
//1. A pattern. Patterns are premade, and consist of various ways that the panels change their color
//        It can consist of each panel smoothly changing lights, a rainbow of lights moving up/down the wall, etc
//2. A playspeed. This determines how fast the pattern on the lights is updating its position
//3. A color palatte. Color palattes define what colors are being used for the panels in the current pattern
//4. A pointer to the function of the pattern. This allows the pattern to be played dynamically later on without a switch
//5. The number of colors present in the color palette
struct panelModes{
  uint8_t set = 0;
  uint8_t playspeed;  
  uint8_t pattern; 
  uint8_t colors;
   
  void  (*pattern_func)();

  CRGB colorPalette[16];  
};

struct panelModes PLAYABLE_MODES[MAX_MODES];



CRGBPalette16 currentPalette;
TBlendType    currentBlending;
CRGB *leds; //leds stores an array of the current config of each led for fastled
            //Instead, a pointer to an array. Each spot is allocated on startup, as 
            //well as anytime the number of panels is changed

// Declare functions to be exposed to the API
int addMode(String command);
int setupPanels(String command);

struct panelOrient {
  uint8_t panelNum;
  uint8_t height;
  uint8_t firstLedInPanel;

  uint8_t upright; //0 if base is on bottom, 1 if base on top
  panelOrient *left;
  panelOrient *right;
  panelOrient *above;
  panelOrient *below;
};

//panelOrient PanelInfo = new panelOrient[NUM_PANELS];


void configurePanels() {

}


void changePanelColor(int firstLED, CRGB color) {
  for (int i = firstLED; i < firstLED+LEDS_PER_PANEL; i++) {
    leds[i] = color;    
  }  
  FastLED.show();
}

void setup() {
  delay( 3000 ); // power-up safety delay
  
  Serial.begin(9600);   
  Serial.println("Start Serial ");   
   
  //---------LED strip setup  -----------
  FastLED.addLeds<LED_TYPE, LED_PIN, COLOR_ORDER>(leds, NUM_LEDS).setCorrection( TypicalLEDStrip );
  FastLED.setBrightness(  BRIGHTNESS );
  
  pinMode(LED_PIN, OUTPUT); //setup the led pin   
  currentBlending = LINEARBLEND;

  //------Setup the SD card and file---------
  //Init the SD card
  if (!SD.begin(chipSelect)) {
    Serial.println("initialization failed!");
    return;
  }
  //loads the modes from the save file
  pinMode(SS, OUTPUT);
  loadModes();


  //--------Setup the restful functions---------
  // Functions to be exposed for restful calls
  //Can have up to 5 functions for the MKR1000
  rest.function("setup_panels",setupPanels);
  rest.function("add_mode",addMode);
  rest.function("settings",changeSettings);


  //initializations, the actual leds and RNG
  initLEDarray();  
  randomSeed(analogRead(1));
  
  // Give name and ID to device (ID should be 6 characters long)
  rest.set_id("1");
  rest.set_name("IoT Lights");
    

  while ( status != WL_CONNECTED) {
    Serial.print("Attempting to connect to SSID: ");
    Serial.println(ssid);                   // print the network name (SSID);
    // Connect to WPA/WPA2 network. Change this line if using open or WEP network:
    status = WiFi.begin(ssid, pass);
    // wait 10 seconds for connection:
    delay(10000);
  }  

  // start the WiFi OTA library with internal (flash) based storage
  WiFiOTA.begin("Arduino", "Password01", InternalStorage);
  
  server.begin();                           // start the web server on port 80
  printWifiStatus();                        // you're connected now, so print out t

}
// put your main code here, to run repeatedly:
//Purpose:
//1. Check for software updates
//2. Listens for a conneciton
//3. If on, allows the pattern to move forward 1 cycle
//4. 
void loop() {
  //Listens for OTA updates
  WiFiOTA.poll();
  
  WiFiClient client = server.available();   // listen for incoming clients
  if (!client) { // if you get a client,
    // close the connection:
    FastLED.delay(2000);
  }
  else {
    
    if (ON) {
      PLAYABLE_MODES[CURRENT_MODE].pattern_func();
    } 
    //FastLED.show();   
    FastLED.delay(1000 / UPDATES_PER_SECOND);

    rest.handle(client);
  }
}

//Initiates the panels so the program knows how many panels theyre sending 
//information to. Also update num_leds
//RESTful call from the app
//Params: String with the number of panels, converted to int inside
//Output: 1 on success, 0 on fail 
int setupPanels(String command) {

  //Checks if an appropriate number of panels is added
  if (command.toInt() > 0 && command.toInt() < 256) {
    NUM_PANELS = command.toInt(); //Convert string to int
    NUM_LEDS = NUM_PANELS * LEDS_PER_PANEL; //Also changes number of leds
    initLEDarray(); //Creates new led array
    return 1;
  }
  return 0;  
}

//Initialize Led arrays
//leds color stored in array of size num_leds
//When new panels are added (or panels removed) the old array is freed and a new one is created
void initLEDarray() {

  if (leds != NULL) //frees old leds
    free (leds);
  
  leds = (CRGB *) malloc (NUM_LEDS * sizeof (CRGB));  //create new array
  
}

void printWifiStatus() {
  // print the SSID of the network you're attached to:
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());
 
  // print your WiFi shield's IP address:
  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);
 
  // print the received signal strength:
  long rssi = WiFi.RSSI();
  Serial.print("signal strength (RSSI):");
  Serial.print(rssi);
  Serial.println(" dBm");
  // print where to go in a browser:
  Serial.print("To see this page in action, open a browser to http://");
  Serial.println(ip);
}
