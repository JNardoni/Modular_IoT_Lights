package iot.nanoleaf.iot_leaf;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class panel_setup extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panel_layout);


        findViewById(R.id.triangle1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void onClick() {


    }

}
