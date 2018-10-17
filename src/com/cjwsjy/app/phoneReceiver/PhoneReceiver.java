package com.cjwsjy.app.phoneReceiver;

import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * 时间转换类工具
 * 
 */
public class PhoneReceiver extends BroadcastReceiver
{
	private Context m_context;

	@Override
	public void onReceive(Context context, Intent intent)
	{
		String straction = intent.getAction();
		android.util.Log.i("cjwsjy", "------straction="+straction+"-------onReceive");

		m_context = context;
		//去电
		if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL))
		{
			String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		}
		else
		{
			//来电
			//android.util.Log.i("cjwsjy", "------straction="+straction+"-------");

			//设置一个监听器
			TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);

			//tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
			tm.listen(new TelListener(context), PhoneStateListener.LISTEN_CALL_STATE);
		}
	}

		PhoneStateListener listener = new PhoneStateListener()
		{

			@Override
			public void onCallStateChanged(int state, String incomingNumber)
			{
				//注意，方法必须写在super方法后面，否则incomingNumber无法获取到值。
				super.onCallStateChanged(state, incomingNumber);
				switch(state)
				{
					case TelephonyManager.CALL_STATE_IDLE:
						android.util.Log.i("cjwsjy", "------挂断-------");
						break;
					case TelephonyManager.CALL_STATE_OFFHOOK:
						android.util.Log.i("cjwsjy", "------接听-------");
						break;
					case TelephonyManager.CALL_STATE_RINGING:
						android.util.Log.i("cjwsjy", "------来电号码="+incomingNumber+"-------");
						/*Toast.makeText(m_context.getApplicationContext(), "来电号码"+incomingNumber, Toast.LENGTH_SHORT).show();
						//输出来电号码

						AlertDialog.Builder builder = new AlertDialog.Builder(m_context.getApplicationContext());
						builder.setTitle("更新");
						builder.setMessage("通讯录有新版本，是否同步？");
						builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								dialog.cancel();
							}
						});

						builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								dialog.cancel();
							}
						});

						builder.create();
						builder.show();*/

						break;
				}
			}
		};

}
