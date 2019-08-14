package com.sap.mim.util;

import java.util.concurrent.atomic.AtomicLong;

public class MessageIdGenerator {

    private transient static final AtomicLong msgIdGenerotor = new AtomicLong();

    public static final long getMsgId(){
        return msgIdGenerotor.incrementAndGet();
    }
}
