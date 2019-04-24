package com.lavenir.communication.utils;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
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

        //设置心跳机制
        MinaServerKeepAliveMessageFactory keepAlive = new MinaServerKeepAliveMessageFactory();
        KeepAliveFilter kaf = new KeepAliveFilter(keepAlive, IdleStatus.BOTH_IDLE);
        kaf.setForwardEvent(true); //idle事件回发  当session进入idle状态的时候 依然调用handler中的idled方法（使用了 KeepAliveFilter之后，IoHandlerAdapter中的 sessionIdle方法默认是不会再被调用的）
        kaf.setRequestInterval(15);  //本服务器为被定型心跳  即需要每10秒接受一个心跳请求  否则该连接进入空闲状态 并且发出idled方法回调
        kaf.setRequestTimeout(5); //超时时间   如果当前发出一个心跳请求后需要反馈  若反馈超过此事件 默认则关闭连接
        chain.addLast("heart", kaf);

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
