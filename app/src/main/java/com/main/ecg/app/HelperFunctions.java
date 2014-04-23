package com.main.ecg.app;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by yosefschechter on 23/04/14.
 */
public class HelperFunctions {

    private static Integer activityNumber;
    private static String [] stepName = new String[14];
    private static ImageView ecgPic;
    private static boolean isFromCamera;
    private static Bitmap ecgBitmap;
    private static String ecgPicPath;


    public HelperFunctions(){
        super();
        activityNumber=0;

        //initiate the step names:
        stepName[0]="N/A";
        stepName[1]="Measure heart rate (RR interval)";
        stepName[2]="Determine rhythm";
        stepName[3]="Measure PR interval";
        stepName[4]="Measure P wave height and width";
        stepName[5]="Measure QRS width";
        stepName[6]="Measure QRS voltage";
        stepName[7]="Measure QT interval";
        stepName[8]="Determine electrical axis";
        stepName[9]="Determine R progression";
        stepName[10]="Look for abnormal q waves";
        stepName[11]="Measure ST segment";
        stepName[12]="Look for abnormal T waves";
        stepName[13]="Look for U waves";

    }

    public static void plusOne(){

        activityNumber++;
    }

    public static Integer getActivityNumberCounter(){
        return activityNumber;
    }

    public static String getStepName(){
        return stepName[activityNumber];
    }

    public static void setImageView(ImageView iv){
        ecgPic=iv;
    }

    public static ImageView getImageView(){
        return ecgPic;
    }

    public static void setIsFromCamera(boolean b){
        isFromCamera=b;
    }

    public static boolean getIsFromCamera(){
        return isFromCamera;
    }

    public static void setEcgBitmap(Bitmap bm){
        ecgBitmap=bm;
    }

    public static Bitmap getEcgBitmap(){
        return ecgBitmap;
    }

    public static void setEcgPath(String s){
        ecgPicPath=s;
    }

    public static String getEcgPath(){
        return ecgPicPath;
    }






}
