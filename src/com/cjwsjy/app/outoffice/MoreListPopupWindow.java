package com.cjwsjy.app.outoffice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.utils.ValidUtil;
import com.cjwsjy.app.webview.WebViewHome;
import com.do1.cjmobileoa.db.model.EmployeeVO;

/**
 * 自定义的弹出窗口,主要用于选择手机号码
 * 
 */
public class MoreListPopupWindow extends PopupWindow implements OnClickListener {
	private SharedPreferences sp;
	
	int showView = R.layout.outofoffice_more_view;
	private View mMenuView;
	private Context mContext;
	private int sourceType;

	public static int SOURCE_SMS = 0;
	public static int SOURCE_TEL = 1;

	String loginname;
	String userId;
	String appUrl;
	String url;
	
	RelativeLayout tv_regiest;//我要出差
	RelativeLayout tv_outofoffice;//审批待办
	RelativeLayout tv_search;//在岗查询
	RelativeLayout tv_history;//登记历史
	RelativeLayout tv_searchUser;//查找员工
	
	public MoreListPopupWindow(Activity context, int showView, int source) 
	{
		super(context);

		mContext = context;
		sourceType = source;
		
		//获取用户登录名
		 sp = SmApplication.sp;
		 loginname =sp.getString("USERDATA.LOGIN.NAME", "");
		 userId =sp.getString("USERDATA.USER.ID", "");
		 appUrl = UrlUtil.HOST;

		if (showView != 0) 
		{
			this.showView = showView;
		}

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(this.showView, null);
		
		tv_regiest =(RelativeLayout) mMenuView.findViewById(R.id.tv_regiest);
		tv_outofoffice =(RelativeLayout) mMenuView.findViewById(R.id.tv_outofoffice);
		tv_search =(RelativeLayout) mMenuView.findViewById(R.id.tv_search);
		tv_history =(RelativeLayout) mMenuView.findViewById(R.id.tv_history);
		tv_searchUser =(RelativeLayout) mMenuView.findViewById(R.id.tv_searchUser);
		
		mMenuView.findViewById(R.id.tv_cancle).setOnClickListener(this);
		
		tv_regiest.setOnClickListener(this);
		tv_outofoffice.setOnClickListener(this);
		tv_search.setOnClickListener(this);
		tv_history.setOnClickListener(this);
		tv_searchUser.setOnClickListener(this);

		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimBottom);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// 在输入法退出后重新编排布局
		this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		this.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mMenuView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int height = mMenuView.findViewById(R.id.ll_pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub		
		switch (v.getId()) {

		case R.id.tv_regiest:
			//我要出差
			//url=""+appUrl+"/OutWeb/registerOutOfOffice?userName="+loginname;
			url=appUrl+"/OutWeb/wepRegisterOutOfOffice/"+userId;
			Intent intent = new Intent();           	
			intent.setClass(mContext, WebViewHome.class);
			intent.putExtra("webUrl",url);
			intent.putExtra("titleName","出差审批登记"); 
			dismiss();
			mContext.startActivity(intent);
			break;
		case R.id.tv_outofoffice:
			//审批待办
			//url=""+appUrl+"/OutWeb/gTasks?userName="+loginname;
			url=appUrl+"/OutWeb/wapgTasks/"+userId;
			intent = new Intent();           	
			intent.setClass(mContext, WebViewHome.class);
			intent.putExtra("webUrl",url);
			intent.putExtra("titleName","出差审批登记"); 
			dismiss();
			mContext.startActivity(intent);
			break;
		case R.id.tv_search:
			//在岗查询
			url=appUrl+"/OutWeb/wapqueryAttentions/"+userId;
			intent = new Intent();           	
			intent.setClass(mContext, WebViewHome.class);
			intent.putExtra("titleName","出差审批登记"); 
			intent.putExtra("webUrl",url);
			dismiss();
			mContext.startActivity(intent);
			break;
		case R.id.tv_history:
			//登记历史
			url=""+appUrl+"/OutWeb/wapqueryout/"+userId;
			intent = new Intent();           	
			intent.setClass(mContext, WebViewHome.class);
			intent.putExtra("webUrl",url);
			intent.putExtra("titleName","出差审批登记"); 
			dismiss();
			mContext.startActivity(intent);
			break;
		case R.id.tv_searchUser:
			//查找员工
			url=""+appUrl+"/OutWeb/wapgetAllCompany/"+userId;
			intent = new Intent();           	
			intent.setClass(mContext, WebViewHome.class);
			intent.putExtra("webUrl",url);
			intent.putExtra("titleName","出差审批登记"); 
			dismiss();
			mContext.startActivity(intent);
			break;	
			
		case R.id.tv_cancle:
			dismiss();
			break;
		}

	}

	public String findViewTextById(int id) {
		TextView view = (TextView) mMenuView.findViewById(id);
		String text = view.getText().toString();
		return text;
	}

	public void setViewTextById(int id, String text) {
		((TextView) mMenuView.findViewById(id)).setText(text);
		;
	}

	public void setViewBgById(int id, int resourceId) {
		mMenuView.findViewById(id).setBackgroundDrawable(
				mContext.getResources().getDrawable(resourceId));
	}

}
