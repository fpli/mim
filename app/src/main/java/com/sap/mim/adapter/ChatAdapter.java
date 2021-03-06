package com.sap.mim.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.sap.mim.adapter.holder.ChatAcceptViewHolder;
import com.sap.mim.adapter.holder.ChatSendViewHolder;
import com.sap.mim.entity.MessageInfo;
import com.sap.mim.util.Constants;


public class ChatAdapter extends RecyclerArrayAdapter<MessageInfo> {

    private onItemClickListener onItemClickListener;
    public Handler handler;

    public ChatAdapter(Context context) {
        super(context);
        handler = new Handler();
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder viewHolder = null;
        switch (viewType) {
            case Constants.CHAT_ITEM_TYPE_LEFT:
                viewHolder = new ChatAcceptViewHolder(parent, onItemClickListener, handler);
                break;
            case Constants.CHAT_ITEM_TYPE_RIGHT:
                viewHolder = new ChatSendViewHolder(parent, onItemClickListener, handler);
                break;
        }
        return viewHolder;
    }

    @Override
    public int getViewType(int position) {
        return getAllData().get(position).getType();
    }

    /**
     * 描述:条目点击事件
     * @param onItemClickListener
     */
    public void addItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface onItemClickListener {

        void onHeaderClick(int position);

        void onImageClick(View view, int position);

        void onVoiceClick(ImageView imageView, int position);
    }
}
