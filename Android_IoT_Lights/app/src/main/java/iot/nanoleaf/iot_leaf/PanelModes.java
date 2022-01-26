package iot.nanoleaf.iot_leaf;

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
