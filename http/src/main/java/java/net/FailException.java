package java.net;

import java.io.IOException;

import okhttp3.Response;

/**
 * 2018/4/11
 * By Liux
 * lx0758@qq.com
 */
public class FailException extends IOException {

    private Response mResponse;

    public FailException() {
    }

    public FailException(String msg) {
        super(msg);
    }

    public FailException(Response response) {
        super(response.message());
        mResponse = response;
    }

    public Response getResponse() {
        return mResponse;
    }

    @Override
    public String toString() {
        String s = getClass().getSimpleName();
        String message = getLocalizedMessage();
        return (message != null) ? (s + ": " + message) : s;
    }
}
