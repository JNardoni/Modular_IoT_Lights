package iot.nanoleaf.iot_leaf;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.math.RoundingMode;

public class AddMode extends Activity {

    SeekBar SeekSpeed;
    TextView TextSpeed;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_mode);

        SeekSpeed = (SeekBar) findViewById(R.id.speedBar);
        TextSpeed = (TextView) findViewById(R.id.textSpeedValue);
        SeekSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                int speed = progress/10;
                TextSpeed.setText(""+ speed );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

}
