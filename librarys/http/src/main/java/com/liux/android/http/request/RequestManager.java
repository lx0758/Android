package com.liux.android.http.request;

import java.util.LinkedList;
import java.util.List;

/**
 * 2018/4/10
 * By Liux
 * lx0758@qq.com
 */
public interface RequestManager {

    void add(Request request);

    void remove(Request request);

    void cancelAll();

    class Builder {
        public static RequestManager build() {
            return new RequestManager() {
                List<Request> mRequests = new LinkedList<>();
                @Override
                public void add(Request request) {
                    if (!mRequests.contains(request)) {
                        mRequests.add(request);
                    }
                }

                @Override
                public void remove(Request cancel) {
                    mRequests.remove(cancel);
                }

                @Override
                public void cancelAll() {
                    for (Request cancel : mRequests) {
                        if (cancel != null) cancel.cancel();
                    }
                    mRequests.clear();
                }
            };
        }
    }
}
