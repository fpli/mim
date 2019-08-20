package com.sap.mim.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.sap.mim.R;
import com.sap.mim.bean.MessageType;
import com.sap.mim.bean.SearchFriendMessage;
import com.sap.mim.util.MessageIdGenerator;
import com.sap.mim.widget.TitleBarView;

public class SearchFriendActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.title_bar)
    TitleBarView mTitleBarView;
    @Bind((R.id.search_friend_by_name_edit_name))
    EditText     mSearchEtName;
    @Bind(R.id.search_friend_by_name_btn_search)
    Button       mBtnSearchByName;

    private static boolean mIsReceived;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        ButterKnife.bind(this);
        initWidget();
        mTitleBarView.setCommonTitle(View.GONE, View.VISIBLE, View.GONE);
        mTitleBarView.setTitleText("查找朋友");
    }

    private void initWidget() {
        mIsReceived = false;
        mBtnSearchByName.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_friend_by_name_btn_search:
                flag = false;
                final String searchName = mSearchEtName.getText().toString();

                flag = true;
                int accountNo = Integer.parseInt(searchName);
                SearchFriendMessage searchFriendMessage = new SearchFriendMessage();
                searchFriendMessage.setMsgId(MessageIdGenerator.getMsgId());
                searchFriendMessage.setMessageType(MessageType.C2S);

                searchFriendMessage.setAccountNo(accountNo);
                break;
        }

        if (flag) {
            mIsReceived = false;
            //showLoadingDialog("正在查找...");
            while (!mIsReceived) {

            }
            System.out.println("准备跳转查找结果页面");
            Intent intent = new Intent(this, FriendSearchResultActivity.class);
            startActivity(intent);
            finish();
            System.out.println("已跳转");
        }
    }
}
