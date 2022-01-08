package iot.nanoleaf.iot_leaf;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class main_panels extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);


        Button btnConfirm = findViewById(R.id.btn_main_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent setupPanels = new Intent(main_panels.this, panel_setup.class);
                startActivityForResult(setupPanels,1);

            }
        });
    }

    //Gets the result from the Panel Setup
    //When a new light structure is set, it comes back here with the panels that are being used,
    //defined by a 2 the panel matrix.
    //Request codes:
    //1: panel_setup return
    //result codes:
    //success: fail
    //ok.: success, data in matrix, begin restructuring here
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int [] [] k = new int[panel_setup.X_MAX][panel_setup.Y_MAX];
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Object[] obj_array = (Object[]) data.getExtras().getSerializable("matrix");

                if (obj_array != null) {
                    for (int i = 0; i < panel_setup.Y_MAX; i++) {
                        k[i] = (int[]) obj_array[i];
                    }
                }
            }
        }

    }


}
