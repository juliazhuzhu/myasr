package com.baidu.aip.asrwakeup3.core.inputstream;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class MyPipedStream {

    private PipedInputStream pipedInputStream;
    private PipedOutputStream pipedOutputStream;
    private Context context;

    private MyPipedStream(Context context) {
        pipedInputStream = new PipedInputStream();
        //pipedOutputStream = new PipedOutputStream();
        this.context = context;
    }

    private void start() throws IOException {
        //final InputStream is = context.getAssets().open("outfile.pcm");
        MyPipedApplication myApp = (MyPipedApplication) context.getApplicationContext();
        pipedOutputStream = myApp.getPipedOutStream();
        pipedInputStream.connect(pipedOutputStream);
        myApp.setPipedStreamReady(true);

    }

    public static PipedInputStream createAndStart(Context context) {
        MyPipedStream obj = new MyPipedStream(context);
        try {
            obj.start();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return obj.pipedInputStream;
    }
}
