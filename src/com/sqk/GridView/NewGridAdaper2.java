package com.sqk.GridView;

import java.util.List;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cjwsjy.app.R;
import com.sqk.GridView.Grid_Item;

public class NewGridAdaper2 extends BaseAdapter 
{
	private int m_PixelsX;
	private List<Grid_Item> Grid_Items;
	private LayoutInflater inflater;
	
	public NewGridAdaper2(Context context,List<Grid_Item> menus, int widthPixels) 
	{
		inflater=LayoutInflater.from(context);
		m_PixelsX = widthPixels;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		int width=parent.getWidth()/3;
		//AbsListView.LayoutParams lp =new AbsListView.LayoutParams(width, width);
		ViewHolder holder = null;
		if(convertView==null)
		{
			convertView=inflater.inflate(R.layout.grid_item2, parent, false);
			//convertView.setLayoutParams(new GridView.LayoutParams(80, 50));//重点行
			
			holder=new ViewHolder();
			holder.image=(ImageView) convertView.findViewById(R.id.icon);
			holder.title=(TextView) convertView.findViewById(R.id.text);
			convertView.setTag(holder);
		}
		else 
		{
			holder=(ViewHolder) convertView.getTag();
		}
		Grid_Item item=Grid_Items.get(position);
		holder.image.setImageResource(item.imageID);
		holder.title.setText(item.Item_title);
		//convertView.setLayoutParams(lp);
		
		//根据dpi设置间距
		/*LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		if( m_PixelsX<=800 )
		{
			convertView.setLayoutParams(new AbsListView.LayoutParams(180, 225));
		}
		else if( m_PixelsX<=1200 )
		{
			lp.setMargins(40, 30, 40, 0);
			convertView.setLayoutParams(new AbsListView.LayoutParams(260, 350));
		}
		else if( m_PixelsX<=1440 )
		{
			convertView.setLayoutParams(new AbsListView.LayoutParams(180, 250));
		}*/
		
		//convertView.setLayoutParams(new GridView.LayoutParams(180, 210));//重点行
		return convertView;
	}
	private class ViewHolder {
		ImageView image;
		TextView title;
	}
}
