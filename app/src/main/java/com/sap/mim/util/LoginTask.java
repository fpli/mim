package com.sap.mim.util;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import com.sap.mim.bean.Account;
import com.sap.mim.bean.C2SMessageType;
import com.sap.mim.bean.LoginMessage;
import com.sap.mim.bean.MessageType;
import com.sap.mim.net.Engine;
import com.sap.mim.net.NetService;
import com.sap.mim.ui.activity.MainActivity;

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
            loginMessage.setC2SMessageType(C2SMessageType.C_2_S_LOGIN);
            loginMessage.setMsgId(MessageIdGenerator.getMsgId());
            loginMessage.setAccount(account);
            NetService.getNetService().sendMessageModel(loginMessage);
            Engine.setLoginMessage(loginMessage);
            try {
                synchronized (loginMessage){
                    loginMessage.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return -2;
            }
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
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
            ((AppCompatActivity)context).finish();
        }
    }


}
