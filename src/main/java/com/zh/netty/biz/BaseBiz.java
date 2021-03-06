package com.zh.netty.biz;

import com.zh.netty.model.ResultObj;
import io.netty.channel.Channel;

public interface BaseBiz {
    void sendError(Channel channel);

    void sendMessage(Channel incoming, ResultObj resultObj);
}
