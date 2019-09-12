package com.sap.mim.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.sap.mim.R;
import com.sap.mim.ui.fragment.FriendListFragment;
import com.sap.mim.ui.fragment.MessageFragment;
import com.sap.mim.ui.fragment.NearByFragment;
import com.sap.mim.ui.fragment.UserInfoFragment;

public class MainActivity extends AppCompatActivity {

    protected static final String TAG = "MainActivity";
    private Context mContext;
    private ImageButton mNews, mConstact, mDeynaimic, mSetting;
    private View mPopView;
    private View currentButton;

    private TextView app_cancle;
    private TextView app_exit;
    private TextView app_change;

    private PopupWindow mPopupWindow;
    private LinearLayout buttomBarGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        findView();
        init();
    }

    private void findView(){
        mPopView = LayoutInflater.from(mContext).inflate(R.layout.app_exit, null);
        buttomBarGroup = findViewById(R.id.buttom_bar_group);
        mNews = findViewById(R.id.buttom_news);
        mConstact = findViewById(R.id.buttom_constact);
        mDeynaimic = findViewById(R.id.buttom_deynaimic);
        mSetting = findViewById(R.id.buttom_setting);

        app_cancle = mPopView.findViewById(R.id.app_cancle);
        app_change = mPopView.findViewById(R.id.app_change_user);
        app_exit = mPopView.findViewById(R.id.app_exit);
    }

    private void init(){
        mNews.setOnClickListener(newsOnClickListener);
        mConstact.setOnClickListener(constactOnClickListener);
        mDeynaimic.setOnClickListener(deynaimicOnClickListener);
        mSetting.setOnClickListener(settingOnClickListener);

        mConstact.performClick();

        mPopupWindow = new PopupWindow(mPopView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        app_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });

        app_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                ((Activity)mContext).overridePendingTransition(R.anim.activity_up, R.anim.fade_out);
                finish();
            }
        });

        app_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private View.OnClickListener newsOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            MessageFragment messageFragment = new MessageFragment();
            ft.replace(R.id.fl_content, messageFragment, MainActivity.TAG);
            ft.commit();
            setButton(v);
        }
    };

    private View.OnClickListener constactOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            FriendListFragment constactFatherFragment = new FriendListFragment();
            ft.replace(R.id.fl_content, constactFatherFragment, MainActivity.TAG);
            ft.commit();
            setButton(v);
        }
    };

    private View.OnClickListener deynaimicOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            NearByFragment dynamicFragment = new NearByFragment();
            ft.replace(R.id.fl_content, dynamicFragment, MainActivity.TAG);
            ft.commit();
            setButton(v);
        }
    };

    private View.OnClickListener settingOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            UserInfoFragment settingFragment = new UserInfoFragment();
            ft.replace(R.id.fl_content, settingFragment, MainActivity.TAG);
            ft.commit();
            setButton(v);
        }
    };

    private void setButton(View v){
        if (currentButton != null && currentButton.getId()!= v.getId()){
            currentButton.setEnabled(true);
        }
        v.setEnabled(false);
        currentButton = v;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode== KeyEvent.KEYCODE_MENU){
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#b0000000")));
            mPopupWindow.showAtLocation(buttomBarGroup, Gravity.BOTTOM, 0, 0);
            mPopupWindow.setAnimationStyle(R.style.app_pop);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setFocusable(true);
            mPopupWindow.update();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 用户点击后退键,在后台运行
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}

