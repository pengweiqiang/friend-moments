package com.anda.moments.ui.publish;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.anda.moments.R;
import com.anda.moments.apdater.ImageGridAdapter;
import com.anda.moments.commons.AppManager;
import com.anda.moments.entity.ImageItem;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.utils.publish.Bimp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class ImageGridActivity extends BaseActivity {
	public static final String EXTRA_IMAGE_LIST = "imagelist";

	List<ImageItem> dataList;
	GridView gridView;
	ImageGridAdapter adapter;//
	AlbumHelper helper;
	Button bt;
	private View mBtnCancel;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				ToastUtils.showToast(ImageGridActivity.this,"最多选择9张图片");
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_image_grid);
		super.onCreate(savedInstanceState);

		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());


		dataList = (List<ImageItem>) getIntent().getSerializableExtra(
				EXTRA_IMAGE_LIST);

		adapter = new ImageGridAdapter(ImageGridActivity.this, dataList,
				mHandler);
		gridView.setAdapter(adapter);
		adapter.setTextCallback(new ImageGridAdapter.TextCallback() {
			public void onListen(int count) {
				bt.setText("完成" + "(" + count + ")");
			}
		});





	}

	/**
	 */
	@Override
	public void initView() {
		gridView = (GridView) findViewById(R.id.gridview);
		mBtnCancel = findViewById(R.id.tv_cancel);
		bt = (Button) findViewById(R.id.bt);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));

	}

	@Override
	public void initListener() {
		bt.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				ArrayList<String> list = new ArrayList<String>();
				Collection<String> c = adapter.map.values();
				Iterator<String> it = c.iterator();
				for (; it.hasNext();) {
					list.add(it.next());
				}

				if (Bimp.act_bool) {
					Intent intent = new Intent(ImageGridActivity.this,
							PublishPictureActivity.class);
					startActivity(intent);
					Bimp.act_bool = false;
				}
				for (int i = 0; i < list.size(); i++) {
					if (Bimp.drr.size() < 9) {
						Bimp.drr.add(list.get(i));
					}
				}
				AppManager.getAppManager().finishActivity();
			}

		});
		mBtnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AppManager.getAppManager().finishActivity();
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				adapter.notifyDataSetChanged();
			}

		});
	}
}
