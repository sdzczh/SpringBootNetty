package com.zh.netty;

import com.zh.netty.handler.WebSocketinitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NettyApplication {

    public static void main(String[] args) throws InterruptedException  {
        SpringApplication.run(NettyApplication.class, args);

        EventLoopGroup mainGroup =  new NioEventLoopGroup();
        EventLoopGroup subGroup =  new NioEventLoopGroup();
        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(mainGroup, subGroup)
                    //channel类型
                    .channel(NioServerSocketChannel.class)
                    //针对subGroup做的子处理器，childHandler针对WebSokect的初始化器
                    .childHandler(new WebSocketinitializer());

            //绑定端口并以同步方式进行使用
            ChannelFuture channelFuture = server.bind(10086).sync();

            //针对channelFuture，进行相应的监听
            channelFuture.channel().closeFuture().sync();

        }finally {
            //针对两个group进行优雅地关闭
            mainGroup.shutdownGracefully();
            subGroup.shutdownGracefully();
        }
    }

}
