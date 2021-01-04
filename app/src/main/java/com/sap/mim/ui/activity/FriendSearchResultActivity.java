package com.sap.mim.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.sap.mim.R;
import com.sap.mim.adapter.FriendSearchResultAdapter;
import com.sap.mim.bean.Account;
import com.sap.mim.widget.TitleBarView;

import java.util.ArrayList;
import java.util.List;

public class FriendSearchResultActivity extends AppCompatActivity {

    private ListView      mListviewOfResults;
    private TitleBarView  mTitleBarView;
    private List<Account> mFriendList;
    private Account       requestee;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friend_search_result);
        initView();
        initEvent();
    }

    private void initView() {
        mListviewOfResults = findViewById(R.id.friend_search_result_listview);
        mTitleBarView =  findViewById(R.id.title_bar);
        mTitleBarView.setCommonTitle(View.GONE, View.VISIBLE, View.GONE);
        mTitleBarView.setTitleText("查找好友结果");
    }

    private void initEvent() {
        //mFriendList = ApplicationData.getInstance().getFriendSearched();
        mFriendList = new ArrayList<>();
        System.out.println(mFriendList.size() + "friendSearch result");

        mListviewOfResults.setAdapter(new FriendSearchResultAdapter(FriendSearchResultActivity.this, mFriendList));

        mListviewOfResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Account mySelf = ApplicationData.getInstance().getUserInfo();
                Account mySelf = null;
                requestee = mFriendList.get(position);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FriendSearchResultActivity.this);

                alertDialogBuilder.setTitle(null);
                if (mySelf.getId() == requestee.getId()) {
                    alertDialogBuilder
                            .setMessage("你不能添加自己为好友")
                            .setCancelable(true)
                            .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                } else if (!hasFriend(mFriendList, requestee)){
                    alertDialogBuilder
                            .setMessage("确定发送请求？")
                            .setCancelable(true)
                            .setPositiveButton("是",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //UserAction.sendFriendRequest(Result.MAKE_FRIEND_REQUEST, requestee.getId());
                                }
                            })
                            .setNegativeButton("否",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                } else {
                    alertDialogBuilder
                            .setMessage("你们已经是好友")
                            .setCancelable(true)
                            .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                }

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mInstance = null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private boolean hasFriend(List<Account> friendList, Account person) {
        for(int i = 0;i < friendList.size();i++) {
            if(friendList.get(i).getId() == person.getId())
                return true;
        }
        return false;
    }
}
