package com.zh.netty.handler;

import com.alibaba.fastjson.JSONObject;
import com.zh.netty.biz.BaseBiz;
import com.zh.netty.biz.BroadCastBiz;
import com.zh.netty.biz.JoinBiz;
import com.zh.netty.biz.PingBiz;
import com.zh.netty.model.ResultCode;
import com.zh.netty.model.ResultObj;
import com.zh.netty.model.WebSocketClient;
import com.zh.netty.utils.StrUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.ImmediateEventExecutor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


//TextWebSocketFrame：处理消息的handler，在Netty中用于处理文本的对象，frames是消息的载体
@Log4j2
@Component
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    //用于记录和管理所有客户端的channel，可以把相应的channel保存到一整个组中
    //DefaultChannelGroup：用于对应ChannelGroup，进行初始化
    private static ChannelGroup channelClient =  new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static ChannelGroup group = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private static Map<String, WebSocketClient> allSocketClients = new ConcurrentHashMap<>();
    @Resource
    PingBiz pingBiz;
    @Resource(name = "baseBizImpl")
    BaseBiz baseBiz;
    @Resource
    JoinBiz joinBiz;
    @Resource
    BroadCastBiz broadCastBiz;
    private static ChatHandler  chatHandler ;

    @PostConstruct //通过@PostConstruct实现初始化bean之前进行的操作
    public void init() {
        chatHandler = this;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg){
        //text()获取从客户端发送过来的字符串
        String receiveMsg = msg.text();
        Channel incoming = ctx.channel();
        log.info("websocket服务器收到数据：" + receiveMsg);

        try {
            JSONObject json = JSONObject.parseObject(receiveMsg);
            String action = json.getString("action");
            String cmsgCode = json.getString("cmsg_code");
            if (cmsgCode == null || "".equals(cmsgCode)) {
                cmsgCode = "-1";
            }
            ResultObj resultObj = new ResultObj();
            resultObj.setCmsgCode(cmsgCode);
            switch (action) {
                case "ping": {
                    chatHandler.pingBiz.ping(incoming, cmsgCode);
                    break;
                }
                case "join": {
                    JSONObject data = json.getJSONObject("data");
                    if (data == null) {
                        resultObj.setCode(ResultCode.TYPE_ERROR_PARAMS.code());
                        resultObj.setMsg(ResultCode.TYPE_ERROR_PARAMS.message());
                        chatHandler.baseBiz.sendMessage(incoming, resultObj);
                        return;
                    }
                    chatHandler.joinBiz.join(incoming, data, resultObj, allSocketClients);
                    break;
                }
                case "broadcast": {
                    JSONObject data = json.getJSONObject("data");
                    if (StrUtils.isBlank(action) || data == null) {
                        resultObj.setCode(ResultCode.TYPE_ERROR_PARAMS.code());
                        resultObj.setMsg(ResultCode.TYPE_ERROR_PARAMS.message());
                        baseBiz.sendMessage(incoming, resultObj);
                        return;
                    }
                    chatHandler.broadCastBiz.broadCast(data, allSocketClients, channelClient);
                    break;
                }
                default: {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ResultObj resultObj = new ResultObj();
            resultObj.setMsg(ResultCode.TYPE_ERROR_UNKNOW.message());
            resultObj.setCode(ResultCode.TYPE_ERROR_UNKNOW.code());
            chatHandler.baseBiz.sendMessage(incoming, resultObj);
//            ctx.close();
        }
    }

    //当客户端连接服务端（或者是打开连接之后）
    @Override
    public void handlerAdded(ChannelHandlerContext ctx){
        //获取客户端所对应的channel，添加到一个管理的容器中即可
        Channel incoming = ctx.channel();
        group.add(incoming);
        log.info("Client:" + incoming.remoteAddress().toString() + "加入,当前链接数：" + group.size());
        channelClient.add(ctx.channel());
    }

    //客户端断开
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx){
        //实际上是多余的，只要handler被移除，client会自动的把对应的channel移除掉
        channelClient.remove(ctx.channel());
        //每一而channel都会有一个长ID与短ID
        Channel incoming = ctx.channel();
        log.info("Client:" + incoming.remoteAddress().toString() + "离开,当前链接数：" + group.size());
        allSocketClients.remove(incoming.remoteAddress().toString());
        incoming.close();
    }
}
