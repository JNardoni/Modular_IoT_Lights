package iot.nanoleaf.iot_leaf;

public class PanelClass {
    int active; //Whether or not the panel is active

    //The arduino lights are a continuous strand of light strips. Each panel contains 11 lights
    //These panels form a continuous strip. In order for the arduino to know which panel the user wants to modify,
    //its corresponding arduino position must be defined. When a panel is modified, it sends its arduino panel position to the arduino, which
    //then can modify the appropriate LEDs in the strand
    int ArduinoPanelNum;

    PanelClass() {
        active = 0;
    }


    public void ChangePanelColor() {

    }

    public void ChangePanelMode() {

    }





}
