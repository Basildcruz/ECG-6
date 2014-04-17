package com.main.ecg.app;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
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

    public ImageButton cameraButton;
    public ImageButton galleryButton;

    static final int TAKE_PIC_REQUEST = 1;
    static  final int RESULT_LOAD_IMAGE=2;
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

        cameraButton = (ImageButton) findViewById(R.id.camera_button);
        galleryButton= (ImageButton) findViewById(R.id.gallery_button);

//
//        if(v.getId() == R.id.camera_button) {
//            // do this
//        }else if(v.getId() == R.id.button2) {
//            // do that
//        }
        cameraButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this,
                        "cameraButton is clicked!", Toast.LENGTH_SHORT).show();

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivity(cameraIntent);

                Bitmap ECG_STRIP_PIC;

                startActivityForResult(cameraIntent, TAKE_PIC_REQUEST);

            }

        });

        galleryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this,
                        "galleryButton is clicked!", Toast.LENGTH_SHORT).show();

                Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //camera intent
        // Check which request we're responding to
        if (requestCode == TAKE_PIC_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                ECG_STRIP_PIC_URI=data.getData();

                //get the pic and show it on a imageView
                Bundle extras = data.getExtras();
                Bitmap mImageBitmap = (Bitmap) extras.get("data");
//                cameraButton = (ImageButton) findViewById(R.id.camera_button);
                iv= (ImageView) findViewById(R.id.imageView1);
                iv.setImageBitmap(mImageBitmap);

                // delegate the pic to the next window: "GridActivity2"
                Intent goToGridView = new Intent(MainActivity.this, GridActivity2.class);
                goToGridView.putExtra("picFromCamera", (Bitmap) extras.get("data"));
                MainActivity.this.startActivity(goToGridView);


            }

        //gallery intent
        } else if ((requestCode == RESULT_LOAD_IMAGE)){

            if (resultCode == RESULT_OK) {
//                ECG_STRIP_PIC_URI=data.getData();
//
//                //get the pic and show it on a imageView
//                Bundle extras = data.getExtras();
//                Bitmap mImageBitmap = (Bitmap) extras.get("data");
////                cameraButton = (ImageButton) findViewById(R.id.camera_button);
//                iv= (ImageView) findViewById(R.id.imageView1);
//                iv.setImageBitmap(mImageBitmap);

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
//                ImageView imageView = (ImageView) findViewById(R.id.imgView);
//                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                //get the pic and show it on a imageView
//                Bundle extras = data.getExtras();
//                Bitmap mImageBitmap = (Bitmap) extras.get("data");
//                cameraButton = (ImageButton) findViewById(R.id.camera_button);
    //                iv= (ImageView) findViewById(R.id.imageView1);
    //                iv.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                // delegate the pic to the next window: "GridActivity2"
                Intent goToGridView = new Intent(MainActivity.this, GridActivity2.class);
                goToGridView.putExtra("picFromGallery", picturePath);
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
