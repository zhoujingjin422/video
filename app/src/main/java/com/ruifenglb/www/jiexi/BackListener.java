package com.ruifenglb.www.jiexi;

public interface BackListener {

    void onSuccess(String url,int curParseIndex);

    void onError();

    void onProgressUpdate(String msg);
}
