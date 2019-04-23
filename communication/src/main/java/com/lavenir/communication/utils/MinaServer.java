package com.lavenir.communication.utils;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MinaServer {
    private static int bindPort = 10026;
    private static NioSocketAcceptor acceptor;

    public static void start(int port){
        System.out.println("The mina server startup...");
        //创建ServerScoket
        acceptor = new NioSocketAcceptor();

        //设置传输方式（这里设置成对象传输模式，还有很多的类型后面会具体讲到
        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();

        //设置日志过滤器
        chain.addLast("logger", new LoggingFilter());
        //设置编码器-对象编码解码器
        ProtocolCodecFilter filter = new ProtocolCodecFilter(new ObjectSerializationCodecFactory());
        chain.addLast("codec", filter);
        //设置读缓冲
        acceptor.getSessionConfig().setReadBufferSize(2048*2048);
        //设置心跳频率
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 30);

        // 添加消息处理
        acceptor.setHandler(new MinaServerHandler());

        try {
            // 开启服务器
            acceptor.bind(new InetSocketAddress(port));
        } catch (IOException e) {
            e.printStackTrace();

        }
        System.out.println("The mina server startup is complete. port:" + port);
    }

    public static void destory(){
        acceptor.unbind();
        acceptor.dispose();
    }

    public static void main(String []args){
        start(bindPort);
    }
}
