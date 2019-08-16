package com.sap.mim.util;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import com.sap.mim.bean.Account;
import com.sap.mim.bean.LoginMessage;
import com.sap.mim.bean.MessageType;
import com.sap.mim.net.NetService;
import com.sap.mim.ui.activity.ChatActivity;

public class LoginTask extends AsyncTask<Account, Integer, Integer> {

    private Context context;

    public LoginTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // 显示登陆滚动条
    }

    @Override
    protected Integer doInBackground(Account... accounts) {
        if (accounts.length>0){
            Account account = accounts[0];
            LoginMessage loginMessage = new LoginMessage();
            loginMessage.setMessageType(MessageType.C2S);
            loginMessage.setMsgId(MessageIdGenerator.getMsgId());
            loginMessage.setAccountNo(account.getAccount());
            loginMessage.setPassword(account.getPassword());
            NetService.getNetService().sendMessageModel(loginMessage);
            return 0;
        }
        return -1;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        // 取消登陆滚动条
        if (integer == -1){
            //  没有账号信息
        }
        if (integer == 0){
            Intent intent = new Intent(context, ChatActivity.class);
            context.startActivity(intent);
            ((AppCompatActivity)context).finish();
        }
    }


}
