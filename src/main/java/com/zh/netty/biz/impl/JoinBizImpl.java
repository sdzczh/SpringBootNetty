package com.zh.netty.biz.impl;


import com.alibaba.fastjson.JSONObject;
import com.zh.netty.biz.JoinBiz;
import com.zh.netty.biz.PingBiz;
import com.zh.netty.model.ResultCode;
import com.zh.netty.model.ResultObj;
import com.zh.netty.model.WebSocketClient;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

@Component
@Transactional
public class JoinBizImpl extends BaseBizImpl implements JoinBiz {

    @Override
    public void join(Channel channel, JSONObject data, ResultObj resultObj, Map<String, WebSocketClient> allSocketClients) {
        int scene = data.getIntValue("scene");
        resultObj.setScene(scene);
        if (scene == 0) {
            resultObj.setMsg(ResultCode.TYPE_ERROR_PARAMS.message());
            resultObj.setCode(ResultCode.TYPE_ERROR_PARAMS.code());
            sendMessage(channel, resultObj);
            return;
        }
        String comingIp = channel.remoteAddress().toString();
        WebSocketClient wsc = allSocketClients.get(comingIp);
        if (wsc == null) {
            wsc = new WebSocketClient(comingIp, scene);
            wsc.setChannel(channel);
            wsc.setScene(scene);
            allSocketClients.put(comingIp, wsc);
        } else {
            wsc.setChannel(channel);
            wsc.setScene(scene);
        }
        if(scene == 301){
            channel.writeAndFlush(new TextWebSocketFrame("【服务器于 " + LocalDate.now() + "接收到消息：】 ，消息内容为：" + data.toJSONString()));
        }
    }
}
