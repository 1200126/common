package com.hzy.common.smartrefresh.layout.listener;

import com.hzy.common.smartrefresh.layout.api.RefreshLayout;

public interface OnSmartRefreshListener {

    void onRefresh(RefreshLayout refreshlayout);

    void onLoadmore(RefreshLayout refreshlayout);
}
