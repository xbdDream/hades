package com.lavenir.communication.utils;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.text.DateFormat;
import java.util.Date;

public class MinaClientHandler extends IoHandlerAdapter {
    public static IoSession iosession = null;

    public void sessionOpened(IoSession session) throws Exception {
        System.out.println("客户端登陆");
        iosession = session;
    }

    public void sessionClosed(IoSession session)
    {
        System.out.println("client close");
        iosession = null;
    }

    public void messageReceived(IoSession session , Object message)throws Exception
    {
        System.out.println("客户端接受到了消息"+message) ;
    }

    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        //出现异常
        cause.printStackTrace();
        session.closeNow();
    }

    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
        //心跳
        System.out.println("客户端ide:" + DateFormat.getDateInstance().format(new Date()));
    }

    //    获取session连接，用来随时向客户端发送消息
    public static void sessionWrite(String message){
        if(iosession != null){
            iosession.write(message);
        }else{
            System.out.println("客户端未连接服务器");
        }
    }
}
