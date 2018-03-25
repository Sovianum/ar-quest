package com.google.ar.core.examples.java.helloar.network;

import java.io.IOException;

public class NetworkError extends IOException {
    private String errMsg;
    private int responseCode;

    public NetworkError(int responseCode) {
        this.responseCode = responseCode;
    }

    public NetworkError(String errMsg, int responseCode) {
        this.errMsg = errMsg;
        this.responseCode = responseCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public int getResponseCode() {
        return responseCode;
    }
}
