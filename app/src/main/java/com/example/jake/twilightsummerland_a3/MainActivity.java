package com.example.jake.twilightsummerland_a3;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnDragListener, View.OnClickListener{

    TextView textRandom1, textRandom2, textRandom3, textRandom4;
    TextView[] randomButtons;
    TextView view2, view3, view10, view5;
    Button buttonGetRandom, buttonClear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkConnection();
        textRandom1 = findViewById(R.id.buttonRandom1);
        textRandom2 = findViewById(R.id.buttonRandom2);
        textRandom3 = findViewById(R.id.buttonRandom3);
        textRandom4 = findViewById(R.id.buttonRandom4);
        randomButtons = new TextView[4];
        randomButtons[0] = textRandom1;
        randomButtons[1] = textRandom2;
        randomButtons[2] = textRandom3;
        randomButtons[3] = textRandom4;

        Button buttonGetRandom = findViewById(R.id.buttonRetrieveRandom);
        Button buttonGetClear = findViewById(R.id.buttonClear);
        view2 = findViewById(R.id.view2);
        view2.setOnDragListener(this);
        view3 = findViewById(R.id.view3);
        view3.setOnDragListener(this);
        view5 = findViewById(R.id.view5);
        view5.setOnDragListener(this);
        view10 = findViewById(R.id.view10);
        view10.setOnDragListener(this);

        buttonGetRandom.setOnClickListener(this);
        buttonGetClear.setOnClickListener(this);

    }

    public void checkConnection(){
        ConnectivityManager connectMgr =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            //fetch data

            String networkType = networkInfo.getTypeName().toString();
            Toast.makeText(this, "connected to " + networkType, Toast.LENGTH_LONG).show();
        }
        else {
            //display error
            Toast.makeText(this, "no network connection", Toast.LENGTH_LONG).show();
        }
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onDrag(View v, DragEvent dragEvent) {
        switch(dragEvent.getAction()){
            case DragEvent.ACTION_DRAG_ENTERED:
                ((TextView) v).setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_highlight));
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                ((TextView) v).setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_corner));
                break;
            case DragEvent.ACTION_DROP:
                View view = (View) dragEvent.getLocalState();
                TextView dropped = (TextView) view;
                int val = Integer.valueOf((String) dropped.getText());
                int divisible = -1;
                if(v.getId() == R.id.view2)
                {
                    divisible = 2;
                }
                else if(v.getId() == R.id.view3)
                {
                    divisible = 3;
                }
                else if(v.getId() == R.id.view5)
                {
                    divisible = 5;
                }
                else if(v.getId() == R.id.view10)
                {
                    divisible = 10;
                }
                if(divisible > 0)
                {
                    if(val % divisible == 0)
                    {
                        String text = (String) ((TextView) v).getText();
                        text = text + "\n" + val;
                        ((TextView) v).setText(text);
                        dropped.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                return true;
                            }
                        });
                        dropped.setText("");
                    }
                }
                ((TextView) v).setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_corner));
                break;
        }
        return true;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            ClipData clip = ClipData.newPlainText("","");
            View.DragShadowBuilder shadow = new View.DragShadowBuilder(view);
            view.startDrag(clip, shadow, view, 0);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonRetrieveRandom:
                new GetRandomNumbers(this).execute("https://qrng.anu.edu.au/API/jsonI.php?length=4&type=uint8");
                break;
            case R.id.buttonClear:
                textRandom1.setText("_");
                textRandom2.setText("_");
                textRandom3.setText("_");
                textRandom4.setText("_");
                view2.setText("Multiples of 2");
                view3.setText("Multiples of 3");
                view5.setText("Multiples of 5");
                view10.setText("Multiples of 10");
        }
    }
    private class GetRandomNumbers extends AsyncTask<String, Void, String> {

        Exception exception = null;
        View.OnTouchListener listener;

        GetRandomNumbers(View.OnTouchListener l)
        {
            listener = l;
        }

        protected String doInBackground(String... urls) {
            try{
                return readJSONData(urls[0]);
            }catch(IOException e){
                exception = e;
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
//                JSONObject weatherObservationItems =
//                        new JSONObject(jsonObject.getString("data"));
                JSONArray randomArray = jsonObject.getJSONArray("data");
                String toastMesg = "";
                for (int i=0; i < randomArray.length(); i++) {
                    int val = (Integer) randomArray.get(i);
                    toastMesg = toastMesg + val + ",";
                    randomButtons[i].setText("" + val);
                    randomButtons[i].setOnTouchListener(listener);
                    if(val % 2 != 0 && val % 3 != 0 && val % 5 != 0 && val % 10 !=0)
                    {
                        randomButtons[i].setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                return true;
                            }
                        });
                    }
                }
                toastMesg = toastMesg.substring(0 ,toastMesg.length()-1);
                Toast.makeText(getBaseContext(), toastMesg, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.d("GetRandomNumbers", e.getLocalizedMessage());
                Toast.makeText(getBaseContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private String readJSONData(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 2500;

        URL url = new URL(myurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("tag", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
                conn.disconnect();
            }
        }
    }
    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}
