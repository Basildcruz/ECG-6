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
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

public class StepActivity extends ActionBarActivity implements View.OnTouchListener {



    public ImageView ivOnGrid;
    public ImageView leftRuller;
    public ImageView rightRuller;
    public ImageView upRuller;
    public ImageView downRuller;
    public ImageView grid;
    public ImageView infoIV;

    public ImageButton informationButton;
    public ImageButton moveOrMeasureButton;
    public ImageButton checkButton;

    public EditText intervalED;

    public PointF pivot= new PointF();
    public PointF offset=new PointF(); // the offset is the difference between the point of the press to the 0,0 point of the picture

    public Float initialTouchDistance;
    public Float initialScale;
    public Float initialRotation;
    public Float oldAngle;
    public float intervalBetweenRullers=0;

    public boolean isPicLocked=false;
    public boolean measureBtnWasClickd=false;
    DrawView drawView;
    FrameLayout MainLayout;
    private static final String TAG = "Touch" ;

    public Integer activity_number;


    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    //touch states
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

        //plus one the counter
        HelperFunctions.plusOne();
        activity_number= HelperFunctions.getActivityNumberCounter();

        //example for how this is going to work
//        switch (ActivityNumberCounter.getActivityNumberCounter()){
//            case 1:
//                iv.setImageDrawable(getResources().getDrawable(R.drawable.info_32px));
//                break;



        //action bar settings
        ActionBar ab = getActionBar();
//        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#336699")));
        ab.setTitle(HelperFunctions.getStepName());
        ab.setSubtitle("step "+ activity_number.toString()+"/12");

        //things to do at the first step
        if (activity_number==1){
            thingsToDoAtFirstStep();
        }

        //things to do from step 2 till summary
        if (activity_number>1){
            thingsToDoAfterFirstStep();
        }

        //interval edit text settings
        intervalED=(EditText) findViewById(R.id.intervalEditText);
        intervalED.setText(Float.toString(intervalBetweenRullers));
        intervalED.bringToFront();


        //draw a grid
        MainLayout = (FrameLayout) findViewById(R.id.frame_layout_1);
        CustomView customview = new CustomView(this);
        MainLayout.addView(customview);

        //add a listener to the touch events
        addTouchListeners();

        //add listeners on buttons
        addListenerToMeasureButton();
        addListenerToInfoButton();
        addListenerToCheckButton();

        //add animation on measure button
        moveOrMeasureButton= (ImageButton) findViewById(R.id.moveOrMeasureImageButton);
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(StepActivity.this,R.anim.fade);
        moveOrMeasureButton.startAnimation(myFadeInAnimation);

    }


    private void thingsToDoAtFirstStep() {
        // get the image from the camera or the gallery and display it on this activity
        Intent picIntent = getIntent();
        Bundle extras = picIntent.getExtras();

        //get pic from camera
        if (extras.get("picFromCamera") instanceof Bitmap) {
            Bitmap delegatedImageBitmap = (Bitmap) extras.get("picFromCamera");
            HelperFunctions.setIsFromCamera(true);
            HelperFunctions.setEcgBitmap(delegatedImageBitmap);
            ivOnGrid=(ImageView) findViewById(R.id.picImageView);
            ivOnGrid.setImageBitmap(delegatedImageBitmap);
            delegatedImageBitmap.recycle();
            //get pic from gallery
        } else{
            String delegatedImagePath = (String) extras.get("picFromGallery");
            HelperFunctions.setIsFromCamera(false);
            HelperFunctions.setEcgPath(delegatedImagePath);
            ivOnGrid= (ImageView) findViewById(R.id.picImageView);
//            ivOnGrid.setImageBitmap(BitmapFactory.decodeFile(delegatedImagePath));
            ivOnGrid.setImageBitmap(getScaledBitmap(delegatedImagePath, 800, 800));
        }
    }

    private void thingsToDoAfterFirstStep() {
        ivOnGrid=(ImageView) findViewById(R.id.picImageView);
        if (HelperFunctions.getIsFromCamera()==true){
            ivOnGrid.setImageBitmap(HelperFunctions.getEcgBitmap());
            HelperFunctions.getEcgBitmap().recycle();
        }else {
            ivOnGrid.setImageBitmap(getScaledBitmap(HelperFunctions.getEcgPath(), 800, 800));
//            HelperFunctions.getEcgBitmap().recycle();
        }

        ivOnGrid.setTranslationX(HelperFunctions.getImageView().getTranslationX());
        ivOnGrid.setTranslationY(HelperFunctions.getImageView().getTranslationY());

        ivOnGrid.setScaleX(HelperFunctions.getImageView().getScaleX());
        ivOnGrid.setScaleY(HelperFunctions.getImageView().getScaleY());

        ivOnGrid.setRotation(HelperFunctions.getImageView().getRotation());


    }


    public void addListenerToInfoButton(){
        infoIV = (ImageView)findViewById(R.id.infoImageView);
        switch (activity_number){
            case 2:
                infoIV.setImageDrawable(getResources().getDrawable(R.drawable.ecgrid_logo));
                break;
            case 3:
                infoIV.setImageDrawable(getResources().getDrawable(R.drawable.pr_interval_info_bubble));
                break;

        }

        infoIV.setVisibility(View.INVISIBLE);
        informationButton= (ImageButton) findViewById(R.id.infoImageButton);
        informationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(infoIV.getVisibility() == View.INVISIBLE)
                {
                    infoIV.setVisibility(View.VISIBLE);
                    infoIV.bringToFront();
                }
                else if (infoIV.getVisibility() == View.VISIBLE)
                {
                    infoIV.setVisibility(View.INVISIBLE);
                }


            }

        });

    }


    public void addListenerToMeasureButton(){
        moveOrMeasureButton = (ImageButton) findViewById(R.id.moveOrMeasureImageButton);

        moveOrMeasureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //stoping the fade animation on the button, since the user used it
                moveOrMeasureButton.clearAnimation();


                if (isPicLocked == false) {
                    isPicLocked = true;
                    moveOrMeasureButton.setImageDrawable(getResources().getDrawable(R.drawable.movepic_32px));
                    switch (activity_number){
                        case 1:
                        case 3:
                        case 5:
                        case 6:
                        case 11:
                            leftRuller.setVisibility(View.VISIBLE);
                            rightRuller.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                        case 7:
                            upRuller.setVisibility(View.VISIBLE);
                            downRuller.setVisibility(View.VISIBLE);
                            break;
                    }
                } else if (isPicLocked == true) {
                    isPicLocked = false;
                    moveOrMeasureButton.setImageDrawable(getResources().getDrawable(R.drawable.measure_32px));
                    switch (activity_number){
                        case 1:
                        case 3:
                        case 5:
                        case 6:
                        case 11:
                            leftRuller.setVisibility(View.VISIBLE);
                            rightRuller.setVisibility(View.VISIBLE);
                            break;

                        case 2:
                        case 7:
                            upRuller.setVisibility(View.VISIBLE);
                            downRuller.setVisibility(View.VISIBLE);
                            break;
                    }

                }
            }

        });

    }


    public void addListenerToCheckButton(){
        checkButton= (ImageButton) findViewById(R.id.checkImageButton);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //stop the animation on the button
                checkButton.clearAnimation();


                if(activity_number<HelperFunctions.getMaxNumOfSteps()){

                    //save the ecg picture state
                    HelperFunctions.setImageView(ivOnGrid);

//                    //save the measurment
//                    HelperFunctions.setDataInDataArry(activity_number,intervalBetweenRullers);

                    // move to the next step
                    Intent goToNextStep = new Intent(StepActivity.this, StepActivity.class);
                    StepActivity.this.startActivity(goToNextStep);

                }else {
                    // move to the summary window
                    Intent goToNextStep = new Intent(StepActivity.this, SummaryActivity.class);
                    StepActivity.this.startActivity(goToNextStep);
                }

            }

        });
    }






    public void addTouchListeners() {

        //add move, zoom, and tilt capabilities to pic
        grid= (ImageView) findViewById(R.id.gridImageView);
        grid.setOnTouchListener(new View.OnTouchListener(){
            // manage the move, zoom and tilt capabilities

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

        });

        //add rullers
        //begin measure ruller

        leftRuller =(ImageView) findViewById(R.id.leftRullerImageView);
        leftRuller.setImageDrawable(getResources().getDrawable(R.drawable.right_ruller_4pt));
        leftRuller.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (isPicLocked == true) {
                    leftRuller.bringToFront();

                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            offset.set(event.getX() - leftRuller.getTranslationX(), event.getY());
                            Log.d("offset", offset.toString());
                            mode = DRAG;
                            break;

                        case MotionEvent.ACTION_MOVE:
                            if (mode == DRAG) {

                                //move just on the horizontal plane
                                leftRuller.setTranslationX(event.getX() - offset.x);
                                intervalBetweenRullers = rightRuller.getX() - leftRuller.getX();
                                intervalED.setText(Float.toString(intervalBetweenRullers));
                                //save the measurment
                                HelperFunctions.setDataInDataArry(activity_number,intervalBetweenRullers);

                            }
//                        case MotionEvent.ACTION_UP:
//                            mode = NONE;
//                            Log.d(TAG, "mode=NONE" );
//                            break;
                    }


                }
                return true;
            }
        });


        //end measure ruller
        rightRuller =(ImageView) findViewById(R.id.rightRullerImageView);
        rightRuller.setImageDrawable(getResources().getDrawable(R.drawable.left_ruller_4pt));
        rightRuller.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (isPicLocked == true) {
                    rightRuller.bringToFront();

                    //starting the fade animation on the check button, since this is the next step
                    checkButton = (ImageButton) findViewById(R.id.checkImageButton);
                    Animation myFadeInAnimation = AnimationUtils.loadAnimation(StepActivity.this, R.anim.fade);
                    checkButton.startAnimation(myFadeInAnimation);

                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            offset.set(event.getX() - rightRuller.getTranslationX(), event.getY());
                            Log.d("offset", offset.toString());
                            mode = DRAG;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (mode == DRAG) {

                                //move just on the horizontal plane
                                rightRuller.setTranslationX(event.getX() - offset.x);
                                intervalBetweenRullers = rightRuller.getX() - leftRuller.getX();
                                intervalED.setText(Float.toString(intervalBetweenRullers));
                                //save the measurment
                                HelperFunctions.setDataInDataArry(activity_number,intervalBetweenRullers);
                            }
//                        case MotionEvent.ACTION_UP:
//                            mode = NONE;
//                            Log.d(TAG, "mode=NONE" );
//                            break;
                    }


                }
                return true;
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
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

//            float[] pointsForGrid={0, (float) (height*0.2), 0, (float) (height*0.4), 0, (float) (height*0.6), 0, (float) (height*0.8), 0 ,(float) (height*1), width, (float) (height*0.2), width, (float) (height*0.4), width,(float) (height*0.6), width, (float) (height*0.8), width,(float) (height*1)};
//            canvas.drawLines(pointsForGrid, 0, 0, paint);

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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        // Inflate the menu items for use in the action bar
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.grid_activity2, menu);
////        return super.onCreateOptionsMenu(menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle presses on the action bar items
//        switch (item.getItemId()) {
////            case R.id.action_settings:
////                openSettings();
////                return true;
//            case R.id.lock_pic:
//                isPicLocked=true;
//                return true;
//            case R.id.unlock_pic:
//                isPicLocked=false;
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

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
