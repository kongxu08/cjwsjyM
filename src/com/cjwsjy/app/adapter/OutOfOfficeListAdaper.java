package com.cjwsjy.app.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjwsjy.app.R;
import com.cjwsjy.app.item.OutOfOfficeItem;
import com.sqk.GridView.Grid_Item;
public class OutOfOfficeListAdaper extends BaseAdapter {
	private List<OutOfOfficeItem> listItems;
	private LayoutInflater inflater;  
	
	public OutOfOfficeListAdaper(Context context,List<OutOfOfficeItem> list) {
	   inflater=LayoutInflater.from(context);
	   this.listItems=list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
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
			convertView=inflater.inflate(R.layout.register_item, parent, false);
			holder=new ViewHolder();
			holder.tv_outTimeFrom=(TextView) convertView.findViewById(R.id.tv_outTimeFrom);
			holder.tv_address=(TextView) convertView.findViewById(R.id.tv_address);
			holder.tv_event=(TextView) convertView.findViewById(R.id.tv_event);
			holder.iv_modify=(ImageView) convertView.findViewById(R.id.iv_modify);

			convertView.setTag(holder);	
		}else {
			holder=(ViewHolder) convertView.getTag();
		}
		OutOfOfficeItem listItem=listItems.get(position);
		holder.tv_outTimeFrom.setText(listItem.getTv_outTimeFrom());
		holder.tv_address.setText(listItem.getTv_address());
		holder.tv_event.setText(listItem.getTv_event());
		holder.iv_modify.setImageResource(listItem.getIv_modify());
	
		return convertView;
	}
	private class ViewHolder {
		TextView tv_outTimeFrom;
		TextView tv_address;
		TextView tv_event;
		ImageView iv_modify;
	}
}
