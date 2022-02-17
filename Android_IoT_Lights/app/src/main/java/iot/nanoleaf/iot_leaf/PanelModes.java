package iot.nanoleaf.iot_leaf;

//TODO use or delete
//Rpobably use, cane store more info on modes and edit/delete them
//Defined, but unused. Should be used for displays
public class PanelModes {
    String name;
    int set;
    int playspeed;
    int pattern = 0;
    int color;


    PanelModes() {
        set = 0;
    }

    public void newMode(String modeName) {
        name = modeName;
        set = 1;

    }
}
