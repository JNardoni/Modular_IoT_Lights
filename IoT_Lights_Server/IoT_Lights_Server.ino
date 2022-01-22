#include <WiFi101.h>
#include <WiFiClient.h>
#include <WiFiServer.h>
#include <WiFiSSLClient.h>
#include <WiFiUdp.h>
#include <SPI.h>
#include <WiFi101OTA.h>
#include <aREST.h>

//#define NUM_LEDS  6

#include <FastLED.h>
#include <time.h>
#include <stdlib.h>

//Network Setup

char ssid[] = "Nardoni Network";
char pass[] = "2E0325D0CE";
int status = WL_IDLE_STATUS;
WiFiServer server(80);

aREST rest = aREST();
//Definitions for led strip setup
#define LED_PIN         0
#define LEDS_PER_PANEL  11

#define LED_TYPE    WS2812B
#define COLOR_ORDER GRB
#define UPDATES_PER_SECOND 10000

uint8_t NUM_PANELS = 0;
int NUM_LEDS = NUM_PANELS*LEDS_PER_PANEL;

//Definitions for modes and status

#define MAX_MODES   10

//int NUM_PANELS = 1;
//int NUM_LEDS = NUM_PANELS*LEDS_PER_PANEL;
uint8_t ON = 1;
uint8_t BRIGHTNESS = 60;
uint8_t CURRENT_MODE = 0;
uint8_t MODES_SET = 0;

/* MODES
 * 
 * 1. 
 * 2.
 * 3.
 * 4.
 * 5.
 * 6.
 * 7.
 * 8.
 */

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
   
  //LED strip setup  
  FastLED.addLeds<LED_TYPE, LED_PIN, COLOR_ORDER>(leds, NUM_LEDS).setCorrection( TypicalLEDStrip );
  FastLED.setBrightness(  BRIGHTNESS );
  
  pinMode(LED_PIN, OUTPUT);       
  currentBlending = LINEARBLEND;

   
  // Function to be exposed
  rest.function("setup_panels",setupPanels);


  initLEDarray();

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
void loop() {
  //Listens for OTA updates
  WiFiOTA.poll();
  
  WiFiClient client = server.available();   // listen for incoming clients

  if (!client) { // if you get a client,
    //MODE = getNewMode(client);
    //Serial.println(MODE);
    // close the connection:
    client.stop();
    Serial.println("client disonnected");
  }

  if (ON) {


    
  }
  
    for (int j = 0; j < LEDS_PER_PANEL; j++) {
      leds[j] = CRGB::Blue;
    }
    for (int j = LEDS_PER_PANEL; j < LEDS_PER_PANEL*2; j++) {
      leds[j] = CRGB::Red;
    }
    for (int j = LEDS_PER_PANEL*2; j < LEDS_PER_PANEL*3; j++) {
      leds[j] = CRGB::Purple;
    }
  
/*
  switch (MODE) {
    case 0 :
      for (int i =0; i < NUM_LEDS; i++) {
        leds[i] = CRGB::Red;
      }
      break;

    case 1 :
      for (int i =0; i < NUM_LEDS; i++) {
        leds[i] = CRGB::Blue;
      }
      break;

    case 2 :
      for (int i =0; i < NUM_LEDS; i++) {
        leds[i] = CRGB::Green;
      }
      break;
      
    default:
      break;
  }      */
  
  FastLED.show();   

  FastLED.delay(100);

  rest.handle(client);
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
    initLEDarray();
    return 1;
  }
  return 0;  
}


void initLEDarray() {

  if (leds != NULL)
    free (leds);
  
  leds = (CRGB *) malloc (NUM_LEDS * sizeof (CRGB));  
  
}

//Mode 1 : 
void Mode_1() {

  

  
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
