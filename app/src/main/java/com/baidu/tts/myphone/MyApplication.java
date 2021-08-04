package com.baidu.tts.myphone;

import android.app.Application;

import com.baidu.aip.asrwakeup3.core.inputstream.MyPipedApplication;

import java.io.PipedOutputStream;

public class MyApplication extends Application implements MyPipedApplication {

    private PipedOutputStream pipedOutputStream;
    boolean streamReady = false;
    @Override
    public PipedOutputStream createPipedStream(){
        pipedOutputStream = new PipedOutputStream();
        return pipedOutputStream;
    }


    @Override
    public PipedOutputStream getPipedOutStream() {
        return pipedOutputStream;
    }

    @Override
    public void setPipedStreamReady(boolean ready){
        streamReady = ready;
    }

    @Override
    public boolean isStreamReady() {
        return streamReady;
    }
}
