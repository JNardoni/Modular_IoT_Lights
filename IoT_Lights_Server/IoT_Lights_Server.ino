#include <WiFi101.h>
#include <WiFiClient.h>
#include <WiFiServer.h>
#include <WiFiSSLClient.h>
#include <WiFiUdp.h>
#include <SPI.h>
#include <WiFi101OTA.h>

//#define NUM_LEDS  6

#include <FastLED.h>
#include <time.h>
#include <stdlib.h>


#define LED_PIN   0
#define LEDS_PER_PANEL  11
#define NUM_PANELS 3
#define NUM_LEDS  NUM_PANELS*LEDS_PER_PANEL

//#define BRIGHTNESS  175
#define LED_TYPE    WS2812B
#define COLOR_ORDER GRB
#define UPDATES_PER_SECOND 10000


char ssid[] = "Nardoni Network";
char pass[] = "2E0325D0CE";
int status = WL_IDLE_STATUS;
WiFiServer server(80);

//int NUM_PANELS = 1;
//int NUM_LEDS = NUM_PANELS*LEDS_PER_PANEL;

int BRIGHTNESS = 60;
int MODE = 0;

/*
 * MODES
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
CRGB leds[NUM_LEDS];

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
  //LED strip setup
  FastLED.addLeds<LED_TYPE, LED_PIN, COLOR_ORDER>(leds, NUM_LEDS).setCorrection( TypicalLEDStrip );
  FastLED.setBrightness(  BRIGHTNESS );
    
  currentBlending = LINEARBLEND;
  
  Serial.begin(9600); 
  
  pinMode(LED_PIN, OUTPUT);
  
  Serial.println("Start Serial ");  
  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("NOT PRESENT");
    return; // don't continue
  }
  Serial.println("DETECTED");


  while ( status != WL_CONNECTED) {
    Serial.print("Attempting to connect to Network named: ");
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


  for(int i = 0; i < NUM_PANELS; i++) {
    
  }

}
// put your main code here, to run repeatedly:
void loop() {
  WiFiOTA.poll();
  
  WiFiClient client = server.available();   // listen for incoming clients

  if (client) {                             // if you get a client,
    MODE = getNewMode(client);
    //Serial.println(MODE);
    // close the connection:
    client.stop();
    Serial.println("client disonnected");
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
  Serial.print("\nMode: ");
  Serial.print(MODE);
  FastLED.delay(100);
}

int getNewMode(WiFiClient client) {
    Serial.println("new client");           // print a message out the serial port
    String currentLine = "";                // make a String to hold incoming data from the client

       
    while (client.connected()) {            // loop while the client's connected
      if (client.available()) {             // if there's bytes to read from the client,
        char c = client.read();             // read a byte, then
        Serial.println(c);
        //currentLine += c;                   
        if (c == '\n') {                    // if the byte is a newline character
          // if the current line is blank, you got two newline characters in a row.
          // that's the end of the client HTTP request, so send a response:
          if (currentLine.length() == 0) {
            // HTTP headers always start with a response code (e.g. HTTP/1.1 200 OK)
            // and a content-type so the client knows what's coming, then a blank line:
            client.println("HTTP/1.1 200 OK");
            client.println("Content-type:text/html");   
 
            // The HTTP response ends with another blank line:
            client.println();
            // break out of the while loop:
            break;
          }
          else {      // if you got a newline, then clear currentLine:
            currentLine = "";
          }
        }
        else if (c != '\r') {    // if you got anything else but a carriage return character,
          currentLine += c;      // add it to the end of the currentLine
        }
 
        // Check to see if the client request was "GET /H" or "GET /L":
        // Serial.println(currentLine);
 
        if (currentLine.indexOf("GET MODE_0" >= 0)) {
      //    digitalWrite(LED_PIN, HIGH);               // GET /H turns the LED on
            Serial.println("New mode 0");
            return 0;
        }
        if (currentLine.indexOf("GET MODE_1" >= 0)) {
      //    digitalWrite(LED_PIN, LOW);                // GET /L turns the LED off
            Serial.println("New mode 1");
            return 1;
        }
        if (currentLine.indexOf("GET MODE_2" >= 0)) {
      //    digitalWrite(LED_PIN, HIGH);                // GET /L turns the LED off
            Serial.println("New mode 2");
            return 2;
        }
     /*   else {
          Serial.println("Something else... going 4");
          return 4;
        }*/
      }
    delay(1);
    client.stop();
    }    
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
