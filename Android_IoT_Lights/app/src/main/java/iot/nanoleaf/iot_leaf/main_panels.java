package iot.nanoleaf.iot_leaf;

import androidx.appcompat.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

//TODO Edit and delete modes
//TODO Send panel information/positions to arduino for specific patterns - using panelclass LightPanels
//TODO change brightness

/*Main activity

---------Purpose and functionality---------
The purpose of the app is to provide a visual interface for a user to control a panel if modular RGB panel lights.
The arduino acts as a RESTful server, which receives calls from this android app, as the Arduino setup has no way
to manually receive information itself. The arduino's server is on a local IP address, which this is set to, and by
sending specific HTTP strings initiates functions on the Arduino, with attached parameter strings attached. The arduino
parses the string in order to execute the funstion as the user inteded.

This app is able to do tell the arduino to do the following:
-Turn the lights on/off
-Add a new mode, based off of preset patterns on the arduino
-Change the current playing mode
-Change the brightness of the lights

------------Server calls in detail-------------
Every call is sent with a function and a command. This is done in the ArduinoComms activity. The URL is set to the local IP
of the arduino, and the HTTP format looks like URL+"/"+FUNCTION+"?params="+COMMAND, where the function is a string of the
function to execute on the arduino, and the command is a string parameter for the arduino to parse. Each function has a different
format for the command param

Functions:
1. settings
    Changes current settings of the device. This includes turning it on/off, changing brightness, and changing the active mode.

    String params: First digit is which setting is being changed
                    If 0, on/off      second digit - 0 for off, 1 for on
                    If 1, brightness    second digit - new brightness, from 0-9 (arduino modifies up to 1-10)
                    if 2, active mode   second digit, number of the mode (from 0-9)

         Ex: URL/settings?params=01 would tell the arduino to turn the lights on

2. setup_panels
    Tells the arduino the new number of panels on the wall. When new panels are added, or some taken away, the user must ttell
    the device so that it can account for the change in panels

    String params: How many panels are added to the wall

     Ex: URL/setup_panels?params=13 would tell the arduino that there are now 13 panels on the wall, and to adjust accordingly

3. add_mode
  Tells the arduino the neccesary information to add a new mode. While the android app stores the name of the mode for the user to
  reference, the server doesnt care and the info is not sent. The list of names of current modes makes up the main screen for the app.
  add_mode is also used when the pattern is changed

  String params: First digit is the whether its being added or removed
                If 1: being added
                If 0, being removed
          If being added...
                Second digit is the pattern which is selected (0-9)
                Third digit is the speed the pattern moves (0-9)
                fourth digit begins the colors which the pattern uses. These colors are compressed into three digit RGB ascii values.
                            While these colors are normally 256 bit values per color, the difference is unnocticable between small variations
                            in the RGB. Instead, the numbers range from 0-86, which are broken into ascii components to take up less space in
                            a command string. The value is offset to begin at '!' on the ascii table, and end at 'v'. Each color has 3 digits,
                            corresponding to the RGB, and as many are appended as the user has added into the pattern of lights

    Ex: URL/add_mode?params=104!v!v!v!!v would mean, mode is being added, using pattern 0, speed 4, and colors green (!v! -> 0,255,0), red, and blue


------------Start screen ------------
Start screen is composed of a few parts.
Top action bar
        Appname, an on/off button, a button to add new modes, options menu
    Options bar
       Setup panels, about
The main body is a list of all the available set modes (up to max modes
 */
public class main_panels extends AppCompatActivity {

    //Panel Class - 2d matrix containing the information on every element of the panels
    //It is defined as a general, global value here, while each individual panel is defined in DefinePanelClass beloe
    private PanelClass [] [] LightPanels = new PanelClass[panel_setup.X_MAX][panel_setup.Y_MAX];
    private int numPanels = 0;
    private int modesSet = 0; //Counts how many modes have been properly set
    static int MAX_MODES = 10; //the max modes which can be set at any given time
    //List of user built modes
    ArrayList<String> modes = new ArrayList<String>();

    PanelAdapter panelAdapter;
    ListView listView;
    ActionBar actionBar;
    String filename = "ModeInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        //-------Load bundles from previous usages of the app-----

        try {
            //New buffered reader, which opens the file
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                    openFileInput(filename)
            ));
            String inputModes;
            //Grabs a string for each line of the file, corresponding to the filename
            while ((inputModes = inputReader.readLine()) != null) {
                if (modesSet < MAX_MODES) { //Checks to make sure there arent too many modes set in the file
                    modes.add(inputModes);
                    modesSet++;     //adds the mode to the list and counter
                }
            }
        }
        catch (IOException e) {        }

        //------Create the adapter for the list of modes to switch between----

        listView = findViewById(R.id.linear_list);

        panelAdapter = new PanelAdapter(this, modes); //Setup the panel adapter
        listView.setAdapter(panelAdapter);  //attach it

        //Set up the on click for a mode. If a mode is clicked, makes an HTTP call to the server, indicating a change in mode
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
               sendToArduino("settings","2"+position); //Function: changing mode is a feauter of settings
            }                                                             //command: the first digit is which setting is being changed, 2->active mode
        });                                                               //        the second digit is which mode is being changed to, meaning position

        //-----Set up the title bar------
        actionBar = getSupportActionBar();
        actionBar.setTitle("Pattern Lights");


        //While the overall PanelClass is already defined, each element in the matrix must be too.
        //This defines the panel class, and will add any panels which have their information saved when the
        //app is started up
        DefinePanelClass();
    }

    //--------------------------------Action BAR CLASSES------------------------------------
    //Inflater for the action menu, houses general settings and some needed settings
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {

        getMenuInflater().inflate(R.menu.actionbar_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //The PanelClass is set up here. Each inidividual element in the 2d matrix must be defined
    void DefinePanelClass() {
        for (int y = 0; y < panel_setup.Y_MAX; y++) {
            for (int x = 0; x < panel_setup.X_MAX; x++) {
                LightPanels[x][y] = new PanelClass(); //So far the built light class is not used
            }
        }
    }

    //Check if an item from the action bar is selected. Each icon has its meaning
    // + button : adds/changes the light panels
    // play/stop: turn the panells on or off
    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        switch (item.getItemId()){
            case R.id.add_new_panels: //Adds a new panel. Calls panel_setup as an intent. Passes nothing, recieves a new matrix of lights
                Intent setupPanels = new Intent(main_panels.this, panel_setup.class);
                startActivityForResult(setupPanels,1);
                break;
            case R.id.on_off_toggle: //Checks if the panel is on or not. Changes the on/off button, and marks it as its new state
                String command;
                if(item.isChecked()) {
                    item.setIcon(R.drawable.off_switch);
                    item.setChecked(false);
                    command = "00";
                }
                else {
                    item.setIcon(R.drawable.on_switch);
                    item.setChecked(true);
                    command = "01";
                }
                //Settings string command: two digits.
                // First digit -> dictates which setting is being changed. 0 -> power, 1-> brightness, 2-> active mode
                    // second digit for power -> dictates whether it is turning on or off
                sendToArduino("settings", command);  // ("function","params")

                break;
            case R.id.add_new_mode: //Adds new modes to the setup
                if (modesSet >=MAX_MODES) { //Only 10 modes can be active at a time
                    Toast.makeText(this, "Max modes set!", Toast.LENGTH_SHORT).show();
                    break;
                }
                //Calls the add mode to adda a new mode from scratch
                Intent AddModeIntent = new Intent(main_panels.this, AddMode.class);
                this.startActivityForResult(AddModeIntent, 2);

                break;

         /*   case R.id.copy:
                Toast.makeText(this, "Copy Clicked", Toast.LENGTH_SHORT).show();
                break;*/
        }
        return super.onOptionsItemSelected(item);
    }

    //-----------------------------BUILDING THE PANELS---------------------------------

    //Gets the result from the Panel Setup
    //When a new light structure is set, it comes back here with the panels that are being used,
    //defined by a 2 the panel matrix.
    //Request codes:
    //1: panel_setup return
    //If result is ok, success, data is added to a temporary matrix and sent to buildPanels
    //2. add_mode return
    //If mode is ok, adds the mode to the list of created modes, and saves it into memory
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //------------from panel_setup--------------
        if (requestCode == 1) {
            int [] [] color_array = new int[panel_setup.X_MAX][panel_setup.Y_MAX]; //Will be a 2d array which stores the status for each possible light panel
                                                                                //1 will be active, 0 will be inactive

            if (resultCode == RESULT_OK) {  //if ok, converts the serialized data into a 1d object array of columns
                Object[] obj_array = (Object[]) data.getExtras().getSerializable("matrix");
                if (obj_array != null) {
                    for (int i = 0; i < panel_setup.Y_MAX; i++) {   //Converts the 1d object array into a useable 2d int matrix
                        color_array[i] = (int[]) obj_array[i];
                    }
                }
            }
            buildPanels(color_array);
        }

        //----------------from add_mode--------------
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                String newMode = data.getStringExtra("modeName"); //Get return info
                modes.add(newMode); //Adds mode to the list of set modes
                listView.setAdapter(panelAdapter); //Update the main screen list to include the new mode

                //Save the data into a local file, so that it can be pulled when the app is closed
                try {
                    FileOutputStream fos = openFileOutput(filename, MODE_PRIVATE); //Opens output file
                    fos.write(newMode.getBytes());
                    fos.close(); //Close file

                    modesSet++; //It shouldnt get here if modeset is over the max amount, assumes thats the case
                }
                catch (FileNotFoundException e) {
                }
                catch (IOException e) {
                }
            }
        }
    }

    //Receives information to build the panel matrix
    //When the layout is assigned by the user in panel_setup, the matrix is composed of 0, 1, and 2
    //0s mean the spot in the array was never assigned. 1s mean that something nearby was placed, but that spot was not
    //2s are active spots, and are set to active in the LightPanel array
    protected void buildPanels(int [] [] color_array) {
        if (color_array == null) {
            return;
        }
        else {  //Shuffles through the entire x,y array, and if the spot was set to active by user, sets it active in this one
            numPanels = 0;
            for (int y = 0; y < panel_setup.Y_MAX; y++) {   //columns
                for (int x = 0; x < panel_setup.X_MAX; x++) {   //rows
                    if (color_array[x][y] == 2) {   //2 means active, 1 means not active but set, 0 means never set
                        LightPanels[x][y].active = 1;   //when creating the new, panel matrix, 1 means active, 0 means not active
                        numPanels += 1;
                    }
                }
            }
        }   //doesnt send the relation of panels atm, add to TODO
        sendToArduino("setup_panels", numPanels+"");
    }
/*
    protected void sendPanelsToArduino() {
        String panels = ;

        ArduinoComms.PosttoArduino posttoArduino = new ArduinoComms.PosttoArduino();
        posttoArduino.execute("setup_panels", panels);  // ("function","params")


    }*/

    //Sends a RESTful function command to the server being hosted by the arduino.
    //The arduino looks for a function call, which will trigger a function to be executed on the arduino
    //It will then grab the parameters from the command string. The command string is set up as a series
    //of combined parameters. Each digit in the string is used to tell the arduino the next parameter.
    //Types of function calls:
    //1. settings
    //      First digit in string: The setting being changed
    //      0 -> power, 1-> brightness, 2-> active mode
    //      Second digit: The value its being changed to. Power: 0 off, 1 on. Brightness: 0-9. Mode: 0-9 if mode is set
    //2. setup_panels
    //      Only sends the number of panels added to the wall. Whatever number will be converted to an int
    //3. add_mode
    public void sendToArduino(String function, String command) {
        ArduinoComms.PosttoArduino posttoArduino = new ArduinoComms.PosttoArduino();
        posttoArduino.execute(function, command); // ("restful function","params")
    }

}
