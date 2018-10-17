package com.cjwsjy.app.vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cjwsjy.app.R;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.ValidUtil;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 用车评价详情
 * 
 * @Company: 广州市道一信息有限公司
 * @author: liuwende
 * 
 */
public class vehileCommenDetailActivity extends BaseActivity2 implements OnClickListener
{
	private Map dataMap = new  HashMap<String, Object>();
	private String order_no; 
	private TextView comment_order_no;
	private TextView comment_order_date;
	private TextView comment_order_time;
	private TextView comment_car_no;
	private TextView comment_driver;
	private RatingBar room_ratingbar;
	private EditText content;
	private Button btn_submit;
	private int evaluation;
	private String evaluation_content;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vehilecommentditail);
		setHeadView(R.drawable.onclick_back_btn, "", "用车评价", 0, "", this, this, this);

		Intent intent = this.getIntent();
		order_no=intent.getStringExtra("order_no");
		init();
		getDetail();
	}

		public void init(){
			comment_order_no=(TextView) findViewById(R.id.comment_order_no);
			comment_order_date=(TextView) findViewById(R.id.comment_order_date);
			comment_order_time=(TextView) findViewById(R.id.comment_order_time);
			comment_car_no=(TextView) findViewById(R.id.comment_car_no);
			comment_driver=(TextView) findViewById(R.id.comment_driver);
			content=(EditText) findViewById(R.id.content);
			room_ratingbar=(RatingBar) findViewById(R.id.room_ratingbar);
			//评分
			room_ratingbar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
				
				@Override
				public void onRatingChanged(RatingBar arg0, float arg1, boolean arg2) {
					// TODO Auto-generated method stub
					evaluation=(int) arg1;
				}
			});
			btn_submit=(Button) findViewById(R.id.btn_submit);
			btn_submit.setOnClickListener(this);
		}
		
		public void initData(){
			comment_order_no.setText(dataMap.get("order_no") != null ? dataMap.get("order_no").toString() : "");
			String dateString=(String) dataMap.get("startdate");
			if(dateString!=null&&!dateString.equals("")){
				String date=dateString.substring(0, 10);
				String time=dateString.substring(12);
				comment_order_date.setText(date);
				comment_order_time.setText(time);
			}
			comment_car_no.setText(dataMap.get("plate_no") != null ? dataMap.get("plate_no").toString() : "");
			comment_driver.setText(dataMap.get("driver_name") != null ? dataMap.get("driver_name").toString() : "");
	     }
		
	/*
		* 获取订单详情
	*/
	public void getDetail()
	{
		if (!ValidUtil.isNullOrEmpty(order_no)) {
			requestBeforeDialog(vehileCommenDetailActivity.this);
			Map<String, String> map = new HashMap<String, String>();
			//String url = UrlManager.GET_ORDER_DETAIL + "?order_no=" + order_no;
			map.put("order_no",order_no);
			String url = UrlManager.GET_ORDER_DETAIL;
			doRequest(0, url, map);
		}
	}

	public void doRequest(final int requestId, final String url, final Map dataMap)
	{
		new ReqThread(requestId, url, dataMap).start();
	}

	class ReqThread extends Thread{
		private int requestId;
		private String url;
		private Map dataMap;
		public ReqThread(int requestId,  String url, Map dataMap)
		{
			this.requestId = requestId;
			this.url = url;
			this.dataMap = dataMap;
		}
		@Override
		public void run()
		{
			int length = 0;
			String strurl = "";
			String resultStr = "";

			strurl = UrlManager.appRemoteUrl+url;
			if(requestId==0)
			{
				resultStr = HttpClientUtil.HttpUrlConnectionGet2(strurl, dataMap,"UTF-8");
			}
			else if(requestId==1)
			{
				resultStr = HttpClientUtil.HttpUrlConnectionGet2(strurl, dataMap,"UTF-8");
			}
			else if(requestId==12)
			{
				resultStr = HttpClientUtil.HttpUrlConnectionPost6(strurl, dataMap,"UTF-8",103);
				//resultStr = HttpClientUtil.HttpUrlConnectionGet2(strurl, dataMap,"UTF-8");
			}
			else
			{
				resultStr = HttpClientUtil.HttpUrlConnectionPost4(strurl, dataMap,"UTF-8");
			}

			length = resultStr.length();
			if( length==0 )
			{
				baseHandler.obtainMessage(2, requestId, 0, null).sendToTarget();
				return;
			}

			try {
				JSONObject jsonObj = new JSONObject(resultStr);
				ResultObject obj = DefaultDataParser.getInstance().parseData3(jsonObj);
				try{
					if (obj.isSuccess())
					{
						baseHandler.obtainMessage(0, requestId, 0, obj).sendToTarget();
					}
					else
					{
						baseHandler.obtainMessage(1, requestId, 0, obj).sendToTarget();
					}

				} catch(Exception e){
					baseHandler.obtainMessage(1, requestId, 0, obj)
							.sendToTarget();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private Handler baseHandler = new Handler()
	{
		@Override
		public void handleMessage(android.os.Message msg)
		{
			requestAfterDialog();
			if (msg.what == 0)
			{
				onExecuteSuccess(msg.arg1, (ResultObject) msg.obj);
			}
			else if (msg.what == 1)
			{
				onExecuteFail(msg.arg1, (ResultObject) msg.obj);
			}
			else if (msg.what == 2)
			{
				Toast.makeText(getApplicationContext(), "暂无数据", Toast.LENGTH_SHORT).show();
			}
		};
	};

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
			case R.id.leftImage:
				finish();
				break;
			case R.id.btn_submit:
				evaluation_content = content.getText().toString();
				if (!ValidUtil.isNullOrEmpty(evaluation_content))
				{
					commit(evaluation_content);
				}
				else
				{
					Toast.makeText(this, "请填写您的宝贵意见...", Toast.LENGTH_LONG).show();
					return;
				}
				break;
			default:
				break;
		}
	}
	
	
	@Override
	protected void onExecuteSuccess(int requestId, ResultObject result) {
		// TODO Auto-generated method stub
		requestAfterDialog();
		switch (requestId) {
		case 0:
			dataMap=result.getDataMap();
			initData();
			break;
		case 1:
			Toast.makeText(this, "评价成功！",Toast.LENGTH_LONG).show();
			finish();
			break;

		default:
			break;
		}


	}

	@Override
	protected void onExecuteFail(int requestId, ResultObject result) {
		// TODO Auto-generated method stub
		requestAfterDialog();
		switch (requestId) {
		case 0:
			Toast.makeText(this, "查询失败，请重试",Toast.LENGTH_LONG).show();
			break;
		case 1:
			Toast.makeText(this, "评论失败，请重试",Toast.LENGTH_LONG).show();
			break;

		default:
			break;
		}
	}

	/*
	 * 提交评价
	 */
	  public void commit(String str){
   	   if (!ValidUtil.isNullOrEmpty(order_no)) {
   		   dialog = new ProgressDialog(vehileCommenDetailActivity.this);
		   dialog.setMessage("正在提交，请等待...");
		   dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
		   dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
		   dialog.show();
  			String url=UrlManager.COMMIT_COMMENT;
  			Map<String, Object> map = new HashMap<String, Object>();
  			map.put("order_no", order_no);
  			map.put("evaluation", evaluation+"");
  			map.put("evaluation_content", evaluation_content);
  			
  			doRequest(1, url, map);
   	   }
      }
}
