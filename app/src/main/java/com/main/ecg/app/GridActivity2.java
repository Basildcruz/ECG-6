package com.main.ecg.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class GridActivity2 extends ActionBarActivity  {

    public ImageView ivOnGrid;
    DrawView drawView;
    FrameLayout MainLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_activity2);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }


        // get the image from the camera and display it on this activity
        Intent picIntent = getIntent();
        Bundle extras = picIntent.getExtras();
        Bitmap delegatedImageBitmap = (Bitmap) extras.get("pic");
        ivOnGrid=(ImageView) findViewById(R.id.gridImageView);
        ivOnGrid.setImageBitmap(delegatedImageBitmap);

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
