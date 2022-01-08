package iot.nanoleaf.iot_leaf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Constraints;

public class panel_setup extends Activity {

    ImageView imageview;
    ConstraintLayout parentLayout;
    Button btnConfirm;

    static int X_MAX = 11;
    static int Y_MAX = 11;

    //Keeps the stored coordianates of panel. This is used to
    //1. Ensure that the panel is not created twice in the layout
    //2. Ensure that, when ready, the full known layout is passed to the android app
    //Auto to 0 which means not added to layout, set to 1 when in layout but not added to the list of
    //panels, set to 2 when added to list of panels
    int [] [] coordinate_array = new int[X_MAX][Y_MAX];

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panel_layout);
        parentLayout = (ConstraintLayout) findViewById(R.id.panel_layout);

        // Starts by defining the very center triangle. Its already added in the layout xml, but this helps set
        //the constraints
        addNearby((X_MAX - 1) / 2, (Y_MAX - 1) / 2, 0);

        btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("int", 1);

                Bundle mBundle = new Bundle();
                mBundle.putSerializable("matrix", coordinate_array);
                returnIntent.putExtras(mBundle);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }

    //Adds bordering triangles to the panel. Allows the structure to continue to expand, without
    //bogging the system down with unneeded triangles
    //Recieves the coordinates of the PANEL BEING ADDED, and its position in relation to the panel being added to
    //For a new imageview to be added to an area, it must..
    //1. Not be out of bounds in the coordinate array.
    //2. Not already be created for the coordinate array.
    //3. Be bordering a side of the triangle. A triangle with the base on the bottom and point on top would
    //  not have one added above it, for example
    public void addNearby(final int x, final int y, int position) {

        //1. In bounds
        if(x < 0 || x >= X_MAX) {
            return;
        }
        if(y < 0 || y >= Y_MAX) {
            return;
        }
        //2. Not created
        if (coordinate_array[x][y] != 0) {
            return;
        }

        //3. Properly bordering
        //Checks if its above and odd. The coordinate grid makes every "even" (x+y) triangle is pointed up, meaning it wouldnt have a direct
        //triangle above it. Since this is checking the triangle thats being placed, it checks if its odd and above instead
        if (position == 0 && (x + y) % 2 != 0) return;

        //Same with odd triangles, except it chekcs below
        if (position == 2 && (x + y) % 2 == 0) return;

        coordinate_array[x][y] = 1; //ensures it cant be placed again

        //First defines the parameters for the triangles. Ensures theyre all the same size
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(200,200);

        params.leftMargin = 100;
        params.rightMargin = 10;
        params.topMargin = 10;
        params.bottomMargin = 10;

        //Creates the imageview for the new triangle
        ImageView newTri= new ImageView(this);
        newTri.setId(View.generateViewId());

        newTri.setImageDrawable(getResources().getDrawable(R.drawable.triangle_add));

        if((x+y) % 2 != 0 )  newTri.setRotation(180);

        newTri.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

             checkNearby(x, y);

             imageview = findViewById(view.getId());

             if (coordinate_array[x][y] == 1) {
                 imageview.setImageDrawable(getResources().getDrawable(R.drawable.triangle));
                 coordinate_array[x][y] = 2;
             }
             else {
                 imageview.setImageDrawable(getResources().getDrawable(R.drawable.triangle_add));
                 coordinate_array[x][y] = 1;
             }
          }
        });
        parentLayout = (ConstraintLayout) findViewById(R.id.panel_layout);

        parentLayout.addView(newTri);

        //Constraints
        ConstraintSet constraints = new ConstraintSet();
        constraints.clone(parentLayout);

        constraints.constrainHeight(newTri.getId(), 190);

        constraints.connect(newTri.getId(), ConstraintSet.TOP, R.id.centerpoint, ConstraintSet.TOP, 210*y);
        constraints.connect(newTri.getId(), ConstraintSet.LEFT, R.id.centerpoint, ConstraintSet.LEFT, 121*x);

        constraints.applyTo(parentLayout);
    }

    //Cycles through the 4 potential sides, determining which ones need to have a new empty imageview added.
    //For a new imageview to be added to an area, it must..
    //1. Not already be defined in the coordinate array.
    //2. Not be out of bounds in the coordinate array.
    //3. Be bordering a side of the triangle. A triangle with the base on the bottom and point on top would
    //  not have one added above it, for example
    //Sieds: 0 top, 1 right, 2 bot, 3 left.
    //If all is ok, passes the NEW PANEL to addNearby
    public void checkNearby(int x, int y) {

        addNearby(x-1,y, 3); //Checks to the left
        addNearby(x+1,y, 1); //Checks to right
        addNearby(x,y+1, 2); //Bot
        addNearby(x,y-1, 0); //Top. Only max of one top/bot can place
    }

}
