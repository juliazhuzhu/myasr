package com.hexmeet.asrwrapper;

public class HexmeetAsrEngineFactory {
    //"baidu"
    static public HexmeetAsrEngineImpl getEngineByManufacture(String manufature){

        if (manufature == "baidu"){
            return new BaiduAsrEngineImpl();
        }

        return null;
    }
}
