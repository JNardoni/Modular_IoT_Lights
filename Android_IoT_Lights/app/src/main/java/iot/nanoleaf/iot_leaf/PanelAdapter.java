package iot.nanoleaf.iot_leaf;

import android.app.LauncherActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;


//The adapter which handles information being displayed on the list of modes
public class PanelAdapter extends ArrayAdapter<String> {
    public ArrayList<String> LightModes;

    public PanelAdapter(Context context, ArrayList<String> modes) {
        super(context,R.layout.main_layout, modes);
        this.LightModes = modes;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        LayoutInflater ListInflater = LayoutInflater.from(getContext());
        View listView = ListInflater.inflate(R.layout.list_mode_display, parent, false);

        //Thumbnail array
 /*       ArrayList<Integer> image = new ArrayList<Integer>(
                Arrays.asList(R.drawable.iphone11th, R.drawable.galaxys20th,R.drawable.pixel4th,
                        R.drawable.v60thinqth,R.drawable.edgeth,R.drawable.pureviewth));*/

        //choose the right positions in each of the xml string lists
        String displayModes = LightModes.get(position);

        //Set the name of the mode in the list
        //set views
        TextView topText = (TextView) listView.findViewById(R.id.ModeName);
      //  TextView botText = (TextView) listView.findViewById(R.id.phoneInfoText);
       // ImageView thumbnailImage = (ImageView) listView.findViewById(R.id.phoneThumbnail);

        //change text fields
        topText.setText(displayModes);
       // botText.setText(phoneInfo);
       // thumbnailImage.setImageResource(image.get(position));

        //return this mode
        return listView;
    }




}
