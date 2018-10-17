package com.cjwsjy.app.plate;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.cjwsjy.app.R;
import com.cjwsjy.app.webview.WebViewHome;
import com.sqk.GridView.Grid_Item;
import com.sqk.GridView.NewGridAdaper;

public class NewListAdaper extends BaseAdapter 
{
	private List<Grid_Item1> Grid_Items;
	private LayoutInflater inflater;
	
	public NewListAdaper(Context context,List<Grid_Item1> menus) 
	{
	   inflater=LayoutInflater.from(context);
	   this.Grid_Items=menus;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Grid_Items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return Grid_Items.get(position);
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
		//int width=parent.getWidth()/1;
		//AbsListView.LayoutParams lp =new AbsListView.LayoutParams(width, width);
		ViewHolder holder = null;
		if(convertView==null)
		{
			convertView=inflater.inflate(R.layout.fragment_plate3, parent, false);
			holder=new ViewHolder();
			//holder.image=(ImageView) convertView.findViewById(R.id.icon);		
			holder.title=(TextView) convertView.findViewById(R.id.titleXX);
			holder.grid =(GridView)convertView.findViewById(R.id.listview_item_gridview);  
			convertView.setTag(holder);	
		}
		else 
		{
			holder=(ViewHolder) convertView.getTag();
		}
		Grid_Item1 item=Grid_Items.get(position);
		holder.title.setText(item.Item_title);
		holder.grid.setAdapter(item.gridViewAdapter);
		
		//2行时，改变gridview的高度
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)holder.grid.getLayoutParams();
		int size = holder.grid.getCount();
		if(size>3 && size<7)
		{
			lp.height = 740;
			holder.grid.setLayoutParams(lp);
		}
		else if(size>7)
		{
			lp.height = 1140;
			holder.grid.setLayoutParams(lp);
		}

		holder.grid.setOnItemClickListener(gridViewListener);
		
		return convertView;
	}
	
    private  OnItemClickListener  gridViewListener = new OnItemClickListener() 
    {
        @Override
        public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3)
        {
        	if( arg3==0)  //我要出差
            {

            }
        	else if( arg3==1)  //审批待办
            {	

            }
        }
    };
        
	private class ViewHolder {
		//ImageView image;
		TextView title;
		GridView grid;
	}
}
