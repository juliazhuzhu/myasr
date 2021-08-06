package com.baidu.tts.myphone;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import com.hexmeet.asrwrapper.HexmeetAsrEngineListener;
import com.hexmeet.asrwrapper.HexmeetAsrEngine;

//
// //ActivityMiniRecog
public class MainActivity extends AppCompatActivity implements HexmeetAsrEngineListener {

    protected TextView txtLog;
    protected TextView txtResult;
    protected Button btn;
    protected Button stopBtn;
    private static String DESC_TEXT = "精简版识别，带有SDK唤醒运行的最少代码，仅仅展示如何调用，\n" +
            "也可以用来反馈测试SDK输入参数及输出回调。\n" +
            "本示例需要自行根据文档填写参数，可以使用之前识别示例中的日志中的参数。\n" +
            "需要完整版请参见之前的识别示例。\n" +
            "需要测试离线命令词识别功能可以将本类中的enableOffline改成true，首次测试离线命令词请联网使用。之后请说出“打电话给李四”";

    private HexmeetAsrEngine asr;

    private boolean logTime = true;

    protected boolean enableOffline = false; // 测试离线命令词，需要改成true


    private void start() {

        printLog("Start");
        txtLog.setText("");


        asr = new HexmeetAsrEngine();
        asr.init(this);
        asr.registerListener(this);
        asr.start();

    }


    private void stop() {
        printLog("停止识别：ASR_STOP");
        //asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
        asr.unregisterListener(this);
        asr.destroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.baidu.aip.asrwakeup3.core.R.layout.common_mini);
        initView();
        initPermission();
        // 基于sdk集成1.1 初始化EventManager对象

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                start();
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stop();
            }
        });
        if (enableOffline) {
            //loadOfflineEngine(); // 测试离线命令词请开启, 测试 ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH 参数时开启
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        asr.pause();
        Log.i("ActivityMiniRecog", "On pause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asr.unregisterListener(this);
        asr.destroy();

    }

    @Override
    public void onAsrNetworkError() {
        printLog("onAsrNetworkError");
        //might exited by network failure, shoud start again;
        //set asr tag to toggle
        start();
    }

    @Override
    public void onAsrReady() {
        //set asr tag to toggle
        printLog("onAsrReady");

        Runnable run = new Runnable() {
            @Override
            public void run() {

                try {
                    final InputStream is = getAssets().open("outfile.pcm");

                    int bytePerMs = 16000 * 2 / 1000;
                    int count = bytePerMs * 20; // 20ms 音频数据
                    int r = 0;
                    byte[] buffer = new byte[count];
                    do {

                        r = is.read(buffer);
                        int sleepTime = 0;
                        PipedOutputStream pipedOutputStream = asr.getPipedOutStream();
                        if (r > 0 ) {
                            pipedOutputStream.write(buffer, 0, count);
                            sleepTime = r / bytePerMs;
                        } else if (r == 0) {
                            sleepTime = 100; // 这里数值按照自己情况而定
                        }
                        if (sleepTime > 0) {
                            try {
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    } while (r >= 0);
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

            }
        };
        (new Thread(run)).start();


    }

    @Override
    public void onAsrText(String asr_content, boolean isPartial) {
        printLog(asr_content);
    }

    @Override
    public void onAsrError(String err_info) {
        printLog(err_info);
    }

    private void printLog(String text) {
        if (logTime) {
            text += "  ;time=" + System.currentTimeMillis();
        }
        text += "\n";
        Log.i(getClass().getName(), text);
        txtLog.append(text + "\n");
    }


    private void initView() {
        txtResult = (TextView) findViewById(com.baidu.aip.asrwakeup3.core.R.id.txtResult);
        txtLog = (TextView) findViewById(com.baidu.aip.asrwakeup3.core.R.id.txtLog);
        btn = (Button) findViewById(com.baidu.aip.asrwakeup3.core.R.id.btn);
        stopBtn = (Button) findViewById(com.baidu.aip.asrwakeup3.core.R.id.btn_stop);
        txtLog.setText(DESC_TEXT + "\n");
    }


     // android 6.0 以上需要动态申请权限
    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }


}