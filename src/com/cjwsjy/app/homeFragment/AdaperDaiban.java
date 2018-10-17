package com.cjwsjy.app.homeFragment;

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

public class AdaperDaiban extends BaseAdapter 
{
	private List<ItemDaiban> listItems =new ArrayList<ItemDaiban>();
	private LayoutInflater inflater;

	public AdaperDaiban(Context context, List<ItemDaiban> list)
	{
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
		if(convertView==null)
		{
			convertView=inflater.inflate(R.layout.listitem_daiban, parent, false);
			holder=new ViewHolder();
			holder.tv_title=(TextView) convertView.findViewById(R.id.et_text1);
			holder.tv_date=(TextView) convertView.findViewById(R.id.tv_count1);
			
			convertView.setTag(holder);	
		}
		else
		{
			holder=(ViewHolder) convertView.getTag();
		}

		ItemDaiban listItem=listItems.get(position);
		holder.tv_title.setText(listItem.getTv_title());
		holder.tv_date.setText(listItem.getTv_date());
		
		return convertView;
	}
	private class ViewHolder {
		ImageView iv_icon1;
		TextView tv_title;
		TextView tv_state;
		TextView tv_date;
		ImageView iv_icon2;
		TextView tv_formid;
	}
}
