package com.cjwsjy.app.phonebook;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjwsjy.app.R;
import com.cjwsjy.app.phonebook.Org;

public class OrgListAdaper extends BaseAdapter 
{
	private List<Org> orgList;
	private LayoutInflater inflater;  
	
	public OrgListAdaper(Context context,List<Org> list) 
	{
	   inflater=LayoutInflater.from(context);
	   this.orgList=list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return orgList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return orgList.get(position);
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
			convertView=inflater.inflate(R.layout.org_item, parent, false);
			holder=new ViewHolder();
			holder.iv_goto=(ImageView) convertView.findViewById(R.id.iv_goto);
			holder.org_title=(TextView) convertView.findViewById(R.id.org_title);

			convertView.setTag(holder);	
		}
		else 
		{
			holder=(ViewHolder) convertView.getTag();
		}
		Org org=orgList.get(position);
		holder.iv_goto.setImageResource(org.getIvIcon());
		holder.org_title.setText(org.getTvTitle());
	
		return convertView;
	}
	
	private class ViewHolder {
		ImageView iv_goto;
		TextView org_title;
		
	}
}
