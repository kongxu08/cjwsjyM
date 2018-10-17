package com.cjwsjy.app.vehicle;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.ValidUtil;

import com.cjwsjy.app.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 用车申请
 * 
 * @Company: 广州市道一信息有限公司
 * 
 * 
 */
public class vehileApply_dispatchActivity extends BaseActivity2 implements OnClickListener {
	public static String parentId="a90aeaec-e3d4-43de-bb67-85407b57b171";
	public static String empName;
	public static String empName2;
	public static String empDept;
	public static List<Activity> activityList=new ArrayList<Activity>();
	Calendar calendar = Calendar.getInstance();
	private int year;
	private int month;
	private int day;
	
	private Map dataMap = new  HashMap<String, Object>();
    private RadioGroup rg_date;
    private RadioGroup rg_car_type;
    private RadioGroup rg_count;
    private RadioGroup rg_mid_address;
    private RadioGroup rg_return_address;
    private RadioGroup rg_way;
    private RadioGroup rg_resean;
    private TextView tv_date;
    private TextView tv_time;
    private TextView tv_userName;
    private TextView userDepartment;
    private EditText et_count;
    private  EditText et;
    private Button btn_apply_submit;
   
    private String startdate;
    private String vehicle_type;
    private String passenger;
    private String pick_place;
    private String mid_place;
    private String return_place;
    private String wayorround;
    private String comment;
    private String order_no;
    private String VehiclesSubject; 
    
    private String dataLastString;
     
    private SharedPreferences sp;
    private  Editor ed;
    
    private String responseMsg = "";
    private JSONObject jsonObj = null;
    
    private boolean isDate;
    private boolean isType;

    private  TextView rb_plite_1;          //选择按钮
    private  TextView rb_mid_1;          //选择按钮
    private  TextView rb_return_1;          //选择按钮
    
    private  Button rb_plite_moren;          //选择按钮
    private  Button rb_mid_moren;          //选择按钮
    private  Button rb_return_moren;          //选择按钮
    
    private LinearLayout ll_select_emp;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vehicleapply2);
		Intent intent = this.getIntent();
		setHeadView(R.drawable.onclick_back_btn, "", "用车申请", 0, "", this, this, this);
		 empName="";
		 empName2="";
		 empDept="";
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		parentId="a90aeaec-e3d4-43de-bb67-85407b57b171";
		activityList=new ArrayList<Activity>();
		 sp= SmApplication.sp;
		 ed=sp.edit();
			init();
	}
		 
	   
	   
		public void init(){
			ll_select_emp=(LinearLayout) findViewById(R.id.ll_select);
			ll_select_emp.setOnClickListener(this);
			rb_plite_1=(TextView) findViewById(R.id.rb_plite_1);          //选择按钮
			rb_mid_1=(TextView) findViewById(R.id.rb_mid_1);          //选择按钮
			rb_return_1=(TextView) findViewById(R.id.rb_return_1);          //选择按钮
			rb_plite_moren=(Button) findViewById(R.id.rb_plite_moren);          //选择按钮
			rb_mid_moren=(Button) findViewById(R.id.rb_mid_moren);          //选择按钮
			rb_return_moren=(Button) findViewById(R.id.rb_return_moren);          //选择按钮
			
			et=(EditText) findViewById(R.id.et_comment);
			tv_userName=(TextView) findViewById(R.id.userName);
			if(empName.equals("")||empName==null){
				tv_userName.setText("请选择人员");
				tv_userName.setTextColor(Color.parseColor("#b2b2b2"));
			}else{
				tv_userName.setText(empName);
				tv_userName.setTextColor(Color.parseColor("#777777"));
			}
			userDepartment=(TextView) findViewById(R.id.userDepartment);
			if(empDept.equals("")||empDept==null){
				//userDepartment.setText("(枢纽处)");
			}else{
				userDepartment.setText("("+empDept+")");
			}
			btn_apply_submit=(Button) findViewById(R.id.btn_apply_submit);
			btn_apply_submit.setOnClickListener(this);
			year = calendar.get(Calendar.YEAR);
			month = calendar.get(Calendar.MONTH);
			day = calendar.get(Calendar.DAY_OF_MONTH);
			tv_date=(TextView) findViewById(R.id.tv_date);
			String str1="";
			String str2="";
			String str = "";
			if(month+1<10){
				str1="0"+ (month+ 1) + "月";
			}else{
				str1= (month+ 1) + "月";
			}
			if(day<10){
				str2="0"+day+"日";
			}else{
				str2=day+"日";
			}
			str = year + "年" +str1 + str2;
			dataLastString=str;
			tv_date.setText(str.substring(5));
			tv_time=(TextView) findViewById(R.id.tv_time);
		
		     int hour=calendar.get(Calendar.HOUR_OF_DAY);
			 int minute = calendar.get(Calendar.MINUTE);
			 if(hour<10){
					str1="0"+ hour;
				}else{
					str1=hour+"";
				}
				if(minute<10){
					str2="0"+minute;
				}else{
					str2=minute+"";
				}
				tv_time.setText(str1+":"+str2);
			et_count=(EditText) findViewById(R.id.et_count);
			//日期
			rg_date=(RadioGroup) findViewById(R.id.rg_date);
			rg_date.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(RadioGroup arg0, int arg1) {
					// TODO Auto-generated method stub
					String str1="";
					String str2="";
					String str = "";
					switch (arg1) {
					case R.id.rb_today:
						if(month+1<10){
							str1="0"+ (month+ 1) + "月";
						}else{
							str1= (month+ 1) + "月";
						}
						if(day<10){
							str2="0"+day+"日";
						}else{
							str2=day+"日";
						}
						str = year + "年" +str1 + str2;
						dataLastString=str;
						tv_date.setText(str1 + str2);
						break;
                    case R.id.rb_tommorow:
                    	calendar= Calendar.getInstance();
                    	calendar.add(Calendar.DATE, 1);
                    	Date newDate= new Date(calendar.getTimeInMillis()); 
    				     SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日");
    				     SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
    				     String date = simpleDateFormat.format(newDate);
    				     String date2 = simpleDateFormat2.format(newDate);
						dataLastString=date2;
						tv_date.setText(date);
						break;
                    case R.id.rb_tommorow2:
                    	calendar= Calendar.getInstance();
                    	calendar.add(Calendar.DATE, 2);
                   	 newDate= new Date(calendar.getTimeInMillis()); 
   				     simpleDateFormat = new SimpleDateFormat("MM月dd日");
   				     simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
   				     date = simpleDateFormat.format(newDate);
   				     date2 = simpleDateFormat2.format(newDate);
						dataLastString=date2;
						tv_date.setText(date);
						break;
                    case R.id.rb_date_more:
                    	if(isDate){
                    		isDate=false;
                    	}else{
                    	DatePickerDialog dialog = new DatePickerDialog(vehileApply_dispatchActivity.this,
    							new DatePickerDialog.OnDateSetListener()
    							{
    								@Override
    								public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
    								{
    									if(year<vehileApply_dispatchActivity.this.year){
    										 Toast.makeText(vehileApply_dispatchActivity.this,"请选择正确的年份", Toast.LENGTH_SHORT).show();
    										  return;
    									}else if(monthOfYear<vehileApply_dispatchActivity.this.month){
   										 Toast.makeText(vehileApply_dispatchActivity.this, "请选择正确的月份", Toast.LENGTH_SHORT).show();
   										  return;
   									  }else if(dayOfMonth<vehileApply_dispatchActivity.this.day){
   										 Toast.makeText(vehileApply_dispatchActivity.this, "请选择正确的日期", Toast.LENGTH_SHORT).show();
   										  return;
   									  }
    									String str1="";
    									String str2="";
    									if(monthOfYear + 1<10){
    										str1="0"+ (monthOfYear + 1) + "月";
    									}else{
    										str1= (monthOfYear + 1) + "月";
    									}
    									if(dayOfMonth<10){
    										str2="0"+dayOfMonth+"日";
    									}else{
    										str2=dayOfMonth+"日";
    									}
    									String str = year + "年" +str1+ str2;
    									dataLastString=str;
    									tv_date.setText(str1+ str2);
    								}
    							}, year, month, day);
    					dialog.show();
                    	}
						break;
					default:
						break;
					}
				}
			});
			//时间
			tv_time.setOnClickListener(this);
			//车辆类型
			rg_car_type=(RadioGroup) findViewById(R.id.rg_car_type);
			//final RadioButton tView=(RadioButton) findViewById(R.id.rb_type_4);
			final RadioButton radioButton2=(RadioButton) findViewById(R.id.rb_type_5);
			rg_car_type.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(RadioGroup arg0, int arg1) {
					// TODO Auto-generated method stub
					switch (arg1) {
					case R.id.rb_type_1:
						vehicle_type="2";
	    				radioButton2.setText("其他");
						break;
                   case R.id.rb_type_2:
                	   vehicle_type="3";
	    			   radioButton2.setText("其他");
						break;
                   case R.id.rb_type_3:
                	   vehicle_type="4";
                	   radioButton2.setText("其他");
	                    break;
               /* case R.id.rb_type_4:
                	   vehicle_type="4";
	                    break;*/
                   case R.id.rb_type_5:
                	  if(isType){
                		  isType=false;
                	  }else{
                	   Intent intent=new Intent(vehileApply_dispatchActivity.this, selectVehiltTypeActivity.class);
                	   startActivityForResult(intent, 0x002);
                	  }
	                    break;
					default:
						break;
					}
				}
			});
			//用车人数
			rg_count=(RadioGroup) findViewById(R.id.rg_count);
			rg_count.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(RadioGroup arg0, int arg1) {
					// TODO Auto-generated method stub
					RadioButton count5=(RadioButton) findViewById(R.id.rb_count_5);
					switch (arg1) {
					case R.id.rb_count_1:
						passenger="1";
						count5.setText("更多");
						  findViewById(R.id.rb_count_5).setVisibility(View.VISIBLE);
						 et_count.setVisibility(View.GONE);
						break;
                   case R.id.rb_count_2:
                	   passenger="2";
                	   count5.setText("更多");
                	   findViewById(R.id.rb_count_5).setVisibility(View.VISIBLE);
                	   et_count.setVisibility(View.GONE);
						break;
                   case R.id.rb_count_3:
                	   passenger="3";
                	   count5.setText("更多");
                	   findViewById(R.id.rb_count_5).setVisibility(View.VISIBLE);
                	   et_count.setVisibility(View.GONE);
	                    break;
                   case R.id.rb_count_4:
                	   passenger="4";
                	   count5.setText("更多");
                	   findViewById(R.id.rb_count_5).setVisibility(View.VISIBLE);
                	   et_count.setVisibility(View.GONE);
	                    break;
                   case R.id.rb_count_5:
                	  
                	 Intent intent=new Intent(vehileApply_dispatchActivity.this, selectCountActivity.class);
                  	 startActivityForResult(intent, 0x001);
                	   
                	  /* et_count.setVisibility(View.VISIBLE);
                	   passenger= et_count.getText().toString();
                	   findViewById(R.id.rb_count_5).setVisibility(View.GONE);*/
	                    break;
					default:
						break;
					}
				}
			});
			
			//用车地点
			 rb_plite_1.setOnClickListener(this);
			 rb_plite_moren.setOnClickListener(this);
	        //途经地点
			 rb_mid_1.setOnClickListener(this);
			 rb_mid_moren.setOnClickListener(this);
			 //还车地点
			 rb_return_1.setOnClickListener(this);
			 rb_return_moren.setOnClickListener(this);
			//是由
			rg_resean=(RadioGroup) findViewById(R.id.rg_resean);
			rg_resean.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(RadioGroup arg0, int arg1) {
					// TODO Auto-generated method stub
					switch (arg1) {
					case R.id.rb_resean_1:
						VehiclesSubject="出差";
						break;
                   case R.id.rb_resean_2:
                	   VehiclesSubject="接人";
						break;
                   case R.id.rb_resean_3:
                	   VehiclesSubject="参加会议";
	                    break;
                   case R.id.rb_resean_4:
                	   Intent intent=new Intent(vehileApply_dispatchActivity.this, selectReasonActivity.class);
                     	 startActivityForResult(intent, 0x002);
	                    break;
					default:
						break;
					}
				}
			});
			//出车类型
			rg_way=(RadioGroup) findViewById(R.id.rg_way);
			rg_way.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(RadioGroup arg0, int arg1) {
					// TODO Auto-generated method stub
					switch (arg1) {
					case R.id.rb_way_1:
						wayorround="1";
						break;
                   case R.id.rb_way_2:
                	   wayorround="2";
						break;
             
					default:
						break;
					}
				}
			});
		}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub


		Map<String, Object> paraMap = new HashMap<String, Object>();

		switch (v.getId()) {
		case R.id.ll_select: 
			Intent intent=new Intent(vehileApply_dispatchActivity.this, departmentActivity.class);
        	startActivity(intent);
			break;
		case R.id.rb_plite_1: 
			 intent=new Intent(vehileApply_dispatchActivity.this, selectAddressActivity.class);
			intent.putExtra("word", rb_plite_1.getText().toString());
        	 startActivityForResult(intent, 0x002);
			 /*rb_plite_1.setTextColor(Color.parseColor("#b2b2b2"));
			 rb_plite_1.setText("请输入用车地点");*/
			 rb_plite_moren.setTextColor(Color.parseColor("#848484"));
			 rb_plite_moren.setBackgroundResource(R.drawable.bg_check_on);
			 
			break;
		case R.id.rb_plite_moren:
			String plite=sp.getString("pliteAddress", "");
			if(plite!=null&&!plite.equals("")){
			        pick_place=plite;
			        rb_plite_1.setText(plite);
			        rb_plite_1.setTextColor(Color.parseColor("#ff8c03"));
			        rb_plite_moren.setTextColor(Color.parseColor("#ff8c03"));
			        rb_plite_moren.setBackgroundResource(R.drawable.bg_check);
			}else{
			    Toast.makeText(vehileApply_dispatchActivity.this, "抱歉，您还没有上次记录", Toast.LENGTH_SHORT).show();
				return;
			}
			break;
		case R.id.rb_mid_1:  
			intent=new Intent(vehileApply_dispatchActivity.this, selectAddress3Activity.class);
			intent.putExtra("word", rb_mid_1.getText().toString());
        	 startActivityForResult(intent, 0x002);
			/* rb_mid_1.setTextColor(Color.parseColor("#b2b2b2"));
			 rb_mid_1.setText("请输入途经地点");*/
			 rb_mid_moren.setTextColor(Color.parseColor("#848484"));
			 rb_mid_moren.setBackgroundResource(R.drawable.bg_check_on);
		
			break;
		case R.id.rb_mid_moren:
			String mid=sp.getString("midAddress", "");
			if(mid!=null&&!mid.equals("")){
			        mid_place=mid;
			        rb_mid_1.setText(mid);
			        rb_mid_1.setTextColor(Color.parseColor("#ff8c03"));
			        rb_mid_moren.setTextColor(Color.parseColor("#ff8c03"));
			        rb_mid_moren.setBackgroundResource(R.drawable.bg_check);
			}else{
			    Toast.makeText(vehileApply_dispatchActivity.this, "抱歉，您还没有上次记录", Toast.LENGTH_SHORT).show();
				return;
			}
		
			break;
		case R.id.rb_return_1: 
			  intent=new Intent(vehileApply_dispatchActivity.this, selectAddress2Activity.class);
			  intent.putExtra("word", rb_return_1.getText().toString());
	        startActivityForResult(intent, 0x002);
			 rb_return_moren.setTextColor(Color.parseColor("#848484"));
			/* rb_return_1.setTextColor(Color.parseColor("#b2b2b2"));
			 rb_return_1.setText("请输入还车地点");*/
			 rb_return_moren.setBackgroundResource(R.drawable.bg_check_on);
		
			break;
		case R.id.rb_return_moren:
			String returnString=sp.getString("returnAddress", "");
			if(returnString!=null&&!returnString.equals(""))
			{
			        pick_place=returnString;
			        rb_return_1.setText(returnString);
			        rb_return_1.setTextColor(Color.parseColor("#ff8c03"));
			        rb_return_moren.setTextColor(Color.parseColor("#ff8c03"));
			        rb_return_moren.setBackgroundResource(R.drawable.bg_check);
			}else{
			    Toast.makeText(vehileApply_dispatchActivity.this, "抱歉，您还没有上次记录", Toast.LENGTH_SHORT).show();
				return;
			}
		
			break;
		case R.id.leftImage:
			finish();
			break;
		case R.id.tv_time:
			TimePickerDialog timeDialog=new TimePickerDialog(vehileApply_dispatchActivity.this,
					new TimePickerDialog.OnTimeSetListener() {
				
				@Override
				public void onTimeSet(TimePicker arg0, int arg1, int arg2) {
					// TODO Auto-generated method stub
					
					String dateString=dataLastString;
					dateString=dateString.replace("年", "-");
					dateString=dateString.replace("月", "-");
					dateString=dateString.replace("日", "");
					String str[]=dateString.split("-");
					if(Integer.parseInt(str[0])==year&&Integer.parseInt(str[1])==month+1&&Integer.parseInt(str[2])==day){
					Time time = new Time("GMT+8");       
				     time.setToNow();    
				     int hour=time.hour;
					 int minute = time.minute;      
				       if(arg1<hour){
				    	   Toast.makeText(vehileApply_dispatchActivity.this, "所选时间小于当前时间", Toast.LENGTH_SHORT).show();
				    	   return;
				       }else  if(arg2<minute){
				    	   Toast.makeText(vehileApply_dispatchActivity.this, "所选时间小于当前时间", Toast.LENGTH_SHORT).show();
				    	   return;
				       }
					}
				       
					String str1 ="";
					String str2 ="";
					if(arg1<10){
					 str1 = "0"+arg1;
					}else{
						str1 =""+arg1;
					}
					if(arg2<10){
					 str2 = "0"+arg2;
					}else{
						 str2 = ""+arg2;
					}
					 tv_time.setText(str1+":"+str2);
				}
			}, 0, 0, true);
			timeDialog.show();
			break;
		case R.id.btn_apply_submit:
			if (ValidUtil.isNullOrEmpty(empName2))
			{
				Toast.makeText(this, "请选择用车人", Toast.LENGTH_LONG).show();
				return;
			}
			if (!tv_time.getText().toString().contains(":"))
			{
				Toast.makeText(this, "请填写时间", Toast.LENGTH_LONG).show();
				return;
			}
			else
			{
				startdate = dataLastString + " " + tv_time.getText().toString();
				if (!ValidUtil.isNullOrEmpty(startdate))
				{
					startdate = startdate.replace("年", "-");
					startdate = startdate.replace("月", "-");
					startdate = startdate.replace("日", "");
					startdate = startdate.replace(" ", "");
					String str1 = startdate.substring(0, 10);
					String str2 = startdate.substring(10);
					startdate = str1 + " " + str2;
					// startdate+=":"+calendar.get(Calendar.SECOND);
				}
				else
				{
					Toast.makeText(this, "请将日期时间填写完整", Toast.LENGTH_LONG).show();
					return;
				}
			}
			if (ValidUtil.isNullOrEmpty(vehicle_type)) {
				Toast.makeText(this, "请选择派车类型", Toast.LENGTH_LONG).show();
				return;
			}
			if (ValidUtil.isNullOrEmpty(passenger)) {
				passenger = et_count.getText().toString();
				if (ValidUtil.isNullOrEmpty(passenger)) {
					Toast.makeText(this, "请选择用车人数", Toast.LENGTH_LONG).show();
					return;
				}
			}
			//用车地点
			pick_place = rb_plite_1.getText().toString();
			if (ValidUtil.isNullOrEmpty(pick_place) || pick_place.contains("请输入")) {
				Toast.makeText(this, "请选择上车地点", Toast.LENGTH_LONG).show();
				return;
			}
			//还车地点
			return_place = rb_return_1.getText().toString();
			if (ValidUtil.isNullOrEmpty(return_place) || return_place.contains("请输入")) {
				Toast.makeText(this, "请选择目的地", Toast.LENGTH_LONG).show();
				return;
			}
			//途经地点
			if (!ValidUtil.isNullOrEmpty(mid_place)) {
				if (!mid_place.contains("请输入")) {
					mid_place = rb_mid_1.getText().toString();
				}
			}

			if (ValidUtil.isNullOrEmpty(wayorround)) {
				Toast.makeText(this, "请选择出车类型", Toast.LENGTH_LONG).show();
				return;
			}
			comment = et.getText().toString();
			//用车申请
			submit(startdate, vehicle_type, passenger, pick_place, mid_place,
					return_place, wayorround, comment, order_no, VehiclesSubject);

			break;
		default:
			break;
		}
	}

	public void submit(String startdate, String vehicle_type, String passenger, String pick_place, String mid_place,
					   String return_place, String wayorround, String comment, String order_no, String VehiclesSubject)
	{
		final Map<String, String> map = new HashMap<String, String>();

		String VehiclesSubject2 = "";
		String startdate2 = "";
		try
		{
			VehiclesSubject2 = java.net.URLEncoder.encode(VehiclesSubject,"UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

		startdate2 = startdate.replaceAll(" ","%20");

		map.put("applicants_code", empName2);
		map.put("startdate", startdate2);
		map.put("vehicle_type", vehicle_type);
		map.put("passenger", passenger);
		map.put("pick_place", pick_place);
		map.put("mid_place", mid_place);
		map.put("return_place", return_place);
		map.put("wayorround", wayorround);
		map.put("comment", comment);
		map.put("VehiclesSubject", VehiclesSubject);
		map.put("diaodu", "1");
		dialog = new ProgressDialog(vehileApply_dispatchActivity.this);
		dialog.setMessage("正在提交,请等待...");
		dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
		dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
		dialog.show();
		String url = UrlManager.COMMIT_ORDER;
		//doRequest(0, url, map);
		doRequest2(12, url, map,2,1);
	}

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
			if(requestId==12)
			{
				resultStr = HttpClientUtil.HttpUrlConnectionPost6(strurl, dataMap, "UTF-8",103);
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

			//String jsonStr = "{\"data\":" + resultStr + "}";

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
						baseHandler.obtainMessage(0, requestId, 0, obj).sendToTarget();
					}
					else
					{
						baseHandler.obtainMessage(1, requestId, 0, obj).sendToTarget();
					}
				}
				catch(Exception e)
				{
					baseHandler.obtainMessage(1, requestId, 0, obj).sendToTarget();
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
			//requestAfterDialog();
			if (msg.what == 0)
			{
				onExecuteSuccess2(msg.arg1, (ResultObject)msg.obj);
			}
			else if (msg.what == 1)
			{
				onExecuteFail2(msg.arg1, (ResultObject) msg.obj);
			}
			else if (msg.what == 2)
			{
				Toast.makeText(getApplicationContext(), "暂无数据", Toast.LENGTH_SHORT).show();
			}
		};
	};

		//@Override
		protected void onExecuteSuccess2(int requestId, ResultObject result)
		{
			// TODO Auto-generated method stub
			requestAfterDialog();
			switch (requestId) {
			case 12:
				Toast.makeText(this, "提交成功!",Toast.LENGTH_LONG).show();
				finish();
				break;
			default:
				break;
			}
		}

		//@Override
		protected void onExecuteFail2(int requestId, ResultObject result) {
			// TODO Auto-generated method stub
			requestAfterDialog();
			switch (requestId) {
			case 0:
				Toast.makeText(this, "提交失败:"+result.getDesc(),Toast.LENGTH_LONG).show();
				break;

			default:
				break;
			}
		}
	    	
	    	@SuppressLint("NewApi")
			@Override
	    	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    		if(data!=null){
	    		Bundle bundle = data.getExtras();
	    		switch (resultCode) {
				case RESULT_OK:
	    			if (bundle != null) {
	    				String vehile_name=bundle.getString("type_name","");
	    				String vehile_name_no=bundle.getString("vehile_name_no","");
	    				vehicle_type=vehile_name_no;
	    				RadioButton radioButton=(RadioButton) findViewById(R.id.rb_type_5);
	    				radioButton.setText(vehile_name);
	    			}
					break;
				case 0x001:
	    			if (bundle != null) {
	    				String address=bundle.getString("address","");
	    				pick_place=address;
	    				rb_plite_1.setText(address);
	    				rb_plite_1.setTextColor(Color.parseColor("#ff8c03"));
	    				rb_plite_moren.setTextColor(Color.parseColor("#848484"));
	  			        rb_plite_moren.setBackgroundResource(R.drawable.bg_check_on);
	    			}
					break;
				case 0x002:
					if (bundle != null) {
	    				String address=bundle.getString("address","");
	    				return_place=address;
	    				rb_return_1.setText(address);
	    				rb_return_1.setTextColor(Color.parseColor("#ff8c03"));
	    				rb_return_moren.setTextColor(Color.parseColor("#848484"));
	  			        rb_return_moren.setBackgroundResource(R.drawable.bg_check_on);
	    			}
					break;
				case 0x003:
					if (bundle != null) {
	    				String address=bundle.getString("address","");
	    				mid_place=address;
	    				rb_mid_1.setText(address);
	    				rb_mid_1.setTextColor(Color.parseColor("#ff8c03"));
	    				rb_mid_moren.setTextColor(Color.parseColor("#848484"));
	  			        rb_mid_moren.setBackgroundResource(R.drawable.bg_check_on);
	    			}
					break;
				 case 0x004:
					if (bundle != null) {
	    				String reason=bundle.getString("reason","");
	    				RadioButton more=(RadioButton) findViewById(R.id.rb_resean_4);
	    				more.setChecked(true);
	    				VehiclesSubject=reason;
	    			}
					case 0x005:
						if (bundle != null) {
		    				String count=bundle.getString("count","");
		    				RadioButton radioButton=(RadioButton) findViewById(R.id.rb_count_5);
		    				radioButton.setText(count);
		    				passenger=count;
		    			}
						break;
							default:
					break;
				}
	    
	    		super.onActivityResult(requestCode, resultCode, data);
	    	}
	    	}
	  
}
