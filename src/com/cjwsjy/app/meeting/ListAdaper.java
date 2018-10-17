package com.cjwsjy.app.meeting;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjwsjy.app.R;
import com.cjwsjy.app.item.FinishPeddingItem;

public class ListAdaper extends BaseAdapter 
{
	private List<FinishPeddingItem> listItems =new ArrayList<FinishPeddingItem>();
	private LayoutInflater inflater;  
	
	public ListAdaper(Context context,List<FinishPeddingItem> list) 
	{
	   inflater = LayoutInflater.from(context);
	   this.listItems = list;
	}

	@Override
	public int getCount() 
	{
		// TODO Auto-generated method stub
		int items = 0;
		if(listItems!=null)
		{
			items = listItems.size();
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
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView==null)
		{
			convertView=inflater.inflate(R.layout.finishpeddinglist_item, parent, false);
			holder=new ViewHolder();
			holder.iv_icon1=(ImageView) convertView.findViewById(R.id.iv_icon1);
			holder.tv_title=(TextView) convertView.findViewById(R.id.tv_title);
			holder.tv_state=(TextView) convertView.findViewById(R.id.tv_state);
			holder.tv_date=(TextView) convertView.findViewById(R.id.tv_date);
			holder.iv_icon2=(ImageView) convertView.findViewById(R.id.iv_icon2);
			holder.tv_formid=(TextView) convertView.findViewById(R.id.tv_formid);
			
			convertView.setTag(holder);	
		}else {
			holder=(ViewHolder) convertView.getTag();
		}
		FinishPeddingItem listItem=listItems.get(position);
		holder.iv_icon1.setImageResource(listItem.getIv_icon1());
		holder.tv_title.setText(listItem.getTv_title());
		//holder.tv_state.setText(listItem.getTv_state());
		holder.tv_date.setText(listItem.getTv_date());
		holder.iv_icon2.setImageResource(listItem.getIv_icon2());
		holder.tv_formid.setText(listItem.getTv_formid());
		
		//Resources r =inflater.getContext().getResources();
		if("进行中".equals(listItem.getTv_state().trim())){
			holder.tv_state.setBackgroundResource(R.drawable.state_icon3);
		}else if("".equals(listItem.getTv_state().trim())){
			holder.tv_state.setBackgroundResource(R.drawable.index_blank);
		}else{
			holder.tv_state.setBackgroundResource(R.drawable.state_icon1);
		}
		
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
