package iot.nanoleaf.iot_leaf;

import android.content.Context;
import android.database.Cursor;
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
    //Loads all modes to the database
    public void loadModesFromDB(Context context) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor selectAllQuery = db.rawQuery("SELECT * FROM " + TABLE_NAME + ";", null);

        if (selectAllQuery.moveToFirst() == true) {

            int id = 0;
            String name = "";
            String pattern = "";
            String speed = "";
            String color = "";
            //TODO: test - make sure the input needed is the column #, and not the column matching int/str specifically
            //  ie: is getstring(1) col 2? or the second column of type String
            while(selectAllQuery.moveToNext())
                id = selectAllQuery.getInt(0);
                name = selectAllQuery.getString(1);
                pattern = toString().valueOf( selectAllQuery.getInt(2));
                speed = toString().valueOf( selectAllQuery.getInt(3));
                color = selectAllQuery.getString(4);
                PanelModes modes = new PanelModes(id, name, pattern, speed, color, context);
                //TODO return modes, probably array of
        }

        db.close();
    }
    //Adds the new mode to the database
    public void addNewMode(int id, String name, int pattern, int speed, String color) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO " + TABLE_NAME
                + " VALUES ("
                + id + ", "
                + name + ", "
                + pattern + ", "
                + speed + ", "
                + color + ");");
        db.close();
    }
    //Deletes a mode from the database
    public void deleteMode(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_NAME
                    + " WHERE name=" + name + ");");

    }

    //Currently unused, but required - To use if the table gets an upgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            //
        }
    }



}
