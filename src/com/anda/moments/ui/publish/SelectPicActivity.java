package com.anda.moments.ui.publish;

import java.io.Serializable;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.anda.moments.R;
import com.anda.moments.apdater.ImageBucketAdapter;
import com.anda.moments.commons.AppManager;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.ui.base.BaseFragmentActivity;

public class SelectPicActivity extends BaseActivity {
	// ArrayList<Entity> dataList;//用来装载数据源的列表
	List<ImageBucket> dataList;
	GridView gridView;
	ImageBucketAdapter adapter;// 自定义的适配器
	AlbumHelper helper;
	public static final String EXTRA_IMAGE_LIST = "imagelist";
	public static Bitmap bimap;

	private TextView mTvCancel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_image_bucket);
		super.onCreate(savedInstanceState);

		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

		initData();
//		initView();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		dataList = helper.getImagesBucketList(false);	
		bimap=BitmapFactory.decodeResource(
				getResources(),
				R.drawable.add_picture);

		adapter = new ImageBucketAdapter(SelectPicActivity.this, dataList);
		gridView.setAdapter(adapter);
	}

	/**
	 * 初始化view视图
	 */
	@Override
	public void initView() {
		gridView = (GridView) findViewById(R.id.gridview);
		mTvCancel = (TextView) findViewById(R.id.tv_cancel);






	}

	@Override
	public void initListener() {
		mTvCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AppManager.getAppManager().finishActivity();
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				/**
				 * 根据position参数，可以获得跟GridView的子View相绑定的实体类，然后根据它的isSelected状态，
				 * 来判断是否显示选中效果。 至于选中效果的规则，下面适配器的代码中会有说明
				 */
				// if(dataList.get(position).isSelected()){
				// dataList.get(position).setSelected(false);
				// }else{
				// dataList.get(position).setSelected(true);
				// }
				/**
				 * 通知适配器，绑定的数据发生了改变，应当刷新视图
				 */
				// adapter.notifyDataSetChanged();
				Intent intent = new Intent(SelectPicActivity.this,
						ImageGridActivity.class);
				intent.putExtra(SelectPicActivity.EXTRA_IMAGE_LIST,
						(Serializable) dataList.get(position).imageList);
				startActivity(intent);
				AppManager.getAppManager().finishActivity();
			}

		});
	}
}
