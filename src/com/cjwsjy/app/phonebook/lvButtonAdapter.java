package com.cjwsjy.app.phonebook;

import java.util.ArrayList;
import java.util.HashMap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cjwsjy.app.R;
import com.cjwsjy.app.utils.StringHelper;
import com.do1.cjmobileoa.db.DBManager;

public class lvButtonAdapter extends BaseAdapter 
{
	private class ViewItems {
		// ImageView appIcon;
		TextView nameText;
		TextView telText;
		ImageButton telBtn;
		ImageButton smsBtn;
		ImageButton mailBtn;
		ImageButton deleteBtn;
	}

	private ArrayList<HashMap<String, Object>> mAppList;
	private LayoutInflater mInflater;
	private Context mContext;

	public lvButtonAdapter(Context c, ArrayList<HashMap<String, Object>> appList) {
		mAppList = appList;
		mContext = c;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mAppList.size();
	}

	@Override
	public Object getItem(int position) {
		return mAppList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void removeItem(int position) {
		mAppList.remove(position);
		this.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewItems viewItems = null;

		HashMap<String, Object> appInfo = mAppList.get(position);
		String type = (String) appInfo.get("type");

		if (convertView != null) 
		{
			viewItems = (ViewItems) convertView.getTag();
		} 
		else 
		{
			// 根据type来决定该调用哪个listview_item
			if (StringHelper.equals(type, "department")) 
			{
				// 部门数据
				convertView = mInflater.inflate(R.layout.dept_listview_item,parent, false);
				viewItems = new ViewItems();
				viewItems.nameText = (TextView) convertView.findViewById(R.id.ItemText);
				convertView.setTag(viewItems);
			}
			else if (StringHelper.equals(type, "employee")) 
			{
				// 员工数据
				convertView = mInflater.inflate(R.layout.emp_listview_item, parent, false);

				viewItems = new ViewItems();
				viewItems.telBtn = (ImageButton) convertView.findViewById(R.id.item_image_tel);
				viewItems.smsBtn = (ImageButton) convertView.findViewById(R.id.item_image_sms);
				viewItems.mailBtn = (ImageButton) convertView.findViewById(R.id.item_image_mail);
				viewItems.nameText = (TextView) convertView.findViewById(R.id.empItemTextName);
				viewItems.telText = (TextView) convertView.findViewById(R.id.empItemTextTel);

				viewItems.deleteBtn = (ImageButton) convertView.findViewById(R.id.item_image_del);

				convertView.setTag(viewItems);
			}
		}

		if (appInfo != null)
		{
			String userName = (String) appInfo.get("userName");

			if (StringHelper.equals(type, "department"))
			{
				if (viewItems.telText != null)
				{
					convertView = mInflater.inflate(R.layout.dept_listview_item, parent, false);
					viewItems = new ViewItems();
					viewItems.nameText = (TextView) convertView.findViewById(R.id.ItemText);
					convertView.setTag(viewItems);
				}
				// 部门数据
				viewItems.nameText.setText(userName);
			} 
			else if (StringHelper.equals(type, "employee")) 
			{
				// 员工数据
				if (viewItems.telText == null) 
				{
					convertView = mInflater.inflate(R.layout.emp_listview_item, parent, false);

					viewItems = new ViewItems();
					viewItems.telBtn = (ImageButton) convertView.findViewById(R.id.item_image_tel);
					viewItems.smsBtn = (ImageButton) convertView.findViewById(R.id.item_image_sms);
					viewItems.mailBtn = (ImageButton) convertView.findViewById(R.id.item_image_mail);
					viewItems.nameText = (TextView) convertView.findViewById(R.id.empItemTextName);
					viewItems.telText = (TextView) convertView.findViewById(R.id.empItemTextTel);
					viewItems.deleteBtn = (ImageButton) convertView.findViewById(R.id.item_image_del);

					convertView.setTag(viewItems);
				}

				String tel = (String) appInfo.get("tel");

				viewItems.nameText.setText(userName);
				viewItems.telText.setText(tel);

				viewItems.mailBtn.setOnClickListener(new lvButtonListener(
						position, 0, convertView));
				viewItems.smsBtn.setOnClickListener(new lvButtonListener(
						position, 0, convertView));

				viewItems.telBtn.setOnClickListener(new lvButtonListener(
						position, 0, convertView));

				viewItems.deleteBtn.setOnClickListener(new lvButtonListener(
						position, 0, convertView));

				if (appInfo.get("deleteShow") != null) {
					int deleteShow = Integer.valueOf(String.valueOf(appInfo
							.get("deleteShow")));
					switch (deleteShow) {
					case 0:
						viewItems.deleteBtn.setVisibility(View.VISIBLE);
						viewItems.mailBtn.setVisibility(View.GONE);
						viewItems.smsBtn.setVisibility(View.GONE);
						viewItems.telBtn.setVisibility(View.GONE);
						break;
					case 1:
						viewItems.deleteBtn.setVisibility(View.GONE);
						viewItems.mailBtn.setVisibility(View.VISIBLE);
						viewItems.smsBtn.setVisibility(View.VISIBLE);
						viewItems.telBtn.setVisibility(View.VISIBLE);
						break;
					}
				}
			}

		}
		return convertView;
	}

	class lvButtonListener implements OnClickListener {
		private int position;
		private int listenerType;
		private View selcetView;
		private String deleteType;

		lvButtonListener(int pos, int type, View convertView) {
			position = pos;
			listenerType = type;
			selcetView = convertView;
			this.deleteType = deleteType;
		}

		@Override
		public void onClick(View v) {
			int vid = v.getId();

			HashMap<String, Object> appInfo = mAppList.get(position);

			switch (listenerType) 
			{
			case 0:
				if (vid == R.id.item_image_tel) 
				{
					if( Build.VERSION.SDK_INT>=23 )
					{
						int Permission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE);
						if( Permission!=PackageManager.PERMISSION_GRANTED )
						{
							Toast.makeText(mContext, "没有权限，不能打电话", Toast.LENGTH_SHORT) .show();
							break;
						}
					}
					
					String phone = "tel:" + (String) appInfo.get("tel");
					Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse(phone));
					mContext.startActivity(intent);
				}

				if (vid == R.id.item_image_sms) {
					String phone = "smsto:" + (String) appInfo.get("sms");
					Uri smsToUri = Uri.parse(phone);
					Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
					intent.putExtra("sms_body", "");
					mContext.startActivity(intent);
				}

				if (vid == R.id.item_image_mail) {
					String mail = "mailto:" + (String) appInfo.get("mail");
					Intent it = new Intent(Intent.ACTION_SEND);
					it.putExtra(Intent.EXTRA_EMAIL, mail);
					it.putExtra(Intent.EXTRA_TEXT, "The email body text");
					it.setType("text/plain");
					mContext.startActivity(Intent.createChooser(it,
							"Choose Email Client"));
				}

				String deleteType = (String) appInfo.get("deleteType");
				String userid = (String) appInfo.get("userid");
				DBManager db = new DBManager(mContext);
				if (!StringHelper.isEmpty(deleteType)
						&& StringHelper.equals("history", deleteType)) {

					db.delHistoryForUserid(userid);
					v.setVisibility(View.GONE);
					mAppList.remove(position);
					lvButtonAdapter.this.notifyDataSetChanged();
					Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
				} else if (!StringHelper.isEmpty(deleteType)
						&& StringHelper.equals("collect", deleteType)) {

					db.delCollectForUserid(userid);
					v.setVisibility(View.GONE);
					mAppList.remove(position);
					lvButtonAdapter.this.notifyDataSetChanged();
					Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
				}

				break;

			}

		}

	}

}