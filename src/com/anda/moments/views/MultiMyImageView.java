package com.anda.moments.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;


import com.anda.moments.R;
import com.anda.moments.utils.DeviceInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * @ClassName MultiImageView.java
 * @author shoyu
 * @version
 * @Description: 显示1~N张图片的View
 */

public class MultiMyImageView extends LinearLayout {
	public static int MAX_WIDTH = 0;

	// 照片的Url列表
	private List<String> imagesList;

	/** 长度 单位为Pixel **/
	private int pxOneMaxWandH;  // 单张图最大允许宽高
	private int pxMoreWandH = 0;// 多张图的宽高
	private int pxImagePadding = 3;// 图片间的间距

	private int MAX_PER_ROW_COUNT = 2;// 每行显示最大数

	private LayoutParams onePicPara;
	private LayoutParams morePara, moreParaColumnFirst;
	private LayoutParams rowPara;

	private OnItemClickListener mOnItemClickListener;
	public void setOnItemClickListener(OnItemClickListener onItemClickListener){
		mOnItemClickListener = onItemClickListener;
	}

	public MultiMyImageView(Context context) {
		super(context);
	}

	public MultiMyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setList(List<String> lists) throws IllegalArgumentException{
		if(lists==null){
			throw new IllegalArgumentException("imageList is null...");
		}
		if(lists.size()>4){
			imagesList = lists.subList(0,4);
		}else {
			imagesList = lists;
		}

		if(MAX_WIDTH > 0){
			pxMoreWandH = (MAX_WIDTH - pxImagePadding*2 )/2; //解决右侧图片和内容对不齐问题
			pxOneMaxWandH =(MAX_WIDTH - pxImagePadding*2 )/2*2;
			initImageLayoutParams();
		}

		initView();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if(MAX_WIDTH == 0){
			int width = measureWidth(widthMeasureSpec);
			if(width>0){
				MAX_WIDTH = width;
				if(imagesList!=null && imagesList.size()>0){
					setList(imagesList);
				}
			}
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int widthView = DeviceInfo.dp2px(getContext(),110);
		setMeasuredDimension(widthView,widthView);
	}

	/**
	 * Determines the width of this view
	 *
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The width of the view, honoring constraints from measureSpec
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text
			// result = (int) mTextPaint.measureText(mText) + getPaddingLeft()
			// + getPaddingRight();
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	private void initImageLayoutParams() {
		int wrap = LayoutParams.WRAP_CONTENT;
		int match = LayoutParams.MATCH_PARENT;

		onePicPara = new LayoutParams(pxOneMaxWandH, pxOneMaxWandH);

		moreParaColumnFirst = new LayoutParams(pxMoreWandH, pxMoreWandH);
		morePara = new LayoutParams(pxMoreWandH, pxMoreWandH);
		morePara.setMargins(pxImagePadding, 0, 0, 0);

		rowPara = new LayoutParams(match, wrap);
	}

	// 根据imageView的数量初始化不同的View布局,还要为每一个View作点击效果
	private void initView() {
		this.setOrientation(VERTICAL);
		this.removeAllViews();
		if(MAX_WIDTH == 0){
			//为了触发onMeasure()来测量MultiImageView的最大宽度，MultiImageView的宽设置为match_parent
			addView(new View(getContext()));
			return;
		}

		if (imagesList == null || imagesList.size() == 0) {
			return;
		}

		int allCount = imagesList.size();
		if (imagesList.size() == 1) {
			addView(createImageView(0, false));
		}else if(allCount==2){

			this.setOrientation(HORIZONTAL);

			for(int rowCursor = 0;rowCursor<allCount;rowCursor++){

				LinearLayout rowLayout = new LinearLayout(getContext());
				rowLayout.setOrientation(LinearLayout.HORIZONTAL);


				rowPara.width = pxMoreWandH;
				rowPara.height = pxMoreWandH*2;


				rowLayout.setLayoutParams(rowPara);


				addView(rowLayout);
				rowLayout.addView(createImageView(rowCursor, true));
			}


		}else if(allCount==3){
			this.setOrientation(HORIZONTAL);


			for(int rowCursor = 0;rowCursor<2;rowCursor++){

				LinearLayout rowLayout = new LinearLayout(getContext());
				if(rowCursor==0) {
					rowLayout.setOrientation(LinearLayout.HORIZONTAL);


					rowPara.width = pxMoreWandH;
					rowPara.height = pxMoreWandH * 2;


					rowLayout.setLayoutParams(rowPara);
					rowLayout.addView(createImageView(rowCursor, true));

					addView(rowLayout);
				}
				else if(rowCursor==1) {
//					rowPara.setMargins(pxImagePadding/2,0,0,0);
//					rowLayout.setLayoutParams(rowPara);

					rowLayout.setLayoutParams(rowPara);
					rowLayout.setOrientation(LinearLayout.VERTICAL);
					int rowOffset = rowCursor * 1;// 行偏移
					for (int columnCursor = 0; columnCursor < 2; columnCursor++) {
						int position = columnCursor + rowOffset;
						rowLayout.addView(createImageView(position, true));
					}

					addView(rowLayout);
				}

			}
		} else if(allCount==4){

			int rowCount = MAX_PER_ROW_COUNT;
			for (int rowCursor = 0; rowCursor < rowCount; rowCursor++) {
				LinearLayout rowLayout = new LinearLayout(getContext());
				rowLayout.setOrientation(LinearLayout.HORIZONTAL);



				rowLayout.setLayoutParams(rowPara);
				if (rowCursor != 0) {
					rowLayout.setPadding(0, pxImagePadding, 0, 0);
				}

				int columnCount = allCount % MAX_PER_ROW_COUNT == 0 ? MAX_PER_ROW_COUNT
						: allCount % MAX_PER_ROW_COUNT;//每行的列数
				if (rowCursor != rowCount - 1) {
					columnCount = MAX_PER_ROW_COUNT;
				}
				addView(rowLayout);

				int rowOffset = rowCursor * MAX_PER_ROW_COUNT;// 行偏移
				for (int columnCursor = 0; columnCursor < columnCount; columnCursor++) {
					int position = columnCursor + rowOffset;
					rowLayout.addView(createImageView(position, true));
				}
			}
		}
	}

	private ImageView createImageView(int position, final boolean isMultiImage) {
		String url = imagesList.get(position);
		ImageView imageView = new ColorFilterImageView(getContext());
		if(isMultiImage){
			if(position==0 && (imagesList.size() == 2 ||imagesList.size()==3)){
				LayoutParams moreParaColumnFirst = new LayoutParams(pxOneMaxWandH, pxMoreWandH*2);
				moreParaColumnFirst.setMargins(0,0,pxImagePadding,0);
				imageView.setScaleType(ScaleType.CENTER_CROP);
				imageView.setLayoutParams(moreParaColumnFirst);
			}else if(position == 1 && imagesList.size()==2){
				LayoutParams moreParaColumnFirst = new LayoutParams(pxOneMaxWandH, pxMoreWandH*2);
				moreParaColumnFirst.setMargins(pxImagePadding,0,0,0);
				imageView.setScaleType(ScaleType.CENTER_CROP);

				imageView.setLayoutParams(moreParaColumnFirst);

			}else if(position == 2 && imagesList.size() == 3){
				LayoutParams moreParaColumnFirst = new LayoutParams(pxOneMaxWandH/2, pxMoreWandH);
				imageView.setScaleType(ScaleType.CENTER_CROP);
				moreParaColumnFirst.setMargins(pxImagePadding,0,pxImagePadding,0);
				imageView.setLayoutParams(moreParaColumnFirst);
			}else if(position == 1&& imagesList.size() == 3){
				LayoutParams moreParaColumnFirst = new LayoutParams(pxOneMaxWandH/2, pxMoreWandH);
				moreParaColumnFirst.setMargins(pxImagePadding,0,0,pxImagePadding);
				imageView.setScaleType(ScaleType.CENTER_CROP);
				imageView.setLayoutParams(moreParaColumnFirst);
			}
			else{
				imageView.setScaleType(ScaleType.CENTER_CROP);
				imageView.setLayoutParams(position % MAX_PER_ROW_COUNT == 0 ?moreParaColumnFirst : morePara);
			}
		}else {
			imageView.setAdjustViewBounds(true);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageView.setMaxHeight(pxOneMaxWandH);
			imageView.setLayoutParams(onePicPara);
		}

		imageView.setTag(position);
		imageView.setId(url.hashCode());
		imageView.setOnClickListener(mImageViewOnClickListener);
		Picasso.with(getContext()).load(url).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(imageView);

		return imageView;
	}

	// 图片点击事件
	private View.OnClickListener mImageViewOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			if(mOnItemClickListener != null){
				mOnItemClickListener.onItemClick(view, (Integer)view.getTag());
			}
		}
	};

	public interface OnItemClickListener{
		public void onItemClick(View view, int position);
	}
}