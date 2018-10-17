package com.cjwsjy.app.homeFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.item.FinishPeddingItem;
import com.do1.cjmobileoa.db.DBManager;

import java.util.ArrayList;
import java.util.List;

public class AdaperLixian extends BaseAdapter
{
	private int m_nposition = 0;

	private DBManager db;
	private EditText et_text;
	private Context mContext;
	private Dialog noticeDialog;
	private ListView mListView;
	private LayoutInflater inflater;
	private List<FinishPeddingItem> listItems =new ArrayList<FinishPeddingItem>();

	public AdaperLixian(Context context, List<FinishPeddingItem> list)
	{
	   inflater=LayoutInflater.from(context);
	   this.listItems=list;

		this.mContext = context;
		db = SmApplication.dbManager;
	}

	public void setListView(ListView listv)
	{
		this.mListView = listv;
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
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView==null)
		{
			convertView=inflater.inflate(R.layout.listitem_lixian, parent, false);
			holder=new ViewHolder();
			holder.iv_icon1=(ImageView) convertView.findViewById(R.id.iv_icon1);
			holder.tv_title=(TextView) convertView.findViewById(R.id.tv_title);
			//holder.btn_replace=(Button) convertView.findViewById(R.id.button1);
			holder.iv_icon2=(ImageView) convertView.findViewById(R.id.button1);
			
			convertView.setTag(holder);
		}
		else
		{
			holder=(ViewHolder) convertView.getTag();
		}

		FinishPeddingItem listItem = listItems.get(position);
		holder.tv_title.setText(listItem.getTv_title());

		//重命名按钮
		holder.iv_icon2.setOnClickListener(new View.OnClickListener()
		//holder.btn_replace.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				Rename(position);
			}
		});

		return convertView;
	}

	public void Rename(int position)
	{
		int npos = 0;

		FinishPeddingItem item = listItems.get(position);
		String name = item.getTv_title();

		m_nposition = position;

		//弹出窗口，重命名
		showNoticeDialog2(name);
	}

	private void showNoticeDialog2(String strname)
	{
		Button btn_ok;
		Button btn_cancel;

		LayoutInflater inflater = LayoutInflater.from(mContext);
		View viewDialog = inflater.inflate(R.layout.dialog_custom4, null);

		et_text = (EditText)viewDialog.findViewById(R.id.EditText_msg);
		et_text.setText(strname);

		btn_ok = (Button)viewDialog.findViewById(R.id.dialog_ok4);
		btn_cancel = (Button)viewDialog.findViewById(R.id.dialog_cancel4);

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setView(viewDialog);

		noticeDialog = builder.create();
		noticeDialog.show();

		btn_ok.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String strname = "";
				strname = et_text.getText().toString();

				//修改数据库
				db.updateBiaozhun(m_nposition+1,strname);

				//修改listview列表的值
				updateItem(m_nposition,strname);

				noticeDialog.dismiss();
			}
		});

		btn_cancel.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				noticeDialog.dismiss();
				//noticeDialog.cancel();
			}
		});
	}

	private void updateItem(int index,String strname )
	{
		if (mListView == null)
		{
			return;
		}

		// 获取当前可以看到的item位置
		int visiblePosition = mListView.getFirstVisiblePosition();

		// 获取点击的view
		View view = mListView.getChildAt(index - visiblePosition);
		TextView txt = (TextView) view.findViewById(R.id.tv_title);

		// 获取mDataList.set(ids, item);更新的数据
		FinishPeddingItem data = (FinishPeddingItem) getItem(index);

		String name = data.getTv_title();

		// 重新设置界面显示数据
		txt.setText( strname );
	}

	private class ViewHolder
	{
		ImageView iv_icon1;
		TextView tv_title;
		Button btn_replace;
		ImageView iv_icon2;
	}
}
