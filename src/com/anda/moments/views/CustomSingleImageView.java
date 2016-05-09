package com.anda.moments.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.Log;
import com.squareup.picasso.Picasso;


/**
 * Created by Pan_ on 2015/2/2.
 */
public class CustomSingleImageView extends ImageView {
    private String url;
    private boolean isAttachedToWindow;

    public CustomSingleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSingleImageView(Context context) {
        super(context);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Drawable drawable=getDrawable();
                if(drawable!=null) {
                    drawable.mutate().setColorFilter(Color.GRAY,
                            PorterDuff.Mode.MULTIPLY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Drawable drawableUp=getDrawable();
                if(drawableUp!=null) {
                    drawableUp.mutate().clearColorFilter();
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onAttachedToWindow() {
        isAttachedToWindow = true;
        setImageUrl(url);
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        Picasso.with(getContext()).cancelRequest(this);
        isAttachedToWindow = false;
        setImageBitmap(null);
        super.onDetachedFromWindow();
    }

    public static int MAX_WIDTH = 0;
    /** 长度 单位为Pixel **/
    private int pxOneMaxWandH;  // 单张图最大允许宽高
    private LinearLayout.LayoutParams onePicPara;

    public void setImageUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            this.url = url;
            if (isAttachedToWindow) {
//                int width = this.getWidth();
//                int height = this.getHeight();
//                if(width==0){
//                    width = 199;
//                    height =width;
//                }
                Log.e("CustomSingleImageView",this.getWidth()+"   "+this.getHeight());
                Picasso.with(getContext()).load(url).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(this);
            }

            if(MAX_WIDTH > 0){
                pxOneMaxWandH = MAX_WIDTH * 2 / 3;
                initImageLayoutParams();
            }

            initView();

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(MAX_WIDTH == 0){
            int width = measureWidth(widthMeasureSpec);
            if(width>0){
                MAX_WIDTH = width;
                if(!TextUtils.isEmpty(url)){
                    setImageUrl(url);
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
        int wrap = LinearLayout.LayoutParams.WRAP_CONTENT;
        int match = LinearLayout.LayoutParams.MATCH_PARENT;

        onePicPara = new LinearLayout.LayoutParams(pxOneMaxWandH, wrap);
        onePicPara.setMargins(DeviceInfo.dp2px(getContext(),10),DeviceInfo.dp2px(getContext(),6),0,0);

    }

    // 根据imageView的数量初始化不同的View布局,还要为每一个View作点击效果
    private void initView() {
        if(MAX_WIDTH == 0){
            //为了触发onMeasure()来测量MultiImageView的最大宽度，MultiImageView的宽设置为match_parent
//            addView(new View(getContext()));
            return;
        }



        createImageView();
    }

    private void createImageView() {

        if(onePicPara!=null) {
            this.setAdjustViewBounds(true);
            this.setScaleType(ScaleType.CENTER_CROP);
            this.setMaxHeight(pxOneMaxWandH);
            this.setLayoutParams(onePicPara);
        }

    }

}
