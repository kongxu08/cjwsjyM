package com.cjwsjy.app.plate;

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

public class GridAdaperPlate extends BaseAdapter 
{
	private List<Grid_Item> Grid_Items;
	private LayoutInflater inflater;
	
	public GridAdaperPlate(Context context,List<Grid_Item> menus) 
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		int width=parent.getWidth()/3;
		AbsListView.LayoutParams lp =new AbsListView.LayoutParams(width, width);
		ViewHolder holder = null;
		if(convertView==null)
		{
			convertView=inflater.inflate(R.layout.grid_item, parent, false);
			holder=new ViewHolder();
			holder.image=(ImageView) convertView.findViewById(R.id.icon);
			holder.title=(TextView) convertView.findViewById(R.id.text);
			convertView.setTag(holder);	
		}
		else {
			holder=(ViewHolder) convertView.getTag();
		}
		
		int size = Grid_Items.size();
		
		Grid_Item item=Grid_Items.get(position);
		holder.image.setImageResource(item.imageID);
		holder.title.setText(item.Item_title);
		
		convertView.setLayoutParams(lp);
		
		return convertView;
	}
	private class ViewHolder {
		ImageView image;
		TextView title;
	}
}
