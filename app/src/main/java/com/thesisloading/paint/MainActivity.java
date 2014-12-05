package com.thesisloading.paint;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.thesisloading.paint.R;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;


public class MainActivity extends Activity implements View.OnClickListener{

    private int w = 500;
    private int h = 500;
    private PaintView pv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout ll  = (LinearLayout)findViewById(R.id.main_view);
        Button button = (Button) findViewById(R.id.button_save);
        button.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        pv = (PaintView) findViewById(R.id.paint_mu);
        Log.d("ma perchè",pv.getName());
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
    public void onClick(View view) {
        File imageFileFolder = new File(Environment.getExternalStorageDirectory(),"PAINTVIEW");
        imageFileFolder.mkdir();
        FileOutputStream out = null;
        Calendar c = Calendar.getInstance();
        String date = c.getTime().toString();
        File imageFileName = new File(imageFileFolder, date + ".jpg");
        if (imageFileName.exists()) imageFileName.delete();
        try
        {
            out = new FileOutputStream(imageFileName);
            pv.getBmp().compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            out = null;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
