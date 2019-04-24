package com.lavenir.communication.utils;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MinaClientKeepAliveMessageFactory implements KeepAliveMessageFactory {
    private static final byte int_req = '+'; // 2B
    private static final byte int_rep = '-'; // 2D
    private static final IoBuffer KAMSG_REQ = IoBuffer.wrap(new byte[]{0x0D, '+', 0x0A});
    private static final IoBuffer KAMSG_REP = IoBuffer.wrap(new byte[]{0x0D, '-', 0x0A});

    @Override
    public boolean isRequest(IoSession session, Object message) {
        return false;
    }

    @Override
    public boolean isResponse(IoSession session, Object message) {
        if(message.equals(int_rep)){
            return true;
        }
        return false;
    }

    @Override
    public Object getRequest(IoSession session) {
        System.out.println(new SimpleDateFormat("yyyyMMdd-HH:mm:ss-SSS").format(new Date()) + " 客户端发心跳包+：" + KAMSG_REQ.duplicate());
//        return KAMSG_REQ.duplicate();
        return int_req;
    }

    @Override
    public Object getResponse(IoSession session, Object request) {
        return null;
    }
}
