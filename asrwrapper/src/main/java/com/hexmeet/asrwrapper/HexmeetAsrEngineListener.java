package com.hexmeet.asrwrapper;

public interface HexmeetAsrEngineListener {

    void onAsrNetworkError();
    void onAsrText(String asr_content, boolean isPartial);
    void onAsrReady();
    void onAsrError(String err_info);
}
