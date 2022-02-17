package iot.nanoleaf.iot_leaf;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.ArrayList;
import java.util.List;


//Activity to create a new mode  for the wall
//Each mode has a few decisions to be made:
//  1. The name of the function
//  2. The speed of which the pattern transitions. 1 is slower, 10 is faster
//  3. The type of pattern which will appear on the wall
//  4. The colors being displayed by the pattern
//      Each pattern can display up to 16 colors, though 4-8 is probably a better spot...
public class AddMode extends Activity {

    SeekBar SeekSpeed;  //Definitions for the objects in layout
    TextView TextSpeed;
    ImageView Pattern1; //So far, only 2 patterns have been added to the arduino. More to come
    ImageView Pattern2;
    Button addColor;
    Button confirmMode;
    ConstraintLayout layout;
    EditText modeName;

    int selected = 1;

    final int MAX_COLORS = 8; //Sets max to 8. Could be 16, but could be a little much

    int layoutWidth = 0; //the width of the layout, used to dynamically rescale the color bar

    List<Integer> setColors = new ArrayList<>(); //List containing the set colors
    List<Integer> ids = new ArrayList<>();      //list containing the ids of the imageviews, each holding a section of the color bar


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_mode);

        SeekSpeed = (SeekBar) findViewById(R.id.speedBar);
        TextSpeed = (TextView) findViewById(R.id.textSpeedValue);

        layout = (ConstraintLayout) findViewById(R.id.layoutAddMode);

        //Seek speed
        //seek speed dictates the speed of the transitions
        //Speed can go from 1-10
        SeekSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
         //       int speed = progress/11 +1; //Speed can go from 1-10, but the bar goes from 1-100
                TextSpeed.setText(""+ progress );
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //--------Set on click listeners for the patterns, so the user can decide which one they want to use------
        Pattern1 = (ImageView) findViewById(R.id.imgPattern1);
        Pattern2 = (ImageView) findViewById(R.id.imgPattern2);
        //Whichever is selected changes its padding so that it appears to be selected
        Pattern1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = 1;
                Pattern1.setPadding(10,10,10,10);
                Pattern2.setPadding(0,0,0,0);
            }
        });
        Pattern2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = 2;
                Pattern2.setPadding(10, 10, 10, 10);
                Pattern1.setPadding(0, 0, 0, 0);
            }
        });

        //-------Set on click listener for the add color button--------
        addColor = (Button) findViewById(R.id.btnAddColor);
        addColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    //When clicked, checks for the max num of colors added
                if (setColors.size() < MAX_COLORS) {
                    getNewColor(setColors.size());  //then calls getNewColor
                }
            }
        });

        //-----Set on click listener for the confirm mode button
        confirmMode = (Button) findViewById(R.id.btnconfirmMode);
        modeName = (EditText) findViewById(R.id.textInputModeName);
        confirmMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setColors.size() == 0) {        //First ensures that both a name are set, and that at least 1 color is added. Speed and pattern default to 1 so no
                    Toast.makeText(view.getContext(), "You must enter at least one color", Toast.LENGTH_SHORT).show();                    //need to check on them
                }
                else if (modeName.toString() == "") {
                    Toast.makeText(view.getContext(), "You must enter a name for the mode", Toast.LENGTH_SHORT).show();
                }
                else {
                    //Adds the needed information for the HTML REST call
                    String command = "1";   //First digit is whether its being added or removed
                    command += (selected-1);    //Second digit corresponds to the pattern num (starting at 0)
                    command +=  TextSpeed.getText(); //Third digit corresponds to the speed

                    //adds the remaining information corresponding to the mode colors
                    for (int i = 0; i < setColors.size(); i++) {
                        command += toSendableString(Integer.toHexString(setColors.get(i)));
                    }

                    //Makes the server call to add the mode to its memory
                    ArduinoComms.PosttoArduino posttoArduino = new ArduinoComms.PosttoArduino();
                    posttoArduino.execute("add_mode", command); // ("restful function","params")

                    //Adds the name of the mode only, and returns it as an intent
                    String message=modeName.getText().toString();
                    Intent returnIntent=new Intent();
                    returnIntent.putExtra("modeName",message);
                    setResult(2,returnIntent); //resultcode for this return is 2
                    setResult(Activity.RESULT_OK,returnIntent); //Add ok result
                    finish();
                }
            }
        });
    }

    //Allows the user to visualize the colors that will be represented on the panel of lights
    // Creates a strip of bar, filling the width of the screen (|RRRRRRRRRRRRRRRRR| where R = Red)
    // As more colors are added, the bar changes colors, representing the colors being added to it
    // (if blue is added, -> |RRRRRRRRRBBBBBBBB|) This can store up to 8 colors. Each section is clickable
    // and that color will be removed.
    // Each parttern uses the colors differently, but a color palette can have up to 16 colors so the
    // theoretical max colors a mode can have is 16. Not every mode will have this many though

    //CreateColorBar - Creates the new section of bar when a new color is being added
    //      Receives: int barNum, the position of the new section of the bar, starts at 0->MAX-1
    //      Returns: nothing
    //      Calledby: onClick from the addcolor button
    private void createColorBar(final int barNum) {

       //Create a new imageview, and assigns it the basic propeerties. Assigns the colors, sets the background
        final ImageView addingColor = new ImageView(this);
        addingColor.setImageDrawable(getDrawable(R.drawable.colorsquare));
        addingColor.setId(View.generateViewId());   //dynamic ID
        addingColor.setScaleType(ImageView.ScaleType.FIT_XY); //Set scaling to fit width

        //Add the id to to the list. Each piece of the bar has a seperate id, which gains the appearnance of one of the colors in the list
        ids.add(addingColor.getId());

        //-------Adds layout params so each section can be shrunk/grown at will-------

        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT);
        layoutWidth = layout.getWidth();
        resizeColorBars(lp);

        int maxWidth = layoutWidth-100;
        int newWidth = maxWidth/setColors.size();

        lp.width =  newWidth;
        lp.height = 200;

        if (barNum == 0) {
            lp.leftMargin = 50;
        }

        addingColor.setLayoutParams(lp);

        //-------Set the constraints for each color bar--------
        //Left most is constrained to the parent layout, each consecutive is constrained to the left sibling

        ConstraintSet cs = new ConstraintSet(); //New constraint set, which is cloned, modified, and readded to the layout with the new constraints
        layout.addView(addingColor);
        cs.clone(layout);

        //If its the first id, then its leftbound is the parent instead of the previous id
        if (barNum == 0) {
            cs.connect(addingColor.getId(), ConstraintSet.TOP, R.id.btnAddColor, ConstraintSet.BOTTOM);
            cs.connect(addingColor.getId(), ConstraintSet.LEFT, R.id.layoutAddMode, ConstraintSet.LEFT);
        }
        else { //Else, bound to previous id
            cs.connect(addingColor.getId(), ConstraintSet.LEFT, ids.get(ids.size()-2), ConstraintSet.RIGHT);
            cs.connect(addingColor.getId(), ConstraintSet.TOP, R.id.btnAddColor, ConstraintSet.BOTTOM);
        }
        cs.applyTo(layout);

        //-------Change the current color of the bar, and allows it to be clicked on and modified later------

        addingColor.setColorFilter(setColors.get(barNum));
        //When the image is clicked later, allows the color to be changed
        addingColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                getNewColor(barNum);
            }
        });
    }

    //Calls the flower which the user uses to select the new color being added to the color bar
    // When the new color is selected, adds the color to the color list (if new adds, if updating replaces that position)
    // then changes the background color of the image to represent the new color
    //      Recieves: int barnum - the number of the position of the color in the color bar
    private void getNewColor(final int barNum) {
        final Context context = AddMode.this;
        ColorPickerDialogBuilder
                .with(context)
                .setTitle("Choose a color")
                .initialColor(0xffffffff)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        //  Toast.makeText(context, "onColorSelected: 0x" + Integer.toHexString(selectedColor), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(context, "Max modes set!", Toast.LENGTH_SHORT).show();;
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                     ///   setColors.set(barNum, selectedColor);
                        //setColors.set(barNum, selectedColor);

                        if (setColors.size() == barNum) {
                            setColors.add(selectedColor);
                            createColorBar(barNum);
                        }
                        else {    //The bar itself is made up of 1-MAX different imageviews, each connected together. Needs the id of the imageview
                            ImageView addingColor;                                                 //to change the correct sections background color
                            setColors.set(barNum, selectedColor);   //Sets the new color in the color list
                            addingColor = findViewById(ids.get(barNum)); //Finds the id of the imageview which is to be modified, and loads the imageview

                            addingColor.setColorFilter(selectedColor);   //changes the color of the section of bar
                        }
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    //Resizes the individual color bars so that the compilation of all color bars looks like the a single bar take takes up the entire screen
    // First, calculates the new width of the individual bars. Then assigns it to the bar which is being added, then assigns it to each previously added bar
    //      Receives: LayoutParams lp - params for the section of the bar being added
    private void resizeColorBars(ConstraintLayout.LayoutParams lp) {

        //Defines the amount of space the bar has to work with. 50 padding on each side
        int newWidth = (layoutWidth-100)/setColors.size();

        lp.width =  newWidth;   //asigns width and height to the new bar being added
        lp.height = 200;

        for (int i = 0; i < setColors.size()-1; i++) {  //goes through each indiviual bar which has already been added
            ImageView replaceColor = (ImageView) findViewById(ids.get(i));  //Finds the id of the added bar from the list, then the corresponding imageview

            ConstraintLayout.LayoutParams layoutChange = (ConstraintLayout.LayoutParams) replaceColor.getLayoutParams();    //copies its previously made layoutparams
            layoutChange.width = newWidth;  //Replaces its precious width with its newly determined, evenly divided width
        }
    }

    //Converts the hex string to an output that the server can read in its restful html calls
    //Convert base 16->base 85
    //      receives: string hex - string form of a 6 digit hex variable
    //      returns: atring RGB - string with the corresponding base 87 value
    private String toSendableString(String hex) {
        String RGB = ""; //Stores the final 3 digit value

        for (int i = 2; i < 8; i+=2) {  //Circles through the R, G, and B value seperately
            String cmd = hex.substring(i, i+2); //Takes the 2 digit hex code (0-255 decimal)
            int j = Integer.parseInt(cmd, 16) / 3;  //Converts that value base 10, but divided by 3 (0-85)
            RGB += (char) (j + 33);    //returns the corresponding ascii character for the 0-85 value, offset by 33 (starts at !, goes to v)
        }
        return RGB; //returns the ascii value
    }
}
