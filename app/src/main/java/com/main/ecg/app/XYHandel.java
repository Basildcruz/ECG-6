package com.main.ecg.app;

/**
 * Created by yosefschechter on 16/04/14.
 */
public class XYHandel {

    private float xPoint;
    private float yPoint;

    //constractor
    public XYHandel(){
        super();
    }

    public void set(float x, float y){
        xPoint=x;
        yPoint=y;
    }

    public float x(){
        return xPoint;
    }

    public float y(){
        return yPoint;
    }
}