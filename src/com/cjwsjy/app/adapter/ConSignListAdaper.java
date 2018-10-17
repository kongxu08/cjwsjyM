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
import com.cjwsjy.app.item.ConSignItem;

public class ConSignListAdaper extends BaseAdapter {
	private List<ConSignItem> listItems =new ArrayList<ConSignItem>();
	private LayoutInflater inflater;  
	
	public ConSignListAdaper(Context context,List<ConSignItem> list) {
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
			convertView=inflater.inflate(R.layout.pedconsignlist_item, parent, false);
			holder=new ViewHolder();
			holder.con_description=(TextView) convertView.findViewById(R.id.con_description);
			holder.con_signName=(ImageView) convertView.findViewById(R.id.con_signName);
			holder.con_date=(TextView) convertView.findViewById(R.id.con_date);
			
			convertView.setTag(holder);	
		}else {
			holder=(ViewHolder) convertView.getTag();
		}
		ConSignItem listItem=listItems.get(position);
		holder.con_description.setText(listItem.getCon_description());
		//holder.con_signName.setImageResource(listItem.getCon_signName());	
		holder.con_date.setText(listItem.getCon_date());

		holder.con_signName.setImageBitmap(listItem.getImageBitmap());
		
		return convertView;
	}
	private class ViewHolder {
		TextView con_description;
		ImageView con_signName;
		TextView con_date;
	}
}
