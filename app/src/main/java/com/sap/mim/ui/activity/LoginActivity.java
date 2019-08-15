package com.sap.mim.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.sap.mim.R;
import com.sap.mim.bean.Account;
import com.sap.mim.util.LoginTask;

public class LoginActivity extends AppCompatActivity {

    private Context context;

    @Bind(R.id.rl_user)
    RelativeLayout rl_user;
    @Bind(R.id.login)
    Button loginButton;
    @Bind(R.id.register)
    Button registerButton;
    @Bind(R.id.account)
    EditText accountView;
    @Bind(R.id.password)
    EditText passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        this.context = this;
        initWidget();
    }

    private void initWidget() {
        loginButton.setOnClickListener(loginOnClickListener);
    }

    private View.OnClickListener loginOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String accountNo = accountView.getText().toString().trim();
            String password = passwordView.getText().toString().trim();
            Account account = new Account();
            account.setAccount(accountNo);
            account.setPassword(password);
            new LoginTask(context).execute(account);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
