package org.android.framework.ui.paging;

import java.util.List;

public interface PagingResponseHandler<T, R> {

    /**
     * 将返回对象转换为数据集合
     * @param r
     * @return
     */
    List<T> onResponse(R r);

    /**
     * 是否还有更多数据
     * @return
     */
    boolean hasMore();
}
