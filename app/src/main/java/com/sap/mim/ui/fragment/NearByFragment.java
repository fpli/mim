package com.sap.mim.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sap.mim.R;
import com.sap.mim.base.BaseFragment;
import com.sap.mim.widget.TitleBarView;


public class NearByFragment extends BaseFragment {

	private Context mContext;
	private View mBaseView;
	private TitleBarView mTitleBarView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		mBaseView = inflater.inflate(R.layout.fragment_nearby, null);
		findView();
		init();
		return mBaseView;
	}

	private void findView(){
		mTitleBarView = mBaseView.findViewById(R.id.title_bar);
	}
	
	private void init(){
		mTitleBarView.setCommonTitle(View.GONE, View.VISIBLE, View.GONE);
		mTitleBarView.setTitleText("附近的人");
	}
}

