package com.chuchujie.core.network.retrofit.convert;

/**
 * Created by yxb on 2018/7/18.
 */
public class StringJsonParseException extends RuntimeException{

    String responsebody;

    public StringJsonParseException(String message){
        super(message);
    }

    public StringJsonParseException(String message, Throwable cause){
        super(message, cause);
    }

    public StringJsonParseException(String message, Throwable cause, String responsebody){
        super(message, cause);
        this.responsebody = responsebody;
    }

    public String getResponsebody() {
        return responsebody;
    }

    public void setResponsebody(String responsebody) {
        this.responsebody = responsebody;
    }

}
