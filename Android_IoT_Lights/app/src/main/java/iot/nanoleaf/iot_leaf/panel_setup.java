package iot.nanoleaf.iot_leaf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class panel_setup extends Activity {

    ImageView imageview;
    ConstraintLayout parentLayout;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panel_layout);

      //  parentLayout = findViewById(R.id.panel_layout);


        findViewById(R.id.tri_0_0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageview = findViewById(R.id.tri_0_0);
                imageview.setImageDrawable(getResources().getDrawable(R.drawable.triangle));

                addNearby(0,0, view);

            }
        });

    }

    public void addNearby(int x, int y, View view) {

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(200,200);

        params.leftMargin = 100;
        params.rightMargin = 10;
        params.topMargin = 10;
        params.bottomMargin = 10;


        ImageView newTri= new ImageView(this);
        newTri.setId(R.id.tri_n1_0);

        newTri.setImageDrawable(getResources().getDrawable(R.drawable.triangle_add));

        if((x+y) % 2 == 0 )  newTri.setRotation(180);

        newTri.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            int i = 1;
          }
        });
        parentLayout = (ConstraintLayout) findViewById(R.id.panel_layout);

        parentLayout.addView(newTri);
        //newTri.setLayoutParams(params);

        //Constraints


        ConstraintSet constraints = new ConstraintSet();
        constraints.clone(parentLayout);
        //parentLayout.addView(newTri);

/*
        constraints.constrainWidth(imageview.getId(), ConstraintSet.WRAP_CONTENT);
        constraints.constrainHeight(imageview.getId(), ConstraintSet.WRAP_CONTENT);
        constraints.center(imageview.getId(), ConstraintSet.PARENT_ID, ConstraintSet.LEFT,
                0, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0, 0.5f);
        constraints.center(imageview.getId(), ConstraintSet.PARENT_ID, ConstraintSet.TOP,
                0, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0, 0.5f);

*/

       constraints.connect(newTri.getId(), ConstraintSet.BOTTOM, R.id.tri_0_0, ConstraintSet.BOTTOM, 10);
       constraints.connect(newTri.getId(), ConstraintSet.RIGHT, R.id.tri_0_0, ConstraintSet.RIGHT, 100);
       //constraints.connect(newTri.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 10);
       constraints.constrainHeight(newTri.getId(), 200);
       // constraints.connect(newTri.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, R.id.tri_0_0, -10);


        constraints.applyTo(parentLayout);

    }

}
