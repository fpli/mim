package com.sap.mim.bean;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class LoginMessage extends MessageModel{

    private static final long serialVersionUID = 3178351358356989351L;

    private C2SMessageType c2SMessageType;

    private Account account;

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeInt(c2SMessageType.getC2sMessageType());
        out.writeObject(account);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        c2SMessageType = C2SMessageType.getC2SMessageTypeById(in.readInt());
        account = (Account) in.readObject();
    }

    public LoginMessage() {
    }

    public C2SMessageType getC2SMessageType() {
        return c2SMessageType;
    }

    public void setC2SMessageType(C2SMessageType c2SMessageType) {
        this.c2SMessageType = c2SMessageType;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
