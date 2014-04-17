package com.main.ecg.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class GridActivity2 extends Activity implements View.OnTouchListener {

    public ImageView ivOnGrid;
    DrawView drawView;
    FrameLayout MainLayout;
    private static final String TAG = "Touch" ;
    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_activity2);

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new PlaceholderFragment())
//                    .commit();
//        }



        // get the image from the camera and display it on this activity
        Intent picIntent = getIntent();
        Bundle extras = picIntent.getExtras();
        Bitmap delegatedImageBitmap = (Bitmap) extras.get("pic");
        ivOnGrid=(ImageView) findViewById(R.id.gridImageView);
        ivOnGrid.setImageBitmap(delegatedImageBitmap);
        //add zoom capabilities
        ivOnGrid.setOnTouchListener(this);
//        setContentView(R.layout.activity_grid_activity2);
//        Context c= ivOnGrid.getContext();
//
//        new ZoomableImageView(c);


        //draw a grid of squares on the activity


//        drawView = new DrawView(this);
//        drawView.setBackgroundColor(Color.WHITE);
//        setContentView(drawView);

        MainLayout = (FrameLayout) findViewById(R.id.container);
        CustomView customview = new CustomView(this);
        MainLayout.addView(customview);

    }

    private class CustomView extends View {
        public CustomView(Context context) {
            super(context);
        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);;
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(3);

            float width = getWindowManager().getDefaultDisplay().getWidth();
            float height = getWindowManager().getDefaultDisplay().getHeight();

            float[] pointsForGrid={0, (float) (height*0.2), 0, (float) (height*0.4), 0, (float) (height*0.6), 0, (float) (height*0.8), 0 ,(float) (height*1), width, (float) (height*0.2), width, (float) (height*0.4), width,(float) (height*0.6), width, (float) (height*0.8), width,(float) (height*1)};
            canvas.drawLines(pointsForGrid, 0, 0, paint);

            //horizontal lines
            canvas.drawLine(0, (float) (height*0.2), width, (float) (height*0.2), paint);
            canvas.drawLine(0, (float) (height*0.4), width, (float) (height*0.4), paint);
            canvas.drawLine(0, (float) (height*0.6), width, (float) (height*0.6), paint);
            canvas.drawLine(0, (float) (height*0.8), width, (float) (height*0.8), paint);

            //vertical lines
            float edge=(float)(height*0.2);
            float endOfWidth=edge;
            while ( endOfWidth < width){
                canvas.drawLine(endOfWidth ,0, endOfWidth, height , paint);
                endOfWidth=endOfWidth+edge;
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;

        // Dump touch event to log
        dumpEvent(event);

        // Handle touch events here...
        XYHandel start= new XYHandel();
        float oldDist;
        float newDist;
        PointF mid = new PointF();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG" );
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM" );
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                Log.d(TAG, "mode=NONE" );
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x(),
                            event.getY() - start.y());
                }
                else if (mode == ZOOM) {
                    newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        oldDist = spacing(event);
                        float scale = newDist / oldDist;

                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;

        }

        // Perform the transformation
        view.setImageMatrix(matrix);

        return true; // indicate event was handled
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /** Show an event in the LogCat view, for debugging */
    private void dumpEvent(MotionEvent event) {
        String names[] = { "DOWN" , "UP" , "MOVE" , "CANCEL" , "OUTSIDE" ,
                "POINTER_DOWN" , "POINTER_UP" , "7?" , "8?" , "9?" };
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_" ).append(names[actionCode]);
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid " ).append(
                    action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")" );
        }
        sb.append("[" );
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#" ).append(i);
            sb.append("(pid " ).append(event.getPointerId(i));
            sb.append(")=" ).append((int) event.getX(i));
            sb.append("," ).append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";" );
        }
        sb.append("]" );
        Log.d(TAG, sb.toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.grid_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_grid_activity2, container, false);
            return rootView;
        }
    }

}
