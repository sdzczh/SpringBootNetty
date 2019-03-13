package com.zh.netty.biz.impl;


import com.zh.netty.biz.PingBiz;
import com.zh.netty.model.ResultCode;
import com.zh.netty.model.ResultObj;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class PingBizImpl extends BaseBizImpl implements PingBiz {

    @Override
    public void ping(Channel channel, String cmsgCode) {
        ResultObj resultObj = new ResultObj();
        resultObj.setCmsgCode(cmsgCode);
        resultObj.setInfo("pong");
        resultObj.setCode(ResultCode.SUCCESS.code());
        resultObj.setMsg(ResultCode.SUCCESS.message());
        sendMessage(channel, resultObj);
    }
}
