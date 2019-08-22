package com.sap.mim.bean;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class SearchFriendResultMessage extends MessageModel{

    private static final long serialVersionUID = -4838287755627485505L;
    private S2CMessageType s2CMessageType;
    private List<Account>  accounts;

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeInt(s2CMessageType.getS2cMessageType());
        out.writeObject(accounts);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        s2CMessageType = S2CMessageType.getS2CMessageTypeByType(in.readInt());
        accounts = (List<Account>)in.readObject();
    }

    public SearchFriendResultMessage() {
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public S2CMessageType getS2CMessageType() {
        return s2CMessageType;
    }

    public void setS2CMessageType(S2CMessageType s2CMessageType) {
        this.s2CMessageType = s2CMessageType;
    }
}
