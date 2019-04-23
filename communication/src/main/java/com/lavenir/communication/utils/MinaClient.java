package com.lavenir.communication.utils;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
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
