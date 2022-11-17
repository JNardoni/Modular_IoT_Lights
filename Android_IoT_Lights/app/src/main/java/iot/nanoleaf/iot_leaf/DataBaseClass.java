package iot.nanoleaf.iot_leaf;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//Handles the saving and loading needed by the SQLite database
public class DataBaseClass extends SQLiteOpenHelper {

    //Current version of the database
    private static final int DB_Version = 1;

    //name of the overall database
    private static String DB_Name = "LightModesDB";

    //Individuall table of the database - currently only 1 is being designed for
    private static final String TABLE_NAME = "light_modes";

    //ID num for the mode. No autoincrememnt, as the mode number must match up with that held by the server
    private static final String ID_COL = "_id";
    //Name of the light mode
    private static final String NAME_COL = "name";
    //Pattern the light mode is using
    private static final String PATTERN_COL = "pattern";
    //Speed the light mode is running at
    private static final String SPEED_COL = "speed";
    //The hex string the light mode is running at
    private static final String COLOR_COL = "color";

    private static final String CREATE_TABLE_MODES = "create table "
            + TABLE_NAME + "(" + ID_COL + " integer, "
            + NAME_COL + " string, "
            + PATTERN_COL + " integer, "
            + SPEED_COL + " integer, "
            + COLOR_COL + " string);";

    public DataBaseClass(Context context) {
        super(context, DB_Name, null, DB_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_MODES);

    }
    //TODO Save/Load modes
    public void loadModesFromDB() {

    }

    public void addNewMode() {

    }

    //Currently unused, but required - To use if the table gets an upgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {

        }
    }



}
