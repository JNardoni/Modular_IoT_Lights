package iot.nanoleaf.iot_leaf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class panel_setup extends Activity {

    ImageView imageview;
    ConstraintLayout constraintlayout;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panel_layout);

        constraintlayout = findViewById(R.id.add_panels);


        findViewById(R.id.tri_0_0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageview = findViewById(R.id.tri_0_0);
                imageview.setImageDrawable(getResources().getDrawable(R.drawable.triangle));

                addNearby(0,0);

            }
        });

    }

    public void addNearby(int x, int y) {

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(140,140);



        params.leftMargin = 100;
        params.rightMargin = 10;
        params.topMargin = 10;
        params.bottomMargin = 10;




        imageview = new ImageView(this);
        imageview.setId(R.id.tri_1_0);

        imageview.setImageDrawable(getResources().getDrawable(R.drawable.triangle_add));


        imageview.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

             ConstraintSet constraints = new ConstraintSet();
             ConstraintLayout layoutt;
             layoutt= (ConstraintLayout) findViewById(R.id.tri_1_0);

             constraints.constrainWidth(imageview.getId(), ConstraintSet.WRAP_CONTENT);
             constraints.constrainHeight(imageview.getId(), ConstraintSet.WRAP_CONTENT);
             constraints.center(imageview.getId(), ConstraintSet.PARENT_ID, ConstraintSet.LEFT,
                     0, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0, 0.5f);
             constraints.center(imageview.getId(), ConstraintSet.PARENT_ID, ConstraintSet.TOP,
                     0, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0, 0.5f);

             constraints.applyTo(layoutt);
          }
        });
        imageview.setLayoutParams(params);
        constraintlayout.addView(imageview);



    }

}
