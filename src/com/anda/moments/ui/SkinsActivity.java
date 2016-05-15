package com.anda.moments.ui;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;

import com.anda.GlobalConfig;
import com.anda.gson.reflect.TypeToken;
import com.anda.moments.MyApplication;
import com.anda.moments.R;
import com.anda.moments.apdater.SkinsRecyclerViewAdapter;
import com.anda.moments.api.constant.ApiConstants;
import com.anda.moments.api.constant.ReqUrls;
import com.anda.moments.commons.AppManager;
import com.anda.moments.commons.Constant;
import com.anda.moments.entity.Skins;
import com.anda.moments.entity.User;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.utils.JsonUtils;
import com.anda.moments.utils.SharePreferenceManager;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.LoadingDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import okhttp3.Call;

/**
 * 皮肤列表
 * @author pengweiqiang
 *
 */
public class SkinsActivity extends BaseActivity implements SkinsRecyclerViewAdapter.OnItemClickListener{

	ActionBar mActionbar;

	private RecyclerView recyclerView;
	SkinsRecyclerViewAdapter skinsRecyclerViewAdapter;
	private List<Skins> skinsList = new ArrayList<Skins>();


	String skinPath = "";//选中皮肤
	LoadingDialog loadingDialog;

	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_skins);
		super.onCreate(savedInstanceState);

		getSkins();

		skinsRecyclerViewAdapter = new SkinsRecyclerViewAdapter(mContext,skinsList);
		//创建线性布局
		LinearLayoutManager mLayoutManagerComment = new LinearLayoutManager(mContext);
		//垂直方向
		mLayoutManagerComment.setOrientation(OrientationHelper.VERTICAL);
		//给RecyclerView设置布局管理器
		recyclerView.setLayoutManager(mLayoutManagerComment);
		recyclerView.setAdapter(skinsRecyclerViewAdapter);
		skinsRecyclerViewAdapter.setOnItemClickListener(this);

	}

	@Override
	public void initView() {
		mActionbar = (ActionBar)findViewById(R.id.actionBar);
		mActionbar.setTitle("封面列表");
		mActionbar.setLeftActionButtonListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppManager.getAppManager().finishActivity();
			}
		});

		recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

	}

	@Override
	public void initListener() {

	}

	private void getSkins(){
		loadingDialog = new LoadingDialog(mContext);
		loadingDialog.show();
		String url = ReqUrls.DEFAULT_REQ_HOST_IP + ReqUrls.REQUEST_GET_APP_SKINS;
		OkHttpUtils//
				.get()//
				.addHeader("JSESSIONID", GlobalConfig.JSESSION_ID)
				.url(url)//
				.build()//
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e) {
						loadingDialog.cancel();
						ToastUtils.showToast(mContext,"获取失败.");
					}

					@Override
					public void onResponse(String response) {
						JSONObject jsonObject = null;
						try {
							jsonObject = new JSONObject(response);
							if(ApiConstants.RESULT_SUCCESS.equals(jsonObject.getString("retFlag"))) {
								skinsList = JsonUtils.fromJson(jsonObject.getString("result"),new TypeToken<List<Skins>>(){});
								skinsRecyclerViewAdapter.setDatas(skinsList);
							}else{
								ToastUtils.showToast(mContext,jsonObject.getString("info"));
							}
						} catch (JSONException e) {
							e.printStackTrace();
							ToastUtils.showToast(mContext,"获取失败,稍后再试");
						}
						loadingDialog.cancel();

					}
				});
	}

	/**
	 * 更换皮肤
     */
	private void updateSkins(){
		User user = MyApplication.getInstance().getCurrentUser();
		String url = ReqUrls.DEFAULT_REQ_HOST_IP + ReqUrls.REQUEST_UPDATE_SKIN;
		OkHttpUtils//
				.get()//
				.addHeader("JSESSIONID", GlobalConfig.JSESSION_ID)
				.addParams("skinPath",skinPath)
				.addParams("phoneNum",user.getPhoneNum())
				.url(url)//
				.build()//
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e) {
						ToastUtils.showToast(mContext,"更换失败.");
					}

					@Override
					public void onResponse(String response) {
						JSONObject jsonObject = null;
						try {
							jsonObject = new JSONObject(response);
							if(ApiConstants.RESULT_SUCCESS.equals(jsonObject.getString("retFlag"))) {
								ToastUtils.showToast(mContext,"更换成功");
								updateSuccessRefreshCache();
								AppManager.getAppManager().finishActivity();
							}else{
								ToastUtils.showToast(mContext,jsonObject.getString("info"));
							}
						} catch (JSONException e) {
							e.printStackTrace();
							ToastUtils.showToast(mContext,"更换失败,稍后再试");
						}

					}
				});
	}

	/**
	 * 更新个人资料的皮肤缓存
	 */
	private void updateSuccessRefreshCache(){
		User user = MyApplication.getInstance().getCurrentUser();
		user.setSkinPath(skinPath);
		MyApplication.getInstance().setUser(user);
		SharePreferenceManager.saveBatchSharedPreference(mContext, Constant.FILE_NAME,"user", JsonUtils.toJson(user));
	}


	@Override
	public void onItemClick(View view, int position) {
		Skins skins = skinsList.get(position);
		skinPath = skins.getSkinPath();
		updateSkins();
	}

	@Override
	public void onItemLongClick(View view, int position) {

	}
}
