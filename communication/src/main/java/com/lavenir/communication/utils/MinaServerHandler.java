package com.lavenir.communication.utils;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class MinaServerHandler extends IoHandlerAdapter {

    private int count = 0;

    // session 创建调用
    public void sessionCreated(IoSession session) {
        System.out.println("新客户连接");
    }

    // session 关闭调用
    public void sessionClosed(IoSession session) {
        System.out.println("one client disconnect");
    }

    // session 创建后会回调 sessionOpened
    public void sessionOpened(IoSession session) throws Exception {
        count++;
        System.out.println("第 " + count + " 个 client 登陆！address： : "
                + session.getRemoteAddress());


    }

    //    获取session连接，用来随时向客户端发送消息
    public void sessionWrite(IoSession session) throws Exception {
        session.write("Sent by Server1"+1);
        session.write("Sent by Server1"+2);
    }

    // 当收到了客户端发送的消息后会回调这个函数
    public void messageReceived(IoSession session, Object message)
            throws Exception {
        System.out.println("服务器收到客户端发送指令 ：" );
        System.out.println(message);

        //模拟向客户端发送信息
        sessionWrite(session);
    }

    // session 空闲的时候调用 - 心跳
    public void sessionIdle(IoSession session, IdleStatus status) {
        System.out.println("connect idle");
    }

    // 异常捕捉
    public void exceptionCaught(IoSession session, Throwable cause) {
        System.out.println("throws exception");
        //异常时回调
        cause.printStackTrace();
        //关闭session
        session.closeNow();
    }
}
