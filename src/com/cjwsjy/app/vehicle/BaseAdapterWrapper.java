package com.cjwsjy.app.vehicle;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * BaseAdapter类的包装类，给BaseAdapter提供额外的功能，如加载ItemView后进行监听绑定等
 * @Copyright:   Copyright (c)2013  
 * @Company:     广州市道一信息有限公司
 * @author:      Mr.y  
 * @version:     1.0  
 * @date:        2013-10-15 下午3:26:33  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2013-10-15      Mr.y          1.0       1.0 Version
 */
public class BaseAdapterWrapper extends BaseAdapter{
	public BaseAdapter mAdapter;
	private ItemViewHandler mItemViewHandler;
	 
	public BaseAdapterWrapper(BaseAdapter adapter){
		this(adapter, null);
	}
	
	public BaseAdapterWrapper(BaseAdapter adapter, ItemViewHandler itemViewHandler){
		this.mAdapter = adapter;
		this.mItemViewHandler = itemViewHandler;
	}
	
	@Override
	public int getCount() {
		return mAdapter.getCount();
	}

	@Override
	public Object getItem(int position) {
		return mAdapter.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return mAdapter.getItemId(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view =  mAdapter.getView(position, convertView, parent);
		if (mItemViewHandler != null){
			mItemViewHandler.handleItemView(mAdapter, position, view, parent);
		}
		return view;
	}
	
	@Override
	public boolean hasStableIds() {
		return mAdapter.hasStableIds();
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		mAdapter.registerDataSetObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		mAdapter.unregisterDataSetObserver(observer);
	}

	@Override
	public void notifyDataSetChanged() {
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetInvalidated() {
		mAdapter.notifyDataSetInvalidated();
	}

	@Override
	public boolean areAllItemsEnabled() {
		return mAdapter.areAllItemsEnabled();
	}

	@Override
	public boolean isEnabled(int position) {
		return mAdapter.isEnabled(position);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return mAdapter.getDropDownView(position, convertView, parent);
	}

	@Override
	public int getItemViewType(int position) {
		return mAdapter.getItemViewType(position);
	}

	@Override
	public int getViewTypeCount() {
		return mAdapter.getViewTypeCount();
	}

	@Override
	public boolean isEmpty() {
		return mAdapter.isEmpty();
	}
	
	/**
	 * Adapter调用完getView之后调用，提供对ItemView进行额外处理的功能，如绑定监听等
	 * @author Administrator
	 *
	 */
    public static interface ItemViewHandler {
    	/**
    	 * 提供对ItemView进行额外处理的功能，如绑定监听等
    	 * @param adapter 
    	 * @param position he position of the item within the adapter's data set of the item whose view
     *        we want.
    	 * @param itemView
    	 * @param parent itemView所在的父类，如为ListView
    	 */
        void handleItemView(BaseAdapter adapter, int position, View itemView, ViewGroup parent);
    }
}
