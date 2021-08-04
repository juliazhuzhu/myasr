package com.baidu.aip.asrwakeup3.core.inputstream;

import java.io.PipedOutputStream;

public interface MyPipedApplication {

    PipedOutputStream getPipedOutStream();
    PipedOutputStream createPipedStream();
    void setPipedStreamReady(boolean ready);
    boolean isStreamReady();

}
