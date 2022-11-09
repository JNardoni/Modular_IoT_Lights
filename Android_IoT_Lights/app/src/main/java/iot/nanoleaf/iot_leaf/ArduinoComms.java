package iot.nanoleaf.iot_leaf;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class ArduinoComms extends Activity {
    static String URL = "http://192.168.1.48";

    //Input: the function it calls, and params, as two strings
    //Output: HTTP responsecode
    static public class PosttoArduino extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... arg0) {
            int responseCode = -1;
            try { //Arg0[0] is the function to be called in the arduino,
                  //Arg0[1] consists of the parameters being passed
                  //Depending on the Arduino function being called, it will know what its looking for
                java.net.URL restApiUrl = new URL(URL+"/"+arg0[0]+"?params="+arg0[1]);
                HttpURLConnection connection = (HttpURLConnection) restApiUrl.openConnection();
                connection.connect();
                responseCode = connection.getResponseCode();
                Log.i(TAG, "Code" + responseCode);

                //TODO Receive codes to ensure success
            }
            catch(MalformedURLException e) {
                Log.e(TAG, "Malformed Exception Caught:", e);
            }
            catch(IOException e) {
                Log.e(TAG, "IO Exception Caught:", e);
                e.printStackTrace();
            }
            catch(Exception e){
                Log.e(TAG, "Generic Exception Caught:", e);
            }
            return "Code: " + responseCode;
        }
    }
}
