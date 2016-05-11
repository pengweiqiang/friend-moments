package com.anda.moments.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.anda.moments.R;
import com.anda.moments.api.constant.ApiConstants;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class ShareUtil {
	
	private Context context;

	public ShareUtil(Context context) {
		this.context = context;
	}

	// IWXAPI 是第三方app和微信通信的openApi接口
	private IWXAPI api;

	private static final int THUMB_SIZE = 150;

	/**
	 * 初始化微信操作
	 */
	public void initWX() {
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		api = WXAPIFactory.createWXAPI(context, ApiConstants.APP_ID, true);

		// 将应用的appId注册到微信

	}

	/**
	 * 分享文本
	 * @param text  分享内容
	 * @param sendType 分享类型  1---分享到好友  2---分享到朋友圈
	 */
	public void sendMsgToWX(String text, int sendType) {
		// 初始化一个WXTextObject对象
		WXTextObject textObj = new WXTextObject();
		textObj.text = text;

		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		// 发送文本类型的消息时，title字段不起作用
		// msg.title = "Will be ignored";
		msg.description = text;

		// 构造一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis()); // transaction字段用于唯一标识一个请求
		req.message = msg;
		// 要注意的是，SendMessageToWX.Req的scene成员，如果scene填WXSceneSession，那么消息会发送至微信的会话内。
		// 如果scene填WXSceneTimeline（微信4.2以上支持，如果需要检查微信版本支持API的情况，
		// 可调用IWXAPI的getWXAppSupportAPI方法,0x21020001及以上支持发送朋友圈），那么消息会发送至朋友圈。scene默认值为WXSceneSession。

		req.scene = sendType;

		// 调用api接口发送数据到微信
		api.sendReq(req);

	}

	/**
	 * 分享图片
	 * @param text  分享内容
	 * @param sendType 分享类型  1---分享到好友  2---分享到朋友圈
	 */
	public void sendImgToWX(String text, int sendType) {
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_launcher);
		WXImageObject imgObj = new WXImageObject(bmp);

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;
		msg.description = text;
		msg.title = text;

		Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE,
				THUMB_SIZE, true);
		bmp.recycle();
		msg.thumbData = Util.bmpToByteArray(thumbBmp, true); // 设置缩略图

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = sendType;
		api.sendReq(req);

	}

	/**
	 * 分享web到微信
	 * @param text  分享内容
	 * @param sendType 分享类型  1---分享到好友  2---分享到朋友圈
	 */
	public void sendWebPageToWX(String text, int sendType) {
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = ApiConstants.WEB_URL;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		if (sendType == SendMessageToWX.Req.WXSceneTimeline) {
			msg.title = text;
		} else {
			msg.title = ApiConstants.SHARE_TITLE;
		}
		msg.description = text;
		Bitmap thumb = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_launcher);
		msg.thumbData = Util.bmpToByteArray(thumb, true);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = sendType;
		api.sendReq(req);
	}
}
