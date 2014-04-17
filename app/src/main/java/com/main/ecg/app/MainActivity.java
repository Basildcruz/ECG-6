package com.main.ecg.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;



public class MainActivity extends Activity {

    public ImageButton imageButton;
    static final int TAKE_PIC_REQUEST = 1;
    public Uri ECG_STRIP_PIC_URI;
    public ImageView iv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new PlaceholderFragment())
//                    .commit();
//        }

        addListenerOnButton();
        //hello!



    }

    public void addListenerOnButton() {

        imageButton = (ImageButton) findViewById(R.id.camera_button);

        imageButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this,
                        "ImageButton is clicked!", Toast.LENGTH_SHORT).show();

//                Intent browserIntent =
//                        new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.mkyong.com"));
//                startActivity(browserIntent);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivity(cameraIntent);

                Bitmap ECG_STRIP_PIC;

                startActivityForResult(cameraIntent, TAKE_PIC_REQUEST);
                

            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == TAKE_PIC_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                ECG_STRIP_PIC_URI=data.getData();

                //get the pic and show it on a imageView
                Bundle extras = data.getExtras();
                Bitmap mImageBitmap = (Bitmap) extras.get("data");
//                imageButton = (ImageButton) findViewById(R.id.camera_button);
                iv= (ImageView) findViewById(R.id.imageView1);
                iv.setImageBitmap(mImageBitmap);

                // delegate the pic to the next window: "GridActivity2"
                Intent goToGridView = new Intent(MainActivity.this, GridActivity2.class);
                goToGridView.putExtra("pic", (Bitmap) extras.get("data"));
                MainActivity.this.startActivity(goToGridView);


            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
