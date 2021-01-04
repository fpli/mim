package com.sap.mim.adapter.holder;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.sap.mim.R;
import com.sap.mim.adapter.ChatAdapter;
import com.sap.mim.entity.MessageInfo;
import com.sap.mim.util.Utils;
import com.sap.mim.widget.BubbleImageView;
import com.sap.mim.widget.GifTextView;

public class ChatAcceptViewHolder extends BaseViewHolder<MessageInfo> {


    TextView chatItemDate;

    ImageView chatItemHeader;

    GifTextView chatItemContentText;

    BubbleImageView chatItemContentImage;

    ImageView chatItemVoice;

    LinearLayout chatItemLayoutContent;

    TextView chatItemVoiceTime;

    private ChatAdapter.onItemClickListener onItemClickListener;
    private Handler handler;

    public ChatAcceptViewHolder(ViewGroup parent, ChatAdapter.onItemClickListener onItemClickListener, Handler handler) {
        super(parent, R.layout.item_chat_accept);
        initWidget();
        this.onItemClickListener = onItemClickListener;
        this.handler = handler;
    }

    private void initWidget(){
        chatItemDate = itemView.findViewById(R.id.chat_item_date);
        chatItemHeader = itemView.findViewById(R.id.chat_item_header);
        chatItemContentText = itemView.findViewById(R.id.chat_item_content_text);
        chatItemContentImage = itemView.findViewById(R.id.chat_item_content_image);
        chatItemVoice = itemView.findViewById(R.id.chat_item_voice);
        chatItemLayoutContent = itemView.findViewById(R.id.chat_item_layout_content);
        chatItemVoiceTime = itemView.findViewById(R.id.chat_item_voice_time);
    }

    @Override
    public void setData(MessageInfo data) {
        chatItemDate.setText(data.getTime() != null ? data.getTime() : "");
        if (data.getHeader() != null){
            Glide.with(getContext()).load(data.getHeader()).into(chatItemHeader);
        } else {
            chatItemHeader.setImageResource(R.drawable.hdimg_1);
        }
        chatItemHeader.setOnClickListener((v) -> {
                onItemClickListener.onHeaderClick(getDataPosition());
        });
        if (data.getContent() != null) {
            chatItemContentText.setSpanText(handler, data.getContent(), true);
            chatItemVoice.setVisibility(View.GONE);
            chatItemContentText.setVisibility(View.VISIBLE);
            chatItemLayoutContent.setVisibility(View.VISIBLE);
            chatItemVoiceTime.setVisibility(View.GONE);
            chatItemContentImage.setVisibility(View.GONE);
        } else if (data.getImageUrl() != null) {
            chatItemVoice.setVisibility(View.GONE);
            chatItemLayoutContent.setVisibility(View.GONE);
            chatItemVoiceTime.setVisibility(View.GONE);
            chatItemContentText.setVisibility(View.GONE);
            chatItemContentImage.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(data.getImageUrl()).into(chatItemContentImage);
            chatItemContentImage.setOnClickListener((v) -> {
                    onItemClickListener.onImageClick(chatItemContentImage, getDataPosition());
            });
        } else if (data.getFilepath() != null) {
            chatItemVoice.setVisibility(View.VISIBLE);
            chatItemLayoutContent.setVisibility(View.VISIBLE);
            chatItemContentText.setVisibility(View.GONE);
            chatItemVoiceTime.setVisibility(View.VISIBLE);
            chatItemContentImage.setVisibility(View.GONE);
            chatItemVoiceTime.setText(Utils.formatTime(data.getVoiceTime()));
            chatItemLayoutContent.setOnClickListener((v) -> {
                    onItemClickListener.onVoiceClick(chatItemVoice, getDataPosition());
            });
        }
    }
}
