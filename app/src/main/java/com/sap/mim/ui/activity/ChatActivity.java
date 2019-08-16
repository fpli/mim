package com.sap.mim.ui.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.*;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.sap.mim.R;
import com.sap.mim.adapter.ChatAdapter;
import com.sap.mim.adapter.CommonFragmentPagerAdapter;
import com.sap.mim.entity.FullImageInfo;
import com.sap.mim.entity.MessageInfo;
import com.sap.mim.ui.fragment.ChatEmotionFragment;
import com.sap.mim.ui.fragment.ChatFunctionFragment;
import com.sap.mim.util.Constants;
import com.sap.mim.util.GlobalOnItemClickManagerUtils;
import com.sap.mim.util.MediaManager;
import com.sap.mim.util.MessageIdGenerator;
import com.sap.mim.widget.EmotionInputDetector;
import com.sap.mim.widget.NoScrollViewPager;
import com.sap.mim.widget.StateButton;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends AppCompatActivity {

    @Bind(R.id.chat_list)
    EasyRecyclerView        chatList;
    @Bind(R.id.emotion_voice)
    ImageView               emotionVoice;
    @Bind(R.id.edit_text)
    EditText                editText;
    @Bind(R.id.voice_text)
    TextView                voiceText;
    @Bind(R.id.emotion_button)
    ImageView               emotionButton;
    @Bind(R.id.emotion_add)
    ImageView               emotionAdd;
    @Bind(R.id.emotion_send)
    StateButton             emotionSend;
    @Bind(R.id.viewpager)
    NoScrollViewPager       viewpager;
    @Bind(R.id.emotion_layout)
    RelativeLayout          emotionLayout;

    private EmotionInputDetector       mDetector;
    private ArrayList<Fragment>        fragments;
    private ChatEmotionFragment        chatEmotionFragment;
    private ChatFunctionFragment       chatFunctionFragment;
    private CommonFragmentPagerAdapter adapter;

    private ChatAdapter                chatAdapter;
    private List<MessageInfo>          messageInfos;

    private LinearLayoutManager        layoutManager;
    //录音相关
    int animationRes = 0;
    int res = 0;
    AnimationDrawable animationDrawable = null;
    private ImageView animView;

    private Handler mHanler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initWidget();
        loadData();
    }

    private void initWidget() {
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
                .bindToContent(chatList)
                .bindToEditText(editText)
                .bindToEmotionButton(emotionButton)
                .bindToAddButton(emotionAdd)
                .bindToSendButton(emotionSend)
                .bindToVoiceButton(emotionVoice)
                .bindToVoiceText(voiceText)
                .build();

        GlobalOnItemClickManagerUtils globalOnItemClickListener = GlobalOnItemClickManagerUtils.getInstance(this);
        globalOnItemClickListener.attachToEditText(editText);

        chatAdapter = new ChatAdapter(this);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chatList.setLayoutManager(layoutManager);
        chatList.setAdapter(chatAdapter);
        chatList.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
            fullImageInfo.setImageUrl(messageInfos.get(position).getImageUrl());
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
            switch (messageInfos.get(position).getType()) {
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
            MediaManager.playSound(messageInfos.get(position).getFilepath(), (mp) -> animView.setImageResource(res) );
        }
    };

    /**
     * 构造初始聊天数据
     */
    private void loadData() {
        messageInfos = new ArrayList<>();

        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setMsgId(MessageIdGenerator.getMsgId());

        messageInfo.setContent("你好，欢迎使用Rance的聊天界面框架");
        messageInfo.setType(Constants.CHAT_ITEM_TYPE_LEFT);
        messageInfo.setHeader("http://tupian.enterdesk.com/2014/mxy/11/2/1/12.jpg");
        messageInfos.add(messageInfo);

        messageInfo = new MessageInfo();
        messageInfo.setMsgId(MessageIdGenerator.getMsgId());
        messageInfo.setFilepath("http://www.trueme.net/bb_midi/welcome.wav");
        messageInfo.setVoiceTime(3000);
        messageInfo.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
        messageInfo.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
        messageInfo.setHeader("http://img.dongqiudi.com/uploads/avatar/2014/10/20/8MCTb0WBFG_thumb_1413805282863.jpg");
        messageInfos.add(messageInfo);

        messageInfo = new MessageInfo();
        messageInfo.setMsgId(MessageIdGenerator.getMsgId());
        messageInfo.setImageUrl("http://img4.imgtn.bdimg.com/it/u=1800788429,176707229&fm=21&gp=0.jpg");
        messageInfo.setType(Constants.CHAT_ITEM_TYPE_LEFT);
        messageInfo.setHeader("http://tupian.enterdesk.com/2014/mxy/11/2/1/12.jpg");
        messageInfos.add(messageInfo);

        messageInfo = new MessageInfo();
        messageInfo.setMsgId(MessageIdGenerator.getMsgId());
        messageInfo.setContent("[微笑][色][色][色]");
        messageInfo.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
        messageInfo.setSendState(Constants.CHAT_ITEM_SEND_ERROR);
        messageInfo.setHeader("http://img.dongqiudi.com/uploads/avatar/2014/10/20/8MCTb0WBFG_thumb_1413805282863.jpg");
        messageInfos.add(messageInfo);

        chatAdapter.addAll(messageInfos);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(final MessageInfo messageInfo) {
        messageInfos.add(messageInfo);
        chatAdapter.add(messageInfo);
        chatList.scrollToPosition(chatAdapter.getCount() - 1);

        if (messageInfo.getType() == Constants.CHAT_ITEM_TYPE_RIGHT){
            messageInfo.setHeader("http://img.dongqiudi.com/uploads/avatar/2014/10/20/8MCTb0WBFG_thumb_1413805282863.jpg");
        } else {
            messageInfo.setImageUrl("http://img4.imgtn.bdimg.com/it/u=1800788429,176707229&fm=21&gp=0.jpg");
        }

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
        ButterKnife.unbind(this);
        EventBus.getDefault().removeStickyEvent(this);
        EventBus.getDefault().unregister(this);
    }
}
