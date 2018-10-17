package com.sqk.GridView;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjwsjy.app.R;
import com.sqk.GridView.Grid_Item;
public class NewListAdaper extends BaseAdapter {
	private List<List_Item> listItems;
	private LayoutInflater inflater;  
	
	public NewListAdaper(Context context,List<List_Item> list) {
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
			convertView=inflater.inflate(R.layout.news_item, parent, false);
			holder=new ViewHolder();
			holder.iv_tip=(ImageView) convertView.findViewById(R.id.iv_tip);
			holder.tv_title=(TextView) convertView.findViewById(R.id.tv_title);
			holder.tv_description=(TextView) convertView.findViewById(R.id.tv_description);
			holder.tv_source=(TextView) convertView.findViewById(R.id.tv_source);
			holder.tv_time=(TextView) convertView.findViewById(R.id.tv_time);
			//holder.tv_comment_count=(TextView) convertView.findViewById(R.id.tv_comment_count);
			convertView.setTag(holder);	
		}
		else {
			holder=(ViewHolder) convertView.getTag();
		}
		List_Item listItem=listItems.get(position);
		holder.iv_tip.setImageResource(listItem.getIv_tip());
		holder.tv_title.setText(listItem.getTv_title());
		holder.tv_description.setText(listItem.getTv_description());
		holder.tv_source.setText(listItem.getTv_source());
		holder.tv_time.setText(listItem.getTv_time());
		holder.tv_comment_count.setText(listItem.getTv_comment_count());
	
		return convertView;
	}
	private class ViewHolder {
		ImageView iv_tip;
		TextView tv_title;
		TextView tv_description;
		TextView tv_source;
		TextView tv_time;
		TextView tv_comment_count;
		
	}
}
