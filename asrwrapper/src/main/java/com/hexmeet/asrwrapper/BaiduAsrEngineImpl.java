package com.hexmeet.asrwrapper;

import android.content.Context;

import com.baidu.speech.EventListener;
import com.baidu.aip.asrwakeup3.core.inputstream.InFileStream;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


public class BaiduAsrEngineImpl implements HexmeetAsrEngineImpl, EventListener {

    private EventManager asr;
    private Context context;
    BaiduAsrEngineImpl(){

    }

    private ArrayList<HexmeetAsrEngineListener> listeners = new ArrayList();
    @Override
    public void registerListener(HexmeetAsrEngineListener var1){
        if (var1 != null && !this.listeners.contains(var1)) {
            this.listeners.add(var1);
        }
    }

    @Override
    public void unregisterListener(HexmeetAsrEngineListener var1){
        this.listeners.remove(var1);
    }

    @Override
    public void init(Context context){
        asr = EventManagerFactory.create(context, "asr");
        InFileStream.setContext(context);
        // 基于sdk集成1.3 注册自己的输出事件类
        asr.registerListener(this); //  EventListener 中 onEvent方法
        this.context = context;

    }

    @Override
    public void start(){


        //myApp.setPipedStreamReady(false);
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = null;
        event = SpeechConstant.ASR_START; // 替换成测试的event
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.IN_FILE,"#com.baidu.aip.asrwakeup3.core.inputstream.InFileStream.createMyPipedInputStream()");
        //params.put(SpeechConstant.NLU, "enable");
        params.put(SpeechConstant.BDS_ASR_ENABLE_LONG_SPEECH, true);//长语音  优先级高于VAD_ENDPOINT_TIMEOUT
        params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0); // 长语音
        params.put(SpeechConstant.PID, 15372);


        String json = null; // 可以替换成自己的json
        json = new JSONObject(params).toString(); // 这里可以替换成你需要测试的json
        //printLog()
        asr.send(event, json, null, 0, 0);


    }

    @Override
    public void stop(){
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0); //
    }

    @Override
    public void pause() {
        asr.send(SpeechConstant.ASR_CANCEL, null, null, 0, 0);
    }

    @Override
    public void destroy() {

        asr.send(SpeechConstant.ASR_CANCEL, null, null, 0, 0);
        asr.unregisterListener(this);

    }

    @Override
    public PipedOutputStream getPipedOutStream() {
        return InFileStream.getPipedOutputStream();
    }

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {

        synchronized(this.listeners) {
            Iterator var10 = this.listeners.iterator();

            while(var10.hasNext()) {
                final HexmeetAsrEngineListener var11 = (HexmeetAsrEngineListener) var10.next();
                if (name.equals((SpeechConstant.CALLBACK_EVENT_ASR_READY))){
                    var11.onAsrReady();
                }
                if (name.equals((SpeechConstant.CALLBACK_EVENT_ASR_EXIT))) {
                    //start();
                    var11.onAsrExit();
                }

                if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
                    // 识别相关的结果都在这里
                    if (params == null || params.isEmpty()) {
                        return;
                    }
                    if (params.contains("\"nlu_result\"")) {
                        // 一句话的语义解析结果
                        if (length > 0 && data.length > 0) {
                            //logTxt += ", 语义解析结果：" + new String(data, offset, length);
                        }
                    } else if (params.contains("\"partial_result\"")) {
                        // 一句话的临时识别结果
                        //logTxt += ", 临时识别结果：" + params;
                        try {
                            JSONObject jObjectParam = new JSONObject(params);
                            JSONArray jsonWord = jObjectParam.getJSONArray("results_recognition");
                            for (int i = 0; i < jsonWord.length(); i++) {
                                //jArray.optString(i);//等价于getXXX
                                // list.add(jArray.getString(i));
                                var11.onAsrText(jsonWord.getString(i), true);
                            }
                        }catch (JSONException e){
                            var11.onAsrError(e.getLocalizedMessage());
                        }
                       // var11.onAsrText(params,true);
                    }  else if (params.contains("\"final_result\""))  {
                        // 一句话的最终识别结果
                        //logTxt += ", 最终识别结果：" + params;
                        try {
                            JSONObject jObjectParam = new JSONObject(params);

                            JSONArray jsonWord = jObjectParam.getJSONArray("results_recognition");
                            for (int i = 0; i < jsonWord.length(); i++) {
                                //jArray.optString(i);//等价于getXXX
                                // list.add(jArray.getString(i));
                                var11.onAsrText(jsonWord.getString(i), false);
                            }
                        }catch (JSONException e){
                            var11.onAsrError(e.getLocalizedMessage());
                        }
                        //var11.onAsrText(params,false);
                    }  else {
                        // 一般这里不会运行
                        //logTxt += " ;params :" + params;
                        if (data != null) {
                            //logTxt += " ;data length=" + data.length;
                        }
                    }
                } else {
                    // 识别开始，结束，音量，音频数据回调
                    String logTxt = "name: " + name;
                    if (params != null && !params.isEmpty()){
                        logTxt += " ;params :" + params;
                    }
                    if (data != null) {
                        logTxt += " ;data length=" + data.length;
                    }
                    var11.onAsrError(logTxt);

                }

            }
        }



    }




}
