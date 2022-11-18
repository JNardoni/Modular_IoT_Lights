package iot.nanoleaf.iot_leaf;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;

//TODO Use? Delete? leftover from an attempt at .txt based memory management

//Stores all information about the current saved modes for the panel of lights
public class PanelModes {
    String name;
    int set = 1;
    int modeNumber;
    String pattern = "0";
    String playspeed = "1";
    String colors = "";
    Context fileContext;

    //When a new mode is created by the user, this constructor is used
    //      Params: String, Name of the mode. Command, large string of command information, same as is sent to the arduino. Context, app context
    PanelModes(String modeName, String Command, Context context) {
        this.name = modeName;
        this.pattern = Command.substring(0,1);
        this.playspeed = Command.substring(1,2);
        this.colors = Command.substring(2);
        this.fileContext = context;
    }

    //If a panel is being loaded instead, this constructor is used
    // Stores all the information from the DB select statement into a class for easy access
    //      Params:
    PanelModes(int modeNumber, String name, String pattern, String playspeed, String colors, Context fileContext) {
        this.modeNumber = modeNumber;
        this.name = name;
        this.pattern = pattern;
        this.playspeed = playspeed;
        this.colors = colors;
        this.fileContext = fileContext;
    }

    //Saves the created mode into memory. Called
    protected void saveModeToMemory() {

        DataBaseClass sqlclass = new DataBaseClass(fileContext);

        sqlclass.addNewMode(this.modeNumber, this.name, Integer.parseInt(this.pattern), Integer.parseInt(this.playspeed), this.colors);
        /*try {
            FileOutputStream fos = fileContext.getApplicationContext().openFileOutput("savedmodes.txt", Context.MODE_PRIVATE);
            OutputStreamWriter fileStreamWriter = new OutputStreamWriter(fos);
            fileStreamWriter.write(this.name);
            fileStreamWriter.write(this.pattern);  //First digit of string: Pattern Selected
            fileStreamWriter.write(this.playspeed);  //Second digit of string: Speed Selected
            fileStreamWriter.write(this.colors);    //The rest of the string: Hex values of colors

            fos.close();
        }
        catch (java.io.IOException e) {
            Log.e("Exception", "File Write Failed");
        }*/
    }

    protected void editModeInMemory(String Name, String Command) {

    }

    protected void deleteModeFromMemory(String name) {

    }


}
