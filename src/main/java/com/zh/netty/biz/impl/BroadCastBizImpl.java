package com.zh.netty.biz.impl;

import com.alibaba.fastjson.JSONObject;
import com.zh.netty.biz.BroadCastBiz;
import com.zh.netty.model.ResultCode;
import com.zh.netty.model.ResultObj;
import com.zh.netty.model.WebSocketClient;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Map;

@Log4j2
@Component
public class BroadCastBizImpl extends BaseBizImpl implements BroadCastBiz {

    private static ChannelGroup channelClient =  new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    @Override
    public void broadCast(JSONObject data, Map<String, WebSocketClient> allSocketClients, ChannelGroup channelClient) {
        Object info = data.get("info");
        int scene = data.getIntValue("scene");
        String action = data.getString("action");

        //根据join推送
        for (WebSocketClient client : allSocketClients.values()) {
            if(scene == 301){
                try {
                    ResultObj resultObj = new ResultObj();
                    resultObj.setMsg(ResultCode.SUCCESS.message());
                    resultObj.setCode(ResultCode.SUCCESS.code());
                    sendMessage(client.getChannel(), resultObj);
                } catch (Exception e) {
                    e.printStackTrace();
                    ResultObj resultObj = new ResultObj();
                    resultObj.setMsg(ResultCode.TYPE_ERROR_UNKNOW.message());
                    resultObj.setCode(ResultCode.TYPE_ERROR_UNKNOW.code());
                    sendMessage(client.getChannel(), resultObj);
                }
            }
        }
        //全员推送
        for (Channel channel : channelClient) {
            try {
                ResultObj resultObj = new ResultObj();
                resultObj.setMsg(ResultCode.SUCCESS.message());
                resultObj.setCode(ResultCode.SUCCESS.code());
                sendMessage(channel, resultObj);
            } catch (Exception e) {
                e.printStackTrace();
                ResultObj resultObj = new ResultObj();
                resultObj.setMsg(ResultCode.TYPE_ERROR_UNKNOW.message());
                resultObj.setCode(ResultCode.TYPE_ERROR_UNKNOW.code());
                sendMessage(channel, resultObj);
            }
        }
    }


}
