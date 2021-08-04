package com.baidu.tts.myphone;

import android.content.Context;

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
        _impl.stop();
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


}
