package iot.nanoleaf.iot_leaf;

import androidx.appcompat.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class main_panels extends AppCompatActivity {

    //Panel Class - 2d matrix containing the information on every element of the panels
    //It is defined as a general, global value here, while each individual panel is defined in DefinePanelClass beloe
    private PanelClass [] [] LightPanels = new PanelClass[panel_setup.X_MAX][panel_setup.Y_MAX];
    private int numPanels = 0;

    //List of user built modes
    ArrayList<String> modes = new ArrayList<String>();

    PanelAdapter panelAdapter;
    ListView listView;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        //-------Load bundles from previous usages of the app-----
        //TO-DO


        //------Create the adapter for the list of modes to switch between----

        listView = findViewById(R.id.linear_list);

        if (!modes.isEmpty()) {
            panelAdapter = new PanelAdapter(this, modes);
            listView.setAdapter(panelAdapter);
        }

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
                LightPanels[x][y] = new PanelClass();
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
                if(item.isChecked()) {
                    item.setIcon(R.drawable.off_switch);
                    item.setChecked(false);
                }
                else {
                    item.setIcon(R.drawable.on_switch);
                    item.setChecked(true);
                }

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
    //If result is ok, success, data is added to a temporary matrix and sent to vuildPanels
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int [] [] color_array = new int[panel_setup.X_MAX][panel_setup.Y_MAX];

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Object[] obj_array = (Object[]) data.getExtras().getSerializable("matrix");
                if (obj_array != null) {
                    for (int i = 0; i < panel_setup.Y_MAX; i++) {
                        color_array[i] = (int[]) obj_array[i];
                    }
                }
            }
        }
        buildPanels(color_array);
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
            for (int y = 0; y < panel_setup.Y_MAX; y++) {
                for (int x = 0; x < panel_setup.X_MAX; x++) {
                    if (color_array[x][y] == 2) {
                        LightPanels[x][y].active = 1;
                        numPanels += 1;
                    }
                }
            }
        }
        sendPanelsToArduino();
    }

    protected void sendPanelsToArduino() {
        String panels = numPanels+"";

        ArduinoComms.PosttoArduino posttoArduino = new ArduinoComms.PosttoArduino();
        posttoArduino.execute("setup_panels", panels);  // ("function","params")


    }

    void sendToArduino() {
        ArduinoComms.PosttoArduino posttoArduino = new ArduinoComms.PosttoArduino();
        posttoArduino.execute("function","params");
    }






}
