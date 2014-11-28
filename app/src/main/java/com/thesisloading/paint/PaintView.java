package com.thesisloading.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;


public class PaintView extends View {

    private enum STATUS {START,LINE,STOP};
    private Paint paint;
    private Path path;
    private List<Point> points = new ArrayList<Point>();
    private Bitmap bmp = null;
    private STATUS status;
    private Matrix matrix;


    public PaintView(Context context) {
        this(context, null, 0);

    }

    public PaintView(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    public PaintView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        path = new Path();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        WindowManager manager= (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display= manager.getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        matrix=new Matrix();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Log.d("onMeasure","Width " + widthMeasureSpec + " Height " + heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = this.getWidth();
        int h = this.getHeight();
        if (bmp == null) {
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            bmp = Bitmap.createBitmap(w, h, conf);
        }
        canvas.drawColor(Color.YELLOW);
        canvas.drawBitmap(bmp, matrix, null);
        canvas.drawPath(path,paint);

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        String action;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN";
                status =  STATUS.START;
                this.addPoints(event);
                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";
                status = STATUS.LINE;
                this.addPoints(event);
                break;
            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";
                status = STATUS.STOP;
                this.addPoints(event);
                Canvas bitmapCanvas = new Canvas(bmp);
                bitmapCanvas.drawPath(path, paint);
                path.rewind();
                break;
            default:
                action = "OTHER_ACTION";
        }


//            Log.d("OnTouchEventX",event.getHistoricalX(i-1)+"");
//            Log.d("OnTouchEventY",event.getHistoricalY(i-1)+"");

            /*Log.d("OnTouchEventPressure",event.getHistoricalPressure(i-1)+"");
            Log.d("OnTouchEventToolMinor",event.getHistoricalToolMinor(i-1)+"");
            Log.d("OnTouchEventToolMajor",event.getHistoricalToolMajor(i-1)+"");
            Log.d("OnTouchEventTouchMajor",event.getHistoricalTouchMajor(i-1)+"");
            Log.d("OnTouchEventTouchMinor",event.getHistoricalTouchMinor(i-1)+"");*/

        invalidate();
//        this.printSamples(event);
        return true;
    }
    private void addPoints(MotionEvent ev){
        final int historySize = ev.getHistorySize();

        for (int h = 0; h < historySize; h++) {
            if(h==0 && status == STATUS.START) {
                path.moveTo(ev.getHistoricalX(h), ev.getHistoricalY(h));
                status = STATUS.LINE;
            }else {
                path.lineTo(ev.getHistoricalX(h), ev.getHistoricalY(h));
            }
        }
        if(status == STATUS.START) {
            path.moveTo(ev.getX(), ev.getY());
            status = STATUS.LINE;
        }else{
            path.lineTo(ev.getX(), ev.getY());
        }
    }

    private void printSamples(MotionEvent ev) {
        final int historySize = ev.getHistorySize();
        final int pointerCount = ev.getPointerCount();
        String debug;
        for (int h = 0; h < historySize; h++) {
            debug = String.format("Historical Touch at time %d:",ev.getHistoricalEventTime(h));
            for (int p = 0; p < pointerCount; p++) {
                Log.d(debug,String.format("  pointer %d: (%f,%f)",
                        ev.getPointerId(p), ev.getHistoricalX(p, h), ev.getHistoricalY(p, h)));
            }
        }
        debug = String.format("Touch Event At time %d:", ev.getEventTime());
        for (int p = 0; p < pointerCount; p++) {
            Log.d(debug,String.format("  pointer %d: (%f,%f)",
                    ev.getPointerId(p), ev.getX(p), ev.getY(p)));
        }
    }
}