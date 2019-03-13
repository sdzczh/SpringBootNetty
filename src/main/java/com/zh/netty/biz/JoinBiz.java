package com.zh.netty.biz;

import com.alibaba.fastjson.JSONObject;
import com.zh.netty.model.ResultObj;
import com.zh.netty.model.WebSocketClient;
import io.netty.channel.Channel;

import java.util.Map;

public interface JoinBiz {
    void join(Channel channel, JSONObject data, ResultObj resultObj, Map<String, WebSocketClient> allSocketClients);

}
