package com.lavenir.communication.utils;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

public class MinaClient {

    public static void start(String hostname, int port){
        // 创建Socket
        NioSocketConnector connector = new NioSocketConnector();
        // 设置传输方式
        DefaultIoFilterChainBuilder chain = connector.getFilterChain();

        //设置编码器
        ProtocolCodecFilter filter = new ProtocolCodecFilter(new ObjectSerializationCodecFactory());
        chain.addLast("codec", filter);
        //设置日志过滤器
        chain.addLast("logger", new LoggingFilter());

        //设置心跳机制
        MinaClientKeepAliveMessageFactory keepAlive = new MinaClientKeepAliveMessageFactory();
        KeepAliveFilter kaf = new KeepAliveFilter(keepAlive, IdleStatus.READER_IDLE, KeepAliveRequestTimeoutHandler.CLOSE);
        kaf.setForwardEvent(true); //继续调用 IoHandlerAdapter 中的 sessionIdle事件
        kaf.setRequestInterval(10); //设置当连接的读取通道空闲的时候，心跳包请求时间间隔
        kaf.setRequestTimeout(5); //设置心跳包请求后 等待反馈超时时间。 超过该时间后则调用KeepAliveRequestTimeoutHandler.CLOSE
        chain.addLast("heart", kaf);


        //设置消息处理
        connector.setHandler(new MinaClientHandler());

        //超时设置
        connector.setConnectTimeoutCheckInterval(30);

        //连接,异步执行
        ConnectFuture cf = connector.connect(new InetSocketAddress(hostname, port));
        //等待连接建立
        cf.awaitUninterruptibly();
        //等待session关闭
        cf.getSession().getCloseFuture().awaitUninterruptibly();

        //释放连接
        connector.dispose();
    }

    public static void main(String []args){
        String hostname = "127.0.0.1";
        int port = 10026;
        start(hostname, port);
    }
}
