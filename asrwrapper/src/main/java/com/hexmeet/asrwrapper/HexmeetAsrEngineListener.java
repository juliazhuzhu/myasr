package com.hexmeet.asrwrapper;

public interface HexmeetAsrEngineListener {

    void onAsrExit();
    void onAsrText(String asr_content, boolean isPartial);
    void onAsrReady();
    void onAsrError(String err_info);
}
