package com.zh.netty.biz;

import com.alibaba.fastjson.JSONObject;
import com.zh.netty.model.WebSocketClient;
import io.netty.channel.group.ChannelGroup;

import java.util.Map;

public interface BroadCastBiz {
    void broadCast(JSONObject data, Map<String, WebSocketClient> allSocketClients, ChannelGroup channelClient);
}
