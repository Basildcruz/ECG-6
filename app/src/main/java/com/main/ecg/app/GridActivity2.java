package com.main.ecg.app;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class GridActivity2 extends ActionBarActivity implements View.OnTouchListener {

    public ImageView ivOnGrid;
    public ImageView beginRuller;
    public ImageView endRuller;
    public ImageView grid;
    public PointF offset=new PointF(); // the offset is the difference between the point of the press to the 0,0 point of the picture
    public Float initialTouchDistance;
    public Float initialScale;
    public Float initialRotation;
    public Float oldAngle;
    public PointF pivot= new PointF();
    public boolean isPicLocked=false;
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

        //action bar settings
        ActionBar ab = getActionBar();
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#336699")));
        ab.setTitle("Measure RR distance");
        ab.setSubtitle("peak to peak");

        // get the image from the camera or the gallery and display it on this activity
        Intent picIntent = getIntent();
        Bundle extras = picIntent.getExtras();
        //get pic from camera
        if (extras.get("picFromCamera") instanceof Bitmap) {
            Bitmap delegatedImageBitmap = (Bitmap) extras.get("picFromCamera");
            ivOnGrid=(ImageView) findViewById(R.id.picImageView);
            ivOnGrid.setImageBitmap(delegatedImageBitmap);
        //get pic from gallery
        } else{
            String delegatedImagePath = (String) extras.get("picFromGallery");
            ivOnGrid= (ImageView) findViewById(R.id.picImageView);
//            ivOnGrid.setImageBitmap(BitmapFactory.decodeFile(delegatedImagePath));
            ivOnGrid.setImageBitmap(getScaledBitmap(delegatedImagePath, 800, 800));
        }

        //draw a grid
        MainLayout = (FrameLayout) findViewById(R.id.container);
        CustomView customview = new CustomView(this);
        MainLayout.addView(customview);

        //add move, zoom, and tilt capabilities to pic
        grid= (ImageView) findViewById(R.id.gridImageView);
        grid.setOnTouchListener(this);

        //add rullers
        //begin measure ruller
        beginRuller =(ImageView) findViewById(R.id.beginRullerImageView);
        beginRuller.setImageDrawable(getResources().getDrawable(R.drawable.right_ruller));

        //end measure ruller
        endRuller =(ImageView) findViewById(R.id.endRullerImageView);
        endRuller.setImageDrawable(getResources().getDrawable(R.drawable.left_ruller));


    }

    // manage the grid draw on screen
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

    // manage the move, zoom and tilt capabilities
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(isPicLocked==false){
        ImageView view = (ImageView) v;

        // Dump touch event to log
//        dumpEvent(event);

        // Handle touch events here...

        PointF start= new PointF();
        float oldDist;
        float newDist;
        PointF mid = new PointF();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                offset.set(event.getX()-ivOnGrid.getTranslationX(), event.getY()-ivOnGrid.getTranslationY());
                Log.d("offset", offset.toString());
                mode=DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                initialTouchDistance = getTouchSpacing(event);
                initialScale=ivOnGrid.getScaleX();
                initialRotation=ivOnGrid.getRotation();
                oldAngle =getTouchAngle(event);
                Log.d(TAG, "oldDist=" + initialTouchDistance);
                //if the pinch gesture is significant
                if (initialTouchDistance > 10f) {
                    pivot.set((ivOnGrid.getWidth()/2), (ivOnGrid.getHeight()/2));
                    ivOnGrid.setPivotX(pivot.x);
                    ivOnGrid.setPivotY(pivot.y);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM" );
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                Log.d(TAG, "mode=NONE" );
                break;
//
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {

                    ivOnGrid.setTranslationX(event.getX()-offset.x);
                    ivOnGrid.setTranslationY(event.getY()-offset.y);
                }
                else if (mode == ZOOM) {
                    newDist = getTouchSpacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 10f) {

                        // zoom
                        float scale = Math.max((newDist / initialTouchDistance)+(initialScale-1),0);
                        ivOnGrid.setScaleX(scale);
                        ivOnGrid.setScaleY(scale);


                        // rotation
                        float newAngle=getTouchAngle(event);
                        float deltaRotation= newAngle- oldAngle;
                        ivOnGrid.setRotation(ivOnGrid.getRotation()-deltaRotation);
                        oldAngle=newAngle;



                    }
                }
                break;

        }
        }
        return true; // indicate event was handled
    }


    private float getTouchAngle(MotionEvent event) {
        float angle= (float) Math.toDegrees(Math.atan2(event.getX(0) - event.getX(1),event.getY(0) - event.getY(1)));
        return angle;
    }

    private float getTouchSpacing(MotionEvent event) {
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
//    private void dumpEvent(MotionEvent event) {
//        String names[] = { "DOWN" , "UP" , "MOVE" , "CANCEL" , "OUTSIDE" ,
//                "POINTER_DOWN" , "POINTER_UP" , "7?" , "8?" , "9?" };
//        StringBuilder sb = new StringBuilder();
//        int action = event.getAction();
//        int actionCode = action & MotionEvent.ACTION_MASK;
//        sb.append("event ACTION_" ).append(names[actionCode]);
//        if (actionCode == MotionEvent.ACTION_POINTER_DOWN
//                || actionCode == MotionEvent.ACTION_POINTER_UP) {
//            sb.append("(pid " ).append(
//                    action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
//            sb.append(")" );
//        }
//        sb.append("[" );
//        for (int i = 0; i < event.getPointerCount(); i++) {
//            sb.append("#" ).append(i);
//            sb.append("(pid " ).append(event.getPointerId(i));
//            sb.append(")=" ).append((int) event.getX(i));
//            sb.append("," ).append((int) event.getY(i));
//            if (i + 1 < event.getPointerCount())
//                sb.append(";" );
//        }
//        sb.append("]" );
//        Log.d(TAG, sb.toString());
//    }

    //functions that suppose to help the images from the gallery not to crash
    private Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.grid_activity2, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
//            case R.id.action_settings:
//                openSettings();
//                return true;
            case R.id.action_lock_pic_movment:
                isPicLocked=true;
                return true;
            case R.id.action_unlock_pic_movment:
                isPicLocked=false;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
