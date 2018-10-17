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
import com.cjwsjy.app.item.DeptSignItem;

public class DeptSignListAdaper extends BaseAdapter {
	private List<DeptSignItem> listItems =new ArrayList<DeptSignItem>();
	private LayoutInflater inflater;  
	
	public DeptSignListAdaper(Context context,List<DeptSignItem> list) {
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
			convertView=inflater.inflate(R.layout.peddeptlist_item, parent, false);
			holder=new ViewHolder();
			holder.dept_description=(TextView) convertView.findViewById(R.id.dept_description);
			holder.dept_signName=(ImageView) convertView.findViewById(R.id.dept_signName);
			holder.dept_date=(TextView) convertView.findViewById(R.id.dept_date);
			
			convertView.setTag(holder);	
		}else {
			holder=(ViewHolder) convertView.getTag();
		}
		DeptSignItem listItem=listItems.get(position);
		holder.dept_description.setText(listItem.getDept_description());
		//holder.con_signName.setImageResource(listItem.getCon_signName());	
		holder.dept_date.setText(listItem.getDept_date());

		holder.dept_signName.setImageBitmap(listItem.getImageBitmap());
		
		return convertView;
	}
	private class ViewHolder {
		TextView dept_description;
		ImageView dept_signName;
		TextView dept_date;
	}
}
