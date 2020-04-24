package com.yury.demo.book;

import java.io.Serializable;

/**
 * Message of acknowledgement for orders received
 */
public class AcknowledgementMessage {
    public enum ResponseCode implements Serializable {
        ERROR,
        ORDER_ACCEPTED,
        INFO;

        public String getResponse() {
            return this.name();
        }
    }

    private ResponseCode responseCode; //One of: "Error" "Success" "Info"
    private String message;

    public AcknowledgementMessage() {}

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

