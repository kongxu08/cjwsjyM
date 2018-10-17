package com.cjwsjy.app.outoffice;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.utils.StreamTool;
import com.sqk.GridView.List_Item;
import com.sqk.GridView.NewListAdaper;

public class RegisterActivity extends Activity implements View.OnTouchListener{
	
	private SharedPreferences sp;
	
	private static final String[] arr = {"上午","下午"};
	
	private EditText et_outTime;
	private EditText et_backTime;
	private Spinner spinner1;
	private Spinner spinner2;
	private EditText et_submitUser;
	private NewListAdaper listAdaper;
	private List<List_Item> listItems = new ArrayList<List_Item>();
	private String outOfOfficeCode="";
	private  ListView  listView;
	private  TextView tv_submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//			.detectDiskReads().detectDiskWrites().detectAll().penaltyLog().build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//			.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
//			.penaltyLog().build());
        
        setContentView(R.layout.outoffice_register);
        
        spinner1 = (Spinner)findViewById(R.id.spinner1);
        spinner2 = (Spinner)findViewById(R.id.spinner2);
        ArrayAdapter<String> ada = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arr);
        ada.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(ada);
        spinner1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){    
        	            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {    
        	                // TODO Auto-generated method stub                       
        	                arg0.setVisibility(View.VISIBLE);    
        	            }

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
							// TODO 自动生成的方法存根  
							parent.setVisibility(View.VISIBLE);   

						}    

        });
        
        spinner2.setAdapter(ada);
        
        et_outTime = (EditText) findViewById(R.id.et_outTime);
        et_backTime = (EditText) findViewById(R.id.et_backTime);
        et_outTime.setOnTouchListener(this); 
        et_backTime.setOnTouchListener(this); 
        
        //审核人查询
        et_submitUser = (EditText) findViewById(R.id.et_submitUser);
        listView = (ListView)findViewById(R.id.lv_list); 
        

		listAdaper = new NewListAdaper(RegisterActivity.this, listItems);
		listView.setAdapter(listAdaper);
        
		listView.setOnItemClickListener(new OnItemClickListener() {// listView点击事件
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				List_Item aa= (List_Item)listAdaper.getItem(position);
				et_submitUser.setText(aa.getTv_title());
				listView.setVisibility(View.GONE);
			}
		});
		
		et_submitUser.addTextChangedListener(new TextWatcher() {// EditText变化监听
			/**
			 * 正在输入
			 */
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				listItems = new ArrayList<List_Item>();// 每次输入的时候，重新初始化数据列表

				if (!TextUtils.isEmpty(et_submitUser.getText().toString())) {// 判断输入内容是否为空，为空则跳过

					// 查询数据
					//listItems = DatabaseAdapter.getIntance(RegisterActivity.this).queryInfo("aaa");
				}

				//listAdaper.refreshData(listItems);// Adapter刷新数据
				listView.setVisibility(View.VISIBLE);
			}

			/**
			 * 输入之前
			 */
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			/**
			 * 输入之后
			 */
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		//-------end---------------------//
		
		
       	try {
       	//外出登记获取流水号
    		String url ="http://10.6.189.35:8088/CEGWAPServer/out/getSerialNumber/15B72A8F-B3F2-4F2B-A742-A5E7CA4121A3";
			outOfOfficeCode = submitRegister(url,"");
		} catch (Exception e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}
        
        tv_submit =(TextView) findViewById(R.id.tv_submit);

        tv_submit.setOnClickListener(new OnClickListener() {           
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            	sp = SmApplication.sp;
            	
                EditText et_address =(EditText) findViewById(R.id.et_address);
                EditText et_event =(EditText) findViewById(R.id.et_event);
                
                String outTimeFrom = et_outTime.getText().toString();
                String outTimeFromMeridian =spinner1.getSelectedItem().toString();
                String outTimeTo = et_backTime.getText().toString();
                String outTimeToMeridian =spinner2.getSelectedItem().toString();
                String address = et_address.getText().toString();
                String event = et_event.getText().toString();

        		try {

    	           	String path = "http://10.6.189.35:8088/CEGWAPServer/out/updateOutOfOffice/712D8D4C-85E4-4F88-86B2-A1380B4CD056/edit"; 
           			String params = "[{\"userID\":\"15B72A8F-B3F2-4F2B-A742-A5E7CA4121A3\",\"outTimeFrom\":\""+outTimeFrom+"\",\"outTimeFromMeridian\":\""+outTimeFromMeridian+"\",\"outTimeTo\":\""+outTimeTo+"\",\"outTimeToMeridian\":\""+outTimeToMeridian+"\",\"address\":\""+address+"\",\"event\":\""+event+"\",\"step1_UserName\":\"xuli2\",\"code\":\""+outOfOfficeCode+"\"}]";
					Log.d("TagTest","params:"+params);
           			String result =submitRegister(path,params);
					
					Log.d("TagTest", result);
					
					Intent intent = new Intent(RegisterActivity.this, OutOfficeListActivity.class);
					startActivity(intent);
					
					//finish();
				} catch (Exception e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
            }
        });
        
    }
    
    public String  submitRegister(String path,String params) throws Exception{
    	String result ="";
    	
    	String encoding = "UTF-8";
    	byte[] data = params.getBytes(encoding);
    	URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		// application/x-javascript text/xml->xml数据
		// application/x-javascript->json对象
		// application/x-www-form-urlencoded->表单数据
		conn.setRequestProperty("Content-Type","application/x-javascript; charset=" + encoding);
		conn.setRequestProperty("Content-Length", String.valueOf(data.length));
		conn.setConnectTimeout(5 * 1000);
		OutputStream outStream = conn.getOutputStream();
		outStream.write(data);
		outStream.flush();
		outStream.close();
		System.out.println(conn.getResponseCode()); // 响应代码 200表示成功
		if (conn.getResponseCode() == 200) {
			InputStream inStream = conn.getInputStream();
			result = new String(StreamTool.inputStream2Byte(inStream),"UTF-8");
			//System.out.println("获取返回结果："+result);
			//Log.d("TagTest",result);
		}
		
         return result;
    
    }
    
    @Override 
    public boolean onTouch(View v, MotionEvent event) { 
    	if (event.getAction() == MotionEvent.ACTION_DOWN) { 
    		   
            AlertDialog.Builder builder = new AlertDialog.Builder(this); 
            View view = View.inflate(this, R.layout.date_time_dialog, null); 
            final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker); 
           // final TimePicker timePicker = (android.widget.TimePicker) view.findViewById(R.id.time_picker); 
            builder.setView(view); 
   
            Calendar cal = Calendar.getInstance(); 
            cal.setTimeInMillis(System.currentTimeMillis()); 
            datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null); 
   
            //timePicker.setIs24HourView(true); 
            //timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY)); 
            //timePicker.setCurrentMinute(Calendar.MINUTE); 
            
            if (v.getId() == R.id.et_outTime) { 
                final int inType = et_outTime.getInputType(); 
                et_outTime.setInputType(InputType.TYPE_NULL); 
                et_outTime.onTouchEvent(event); 
                et_outTime.setInputType(inType); 
                et_outTime.setSelection(et_outTime.getText().length()); 
                   
                //builder.setTitle("选取起始时间"); 
                builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() { 
   
                    @Override 
                    public void onClick(DialogInterface dialog, int which) { 
   
                        StringBuffer sb = new StringBuffer(); 
                        sb.append(String.format("%d-%02d-%02d",  
                                datePicker.getYear(),  
                                datePicker.getMonth() + 1, 
                                datePicker.getDayOfMonth())); 
                        //sb.append("  "); 
                        //sb.append(timePicker.getCurrentHour()) 
                        //.append(":").append(timePicker.getCurrentMinute()); 
   
                        et_outTime.setText(sb); 
                        et_backTime.requestFocus(); 
                           
                        dialog.cancel(); 
                    } 
                }); 
                   
            } else if (v.getId() == R.id.et_backTime) { 
                int inType = et_backTime.getInputType(); 
                et_backTime.setInputType(InputType.TYPE_NULL);     
                et_backTime.onTouchEvent(event); 
                et_backTime.setInputType(inType); 
                et_backTime.setSelection(et_backTime.getText().length()); 
   
                //builder.setTitle("选取结束时间"); 
                builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() { 
   
                    @Override 
                    public void onClick(DialogInterface dialog, int which) { 
   
                        StringBuffer sb = new StringBuffer(); 
                        sb.append(String.format("%d-%02d-%02d",  
                                datePicker.getYear(),  
                                datePicker.getMonth() + 1,  
                                datePicker.getDayOfMonth())); 
                        //sb.append("  "); 
                        //sb.append(timePicker.getCurrentHour()) 
                        //.append(":").append(timePicker.getCurrentMinute()); 
                        et_backTime.setText(sb); 
                           
                        dialog.cancel(); 
                    } 
                }); 
            } 
               
            Dialog dialog = builder.create(); 
            dialog.show(); 
    	 }
    	
    	return true;
    }
}
