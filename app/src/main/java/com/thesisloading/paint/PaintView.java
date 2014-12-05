package com.thesisloading.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.PointF;
import android.hardware.display.DisplayManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;


public class PaintView extends View {

    private enum STATUS {START,LINE,STOP,AFTERMOVE,AFTERLINE};
    private Paint paint;
    private Path path;
    private float width;
    private float lastPressure;

    private ArrayList<Path> percorsi;
    private List<Point> points = new ArrayList<Point>();
    private Bitmap bmp = null;
    private STATUS status;
    private Matrix matrix;


    public PaintView(Context context) {
        this(context, null, 0);

    }

    public PaintView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaintView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        path = new Path();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Style.STROKE);
        paint.setColor(Color.RED);
        width = 20;
        paint.setStrokeWidth(4);

        percorsi = new ArrayList<Path>();
        WindowManager manager= (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display= manager.getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        matrix=new Matrix();
        Log.d("creato","creato");
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
//                this.addPoints(event);
                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";
                status = STATUS.LINE;
//                this.addPoints(event);
                break;
            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";
                status = STATUS.STOP;
//                this.addPoints(event);
                break;
            default:
                action = "OTHER_ACTION";
        }
        addPressurePointsRoutine(event);
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


    private void drawOnBitman(float pressure){
        Canvas bitmapCanvas = new Canvas(bmp);
        ArrayList<PressurePoint> points = getPoints(lastPressure,pressure);
//        bitmapCanvas.drawPath(path, paint);
//        Log.d("Lunghezza vettore",""+points.length);
        int i = 0;
        for (PressurePoint point : points) {
            i++;
            if (point == null) Log.d("punto nullo","nullissimo"+i);
            Log.d("Punto","Drawing point " + point.x + " "+point.y + " pressione " + (float) Math.log(point.pressure + 0.3f) * width * 2+ width + "effettiva" + point.pressure );
            bitmapCanvas.drawCircle(point.x,point.y,(float) Math.log(point.pressure + 0.3f) * width * 2 + width ,paint);
        }
        path.rewind();
        lastPressure = pressure;
    }

    private void addPressurePointsRoutine(MotionEvent ev){


        final int historySize = ev.getHistorySize();
        for (int h = 0; h < historySize; h++) {
            if(status == STATUS.START) {
//                paint.setStrokeWidth((float) ( width * (ev.getHistoricalPressure(h)-0.2)));
                path.moveTo(ev.getHistoricalX(h), ev.getHistoricalY(h));
                status = STATUS.LINE;
                lastPressure = ev.getHistoricalY(h);
                Log.d("strokeWidth",paint.getStrokeWidth()+"");
                Log.d("Pressione",ev.getHistoricalPressure(h)+"");

            }else {
//                paint.setStrokeWidth((float) ( width * (ev.getHistoricalPressure(h))-0.2));
                path.lineTo(ev.getHistoricalX(h), ev.getHistoricalY(h));
                drawOnBitman(ev.getHistoricalPressure(h));
                path.moveTo(ev.getHistoricalX(h), ev.getHistoricalY(h));
                Log.d("strokeWidth",paint.getStrokeWidth()+"");
                Log.d("Pressione",ev.getHistoricalPressure(h)+"");

            }
        }
        if(status == STATUS.START) {
//            paint.setStrokeWidth((float) ( width * (ev.getPressure()-0.2)));
            path.moveTo(ev.getX(), ev.getY());
            status = STATUS.LINE;
            lastPressure = ev.getPressure();
            Log.d("strokeWidth",paint.getStrokeWidth()+"");
        }else{
//            paint.setStrokeWidth((float) ( width * (ev.getPressure()-0.2)));
            path.lineTo(ev.getX(), ev.getY());
            drawOnBitman(ev.getPressure());
            path.moveTo(ev.getX(), ev.getY());
            Log.d("strokeWidth",paint.getStrokeWidth()+"");
            Log.d("Pressione",ev.getPressure()+"");

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
    public void setPaintColor(int color) {
        paint.setColor(color);
    }
    public void setPaintWidth(float width) {
        this.width = width;
    }
    public float getPaintWidth(){
        return this.width;
    }
    public int getPaintColor(){
        return paint.getColor();

    }
    private ArrayList<PressurePoint> getPoints(float startPressure,float endPressure) {
        ArrayList<PressurePoint> pointArray = new ArrayList<PressurePoint>();
        PathMeasure pm = new PathMeasure(path, false);
//        if(status == STATUS.STOP) return null;
        float pressureSpeed = (endPressure-startPressure)/20;
        float pressure = startPressure;
        float length = pm.getLength();
        Log.d("pathlength",length+"");
        float distance = 0f;
        float speed = length / 20;
        int counter = 0;
        float[] aCoordinates = new float[2];

        while ((distance < length) && (counter < 20)) {
            // get point from the path
            pm.getPosTan(distance, aCoordinates, null);
            pointArray.add(new PressurePoint(aCoordinates[0], aCoordinates[1],pressure));
            pressure += pressureSpeed;
            counter++;
            distance = distance + speed;
        }

        return pointArray;
    }
    class PressurePoint extends PointF{

        float pressure;

        public PressurePoint(float x, float y, float pressure){
            super(x,y);
            this.pressure = pressure;
        }

    }

    public Bitmap getBmp() {
        return bmp;
    }
    public String getName(){
        return "prova";
    }
}