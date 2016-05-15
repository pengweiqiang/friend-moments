package com.anda.moments.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;

import com.anda.GlobalConfig;
import com.anda.gson.Gson;
import com.anda.gson.JsonObject;
import com.anda.gson.reflect.TypeToken;
import com.anda.moments.R;
import com.anda.moments.apdater.ProvinceCityAdapter;
import com.anda.moments.api.ApiUserUtils;
import com.anda.moments.api.constant.ApiConstants;
import com.anda.moments.api.constant.ReqUrls;
import com.anda.moments.commons.AppManager;
import com.anda.moments.entity.ParseModel;
import com.anda.moments.entity.Province;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.utils.HttpConnectionUtil;
import com.anda.moments.utils.JsonUtils;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.LoadingDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 省份、城市
 * @author pengweiqiang
 *
 */
public class ProvinceCityActivity extends BaseActivity {

	ActionBar mActionbar;

	private RecyclerView recyclerView;
	ProvinceCityAdapter provinceCityAdapter;
	private List<Province> provinceList = new ArrayList<Province>();




	LoadingDialog loadingDialog;
	@Override
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_province_city);
		super.onCreate(savedInstanceState);

		getData();

		provinceCityAdapter = new ProvinceCityAdapter(mContext,provinceList);
		//设置固定大小
		recyclerView.setHasFixedSize(true);
		//创建线性布局
		LinearLayoutManager mLayoutManagerPraise = new LinearLayoutManager(mContext);
		//垂直方向
		mLayoutManagerPraise.setOrientation(OrientationHelper.VERTICAL);
		//给RecyclerView设置布局管理器
		recyclerView.setLayoutManager(mLayoutManagerPraise);
		recyclerView.setAdapter(provinceCityAdapter);

		provinceCityAdapter.setOnItemClickListener(new ProvinceCityAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				ArrayList<Province.Sub> citys = provinceList.get(position).getSub();
				Intent intent = new Intent(mContext,CityActivity.class);
				intent.putExtra("citys",citys);
				intent.putExtra("province",provinceList.get(position).getName());
				startActivity(intent);
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

	private void getData(){


		loadingDialog = new LoadingDialog(mContext);
		loadingDialog.show();

		String url = ReqUrls.DEFAULT_REQ_HOST_IP + ReqUrls.REQUEST_GET_DISTRICT_INFO;
		OkHttpUtils
				.get()
				.url(url)
				.addHeader("JSESSIONID", GlobalConfig.JSESSION_ID)
				.build()
				.execute(new StringCallback()
				{
					@Override
					public void onError(Call call, Exception e)
					{
						loadingDialog.cancel();
					}

					@Override
					public void onResponse(String response)
					{

						try {
							JSONObject jsonObject = new JSONObject(response);
							if(jsonObject.getInt("retFlag")==200){
								String result = jsonObject.getString("result");
								List<Province> provinceListNow = JsonUtils.fromJson(result,new TypeToken<List<Province>>(){});
								if(provinceListNow!=null && !provinceListNow.isEmpty()){
									provinceList.addAll(provinceListNow);
									provinceCityAdapter.setDatas(provinceListNow);
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

						loadingDialog.cancel();
					}
				});

//		ApiUserUtils.getDistrictInfo(mContext, new HttpConnectionUtil.RequestCallback() {
//			@Override
//			public void execute(ParseModel parseModel) {
//				if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
//					provinceList = JsonUtils.fromJson(parseModel.getResult().toString(),new TypeToken<List<Province>>(){});
//					if(provinceList!=null && !provinceList.isEmpty()){
//						provinceCityAdapter.notifyDataSetChanged();
//					}
//				}else{
//					ToastUtils.showToast(mContext,parseModel.getInfo());
//				}
//				loadingDialog.cancel();
//			}
//		});
	}


}
