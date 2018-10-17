package com.cjwsjy.app.phoneReceiver;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.TextView;

import com.cjwsjy.app.SmApplication;
import com.do1.cjmobileoa.db.DBManager;
import com.do1.cjmobileoa.db.model.DepartmentEmployeeVO;
import com.do1.cjmobileoa.db.model.DepartmentVO;
import com.do1.cjmobileoa.db.model.EmployeeVO;

import java.util.List;

/**
 * 时间转换类工具
 * 
 */
public class TelListener extends PhoneStateListener
{
	private String m_Number;
	private TipViewController mTipViewController;
	private Context m_context;
	private WindowManager wm;
	private TextView tv;

	public TelListener(Context context){
		this.m_context = context;
	}

	@Override
	public void onCallStateChanged(int state, String incomingNumber)
	{
		// TODO Auto-generated method stub
		super.onCallStateChanged(state, incomingNumber);


		if(state == TelephonyManager.CALL_STATE_RINGING)
		{
			android.util.Log.i("cjwsjy", "------Number="+incomingNumber+"-------");

			//来电
			incomingNumber = "775859";

			m_Number = incomingNumber;
			Thread thread = new Thread(new ThreadDonwImg());
			thread.start();

			//得到来电号码的信息
			//strinfo = GetPhoneInfo(incomingNumber);

			//leng = strinfo.length();
			//if(leng==0) return;

			//showview(strinfo);

			//CharSequence charstr;
			//charstr = incomingNumber;
			//showview2(charstr);
		}
		else if(state == TelephonyManager.CALL_STATE_IDLE)
		{
			//挂断
			android.util.Log.i("cjwsjy", "--------IDLE-------TelListener");
			if(wm != null)
			{
				android.util.Log.i("cjwsjy", "--------removeView-------TelListener");

				wm.removeView(tv);
			}

//			if (mTipViewController != null)
//			{
//				mTipViewController.setViewDismissHandler(null);
//				mTipViewController = null;
//			}
		}
		else if( state==TelephonyManager.CALL_STATE_OFFHOOK )
		{
			//接听
			android.util.Log.i("cjwsjy", "--------OFFHOOK-------TelListener");
		}
	}

	class ThreadDonwImg implements Runnable
	{
		@Override
		public void run()
		{
			int leng;
			String strinfo;
			Message msg;

			strinfo = GetPhoneInfo(m_Number);

			leng = strinfo.length();
			if(leng==0) return;

			msg = handler.obtainMessage();
			msg.what = 1;
			msg.obj = strinfo;
			handler.sendMessage(msg);
		}
	}

	Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			this.obtainMessage();

			String strinfo;

			switch (msg.what)
			{
				case 0:
					break;
				case 1:
					strinfo = msg.obj.toString();
					showview(strinfo);
					break;
				default:
					break;
			}
		}
	};

	private int showview(String incomingNumber)
	{
		Context mcontext = m_context.getApplicationContext();
		wm = (WindowManager)mcontext.getSystemService(Context.WINDOW_SERVICE);

		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		//params.type = WindowManager.LayoutParams.TYPE_TOAST;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		//params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = 300;

		tv = new TextView(mcontext);
		tv.setMaxLines(3);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setTextColor(Color.parseColor("#ED1C24"));
		tv.setText(incomingNumber);
		tv.setBackgroundColor(Color.parseColor("#EFE4B0"));
		wm.addView(tv, params);

		return 1;
	}

	private int showview2(CharSequence contenttext)
	{
		mTipViewController = new TipViewController(m_context.getApplicationContext(), contenttext);
		//mTipViewController.setViewDismissHandler(this);
		mTipViewController.show();

		return 1;
	}

	private String GetPhoneInfo(String incomingNumber)
	{
		int i = 0;
		int size = 0;
		String strtext;
		String displayname;
		String userid;
		String orgid;
		String department;
		String organization;
		DBManager db;
		EmployeeVO datauser;
		List<EmployeeVO> list_employee;

		strtext = "";
		db = SmApplication.dbManager;
		list_employee = db.getEmployeeByPhone(incomingNumber);

		size = list_employee.size();
		android.util.Log.i("cjwsjy", "-------size="+size+"------TelListener");

		if(size==0) return strtext;

		//遍历
		for (EmployeeVO entity1 : list_employee)
		{
			//用户名称
			displayname = entity1.getUserDisplayName();

			//用户id
			userid = entity1.getUserid();

			//部门id
			DepartmentEmployeeVO orgsVO = db.findOrgsbyUserid(userid);
			orgid = orgsVO.getorgRID();

			//机构
			DepartmentVO deptVO = db.findDepartmentbyid(orgid);
			deptVO = db.findOrganizationbyid(deptVO.getDeptParentid());
			organization = deptVO.getDeptDisplayname();

			//部门名称
			department = deptVO.getDeptDisplayname();

			strtext = strtext+organization+"-"+department+"-"+displayname+"\r\n";
		}

		strtext = strtext.substring(0,strtext.length()-2);

		//Toast.makeText(m_context.getApplicationContext(), strtext, Toast.LENGTH_LONG).show();
		//Toast.makeText(m_context.getApplicationContext(), strtext, Toast.LENGTH_LONG).show();

		return strtext;
	}

}
