package com.anda.moments.ui.my;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;

import com.anda.GlobalConfig;
import com.anda.moments.MyApplication;
import com.anda.moments.R;
import com.anda.moments.apdater.CitySelectAdapter;
import com.anda.moments.api.constant.ApiConstants;
import com.anda.moments.api.constant.ReqUrls;
import com.anda.moments.commons.AppManager;
import com.anda.moments.commons.Constant;
import com.anda.moments.entity.Province;
import com.anda.moments.entity.User;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.utils.JsonUtils;
import com.anda.moments.utils.SharePreferenceManager;
import com.anda.moments.utils.ThreadUtil;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 城市
 * @author pengweiqiang
 *
 */
public class CityActivity extends BaseActivity {

	ActionBar mActionbar;

	private RecyclerView recyclerView;
	CitySelectAdapter citySelectAdapter;
	private List<Province.Sub> cityList = new ArrayList<Province.Sub>();


	String district = "";


	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_province_city);
		super.onCreate(savedInstanceState);

		district = this.getIntent().getStringExtra("province");
		cityList = (ArrayList<Province.Sub>)this.getIntent().getSerializableExtra("citys");

		citySelectAdapter = new CitySelectAdapter(mContext,cityList);
		//设置固定大小
		recyclerView.setHasFixedSize(true);
		//创建线性布局
		LinearLayoutManager mLayoutManagerPraise = new LinearLayoutManager(mContext);
		//垂直方向
		mLayoutManagerPraise.setOrientation(OrientationHelper.VERTICAL);
		//给RecyclerView设置布局管理器
		recyclerView.setLayoutManager(mLayoutManagerPraise);
		recyclerView.setAdapter(citySelectAdapter);
		citySelectAdapter.setDatas(cityList);

		citySelectAdapter.setOnItemClickListener(new CitySelectAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				Province.Sub city = cityList.get(position);
				String cityName = city.getName();
				district+=" "+cityName;
				updateInfoByOkHttp();
			}

			@Override
			public void onItemLongClick(View view, int position) {

			}
		});

	}

	@Override
	public void initView() {
		mActionbar = (ActionBar)findViewById(R.id.actionBar);
		mActionbar.setTitle("地区");
//		mActionbar.hideBottonLine();
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


	private LoadingDialog mLoadingDialog;
	private OkHttpClient client = new OkHttpClient();
	private void updateInfoByOkHttp(){

		if(mLoadingDialog==null) {
			mLoadingDialog = new LoadingDialog(mContext);
		}
		mLoadingDialog.show();

		ThreadUtil.getTheadPool(true).submit(new Runnable() {
			@Override
			public void run() {

				//多文件表单上传构造器
				MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

				//添加一个文本表单参数
				builder.addFormDataPart("phoneNum", MyApplication.getInstance().getCurrentUser().getPhoneNum());

				builder.addFormDataPart("district",district);


				RequestBody requestBody = builder.build();
				//构造文件上传时的请求对象Request
				String url = ReqUrls.DEFAULT_REQ_HOST_IP + ReqUrls.REQUEST_UPDATE_USER_INFO;
				Request request = new Request.Builder().url(url)
						.post(requestBody)
						.addHeader("JSESSIONID", GlobalConfig.JSESSION_ID)
						.build();

				Call call = client.newCall(request);
				call.enqueue(new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
						mLoadingDialog.cancel();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ToastUtils.showToast(mContext, "更新失败");
							}
						});
					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						mLoadingDialog.cancel();
						try {
							if (!response.isSuccessful()) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										ToastUtils.showToast(mContext, "更新失败");
									}
								});

							} else {
								try {
									JSONObject jsonResult = new JSONObject(response.body().string());
									int retFlag = jsonResult.getInt("retFlag");
									if (ApiConstants.RESULT_SUCCESS.equals("" + retFlag)) {
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												ToastUtils.showToast(mContext,"修改成功");
												updateSuccessRefreshCache();
												AppManager.getAppManager().finishActivity(ProvinceCityActivity.class);
												AppManager.getAppManager().finishActivity();
											}
										});

									} else {
										final String info = jsonResult.getString("info");
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												ToastUtils.showToast(mContext, info);
											}
										});

									}
								} catch (JSONException e) {
									e.printStackTrace();
								}

							}
						} catch (IOException e) {
							mLoadingDialog.cancel();
							e.printStackTrace();
						}
					}
				});
			}


		});


	}

	private void updateSuccessRefreshCache(){
		User user = MyApplication.getInstance().getCurrentUser();

		user.setDistrict(district);

		MyApplication.getInstance().setUser(user);
		SharePreferenceManager.saveBatchSharedPreference(mContext, Constant.FILE_NAME,"user", JsonUtils.toJson(user));
	}



}
