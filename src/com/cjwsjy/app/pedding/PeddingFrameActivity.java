package com.cjwsjy.app.pedding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.cjwsjy.app.ActivityLogin2;
import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.item.ConSignItem;
import com.cjwsjy.app.item.DeptSignItem;
import com.cjwsjy.app.item.LeaderSignItem;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.ImageService;
import com.cjwsjy.app.utils.UrlUtil;
import com.do1.cjmobileoa.db.DBManager;
import com.do1.cjmobileoa.db.model.DepartmentVO;
import com.sqk.GridView.CustomViewPagerAdapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint({ "InlinedApi", "NewApi" })
public class PeddingFrameActivity extends FragmentActivity {
	private ViewPager m_vp;

	private GigestFragment mfragment1;
	private LeaderShipFragment mfragment2;
	private DeptFragment mfragment3;
	private ConSignFragment mfragment4;
	// 页面列表
	private List<Fragment> fragmentList = new ArrayList<Fragment>();

	private ImageView ivBottomLine;
	private TextView tvTab1, tvTab2, tvTab3, tvTab4;

	private int currIndex = 0;
	private int bottomLineWidth;
	private int offset = 0;
	private int position_one;
	private int position_two;
	private int position_three;
	private Resources resources;
	
	
	private SharedPreferences sp;
	
	private String formId="";
	private String signTaskId="";
	private String signFlag="";
	private String taskStep="";
	
	//json字符串
	private String jsonStr;
	private List<LeaderSignItem> listLeaderItems= new ArrayList<LeaderSignItem>();
	private List<DeptSignItem> listDeptItems= new ArrayList<DeptSignItem>();
	private List<ConSignItem> listConItems= new ArrayList<ConSignItem>();
	
	private List<DepartmentVO> deptList = new ArrayList<DepartmentVO>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.peddingframe_layout);
		
		TextView tv_navtitle = (TextView)findViewById(R.id.tv_navtitle);
	    tv_navtitle.setText("院签报");
		
	    // 后退
	    ImageView iv_back = (ImageView) this.findViewById(R.id.iv_back);
		iv_back.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
	    
	  // 新页面接收数据
		Bundle bundle = this.getIntent().getExtras();
		// 接收title值
		this.setFormId(bundle.getString("formId"));
		this.setSignTaskId(bundle.getString("taskId"));
		this.setTaskStep(bundle.getString("taskStep"));
		//String formId = bundle.getString("formId");
		//Toast.makeText(PeddingFrameActivity.this, formId, Toast.LENGTH_SHORT).show();
		this.setSignFlag(bundle.getString("signFlag"));
		
		//获取当前用户部门
		 sp = SmApplication.sp;
		 String loginname = sp.getString("USERDATA.LOGIN.NAME", "");  
		 DBManager dbManager =SmApplication.dbManager;
		 deptList = dbManager.findUserOrgs(loginname);
		 this.setDeptList(deptList);
		 
        //签报列表
		String httpResult = "";
		String appUrl = UrlUtil.HOST;
		String url = appUrl+"/CEGWAPServer/YQB/getYQBFormDetail/"+bundle.getString("formId");
		
	    httpResult = HttpClientUtil.HttpUrlConnectionGet(url, "UTF-8");
		this.setJsonStr(httpResult);

		httpResult = "{\"data\":" + httpResult + "}";
		try
		{
			JSONArray jsonObjs = new JSONObject(httpResult).getJSONArray("data");
			// Toast.makeText(getActivity(), "aaa:"+jsonObjs.length(),
			// Toast.LENGTH_SHORT).show();
			JSONArray jsonObjs1 = jsonObjs.getJSONArray(2);

			for (int i = 0; i < jsonObjs1.length(); i++)
			{
				JSONObject jsonObj = jsonObjs1.getJSONObject(i);

				String createDate = jsonObj.getString("SignDate");
				createDate = createDate.split("T")[0];

				if ("院领导批示".equals(jsonObj.getString("SignStep")))
				{
					LeaderSignItem leaderSignItem = new LeaderSignItem();
					leaderSignItem.setTv_description(jsonObj.getString("Advice"));

					String signUser = jsonObj.getString("SignUser");
					String formSignId = jsonObj.getString("FormSignID");
					String signStr = UrlUtil.USER_SIGN + formSignId + "/" + signUser;
					// String signStr = jsonObj.getString("SignPicUrl");

					byte[] data = ImageService.getImage(signStr);
					Bitmap bitmap1 = BitmapFactory.decodeByteArray(data, 0, data.length); // 生成位图
					DisplayMetrics dm = this.getResources().getDisplayMetrics();
					int width = (int) (bitmap1.getWidth() * (dm.density) * 0.6);
					int height = (int) (bitmap1.getHeight() * (dm.density) * 0.6);
					Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap1, width, height, false);

					if (!bitmap1.isRecycled())
					{
						bitmap1.recycle();
					}
					leaderSignItem.setImageBitmap(bitmap2);

					leaderSignItem.setTv_date(createDate);

					listLeaderItems.add(leaderSignItem);
				}
				else if ("综合管理部意见".equals(jsonObj.getString("SignStep")))
				{
					DeptSignItem deptSignItem = new DeptSignItem();
					deptSignItem.setDept_description(jsonObj.getString("Advice"));

					String signUser = jsonObj.getString("SignUser");
					String formSignId = jsonObj.getString("FormSignID");
					String signStr = UrlUtil.USER_SIGN + formSignId + "/" + signUser;
					// String signStr = jsonObj.getString("SignPicUrl");
					byte[] data = ImageService.getImage(signStr);
					Bitmap bitmap1 = BitmapFactory.decodeByteArray(data, 0, data.length); // 生成位图
					DisplayMetrics dm = this.getResources().getDisplayMetrics();
					int width = (int) (bitmap1.getWidth() * (dm.density) * 0.6);
					int height = (int) (bitmap1.getHeight() * (dm.density) * 0.6);
					Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap1, width, height, false);

					if (!bitmap1.isRecycled())
					{
						bitmap1.recycle();
					}
					deptSignItem.setImageBitmap(bitmap2);
					deptSignItem.setDept_date(createDate);

					listDeptItems.add(deptSignItem);
				}
				else if ("会签".equals(jsonObj.getString("SignStep")))
				{
					ConSignItem conSignItem = new ConSignItem();

					conSignItem.setCon_description(jsonObj.getString("Advice"));
					String signUser = jsonObj.getString("SignUser");
					String formSignId = jsonObj.getString("FormSignID");
					String signStr = UrlUtil.USER_SIGN + formSignId + "/" + signUser;
					// String signStr = jsonObj.getString("SignPicUrl");
					byte[] data = ImageService.getImage(signStr);
					Bitmap bitmap1 = BitmapFactory.decodeByteArray(data, 0, data.length); // 生成位图
					DisplayMetrics dm = this.getResources().getDisplayMetrics();
					int width = (int) (bitmap1.getWidth() * (dm.density) * 0.6);
					int height = (int) (bitmap1.getHeight() * (dm.density) * 0.6);
					Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap1, width, height, false);

					if (!bitmap1.isRecycled())
					{
						bitmap1.recycle();
					}
					conSignItem.setImageBitmap(bitmap2);
					conSignItem.setCon_date(createDate);

					listConItems.add(conSignItem);
				}
			}
		}
		catch (Exception e)
		{
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

		this.setListLeaderItems(listLeaderItems);
		this.setListDeptItems(listDeptItems);
		this.setListConItems(listConItems);
		
		resources = getResources();
		initWidth();
		initView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void initView() {
		tvTab1 = (TextView) findViewById(R.id.tv_tab1);
		tvTab2 = (TextView) findViewById(R.id.tv_tab2);
		tvTab3 = (TextView) findViewById(R.id.tv_tab3);
		tvTab4 = (TextView) findViewById(R.id.tv_tab4);

		tvTab1.setOnClickListener(new CustomOnClickListener(0));
		tvTab2.setOnClickListener(new CustomOnClickListener(1));
		tvTab3.setOnClickListener(new CustomOnClickListener(2));
		tvTab4.setOnClickListener(new CustomOnClickListener(3));

		m_vp = (ViewPager) findViewById(R.id.viewpager);

		mfragment1 = new GigestFragment();
		mfragment2 = new LeaderShipFragment();
		mfragment3 = new DeptFragment();
		mfragment4 = new ConSignFragment();
		//fragmentList = new ArrayList<Fragment>();
		fragmentList.add(mfragment1);
		fragmentList.add(mfragment2);
		fragmentList.add(mfragment3);
		fragmentList.add(mfragment4);

		m_vp.setCurrentItem(0);	
		m_vp.setOffscreenPageLimit(4);
		m_vp.setAdapter(new CustomViewPagerAdapter(getSupportFragmentManager(),
				fragmentList));
		m_vp.setOnPageChangeListener(new CustomOnPageChangeListener());

	}

	private void initWidth() {
		ivBottomLine = (ImageView) findViewById(R.id.iv_bottom_line);
		bottomLineWidth = ivBottomLine.getLayoutParams().width;

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (int) ((screenW / 4.0 - bottomLineWidth) / 2);

		position_one = (int) (screenW / 4.0);
		position_two = position_one * 2;
		position_three = position_one * 3;
	}

	public class CustomOnClickListener implements View.OnClickListener {
		private int index = 0;

		public CustomOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			m_vp.setCurrentItem(index);
		}
	};

	public class CustomOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(position_one, 0, 0, 0);
					tvTab2.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(position_two, 0, 0, 0);
					tvTab3.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(position_three, 0, 0, 0);
					tvTab4.setTextColor(resources.getColor(R.color.lightwhite));
				}
				tvTab1.setTextColor(resources.getColor(R.color.lightCur));
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, position_one, 0,
							0);
					tvTab1.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(position_two,
							position_one, 0, 0);
					tvTab3.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(position_three,
							position_one, 0, 0);
					tvTab4.setTextColor(resources.getColor(R.color.lightwhite));
				}
				tvTab2.setTextColor(resources.getColor(R.color.lightCur));
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, position_two, 0,
							0);
					tvTab1.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(position_one,
							position_two, 0, 0);
					tvTab2.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(position_three,
							position_two, 0, 0);
					tvTab4.setTextColor(resources.getColor(R.color.lightwhite));
				}
				tvTab3.setTextColor(resources.getColor(R.color.lightCur));
				break;
			case 3:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, position_three,
							0, 0);
					tvTab1.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(position_one,
							position_three, 0, 0);
					tvTab2.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(position_two,
							position_three, 0, 0);
					tvTab3.setTextColor(resources.getColor(R.color.lightwhite));
				}
				tvTab4.setTextColor(resources.getColor(R.color.lightCur));
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
			ivBottomLine.startAnimation(animation);
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO 自动生成的方法存根

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO 自动生成的方法存根

		}
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		
		int home = 0;
		home = SmApplication.gethomekey();

		if(home==1)
		{
			Intent intent = new Intent(this, ActivityLogin2.class);
			startActivity(intent);
		}
	}
	
	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getSignTaskId() {
		return signTaskId;
	}

	public void setSignTaskId(String signTaskId) {
		this.signTaskId = signTaskId;
	}

	public String getSignFlag() {
		return signFlag;
	}

	public void setSignFlag(String signFlag) {
		this.signFlag = signFlag;
	}

	public String getJsonStr() {
		return jsonStr;
	}

	public void setJsonStr(String jsonStr) {
		this.jsonStr = jsonStr;
	}

	public List<LeaderSignItem> getListLeaderItems() {
		return listLeaderItems;
	}

	public void setListLeaderItems(List<LeaderSignItem> listLeaderItems) {
		this.listLeaderItems = listLeaderItems;
	}

	public List<DeptSignItem> getListDeptItems() {
		return listDeptItems;
	}

	public void setListDeptItems(List<DeptSignItem> listDeptItems) {
		this.listDeptItems = listDeptItems;
	}

	public List<ConSignItem> getListConItems() {
		return listConItems;
	}

	public void setListConItems(List<ConSignItem> listConItems) {
		this.listConItems = listConItems;
	}

	public List<DepartmentVO> getDeptList() {
		return deptList;
	}

	public void setDeptList(List<DepartmentVO> deptList) {
		this.deptList = deptList;
	}

	public String getTaskStep()
	{
		return taskStep;
	}

	public void setTaskStep(String taskStep)
	{
		this.taskStep = taskStep;
	}
	
	
}
