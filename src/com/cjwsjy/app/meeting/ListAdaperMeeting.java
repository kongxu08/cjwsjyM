package com.cjwsjy.app.meeting;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjwsjy.app.R;
import com.sqk.GridView.List_Item;

public class ListAdaperMeeting extends BaseAdapter 
{
	private List<ListItem> listItems;
	private LayoutInflater inflater;  
	
	public ListAdaperMeeting(Context context, List<ListItem> list)
	{
		inflater = LayoutInflater.from(context);
		this.listItems = list;
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return listItems.size();
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null)
		{
			convertView = inflater.inflate(R.layout.meeting_listitem, parent, false);
			holder = new ViewHolder();
			//holder.iv_tip = (View) convertView.findViewById(R.id.iv_tip);
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_source);
			holder.tv_source = (TextView) convertView.findViewById(R.id.tv_source2);
			//holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);

			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		ListItem listItem = listItems.get(position);
		
		holder.tv_title.setText(listItem.getTv_title());
		holder.tv_source.setText(listItem.getTv_state());
		//holder.tv_time.setText(listItem.getTv_time());

		return convertView;
	}

	private class ViewHolder
	{
		//ImageView iv_tip;
		View iv_tip;
		TextView tv_title;
		TextView tv_description;
		TextView tv_source;
		TextView tv_time;
		TextView tv_comment_count;

	}
}
