package com.sap.mim.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.sap.mim.R;
import com.sap.mim.adapter.FriendListAdapter;
import com.sap.mim.base.BaseFragment;
import com.sap.mim.base.MimApplication;
import com.sap.mim.bean.Account;
import com.sap.mim.ui.activity.ChatActivity;
import com.sap.mim.ui.activity.SearchFriendActivity;
import com.sap.mim.widget.TitleBarView;

import java.util.List;

public class FriendListFragment extends BaseFragment {

    private Context           mContext;
    private View              mBaseView;
    private TitleBarView      mTitleBarView;
    private ListView          mFriendListView;
    private List<Account>     mFriendList;
    private Handler           handler;
    private FriendListAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext  = getActivity();
        mBaseView = inflater.inflate(R.layout.fragment_friendlist, container, false);
        findView();
        init();
        return mBaseView;
    }

    private void findView() {
        mTitleBarView   = mBaseView.findViewById(R.id.title_bar);
        mFriendListView = mBaseView.findViewById(R.id.friend_list_listview);
    }

    private void init() {
        mFriendList = MimApplication.getInstance().getmAccount().getFriendList();
        adapter     = new FriendListAdapter(mContext, mFriendList);
        mFriendListView.setAdapter(adapter);
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        mFriendListView.setSelection(mFriendList.size());
                        break;
                    default:
                        break;
                }
            }
        };
        MimApplication.getInstance().setFriendListHandler(handler);
        mTitleBarView.setCommonTitle(View.VISIBLE, View.VISIBLE, View.VISIBLE);
        mTitleBarView.setTitleText("好友");
        mTitleBarView.setBtnLeft(R.string.control);
        mTitleBarView.setBtnRight(R.drawable.qq_constact);

        mFriendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Account friend = mFriendList.get(position);
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("friendId",   friend.getId());
                intent.putExtra("friendName", friend.getUserName());
                startActivity(intent);
            }
        });

        mTitleBarView.setBtnRightOnclickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SearchFriendActivity.class);
                startActivity(intent);
            }
        });

    }
}
