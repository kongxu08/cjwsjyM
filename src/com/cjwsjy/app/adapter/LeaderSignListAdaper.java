package com.cjwsjy.app.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjwsjy.app.R;
import com.cjwsjy.app.item.LeaderSignItem;

public class LeaderSignListAdaper extends BaseAdapter {
	private List<LeaderSignItem> listItems =new ArrayList<LeaderSignItem>();
	private LayoutInflater inflater;  
	
	public LeaderSignListAdaper(Context context,List<LeaderSignItem> list) {
	   inflater=LayoutInflater.from(context);
	   this.listItems=list;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int items = 0;
		if(listItems!=null){
			items =listItems.size();
		}
		return items;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView==null){
			convertView=inflater.inflate(R.layout.pedleadershiplist_item, parent, false);
			holder=new ViewHolder();
			holder.tv_description=(TextView) convertView.findViewById(R.id.tv_description);
			holder.iv_signName=(ImageView) convertView.findViewById(R.id.iv_signName);
			holder.tv_date=(TextView) convertView.findViewById(R.id.tv_date);
			
			convertView.setTag(holder);	
		}else {
			holder=(ViewHolder) convertView.getTag();
		}
		LeaderSignItem listItem=listItems.get(position);
		holder.tv_description.setText(listItem.getTv_description());
		//holder.con_signName.setImageResource(listItem.getCon_signName());	
		holder.tv_date.setText(listItem.getTv_date());

		holder.iv_signName.setImageBitmap(listItem.getImageBitmap());
		
		return convertView;
	}
	private class ViewHolder {
		TextView tv_description;
		ImageView iv_signName;
		TextView tv_date;
	}
}
