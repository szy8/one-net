package com.weyong.onenet.dto;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;
import lombok.Data;
import org.apache.commons.codec.Charsets;

import java.io.Serializable;
import java.nio.*;

/**
 * Created by hao.li on 2017/4/12.
 */
@Data
public abstract class BasePackage implements Serializable{
    ThreadLocal<ByteBuffer> byteBufferThreadLocal = new ThreadLocal<>();
    public static final byte OP_TYPE_NEW = 1;
    public static final byte OP_TYPE_HEART_BEAT = 2;
    public static final byte OP_TYPE_CLOSE = 3;
    public static final byte OP_TYPE_ERROR = 4;
    public static final byte OP_TYPE_DATA = 5;
    private Byte opType;
    private String contextName;
    private Long sessionId;

    public BasePackage(Byte opType){
        this.opType = opType;
    }

    public byte[] toBytes() {
        ByteBuffer byteBuffer = this.getByteBufferThreadLocal().get();
        byteBuffer.clear();
        byteBuffer.put(this.getOpType());
        fillBody(byteBuffer);
        byte[] byteEncoding =  new byte[byteBuffer.position()];
        byteBuffer.flip();
        byteBuffer.get(byteEncoding,0,byteEncoding.length);
        return byteEncoding;
    }

    protected abstract void fillBody(ByteBuffer byteBuffer);


    public static BasePackage fromBytes(ByteBuf byteBuf){
       byte typeByte = byteBuf.readByte();
       switch(typeByte){
           case OP_TYPE_NEW:
               return new OneNetInitialPackage(byteBuf);
           case OP_TYPE_HEART_BEAT:
               return HeartbeatPackage.instance();
           case OP_TYPE_CLOSE:
               return new SessionInvalidPackage(byteBuf);
           case OP_TYPE_ERROR:
               return new ServerMessagePackage(byteBuf);
           case OP_TYPE_DATA:
               return new DataPackage(byteBuf);
       }
       return null;
    }

    protected static void stringEncoding(String str,ByteBuffer byteBuf){
        //All string need to encoding must be less than 128
        if(StringUtil.isNullOrEmpty(str)){
            byteBuf.put((byte)0);
        }else {
            byte[] bytes = str.getBytes(Charsets.toCharset("UTF-8"));
            byteBuf.put((byte)bytes.length);
            byteBuf.put(bytes);
        }
    }

    protected static String stringDecoding(ByteBuf byteBuf){
        byte byteLength = byteBuf.readByte();
        byte[] bytes = new byte[byteLength];
        byteBuf.readBytes(bytes,0,byteLength);
        return new String(bytes,Charsets.toCharset("UTF-8"));
    }
}
