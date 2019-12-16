package com.sap.mim.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sap.mim.R;
import com.sap.mim.util.SystemMethod;

public class TitleBarView extends RelativeLayout {

	private Context mContext;
	private Button btnLeft;
	private Button btnRight;
	private TextView tv_center;

	public TitleBarView(Context context){
		super(context);
		mContext=context;
		initView();
	}
	
	public TitleBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
		initView();
	}
	
	private void initView(){
		LayoutInflater.from(mContext).inflate(R.layout.common_title_bar, this);
		btnLeft   = findViewById(R.id.title_btn_left);
		btnRight  = findViewById(R.id.title_btn_right);
		tv_center = findViewById(R.id.title_txt);
		
	}
	
	public void setCommonTitle(int LeftVisibility,int centerVisibility,int rightVisibility){
		btnLeft.setVisibility(LeftVisibility);
		btnRight.setVisibility(rightVisibility);
		tv_center.setVisibility(centerVisibility);
	}
	
	public void setBtnLeft(int icon,int txtRes){
		Drawable img = mContext.getResources().getDrawable(icon);
		int height= SystemMethod.dip2px(mContext, 20);
		int width=img.getIntrinsicWidth()*height/img.getIntrinsicHeight();
		img.setBounds(0, 0, width, height);
		btnLeft.setText(txtRes);
		btnLeft.setCompoundDrawables(img, null, null, null);
	}
	
	public void setBtnLeft(int txtRes){
		btnLeft.setText(txtRes);
	}
	
	
	public void setBtnRight(int icon){
		Drawable img=mContext.getResources().getDrawable(icon);
		int height= SystemMethod.dip2px(mContext, 30);
		int width=img.getIntrinsicWidth()*height/img.getIntrinsicHeight();
		img.setBounds(0, 0, width, height);
		btnRight.setCompoundDrawables(img, null, null, null);
	}
	
	public void setTitleText(String txtRes){
		tv_center.setText(txtRes);
	}
	
	public void setBtnLeftOnclickListener(OnClickListener listener){
		btnLeft.setOnClickListener(listener);
	}
	
	public void setBtnRightOnclickListener(OnClickListener listener){
		btnRight.setOnClickListener(listener);
	}
	
	public void destoryView(){
		btnLeft.setText(null);
		btnRight.setText(null);
		tv_center.setText(null);
	}

}
