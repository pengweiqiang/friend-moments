package com.anda.moments.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.anda.moments.R;
import com.anda.moments.commons.AppManager;
import com.anda.moments.ui.base.BaseFragmentActivity;
import com.anda.moments.ui.fragments.HomeFragment;
import com.anda.moments.ui.fragments.MyFragment;
import com.anda.moments.ui.fragments.OutsourceFragment;
import com.anda.moments.ui.fragments.SchoolAmbassdaorFragment;
import com.anda.moments.ui.fragments.SchoolHelpMainFragment;
import com.anda.moments.utils.ToastUtils;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends BaseFragmentActivity {
	
	//首页
	private HomeFragment mHomeFragment;
	//好友
	private OutsourceFragment mOutsourceFragment;
	//我的
	private MyFragment mMyFragment;
	
	private FragmentPagerAdapter mFragmentAdapter;
	private FragmentManager mFragmentManager;
	
	private RadioGroup mTabIndicators ;
	
	
	
	int[] tabIds = {R.id.home,R.id.friends,R.id.my};
	
	private int checkId = tabIds[0];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initView();
        UmengUpdateAgent.update(this);
    }
    
    private void initView(){
    	mTabIndicators = (RadioGroup) findViewById(R.id.tabIndicators);
    	
    	mFragmentManager = getSupportFragmentManager();
    	mFragmentAdapter = new FragmentPagerAdapter(mFragmentManager) {
			
			@Override
			public int getCount() {
				return tabIds.length;
			}
			
			@Override
			public Fragment getItem(int position) {
				if (position == 0) {
					return mHomeFragment = new HomeFragment();
				} else if (position == 1) {
					return mOutsourceFragment = new OutsourceFragment();
				}else if (position == 2) {
					return mMyFragment = new MyFragment();
				}
				return null;
			}
		};
    	
		
		mTabIndicators
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				Fragment fragment = getInstanceById(checkedId);
				hideFragment(checkedId, fragment);
				checkId = checkedId;
			}
		});
		Fragment fragment = getInstanceById(checkId);
		hideFragment(checkId, fragment);
    	
    }

    public Fragment getInstanceById(int id){
    	switch (id) {
		case R.id.home:
			if(mHomeFragment == null){
				mHomeFragment = new HomeFragment();
			}
			return mHomeFragment;
		case R.id.friends:
			if(mOutsourceFragment == null){
				mOutsourceFragment = new OutsourceFragment();
			}
			return mOutsourceFragment;
		
		case R.id.my:
			if(mMyFragment == null){
				mMyFragment = new MyFragment();
			}
			return mMyFragment;

		default:
			break;
		}
    	return null;
    }

    private void hideFragment(int checkId, Fragment fragment) {
		FragmentTransaction fragmentTransaction = mFragmentManager
				.beginTransaction();
		// 试用fragment替换activity中的main组件
		for (int i = 0; i < tabIds.length; i++) {
			if (tabIds[i] == checkId) {
				if (mFragmentManager.getFragments() != null
						&& mFragmentManager.getFragments().contains(fragment)) {
					fragmentTransaction.show(fragment);
				} else {
					fragmentTransaction.add(R.id.frameLayout, fragment);
				}
				break;
			}
		}
		if (this.checkId != checkId) {
			fragmentTransaction.hide(getInstanceById(this.checkId));
		}
		// 提交事物
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    
    private boolean isExit = false;

	// 退出操作
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isExit == false) {
				isExit = true;
				handler.sendEmptyMessageDelayed(0, 3000);
				ToastUtils.showToast(this, "再按一次退出朋友圈");
				return true;
			} else {
				AppManager.getAppManager().finishAllActivity();
				return false;
			}
		}
		return true;
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}
	};
    
}
