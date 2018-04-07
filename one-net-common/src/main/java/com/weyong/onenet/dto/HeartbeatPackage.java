package com.weyong.onenet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.nio.ByteBuffer;

/**
 * Created by haoli on 2018/4/7.
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class HeartbeatPackage extends BasePackage {
    public HeartbeatPackage() {
        super(DataPackage.OP_TYPE_HEART_BEAT);
    }
    private static HeartbeatPackage heartbeatPackage = new HeartbeatPackage();

    public static BasePackage instance() {
        return heartbeatPackage;
    }

    @Override
    protected void fillBody(ByteBuffer byteBuffer) {

    }
}
