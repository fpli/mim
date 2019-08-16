package com.sap.mim.bean;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ACKMessage extends MessageModel {

    private static final long serialVersionUID = 8171182601241394597L;

    public ACKMessage() {
        setMessageType(MessageType.ACK);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
    }

}
