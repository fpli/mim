package com.sap.mim.ui.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.sap.mim.R;
import com.sap.mim.adapter.ChatAdapter;
import com.sap.mim.adapter.CommonFragmentPagerAdapter;
import com.sap.mim.base.MimApplication;
import com.sap.mim.entity.FullImageInfo;
import com.sap.mim.entity.MessageInfo;
import com.sap.mim.net.Engine;
import com.sap.mim.ui.fragment.ChatEmotionFragment;
import com.sap.mim.ui.fragment.ChatFunctionFragment;
import com.sap.mim.util.Constants;
import com.sap.mim.util.GlobalOnItemClickManagerUtils;
import com.sap.mim.util.MediaManager;
import com.sap.mim.widget.EmotionInputDetector;
import com.sap.mim.widget.NoScrollViewPager;
import com.sap.mim.widget.StateButton;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    Toolbar                 bar;
    ImageView               iv_back;
    TextView                tv;
    EasyRecyclerView        easyRecyclerView;
    ImageView               emotionVoice;
    EditText                editText;
    TextView                voiceText;
    ImageView               emotionButton;
    ImageView               emotionAdd;
    StateButton             emotionSend;
    NoScrollViewPager       viewpager;
    RelativeLayout          emotionLayout;

    private EmotionInputDetector       mDetector;
    private ArrayList<Fragment>        fragments;
    private ChatEmotionFragment        chatEmotionFragment;
    private ChatFunctionFragment       chatFunctionFragment;
    private CommonFragmentPagerAdapter adapter;

    private ChatAdapter                chatAdapter;

    private LinearLayoutManager        layoutManager;
    //录音相关
    int animationRes = 0;
    int res = 0;
    AnimationDrawable animationDrawable = null;
    private ImageView animView;

    private Handler                   chatMessageHandler;

    private int                       friendId;
    private String                    friendName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        friendName    = intent.getStringExtra("friendName");
        friendId      = intent.getIntExtra("friendId", 0);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initWidget();
        loadData();
    }

    private void initWidget() {
        bar = findViewById(R.id.activity_wechat_chat_toolbar);
        iv_back = findViewById(R.id.activity_wechat_chat_back);
        tv = findViewById(R.id.activity_wechat_chat_tv_name);
        easyRecyclerView = findViewById(R.id.chat_list);
        emotionVoice = findViewById(R.id.emotion_voice);
        editText = findViewById(R.id.edit_text);
        voiceText = findViewById(R.id.voice_text);
        emotionButton = findViewById(R.id.emotion_button);
        emotionAdd = findViewById(R.id.emotion_add);
        emotionSend = findViewById(R.id.emotion_send);
        viewpager = findViewById(R.id.viewpager);
        emotionLayout = findViewById(R.id.emotion_layout);
        setSupportActionBar(bar);
        bar.setTitle("");
        tv.setText("与" + friendName + "对话");
        iv_back.setOnClickListener((v) ->  finish());
        fragments = new ArrayList<>();
        chatEmotionFragment = new ChatEmotionFragment();
        fragments.add(chatEmotionFragment);
        chatFunctionFragment = new ChatFunctionFragment();
        fragments.add(chatFunctionFragment);
        adapter = new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(0);

        mDetector = EmotionInputDetector.with(this)
                .setEmotionView(emotionLayout)
                .setViewPager(viewpager)
                .setFriendId(friendId)
                .bindToContent(easyRecyclerView)
                .bindToEditText(editText)
                .bindToEmotionButton(emotionButton)
                .bindToAddButton(emotionAdd)
                .bindToSendButton(emotionSend)
                .bindToVoiceButton(emotionVoice)
                .bindToVoiceText(voiceText)
                .build();

        GlobalOnItemClickManagerUtils globalOnItemClickListener = GlobalOnItemClickManagerUtils.getInstance(this);
        globalOnItemClickListener.attachToEditText(editText);
        chatMessageHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        chatAdapter.addAll(Engine.getmChatMessagesMap().get(friendId));
                        easyRecyclerView.scrollToPosition(chatAdapter.getCount() - 1);
                        chatAdapter.notifyDataSetChanged();
                        Engine.getmChatMessagesMap().get(friendId).clear();
                        break;
                    default:
                        break;
                }
            }
        };
        MimApplication.getInstance().setChatMessageHandler(chatMessageHandler);
        chatAdapter = new ChatAdapter(this);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        easyRecyclerView.setLayoutManager(layoutManager);
        easyRecyclerView.setAdapter(chatAdapter);
        easyRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        chatAdapter.handler.removeCallbacksAndMessages(null);
                        chatAdapter.notifyDataSetChanged();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        chatAdapter.handler.removeCallbacksAndMessages(null);
                        mDetector.hideEmotionLayout(false);
                        mDetector.hideSoftInput();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        chatAdapter.addItemClickListener(itemClickListener);
    }

    /**
     * item点击事件
     */
    private ChatAdapter.onItemClickListener itemClickListener = new ChatAdapter.onItemClickListener() {

        @Override
        public void onHeaderClick(int position) {
            Toast.makeText(ChatActivity.this, "onHeaderClick", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onImageClick(View view, int position) {
            int location[] = new int[2];
            view.getLocationOnScreen(location);
            FullImageInfo fullImageInfo = new FullImageInfo();
            fullImageInfo.setLocationX(location[0]);
            fullImageInfo.setLocationY(location[1]);
            fullImageInfo.setWidth(view.getWidth());
            fullImageInfo.setHeight(view.getHeight());
            fullImageInfo.setImageUrl(chatAdapter.getAllData().get(position).getImageUrl());
            EventBus.getDefault().postSticky(fullImageInfo);
            startActivity(new Intent(ChatActivity.this, FullImageActivity.class));
            overridePendingTransition(0, 0);
        }

        @Override
        public void onVoiceClick(final ImageView imageView, final int position) {
            if (animView != null) {
                animView.setImageResource(res);
                animView = null;
            }
            switch (chatAdapter.getAllData().get(position).getType()) {
                case 1:
                    animationRes = R.drawable.voice_left;
                    res = R.mipmap.icon_voice_left3;
                    break;
                case 2:
                    animationRes = R.drawable.voice_right;
                    res = R.mipmap.icon_voice_right3;
                    break;
            }
            animView = imageView;
            animView.setImageResource(animationRes);
            animationDrawable = (AnimationDrawable) imageView.getDrawable();
            animationDrawable.start();
            MediaManager.playSound(chatAdapter.getAllData().get(position).getFilepath(), (mp) -> animView.setImageResource(res) );
        }
    };

    /**
     * 构造初始聊天数据
     */
    private void loadData() {
        List<MessageInfo> messageInfoList = Engine.getmChatMessagesMap().get(friendId);
        if (messageInfoList != null){
            chatAdapter.addAll(messageInfoList);
            easyRecyclerView.scrollToPosition(chatAdapter.getCount() - 1);
            chatAdapter.notifyDataSetChanged();
            messageInfoList.clear();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(final MessageInfo messageInfo) {
        chatAdapter.add(messageInfo);
        easyRecyclerView.scrollToPosition(chatAdapter.getCount() - 1);
        messageInfo.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
        chatAdapter.notifyDataSetChanged();
    }

    /**
     * 用户点击后退键,在后台运行
     */
    @Override
    public void onBackPressed() {
        if (!mDetector.interceptBackPress()) {
            super.onBackPressed();
        }
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().removeStickyEvent(this);
        EventBus.getDefault().unregister(this);
    }
}
