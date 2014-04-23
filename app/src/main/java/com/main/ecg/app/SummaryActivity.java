package com.main.ecg.app;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SummaryActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new PlaceholderFragment())
//                    .commit();
//        }

        //action bar settings
        android.app.ActionBar ab = getActionBar();
//        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#336699")));
        ab.setTitle("Summary sheet");
//        ab.setSubtitle("step "+ activity_number.toString()+"/12");


        //set the measured data on screen
        setDataOnScreen();

    }

    private void setDataOnScreen() {
        TextView txt;

        for (int i=1; i<=HelperFunctions.getMaxNumOfSteps(); i++){
            switch (i){
                case 1:
                    txt= (TextView) findViewById(R.id.t1);
                    setValueOnScreen(i,txt);
                    break;

                case 2:
                    txt= (TextView) findViewById(R.id.t2);
                    setValueOnScreen(i,txt);
                    break;

                case 3:
                    txt= (TextView) findViewById(R.id.t3);
                    setValueOnScreen(i, txt);
                    break;

                case 4:
                    txt= (TextView) findViewById(R.id.t4);
                    setValueOnScreen(i,txt);
                    break;

                case 5:
                    txt= (TextView) findViewById(R.id.t5);
//                    setValueOnScreen(i,txt);
                    break;

                case 6:
                    txt= (TextView) findViewById(R.id.t6);
//                    setValueOnScreen(i,txt);
                    break;

                case 7:
                    txt= (TextView) findViewById(R.id.t7);
//                    setValueOnScreen(i,txt);
                    break;

                case 8:
                    txt= (TextView) findViewById(R.id.t8);
//                    setValueOnScreen(i,txt);
                    break;

                case 9:
                    txt= (TextView) findViewById(R.id.t9);
//                    setValueOnScreen(i,txt);
                    break;

                case 10:
                    txt= (TextView) findViewById(R.id.t10);
//                    setValueOnScreen(i,txt);
                    break;

                case 11:
                    txt= (TextView) findViewById(R.id.t11);
//                    setValueOnScreen(i,txt);
                    break;

                case 12:
                    txt= (TextView) findViewById(R.id.t12);
//                    setValueOnScreen(i,txt);
                    break;

                case 13:
                    txt= (TextView) findViewById(R.id.t13);
//                    setValueOnScreen(i,txt);
                    break;


            }


        }
    }

    private void setValueOnScreen(int i, TextView txt) {
        String s;
        s= (String) txt.getText();
        txt.setText(s+" " + Float.toString(HelperFunctions.getDataOfSpecificStep(i)));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.summary, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_summary, container, false);
            return rootView;
        }
    }

}
