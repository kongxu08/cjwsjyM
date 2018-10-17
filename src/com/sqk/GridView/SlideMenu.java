package com.sqk.GridView;

import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
/**
 * 自定义侧滑菜单View
 *
 *
 */
public class SlideMenu extends HorizontalScrollView{

    private LinearLayout mWapper;
    private ViewGroup mMenu;
    private ViewGroup mContent;
    private int mScreenWidth;
    private int mMenuRightPadding;
    private int mMenuLeftPadding;
    private int mMenuWidth = 0;
    //菜单滑出宽度
    private int menuWidth=0;
    
    private boolean once = false;
    //Menu是否处于显示状态
    private boolean isSlideOut;

    public static final int RIGHT_PADDING = 100;
    
    //距左150
    public static final int Left_PADDING = 0;
    
    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        //将dp转化为px
        mMenuRightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SlideMenu.RIGHT_PADDING, context.getResources().getDisplayMetrics());
        
        mMenuLeftPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SlideMenu.Left_PADDING, context.getResources().getDisplayMetrics());
        
    }

    /**
     * 设置子View的宽和高
     * 设置自己的宽和高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        if(!once){
            mWapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) mWapper.getChildAt(0);
            mContent = (ViewGroup) mWapper.getChildAt(1);
            
            menuWidth = (int) (0.7 * mScreenWidth);
            
            //mMenuWidth  = mMenu.getLayoutParams().width = mScreenWidth - mMenuRightPadding;
            mMenuWidth  = mMenu.getLayoutParams().width =mScreenWidth - mMenuLeftPadding;
            mContent.getLayoutParams().width = mScreenWidth;
            once = true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    /**
     * 通过设置偏移量将Menu隐藏
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        super.onLayout(changed, l, t, r, b);
        if(changed){
        	this.scrollTo(0, 0);
        }
    }
 /*   @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                //隐藏在右边宽度
                int scrollX = getScrollX();
                if(scrollX >= mMenuWidth /2){
                    //Menu 有滑隐藏起来
                    //this.smoothScrollTo(mMenuWidth, 0);
                	this.smoothScrollTo(0, 0);
                    isSlideOut = false;
                }else{
                    //Menu 左滑 显示出来
                    this.smoothScrollTo(menuWidth, 0);
                    isSlideOut = true;
                }
                return true;
        }
        return super.onTouchEvent(ev);
    } */
    /**
     * 向左滑出菜单显示出来
     */
    public void slideOutMenu(){
        if(!isSlideOut){
            //this.smoothScrollTo(0, 0);
        	this.smoothScrollTo(menuWidth, 0);
            isSlideOut = true;
        }else{
            return;
        }
     }  
    /**
     * 向右滑进菜单隐藏起来 
     */
    public void slideInMenu(){
        if(isSlideOut){
           // this.smoothScrollTo(mMenuWidth, 0);
        	this.smoothScrollTo(0, 0);
            isSlideOut = false;
        }else{
            return;
        }
    }
    /**
     * 切换菜单向左滑出显示或向右滑进隐藏的状态
     */
    public void switchMenu(){
        if(isSlideOut){
            slideInMenu();
        }else{
            slideOutMenu();
        }
    }
    /**
     * 滚动发生时
     */
 /*   @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        // TODO Auto-generated method stub
        super.onScrollChanged(l, t, oldl, oldt);
        //实现抽屉式滑动
        float scale = l * 1.0f /mMenuWidth ;//1 ~ 0
        float menuScale = 1.0f - scale * 0.3f;
        float menuAlpha = 0.0f + 1.0f * (1 - scale);
        float contentScale = 0.8f + 0.2f * scale;
        //调用属性动画，设置TranslationX
        ViewHelper.setTranslationX(mMenu, 0.8f);
        
        //右侧侧菜单的缩放
        ViewHelper.setScaleX(mMenu, menuScale);
        ViewHelper.setScaleY(mMenu, menuScale);
        //左侧内容的透明度缩放
        ViewHelper.setAlpha(mContent, menuAlpha);
        
        //左侧侧内容的缩放
        ViewHelper.setPivotX(mContent, 0);
        ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
        ViewHelper.setScaleY(mContent, contentScale);
        ViewHelper.setScaleX(mContent, contentScale);
       
    }*/
    
}
