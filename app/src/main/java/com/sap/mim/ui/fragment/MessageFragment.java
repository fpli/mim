package com.sap.mim.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.sap.mim.R;
import com.sap.mim.adapter.FriendMessageAdapter;
import com.sap.mim.base.BaseFragment;
import com.sap.mim.bean.MessageTabEntity;
import com.sap.mim.database.ImDB;
import com.sap.mim.ui.activity.ChatActivity;
import com.sap.mim.widget.SlideCutListView;
import com.sap.mim.widget.SlideCutListView.RemoveListener;
import com.sap.mim.widget.TitleBarView;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends BaseFragment implements RemoveListener {

    private Context                mContext;
    private View                   mBaseView;
    private TitleBarView           mTitleBarView;
    private List<MessageTabEntity> mMessageEntityList;
    private SlideCutListView       mMessageListView;
    private FriendMessageAdapter   adapter;

    private Handler                handler;
    private int                    mPosition;
    private MessageTabEntity       chooseMessageEntity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        mBaseView = inflater.inflate(R.layout.fragment_message, null);
        findView();
        init();
        return mBaseView;
    }

    private void findView() {
        mTitleBarView = mBaseView.findViewById(R.id.title_bar);
        mMessageListView = mBaseView.findViewById(R.id.message_list_listview);
    }

    private void init() {
        mMessageListView.setRemoveListener(this);
        //initDialog();
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        mMessageListView.setSelection(mMessageEntityList.size());
                        break;
                    default:
                        break;
                }
            }
        };
        //ApplicationData.getInstance().setMessageHandler(handler);
        mMessageEntityList = new ArrayList<>();
        mMessageListView.setSelection(mMessageEntityList.size());
        mTitleBarView.setCommonTitle(View.GONE, View.VISIBLE, View.GONE);
        mTitleBarView.setTitleText("消息");
        adapter = new FriendMessageAdapter(mContext, mMessageEntityList);
        mMessageListView.setAdapter(adapter);
        mMessageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chooseMessageEntity = mMessageEntityList.get(position);
                chooseMessageEntity.setUnReadCount(0);
                adapter.notifyDataSetChanged();
                ImDB.getInstance(mContext).updateMessages(chooseMessageEntity);
                mPosition = position;
                if (chooseMessageEntity.getMessageType() == MessageTabEntity.MAKE_FRIEND_REQUEST) {
                    //mDialog.show();
                } else if (chooseMessageEntity.getMessageType() == MessageTabEntity.MAKE_FRIEND_RESPONSE_ACCEPT) {

                } else {
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra("friendName", chooseMessageEntity.getName());
                    intent.putExtra("friendId",   chooseMessageEntity.getSenderId());
                    startActivity(intent);
                }
            }
        });
    }

    // 滑动删除之后的回调方法
    @Override
    public void removeItem(SlideCutListView.RemoveDirection direction, int position) {
        MessageTabEntity temp = mMessageEntityList.get(position);
        mMessageEntityList.remove(position);
        adapter.notifyDataSetChanged();
        switch (direction) {
            default:
                ImDB.getInstance(mContext).deleteMessage(temp);
                break;
        }
    }
}
