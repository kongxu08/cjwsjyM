package com.cjwsjy.app.outoffice;

import java.util.ArrayList;
import java.util.List;

import com.cjwsjy.app.R;
import com.cjwsjy.app.adapter.OutOfOfficeListAdaper;
import com.cjwsjy.app.item.OutOfOfficeItem;


import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

public class OutOfficeListActivity extends Activity {
	private ListView listView;
	private List<OutOfOfficeItem> listItems;
	private OutOfOfficeListAdaper listAdaper;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.registerlist);
        
        initList();
       
    }

    public ListView initList() {


		listView = (ListView) findViewById(R.id.registerlist);

		listItems = new ArrayList<OutOfOfficeItem>();
		String outTimeFrom = "2015-07-21";
		String address = "武汉";
		String event = "参加工作会议";

		for (int i = 0; i < 10; i++) {
			OutOfOfficeItem item = new OutOfOfficeItem();
			item.setTv_outTimeFrom(outTimeFrom);
			item.setTv_address(address);
			item.setTv_event(event);
			item.setIv_modify(R.drawable.today_icon);

			listItems.add(item);
		}

		listAdaper = new OutOfOfficeListAdaper(OutOfficeListActivity.this, listItems);

		listView.setAdapter(listAdaper);

		return listView;
	}

}