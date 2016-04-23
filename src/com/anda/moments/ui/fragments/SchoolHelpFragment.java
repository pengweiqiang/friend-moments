package com.anda.moments.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.anda.moments.R;
import com.anda.moments.ui.base.BaseFragment;
import com.anda.moments.views.ActionBar;

/**
 * 校园帮帮
 * @author will
 *
 */
public class SchoolHelpFragment extends BaseFragment{

	private View mContentView;
	private ActionBar mActionBar;
	private View mFindTeamMate,mAskQuestion,mShare;
	
	
	private ImageView mTopImageView;
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.school_help, container,false);
		mActionBar = (ActionBar)mContentView.findViewById(R.id.actionBar);
//		mActionBar.setTitle(R.string.tab_school_help);
		mActionBar.hideLeftActionButton();
		
		
		mFindTeamMate = mContentView.findViewById(R.id.help_find_teammate);
		mAskQuestion = mContentView.findViewById(R.id.help_question);
		mShare = mContentView.findViewById(R.id.help_share);
		
		mTopImageView = (ImageView) mContentView.findViewById(R.id.top_image);
		
		return mContentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		initListener();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	FindTeammateFragment findTeamFragment;
	AskQuestionFragment askQuestionFragment;
	ShareExperienceFragment shareFragment;
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			System.out.println(findTeamFragment+"0   "+askQuestionFragment+" 1"+shareFragment+"   2");
			switch (v.getId()) {
			case R.id.help_find_teammate://找个队友
				if(findTeamFragment == null){
					findTeamFragment = new FindTeammateFragment();
				}
				startFragment(findTeamFragment);
				break;
			case R.id.help_question://有问必答
				if(askQuestionFragment == null){
					askQuestionFragment = new AskQuestionFragment();
				}
				startFragment(askQuestionFragment);
				break;
			case R.id.help_share://经历分享
				if(shareFragment == null){
					shareFragment = new ShareExperienceFragment();
				}
				startFragment(shareFragment);
				break;

			default:
				break;
			}
		}
	};
	private void startFragment(Fragment fragment){
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.frameLayout_help, fragment);
//		ft.hide(this);
//		ft.add(R.id.frameLayout,fragment);
        ft.addToBackStack(null);
        ft.commit();
	}
	public void hideFragment(){
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
//		if(!isHidden()){
//			ft.hide(this);
//		}
		if(findTeamFragment != null && !findTeamFragment.isHidden() &&  fm.getFragments().contains(findTeamFragment)){
			ft.hide(findTeamFragment);
		}
		else if(askQuestionFragment != null && !askQuestionFragment.isHidden() && fm.getFragments().contains(askQuestionFragment)){
			ft.hide(askQuestionFragment);
		}
		else if(shareFragment != null && !shareFragment.isHidden() && fm.getFragments().contains(shareFragment)){
			ft.hide(shareFragment);
		}
		ft.commit();
	}
	public void showFragment(){
		
	}
	private void initListener(){
		mFindTeamMate.setOnClickListener(onClickListener);
		mAskQuestion.setOnClickListener(onClickListener);
		mShare.setOnClickListener(onClickListener);
	}
	
}
