package com.anda.moments.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.anda.moments.R;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.StringUtils;

/**
 * 标题栏, 可设置标题和左右图标
 * 
 * @author Will
 *
 */
public class ActionBar extends FrameLayout {

	private TextView mTitleView;
	private TextView mLeftActionButton;
	private TextView mActionBarTitle;
	private TextView mRightView;

	public ActionBar(Context context) {
		super(context);
	}

	public ActionBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ActionBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.action_bar_layout,
				this);
		mTitleView = (TextView) findViewById(R.id.actionBarTitle);
		mLeftActionButton = (TextView) findViewById(R.id.leftActionButton);
		mActionBarTitle = (TextView)findViewById(R.id.actionBarTitle);
		mRightView = (TextView)findViewById(R.id.btn_right);
	}

	public void setTitle(int resId) {
		mTitleView.setText(getResources().getString(resId));
	}

	public void setTitle(String text) {
		mTitleView.setText(text);
	}
	
	public void setTitleTextColor(int resId){
		mTitleView.setTextColor(getResources().getColor(resId));
	}
	public void setLeftActionButtonListener(OnClickListener listener){
		mLeftActionButton.setOnClickListener(listener);
	}
	public void setLeftActionButton(int icon, String textStr,OnClickListener listener) {
		if(icon!=0){
			Drawable drawable= getResources().getDrawable(icon);
			/// 这一步必须要做,否则不会显示.
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			mLeftActionButton.setCompoundDrawables(null,null,drawable,null);
			mLeftActionButton.setPadding(DeviceInfo.dp2px(getContext(),15), 0, DeviceInfo.dp2px(getContext(),15), 0);
			mLeftActionButton.setCompoundDrawablePadding(DeviceInfo.dp2px(getContext(), 6));
			mLeftActionButton.setText(textStr);
		}
		mLeftActionButton.setOnClickListener(listener);
//		mLeftActionButton.setVisibility(View.VISIBLE);
	}
	
	public void setRightActionButton(OnClickListener listener){
		if(mRightView.getVisibility() == View.GONE){
			mRightView.setVisibility(View.VISIBLE);
		}
		mRightView.setOnClickListener(listener);
	}
	
	public void setRightActionButton(int icon,String text,OnClickListener listener){
		mRightView.setText(text);
		setRightActionButton(listener);
		if(icon == 0){
			mRightView.setCompoundDrawables(null,null,null,null);
		}
	}
	

	public void hideLeftActionButton() {
		mLeftActionButton.setVisibility(View.GONE);
	}
	
	public void setLeftText(String text){
		if(!StringUtils.isEmpty(text)){
			mLeftActionButton.setText(text);
		}else{
			mLeftActionButton.setText("北京");
		}
	}

	
}
