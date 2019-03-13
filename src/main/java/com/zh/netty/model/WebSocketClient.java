package com.zh.netty.model;

import io.netty.channel.Channel;
import lombok.Data;

import java.lang.reflect.Field;

@Data
public class WebSocketClient {
	private Channel channel;//当前的通道
	private String remoteIp;
	private Integer scene;//当前所处的场景 1首页

	public WebSocketClient(String remoteIp, Integer scene){
		this.remoteIp = remoteIp;
		this.scene = scene;
	}
}
