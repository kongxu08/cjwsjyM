package com.cjwsjy.app.vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import android.annotation.SuppressLint;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.ValidUtil;
import com.do1.cjmobileoa.parent.callback.ResultObject;
import com.cjwsjy.app.R;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.util.*;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 申请详情
 * 
 * @Company: 广州市道一信息有限公司
 * @author: liuwende
 * 
 */
public class VehileDispatchDitialActivity extends BaseActivity2 implements OnClickListener {
	private Map dataMap = new  HashMap<String, Object>();
	private List<Map<String, Object>> datalist = new ArrayList<Map<String,Object>>();
	
	private String order_no;
	private String plate_no;
	private String driver_id;
	private String order_state;
	private int state;
	private TextView tv_order_no;
	private TextView person_name;
	private TextView order_department;
	private TextView order_date;
	private TextView order_time;
	private TextView order_address;
	private TextView return_address;
	private TextView order_car_type;
	private TextView order_reason;
	private TextView order_driver;
	private TextView order_phone;
	private TextView order_plate_no;
	
	private ImageView jt1;
	private ImageView jt2;
	private Button btn_submit;
	private Button btn_done;
	
	private LinearLayout ll_driver;
	private LinearLayout ll_plate_no;

	@Override
	protected void onCreate(Bundle savedInstanceState)
    {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dispatchditail);
		setHeadView(R.drawable.onclick_back_btn, "", "用车调度", 0, "", this, this, this);
         
		Intent intent = this.getIntent();
		order_no=intent.getStringExtra("order_no");
		order_state=intent.getStringExtra("order_state");
		if(!ValidUtil.isNullOrEmpty(order_state))
        {
			state=Integer.parseInt(order_state);
		}
		init();
		getDetail();
	}

    public void init()
    {
        tv_order_no = (TextView) findViewById(R.id.order_no_no);
        person_name = (TextView) findViewById(R.id.person_name);
        order_department = (TextView) findViewById(R.id.order_department);
        order_date = (TextView) findViewById(R.id.order_date);
        order_time = (TextView) findViewById(R.id.order_time);
        order_address = (TextView) findViewById(R.id.order_address);
        return_address = (TextView) findViewById(R.id.return_address);
        order_car_type = (TextView) findViewById(R.id.order_car_type);
        order_reason = (TextView) findViewById(R.id.order_reason);
        order_driver = (TextView) findViewById(R.id.order_driver);
        order_phone = (TextView) findViewById(R.id.order_phone);
        order_plate_no = (TextView) findViewById(R.id.order_plate_no);

        jt1 = (ImageView) findViewById(R.id.jt1);
        jt2 = (ImageView) findViewById(R.id.jt2);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        btn_done = (Button) findViewById(R.id.btn_done);
        btn_done.setOnClickListener(this);
        ll_driver = (LinearLayout) findViewById(R.id.ll_driver);
        ll_plate_no = (LinearLayout) findViewById(R.id.ll_plate_no);

        if (state != 1) {
            jt1.setVisibility(View.GONE);
            jt2.setVisibility(View.GONE);
        } else {
            ll_driver.setOnClickListener(this);
            ll_plate_no.setOnClickListener(this);
        }
        switch (state) {
            case 1:
                btn_submit.setText("确认提交");
                break;
            case 2:
                btn_submit.setText("还车确认");
                btn_done.setVisibility(View.VISIBLE);
                break;
            case 3:
                btn_submit.setText("确认取消");
                break;
            case 6:
                btn_submit.setText("确认变更");
                //btn_submit.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }
  
	     
    public void initData()
	{
	    tv_order_no.setText(dataMap.get("order_no") != null ? dataMap.get("order_no").toString() : "");
	    person_name.setText(dataMap.get("applicants") != null ? dataMap.get("applicants").toString() : "");
	    order_department.setText(dataMap.get("department") != null ? dataMap.get("department").toString() : "");
	    String dateString=(String) dataMap.get("startdate");
	    if(dateString!=null&&!dateString.equals(""))
			 {
	    		 dateString=dateString.replace(" ", "");
	    		 String date="";
	    		 String time="";
	    		 if(dateString.length()>=10)
				 {
					 date=dateString.substring(5, 10);
					 date=date.replace("-", "月");
					 date=date+"日";
					 if(dateString.length()>=15)
					 time=dateString.substring(10,15);
	    		 }
	    		 order_date.setText(date);
	    		 order_time.setText(time);
			 }
	    	 int reson_no=8;
	    	 String resean=(String) dataMap.get("VehiclesSubject");
	    	 order_reason.setText(resean);
	    	/* if(resean!=null&&!resean.equals("")){
	    		 reson_no=Integer.parseInt(resean);
	    	 }
	    	 switch (reson_no) {
			case 0:
				order_reason.setText("其它");  
				break;
           case 1:
        	   order_reason.setText("出差");
				break;
           case 2:
        	   order_reason.setText("接人");
				break;
           case 3:
        	   order_reason.setText( "参加会议");
				break;
			default:
				break;
			}*/
	    	 order_address.setText(dataMap.get("pick_place") != null ? dataMap.get("pick_place").toString() : "");
	    	 return_address.setText(dataMap.get("return_place") != null ? dataMap.get("return_place").toString() : "");
	    	 int car_type=0;
	    	 String vehicleTypeString=(String) dataMap.get("vehicle_type");
	    	 if(vehicleTypeString!=null&&!vehicleTypeString.equals("")){
	    		 car_type=Integer.parseInt(vehicleTypeString);
	    	 }
	    	 switch (car_type) {
			case 1:
				order_car_type.setText("其他");
				break;
			case 2:
				order_car_type.setText("小轿车");
				break;
			case 3:
				order_car_type.setText("越野车");
				break;
			case 4:
				order_car_type.setText("商务车");
				break;
			case 5:
				order_car_type.setText("中客车");
				break;
			case 6:
				order_car_type.setText("大客车");
				break;

			default:
				break;
			}
	    	 if(state==1)
			 {
	    	  order_driver.setText(dataMap.get("driver_name") != null ? dataMap.get("driver_name").toString() : "请选择");
		      order_plate_no.setText(dataMap.get("plate_no") != null ? dataMap.get("plate_no").toString() : "请选择");
	    	 }
			 else
			 {
	    	 order_driver.setText(dataMap.get("driver_name") != null ? dataMap.get("driver_name").toString() : "");
	    	 order_plate_no.setText(dataMap.get("plate_no") != null ? dataMap.get("plate_no").toString() : "");
	    	 }
	    	 order_phone.setText(dataMap.get("phone") != null ? dataMap.get("phone").toString() : "");
    }
	/*
	 * 获取订单详情
	 */
    public void getDetail()
    {
        if (!ValidUtil.isNullOrEmpty(order_no))
        {
   			requestBeforeDialog(VehileDispatchDitialActivity.this);
   			Map<String, String> map = new HashMap<String, String>();
   			map.put("order_no", order_no);
   			String url=UrlManager.GET_ORDER_DETAIL+"?order_no="+order_no;
   			doRequest2(0, url, map, 1, 11); //POST方法，不读参数
        }
    }

	//int type  1 POST方法，不读参数,  2 GET方法，读参数
	//int parms  打印使用
    public void doRequest2( int requestId,  String url,  Map dataMap, int type, int parms)
    {
        new ReqThread(requestId,  url, dataMap, type,  parms).start();
    }

    class ReqThread extends Thread
    {
        private int requestId;
		private int type;
		private int parms;
        private String url;
        private Map dataMap;
        public ReqThread(int requestId,  String url, Map dataMap,int type, int parms)
		{
            this.requestId = requestId;
            this.url = url;
            this.dataMap = dataMap;
			this.type = type;
			this.parms = parms;
        }
        @Override
        public void run()
        {
            int length = 0;
            String strurl;
			String resultStr = "";

            strurl = UrlManager.appRemoteUrl+url;
			if(type==1)
			{
				resultStr = HttpClientUtil.HttpUrlConnectionPost4(strurl, dataMap, "UTF-8");
			}
			else
			{
				resultStr = HttpClientUtil.HttpUrlConnectionGet2(strurl, dataMap, "UTF-8");
			}
            length = resultStr.length();
            if( length==0 )
            {
                baseHandler.obtainMessage(2, requestId, 0, null).sendToTarget();
                return;
            }

            String jsonStr = "{\"data\":" + resultStr + "}";

            try
            {
                JSONObject jsonObj = new JSONObject(resultStr);
                //JSONObject jsonObj = new JSONObject(jsonStr);
                //JSONObject jsonObj = new JSONObject(responseMsg);
                com.cjwsjy.app.vehicle.ResultObject obj = DefaultDataParser.getInstance().parseData3(jsonObj);
                try
                {
                    if (obj.isSuccess())
                    {
                        baseHandler.obtainMessage(0, requestId, 0, obj)
                                .sendToTarget();
                    }
                    else
                    {
                        baseHandler.obtainMessage(1, requestId, 0, obj)
                                .sendToTarget();
                    }

                }
                catch(Exception e)
                {
                    baseHandler.obtainMessage(1, requestId, 0, obj)
                            .sendToTarget();
                }
            }
            catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (Exception e)
            {
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
				onExecuteSuccess2(msg.arg1, (com.cjwsjy.app.vehicle.ResultObject)msg.obj);
            }
            else if (msg.what == 1)
            {
                onExecuteFail2(msg.arg1, (com.cjwsjy.app.vehicle.ResultObject) msg.obj);
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
		switch (v.getId()) {
		case R.id.leftImage:
			finish();
			break;
		case R.id.ll_driver:
			Intent intent=new Intent(VehileDispatchDitialActivity.this, selectDriverActivity.class);
			 startActivityForResult(intent, 0x001);
			break;
		case R.id.ll_plate_no:
			 intent=new Intent(VehileDispatchDitialActivity.this, selectVehicleActivity.class);
			 startActivityForResult(intent, 0x001);
			break;
		case R.id.btn_done:
			String url=UrlManager.CompletedAhead;                      //提前完成
   			requestBeforeDialog(VehileDispatchDitialActivity.this);
   			Map<String, String> map = new HashMap<String, String>();
   			map.put("order_no", order_no);
   			doRequest2(5, url, map,2, 13);
			break;
		case R.id.btn_submit:
			switch (state) {
			case 1:
				  if (!ValidUtil.isNullOrEmpty(order_no))
				  {

					  if(ValidUtil.isNullOrEmpty(driver_id))
					  {
		    			  Toast.makeText(VehileDispatchDitialActivity.this, "请选择驾驶员", Toast.LENGTH_SHORT).show();
		    			  return;
					  }
	                  if(ValidUtil.isNullOrEmpty(plate_no))
					  {
	                	  Toast.makeText(VehileDispatchDitialActivity.this, "请选择车辆", Toast.LENGTH_SHORT).show();
		    			  return;
					  }
		    		 url=UrlManager.SUBMIT_DISPATCH;
		   			 requestBeforeDialog(VehileDispatchDitialActivity.this);
		   			 map = new HashMap<String, String>();
		   			map.put("order_no", order_no);
		   			map.put("plate_no", plate_no);
		   			map.put("driver_id", driver_id);
 		   			doRequest2(1, url, map, 2, 12);
		    	   }
				break;
			case 2:
				 url=UrlManager.ConfirmCard;                      //还车确认
	   			requestBeforeDialog(VehileDispatchDitialActivity.this);
	   			map = new HashMap<String, String>();
	   			map.put("order_no", order_no);
	   			doRequest2(4, url, map,2, 14);
	   			break;
				case 3:
					 url=UrlManager.ConfirmCancel;
		   			requestBeforeDialog(VehileDispatchDitialActivity.this);
		   			 map = new HashMap<String, String>();
		   			map.put("order_no", order_no);
		   			doRequest2(2, url, map,2, 15);
		   			break;
				case 6:
					 url=UrlManager.ConfirmChange;
		   			requestBeforeDialog(VehileDispatchDitialActivity.this);
		   			map = new HashMap<String, String>();
		   			map.put("order_no", order_no);
		   			doRequest2(3, url, map,2, 16);
					break;
			default:
				break;
			}
	       
			break;
		default:
			break;
		}
	}

	//@Override
	//protected void onExecuteSuccess2(int requestId, ResultObject result)
	protected void onExecuteSuccess2(int requestId, com.cjwsjy.app.vehicle.ResultObject result)
	{
		// TODO Auto-generated method stub
		requestAfterDialog();
		switch (requestId) {
		case 0:
			dataMap=result.getDataMap();
			initData();
			break;
		case 1:
			Toast.makeText(this, "提交成功!",Toast.LENGTH_LONG).show();
			finish();
			break;
		case 2:
			Toast.makeText(this, "取消成功!",Toast.LENGTH_LONG).show();
			finish();
			break;
		case 3:
			Toast.makeText(this, "变更成功!",Toast.LENGTH_LONG).show();
			finish();
			break;
		case 4:
		case 5:
			Toast.makeText(this, "操作成功!",Toast.LENGTH_LONG).show();
			finish();
			break;
		default:
			break;
		}

	}

	//@Override
	protected void onExecuteFail2(int requestId, com.cjwsjy.app.vehicle.ResultObject result) {
		// TODO Auto-generated method stub
		requestAfterDialog();
		switch (requestId) {
		case 0:
			Toast.makeText(this, result.getDesc(),Toast.LENGTH_LONG).show();
			break;
		case 1:
			Toast.makeText(this, "提交失败:"+result.getDesc(),Toast.LENGTH_LONG).show();
			break;
		case 2:
			Toast.makeText(this, "取消失败:"+result.getDesc(),Toast.LENGTH_LONG).show();
			break;
		case 3:
			Toast.makeText(this, "变更失败:"+result.getDesc(),Toast.LENGTH_LONG).show();
			break;
		case 4:
		case 5:
			Toast.makeText(this, "操作失败!",Toast.LENGTH_LONG).show();
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data!=null){
		Bundle bundle = data.getExtras();
		switch (resultCode) {
		case 0x001:
			if (bundle != null) {
				String driver_name=bundle.getString("driver_name");
				String driver_phone=bundle.getString("driver_phone");
				order_driver.setText(driver_name);
				order_phone.setText(driver_phone);
				driver_id=bundle.getString("driver_id");
			}
			break;
		case 0x002:
			if (bundle != null) {
				String Plate_No=bundle.getString("Plate_No");
				order_plate_no.setText(Plate_No);
				plate_no=Plate_No;
			}
			break;
		case 0x003:
			if (bundle != null) {}
			break;
					default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
	}
}
