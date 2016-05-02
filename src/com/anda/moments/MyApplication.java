package com.anda.moments;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.anda.GlobalConfig;
import com.anda.moments.commons.Constant;
import com.anda.moments.constant.api.ReqUrls;
import com.anda.moments.entity.User;
import com.anda.moments.ui.LoginActivity;
import com.anda.moments.utils.JsonUtils;
import com.anda.moments.utils.SharePreferenceManager;
import com.anda.moments.utils.StringUtils;
import com.anda.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.anda.universalimageloader.cache.memory.MemoryCacheAware;
import com.anda.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.anda.universalimageloader.core.DisplayImageOptions;
import com.anda.universalimageloader.core.ImageLoader;
import com.anda.universalimageloader.core.ImageLoaderConfiguration;
import com.anda.universalimageloader.core.assist.ImageScaleType;
import com.anda.universalimageloader.core.assist.QueueProcessingType;

public class MyApplication extends Application {

	public static String TAG;
	

	private static MyApplication myApplication = null;
	private User user;
	
	public static MyApplication getInstance() {
		return myApplication;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getCurrentUser() {
		if(user==null){
			String userJson = (String)SharePreferenceManager.getSharePreferenceValue(myApplication,Constant.FILE_NAME,"user","");
			if(!StringUtils.isEmpty(userJson)){
				user = JsonUtils.fromJson(userJson,User.class);
				if(user==null){
					SharePreferenceManager.saveBatchSharedPreference(myApplication,Constant.FILE_NAME,"user","");
					Intent intent = new Intent(myApplication, LoginActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					myApplication.startActivity(intent);
				}
			}
		}
		return user;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		TAG = this.getClass().getSimpleName();
		// 由于Application类本身已经单例，所以直接按以下处理即可。
		myApplication = this;
		initImageLoader(this);




		try{

			//获取缓存的登陆对象
			String userJson = (String)SharePreferenceManager.getSharePreferenceValue(myApplication,Constant.FILE_NAME,"user","");
			if(!StringUtils.isEmpty(userJson)){
				user = JsonUtils.fromJson(userJson,User.class);
			}

			String jsessionId = (String) SharePreferenceManager.getSharePreferenceValue(myApplication, Constant.FILE_NAME, ReqUrls.JSESSION_ID, "");
			if(!StringUtils.isEmpty(jsessionId)){
				String tokens[] = jsessionId.split("_&_");
				long currentTime = System.currentTimeMillis();
				long saveCurrentTime = Long.parseLong(tokens[1]);
				if(currentTime - saveCurrentTime < 1000*60*60*24*(2-0.2)){//未超过2天
					//判断token的有效期(设置了一个礼拜,这里设置2天)
					GlobalConfig.JSESSION_ID = tokens[0];
				}

			}
		}catch(Exception e){

		}

	}

	/**
	 * 初始化imageLoader
	 */
	public void initImageLoader(Context context) {
		int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);

		MemoryCacheAware<String, Bitmap> memoryCache;

		memoryCache = new LRULimitedMemoryCache(memoryCacheSize);

		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCache(memoryCache).denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	public DisplayImageOptions getOptions(int drawableId) {
		return new DisplayImageOptions.Builder().showImageOnLoading(drawableId)
				.showImageForEmptyUri(drawableId).showImageOnFail(drawableId)
				.resetViewBeforeLoading(true).cacheInMemory(true)
				.cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}
	/**
	 * 获取导航页
	 */
	/*public void getBoot(final Handler handler){
		
		ApiHomeUtils.getBoot(myApplication, new RequestCallback() {
			
			@Override
			public void execute(ParseModel parseModel) {
				if (!ApiConstants.RESULT_SUCCESS.equals(parseModel
						.getStatus())) {
					String dataStr = (String)SharePreferenceManager.getSharePreferenceValue(myApplication, Constant.FILE_NAME, "boot", "");
					if(!StringUtils.isEmpty(dataStr)){
						mImageView = JsonUtils.fromJson(dataStr, List.class);
					}
				} else {
					mImageView = JsonUtils.fromJson(parseModel.getData().toString(), List.class);
					SharePreferenceManager.saveBatchSharedPreference(myApplication, Constant.FILE_NAME, "boot", parseModel.getData().toString());
				}
				handler.sendEmptyMessage(1);
			}
		});
		
	}
*/
	

}
