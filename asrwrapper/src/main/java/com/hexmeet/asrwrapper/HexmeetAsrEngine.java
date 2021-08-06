package com.hexmeet.asrwrapper;

import android.content.Context;

import java.io.PipedOutputStream;

public class HexmeetAsrEngine {

    HexmeetAsrEngineImpl _impl = HexmeetAsrEngineFactory.getEngineByManufacture("baidu");

    public void init(Context context){
        _impl.init(context);
    }

    public void start(){
        _impl.start();
    }

    public void stop(){
        _impl.stop();
    }

    public void destroy() {
        _impl.destroy();
    }

    public void pause() {
        _impl.pause();
    }

    public void registerListener(HexmeetAsrEngineListener var){
        _impl.registerListener(var);
    }

    public void unregisterListener(HexmeetAsrEngineListener var){
        _impl.unregisterListener(var);

    }

    public PipedOutputStream getPipedOutStream(){
        return _impl.getPipedOutStream();
    }


}
