package com.cjwsjy.app.homeFragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.base.BaseActivity;
import com.cjwsjy.app.item.FinishPeddingItem;
import com.cjwsjy.app.utils.CallOtherOpeanFile;
import com.cjwsjy.app.utils.ThreadUtils;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.webview.WebViewHome2;
import com.do1.cjmobileoa.db.DBManager;
import com.do1.cjmobileoa.db.model.BiaozhunVO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ActivityLixian extends BaseActivity implements OnItemClickListener
{
	private SharedPreferences sp;
	private ThreadUtils m_threadlog;

	// 生成动态数组，加入数据
	List<FinishPeddingItem> listItems = new ArrayList<FinishPeddingItem>();

	private AdaperDangan listItemAdapter;
	private String m_loginname;
	private String appUrl;

	ListView list;
	private DBManager db;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_homelist);

		m_threadlog = ThreadUtils.getInstance();  // 得到单例对象

		db = SmApplication.dbManager;
		sp = SmApplication.sp;
		m_loginname = sp.getString("USERDATA.LOGIN.NAME", "");

		appUrl = UrlUtil.HOST;

		list = (ListView) findViewById(R.id.homelist);

		TextView tv_navtitle = (TextView) findViewById(R.id.tv_navtitle);
		tv_navtitle.setText("离线阅览");

		ImageView iv_back = (ImageView) this.findViewById(R.id.iv_back);
		// 后退
		iv_back.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});

		initList();
	}

	public ListView initList()
	{
		boolean bresult = false;
		String strid = "";
		String strname = "";
		String strdate = "";
		String strdelete = "";
		List<BiaozhunVO> employees1;

		employees1 = new ArrayList<BiaozhunVO>();

		//从数据库中查找
		employees1 = db.SelectDangan();

		for (BiaozhunVO entity1 : employees1)
		{
			strid = entity1.getId();
			strname = entity1.getNewname();
			strdate = entity1.getOldname();
			strdelete = entity1.getIsdelete();

			//是否已经删除
			bresult = strdelete.equals("1");
			if(bresult==true) continue;

			FinishPeddingItem listItem = new FinishPeddingItem();
			listItem.setTv_formid(strid);
			listItem.setTv_title(strname);
			listItem.setTv_date(strdate);
			listItems.add(listItem);
		}

		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new AdaperDangan( ActivityLixian.this, listItems );
		listItemAdapter.setListView(list);
		// 添加并且显示
		list.setAdapter(listItemAdapter);
		list.setOnItemClickListener(this);

		return list;
	}

	public void updateList(int position)
	{
		boolean bresult = false;
		String strid = "";
		String strname = "";
		String strdelete = "";
		List<BiaozhunVO> employees1;

		listItems.remove(position);

		listItemAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id)
	{
		// TODO 自动生成的方法存根
		boolean result = false;
		FinishPeddingItem item = listItems.get(position);
		String name = item.getTv_date();

		Log.d("cjwsjy", "------name="+name+"-------");

		String sdPath = Environment.getExternalStorageDirectory() + "/Download/com.cjwsjy.app/dangan/";
		File file2 = new File(sdPath + name);
		result = file2.exists();
		if( result==false )
		{
			Toast.makeText(ActivityLixian.this, "该文件不存在！", Toast.LENGTH_SHORT).show();
			return;
		}

		CallOtherOpeanFile openfile = new CallOtherOpeanFile();
		openfile.openFile(ActivityLixian.this, file2);
	}

}
