package com.hexmeet.asrwrapper;

import android.content.Context;

import java.io.PipedOutputStream;

public interface HexmeetAsrEngineImpl {

    public void init(Context context);
    public void start();
    public void stop();
    public void pause();
    public void destroy();
    public void registerListener(HexmeetAsrEngineListener var1);
    public void unregisterListener(HexmeetAsrEngineListener var1);
    public PipedOutputStream getPipedOutStream();
}
