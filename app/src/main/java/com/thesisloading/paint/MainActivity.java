package com.thesisloading.paint;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;


public class MainActivity extends Activity {

    private int w = 500;
    private int h = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        String action;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN";
                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";
                break;
            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";
                break;
            default:
                action = "OTHER_ACTION";
        }
        for (int i = event.getHistorySize();i>0;i--){
            Log.d("OnTouchEventX",event.getHistoricalX(i-1)+"");
            Log.d("OnTouchEventY",event.getHistoricalY(i-1)+"");
            Log.d("OnTouchEventOrientation",event.getHistoricalOrientation(i-1)+"");
//            Log.d("OnTouchEventPressure",event.getHistoricalPressure(i-1)+"");
//            Log.d("OnTouchEventToolMinor",event.getHistoricalToolMinor(i-1)+"");
//            Log.d("OnTouchEventToolMajor",event.getHistoricalToolMajor(i-1)+"");
//            Log.d("OnTouchEventTouchMajor",event.getHistoricalTouchMajor(i-1)+"");
//            Log.d("OnTouchEventTouchMinor",event.getHistoricalTouchMinor(i-1)+"");
        }


        return super.onTouchEvent(event);
    }
}
