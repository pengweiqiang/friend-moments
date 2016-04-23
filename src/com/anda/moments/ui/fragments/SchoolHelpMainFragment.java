package com.anda.moments.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anda.moments.R;
import com.anda.moments.ui.base.BaseFragment;

/**
 * 校内帮帮
 * @author pengweiqiang
 *
 */
public class SchoolHelpMainFragment extends BaseFragment {
	
	View mContentView;
	
	private SchoolHelpFragment mSchoolHelpFragment;
	private FragmentManager mFragmentManager;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.school_help_main, container, false);
		mFragmentManager = getChildFragmentManager();
		hideFragment();
		return mContentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	public Fragment getInstanceById(int id){
    	switch (id) {
		case 0:
			if(mSchoolHelpFragment == null){
				mSchoolHelpFragment = new SchoolHelpFragment();
			}
			return mSchoolHelpFragment;
		
		default:
			break;
		}
    	return null;
    }

    private void hideFragment() {
		FragmentTransaction fragmentTransaction = mFragmentManager
				.beginTransaction();
		fragmentTransaction.add(R.id.frameLayout_help, getInstanceById(0));
		// 提交事物
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}

	
}
