package com.yury.demo.model.messages;

import java.io.Serializable;

public class AcknowledgementMessage {
    public static enum ResponseCode implements Serializable {
        ERROR,
        ORDER_ACCEPTED,
        INFO;

        public String getResponse() {
            return this.name();
        }
    }

    private ResponseCode responseCode; //One of: "Error" "Success" "Info"
    private String message;

    public AcknowledgementMessage() {};

    public AcknowledgementMessage(ResponseCode responseCode, String message) {
        this.responseCode = responseCode;
        this.message = message;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }
    public String getMessage() {
        return message;
    }
}

