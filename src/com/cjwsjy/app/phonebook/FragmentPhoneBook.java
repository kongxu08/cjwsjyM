package com.cjwsjy.app.phonebook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.main.MainActivity;
import com.cjwsjy.app.utils.StringHelper;
import com.cjwsjy.app.utils.ThreadUtils;
import com.cjwsjy.app.utils.ValidUtil;
import com.do1.cjmobileoa.db.DBManager;
import com.do1.cjmobileoa.db.model.DepartmentVO;
import com.do1.cjmobileoa.db.model.EmployeeVO;
import com.cjwsjy.app.phonebook.OrgListAdaper;
//import com.cjwsjy.app.phonebook.UserListAdaper;
import com.cjwsjy.app.phonebook.Org;
import com.cjwsjy.app.phonebook.ScrollLayout;

/**
 * 通讯录 fragment
 */
public class FragmentPhoneBook extends Fragment 
{
	private final static int SCREEN_FIRST = 0;
	private final static int SCREEN_SECOND = 1;
	private final static int SCREEN_THIRD = 2;
	
	private static ScrollLayout mScrollLayout;
	private static ListView[] listviews;
	private static OrgListAdaper firstOrgsAdapter, secondOrgsAdapter,lastOrgsAdapter;
	
	private static int curScreen = SCREEN_FIRST;// 默认当前屏幕  保存当前的listview的index

	//private List<Org> firstListOrg =new ArrayList<Org>();
	//private List<Org> secondListOrg =new ArrayList<Org>();
	//private List<Org> lastListOrg =new ArrayList<Org>();

	private int search = 0;
	private DBManager db;
	private List<EmployeeVO>[] employees;
	private List<DepartmentVO>[] departments;
	private List<DepartmentVO> listdepartments;
	private List<EmployeeVO> emplistsearch;
	private List<EmployeeVO> employees0,employees1,employees2,employees3,employees4,
												employees5,employees6,employees7,employees8,employees9;
												
	private List<DepartmentVO> departments0,departments1,departments2,departments3,departments4,
													departments5,departments6,departments7,departments8,departments9;

	private EditText searchValue;
	private ImageView search_delete;
	private String deptParentid="A90AEAEC-E3D4-43DE-BB67-85407B57B171";
	private lvButtonAdapter listItemAdapter;
	//private ListView empList;

	private String[] nametitles;
	//标题栏
	private TextView textview;
	
	private MainActivity mainactivity;
	
	//定义进入收藏界面变量
	public String collectFlag="0";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		return inflater.inflate(R.layout.fragment_phonebook, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		//db = new DBManager(this.getActivity());
		db = SmApplication.dbManager;

		Thread thread = new Thread(new ThreadDatabase());
		thread.start();

		//初始化通讯录列表
		//initFirstLevelList(deptParentid);
		
		searchValue = (EditText) getView().findViewById(R.id.search_edt);

		//绑定事件,判断焦点事件
		searchValue.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if (hasFocus)
				{
					// 此处为得到焦点时的处理内容
					ThreadUtils emperor1 = ThreadUtils.getInstance();  // 得到单例对象
					emperor1.setparm2("点击", "搜索", "通讯录搜索");
					emperor1.writelog();

				}
				else
				{
					// 此处为失去焦点时的处理内容
				}
			}
		});

		// 给编辑框添加文本改变事件
		searchValue.addTextChangedListener(new MyTextWatcher());

		searchValue.setOnEditorActionListener(new EditText.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				android.util.Log.d("cjwsjy", "------actionId="+actionId+"-------");

				// public static final int IME_ACTION_SEARCH = 0x00000003;
				// public static final int IME_ACTION_DONE = 0x00000006;
				if( actionId==EditorInfo.IME_ACTION_DONE || actionId==EditorInfo.IME_ACTION_SEARCH  )
				{
					InputMethodManager imm = ( InputMethodManager )v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow( v.getWindowToken(), 0 );

					return true;
				}
				return false;
			}
		});
	}

	class ThreadDatabase implements Runnable
	{
		@Override
		public void run()
		{
			DownImg();
		}
	}

	private int DownImg()
	{
		try
		{
			initFirstLevelList(deptParentid);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return 1;
	}

	private void hidekeyboard()
	{
		InputMethodManager imm = ( InputMethodManager )searchValue.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow( searchValue.getWindowToken(), 0 );
	}

	//初始化通讯录列表
	public void initFirstLevelList(String deptParentid)
	{	
		int i = 0;
		int size = 0;
		
		departments0 = new ArrayList<DepartmentVO>();
		departments1 = new ArrayList<DepartmentVO>();
		departments2 = new ArrayList<DepartmentVO>();
		departments3 = new ArrayList<DepartmentVO>();
		departments4 = new ArrayList<DepartmentVO>();
		departments5 = new ArrayList<DepartmentVO>();
		departments6 = new ArrayList<DepartmentVO>();
		departments7 = new ArrayList<DepartmentVO>();
		departments8 = new ArrayList<DepartmentVO>();
		departments9 = new ArrayList<DepartmentVO>();
		
		employees0 = new ArrayList<EmployeeVO>();
		employees1 = new ArrayList<EmployeeVO>();
		employees2 = new ArrayList<EmployeeVO>();
		employees3 = new ArrayList<EmployeeVO>();
		employees4 = new ArrayList<EmployeeVO>();
		employees5 = new ArrayList<EmployeeVO>();
		employees6 = new ArrayList<EmployeeVO>();
		employees7 = new ArrayList<EmployeeVO>();
		employees8 = new ArrayList<EmployeeVO>();
		employees9 = new ArrayList<EmployeeVO>();
		
		mScrollLayout = (ScrollLayout)getView().findViewById(R.id.scrolllayout);
		mScrollLayout.setIsScroll(false);
		//mScrollLayout.setIsScroll(true);

		nametitles = new String[10];
		nametitles[0] = "通讯录";

		//获得ListView对象
		listviews = new ListView[10];
		listviews[0] = (ListView)getView().findViewById(R.id.lv_first);
		listviews[1] = (ListView)getView().findViewById(R.id.lv_second);
		listviews[2] = (ListView)getView().findViewById(R.id.lv_third);
		listviews[3] = (ListView)getView().findViewById(R.id.lv_fourth);
		listviews[4] = (ListView)getView().findViewById(R.id.lv_fifth);
		listviews[5] = (ListView)getView().findViewById(R.id.lv_sixth);
		listviews[6] = (ListView)getView().findViewById(R.id.lv_seventh);
		listviews[7] = (ListView)getView().findViewById(R.id.lv_eighth);
		listviews[8] = (ListView)getView().findViewById(R.id.lv_ninth);

		DepartmentVO entity1 = new DepartmentVO();
		entity1.setId("0");
		entity1.setDeptId("0");
		entity1.setDeptDisplayname("智能拨号");
		departments0.add(entity1);
		
		DepartmentVO entity2 = new DepartmentVO();
		entity2.setId("1");
		entity2.setDeptId("1");
		entity2.setDeptDisplayname("我的收藏");
		departments0.add(entity2);

		listdepartments = db.findDepartments(deptParentid);

//		size = listdepartments.size();
//		if( size==0 )
//		{
//			listdepartments = db.findDepartments(deptParentid);
//		}

		//查找，得到1级部门
		for(DepartmentVO entity:listdepartments)
		{
			departments0.add(entity);
		}
		//departments0 = db.findDepartments(deptParentid);
		//departments[0] = departments0;

		// 生成动态数组，加入数据
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();

		i = 0;
		String indexstr;
		for (DepartmentVO entity : departments0) 
		{
			indexstr = String.valueOf(i);
			// HashMap为键值对类型。第一个参数为键，第二个参数为值
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("type", "department");
			map.put("index", indexstr);
			map.put("userName", entity.getDeptDisplayname());
			listItem.add(map);// 添加到listItem中
			i++;
		}

		// 自定义适配器
		lvButtonAdapter listItemAdapter = new lvButtonAdapter( this.getActivity(), listItem);

		// 添加并显示
		listviews[0].setAdapter(listItemAdapter);
		search = 0;
		
		//单击响应
		listviews[0].setOnItemClickListener(mOnItemClickFirst);
		listviews[1].setOnItemClickListener(mOnItemClickSecond);
		listviews[2].setOnItemClickListener(mOnItemClickThird);
		listviews[3].setOnItemClickListener(mOnItemClickFourth);
		listviews[4].setOnItemClickListener(mOnItemClickFifth);
		listviews[5].setOnItemClickListener(mOnItemClickSixth);
		listviews[6].setOnItemClickListener(mOnItemClickSeventh);
		listviews[7].setOnItemClickListener(mOnItemClickEighth);
		listviews[8].setOnItemClickListener(mOnItemClickNinth);
		
//		for( i=0; i<10; i++ )
//		{
//			listviews[i].setOnItemClickListener(mOnItemClick);
//		}
	}

	//listview单击事件
	private OnItemClickListener mOnItemClick = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int index, long id)
		{
			String deptParentid;

			HashMap<String, Object> appInfo = (HashMap<String, Object>) listviews[curScreen].getItemAtPosition(index);
			String type = (String) appInfo.get("type");

			// 进入员工详细界面
			if (StringHelper.equals(type, "employee"))
			{
				EmployeeVO ep;
				if (search == 1)
				{
					ep = emplistsearch.get(index);
				}
				else
					ep = employees[curScreen].get(index);

				// 跳转到员工详细界面
				mainactivity.showEmployee(ep,2);
			}
			//进入下级部门界面
			else if (StringHelper.equals(type, "department"))
			{
				curScreen++;

				// 滑动到下级页面
				mScrollLayout.scrollToScreen(curScreen);

				DepartmentVO entity = departments[index].get(index);
				deptParentid = entity.getDeptId();
				nametitles[curScreen] = entity.getDeptDisplayname();

				// 设置标题栏
				textview.setText(nametitles[curScreen]);

				// 下级列表中，员工数据
				departments[curScreen] = db.findDepartments(deptParentid);

				// 下级列表中，员工数据
				employees[curScreen] = db.findEmployeeBydept(deptParentid, 1);

				// 填充数据到listview
				initListItem(listviews[curScreen], departments[curScreen], employees[curScreen]);
				search = 0;
			}
		}
	};
	
	//1级listview单击事件
	private OnItemClickListener mOnItemClickFirst = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int index,long id)
		{
			String deptParentid;
			
			int sign = 0;
			HashMap<String, Object> appInfo = (HashMap<String, Object>)listviews[0].getItemAtPosition(index);
			String type = (String) appInfo.get("type");
			String indexstr = (String) appInfo.get("index");
			String deptName = (String) appInfo.get("userName");  //名称
			
			if( indexstr==null ) 
			{
				Toast.makeText(getActivity(),"失败代码110103", Toast.LENGTH_LONG).show();
				return;
			}
			sign = Integer.valueOf(indexstr).intValue();
			
			if( deptName.equals("智能拨号") )
			{
				mainactivity.onTab(1);
				return;
			}
			
			if("我的收藏".equals(deptName))
			{
				curScreen++;
				
				// 滑动到下级页面
				mScrollLayout.scrollToScreen(curScreen);
				
				//下级列表中，员工数据
				employees1 = db.findCollect();
				List<DepartmentVO> department = new ArrayList<DepartmentVO>();
				
				departments1.clear();
				
				//填充数据到listview
				initListItem( listviews[curScreen], department, employees1 );
				search=0;
				
				collectFlag="1";
				return;
			}
			
			//进入员工详细界面
			if (StringHelper.equals(type, "employee")) 
			{
				EmployeeVO ep;
				if( search==1 )
				{
					ep = emplistsearch.get(index);
				}
				else  ep = employees0.get(sign);

				//隐藏软键盘
				hidekeyboard();

				//跳转到员工详细界面
				mainactivity.showEmployee(ep,2);
				
				collectFlag="0";
			}
			else if (StringHelper.equals(type, "department")) 
			{
				curScreen++;
				
				// 滑动到下级页面
				mScrollLayout.scrollToScreen(curScreen);
				
				DepartmentVO entity = departments0.get(sign);
				deptParentid = entity.getDeptId();
				nametitles[curScreen] = entity.getDeptDisplayname();
				
				//设置标题栏
				textview.setText(nametitles[curScreen]);
				
				//下级列表中，员工数据
				departments1 = db.findDepartments(deptParentid);
	
				//下级列表中，员工数据
				employees1 = db.findEmployeeBydept(deptParentid,1);
	
				//填充数据到listview
				initListItem( listviews[curScreen], departments1, employees1 );
				search = 0;
				
				collectFlag="0";
			}
		}
	};

	//2级listview单击事件
	private OnItemClickListener mOnItemClickSecond = new OnItemClickListener() 
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int index, long id) 
		{
			String deptParentid;

			int sign = 0;
			HashMap<String, Object> appInfo = (HashMap<String, Object>)listviews[1].getItemAtPosition(index);
			String type = (String) appInfo.get("type");
			String indexstr = (String) appInfo.get("index");

			if(indexstr==null) 
			{
				Toast.makeText(getActivity(),"失败代码110104", Toast.LENGTH_LONG).show();
				return;
			}
			sign = Integer.valueOf(indexstr).intValue();
			
			//进入员工详细界面
			if (StringHelper.equals(type, "employee")) 
			{
				EmployeeVO ep;
				if( search==1 )
				{
					ep = emplistsearch.get(index);
				}
				else ep = employees1.get(sign);

				//隐藏软键盘
				hidekeyboard();

				//跳转到员工详细界面
				mainactivity.showEmployee(ep,2);
			}
			else if (StringHelper.equals(type, "department")) 
			{
				curScreen++;
				
				// 滑动到下级页面
				mScrollLayout.scrollToScreen(curScreen);
				
				DepartmentVO entity = departments1.get(sign);
				deptParentid = entity.getDeptId();
				nametitles[curScreen] = entity.getDeptDisplayname();
				
				//设置标题栏
				textview.setText(nametitles[curScreen]);
				
				//下级列表中，部门数据
				departments2 = db.findDepartments(deptParentid);
				
				//下级列表中，员工数据
				employees2 = db.findEmployeeBydept(deptParentid,2);
	
				//填充数据到listview
				initListItem( listviews[curScreen], departments2, employees2 );
				search = 0;
			}
		}
	};
	
	//3级listview单击事件
	private OnItemClickListener mOnItemClickThird = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int index,long id)
		{
			String deptParentid;

			int sign = 0;
			HashMap<String, Object> appInfo = (HashMap<String, Object>)listviews[2].getItemAtPosition(index);
			String type = (String) appInfo.get("type");
			String indexstr = (String) appInfo.get("index");
			
			if(indexstr==null) 
			{
				Toast.makeText(getActivity(),"失败代码110105", Toast.LENGTH_LONG).show();
				return;
			}
			sign = Integer.valueOf(indexstr).intValue();
			
			//进入员工详细界面
			if (StringHelper.equals(type, "employee")) 
			{
				EmployeeVO ep;
				if( search==1 )
				{
					ep = emplistsearch.get(index);
				}
				else ep = employees2.get(sign);

				//隐藏软键盘
				hidekeyboard();

				//跳转到员工详细界面
				mainactivity.showEmployee(ep,2);
			}
			else if (StringHelper.equals(type, "department")) 
			{
				// 滑动到下级页面
				curScreen++;
				
				// 滑动到下级页面
				mScrollLayout.scrollToScreen(curScreen);
				
				//部门
				DepartmentVO entity = departments2.get(sign);
				deptParentid = entity.getDeptId();
				nametitles[curScreen] = entity.getDeptDisplayname();
	
				//设置标题栏
				textview.setText(nametitles[curScreen]);
				
				// 下级列表中，员工数据
				departments3 = db.findDepartments(deptParentid);
	
				// 下级列表中，员工数据
				employees3 = db.findEmployeeBydept(deptParentid,3);
	
				// 填充数据到listview
				initListItem(listviews[curScreen], departments3, employees3);
				search = 0;
			}
		}
	};

	//4级listview单击事件
	private OnItemClickListener mOnItemClickFourth = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int index,long id)
		{
			String deptParentid;
			
			int sign = 0;
			HashMap<String, Object> appInfo = (HashMap<String, Object>)listviews[3].getItemAtPosition(index);
			String type = (String) appInfo.get("type");
			String indexstr = (String) appInfo.get("index");

			if(indexstr==null) 
			{
				Toast.makeText(getActivity(),"失败代码110106", Toast.LENGTH_LONG).show();
				return;
			}
			sign = Integer.valueOf(indexstr).intValue();
			
			//进入员工详细界面
			if (StringHelper.equals(type, "employee")) 
			{
				EmployeeVO ep;
				if( search==1 )
				{
					ep = emplistsearch.get(index);
				}
				else ep = employees3.get(sign);

				//隐藏软键盘
				hidekeyboard();

				//跳转到员工详细界面
				mainactivity.showEmployee(ep,2);
			}
			else if (StringHelper.equals(type, "department")) 
			{
				curScreen++;
	
				// 滑动到下级页面
				mScrollLayout.scrollToScreen(curScreen);
	
				DepartmentVO entity = departments3.get(sign);
				deptParentid = entity.getDeptId();
				nametitles[curScreen] = entity.getDeptDisplayname();
	
				//设置标题栏
				textview.setText(nametitles[curScreen]);
	
				// 下级列表中，员工数据
				departments4 = db.findDepartments(deptParentid);
	
				// 下级列表中，员工数据
				employees4 = db.findEmployeeBydept(deptParentid,4);
	
				// 填充数据到listview
				initListItem(listviews[curScreen], departments4, employees4);
				search = 0;
			}
		}
	};
	
	//5级listview单击事件
	private OnItemClickListener mOnItemClickFifth = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int index, long id)
		{
			String deptParentid;
			
			int sign = 0;
			HashMap<String, Object> appInfo = (HashMap<String, Object>)listviews[4].getItemAtPosition(index);
			String type = (String) appInfo.get("type");
			String indexstr = (String) appInfo.get("index");

			if(indexstr==null) 
			{
				Toast.makeText(getActivity(),"失败代码110107", Toast.LENGTH_LONG).show();
				return;
			}
			sign = Integer.valueOf(indexstr).intValue();
			
			//进入员工详细界面
			if (StringHelper.equals(type, "employee")) 
			{
				EmployeeVO ep;
				if( search==1 )
				{
					ep = emplistsearch.get(index);
				}
				else  ep = employees4.get(sign);
				
				//跳转到员工详细界面
				mainactivity.showEmployee(ep,2);
			}
			else if (StringHelper.equals(type, "department")) 
			{
				curScreen++;

				// 滑动到下级页面
				mScrollLayout.scrollToScreen(curScreen);
	
				DepartmentVO entity = departments4.get(sign);
				deptParentid = entity.getDeptId();
				nametitles[curScreen] = entity.getDeptDisplayname();
	
				//设置标题栏
				textview.setText(nametitles[curScreen]);
				
				// 下级列表中，员工数据
				departments5 = db.findDepartments(deptParentid);
	
				// 下级列表中，员工数据
				employees5 = db.findEmployeeBydept(deptParentid,5);
	
				// 填充数据到listview
				initListItem(listviews[curScreen], departments5, employees5);
				search = 0;
			}
		}
	};

	// 6级listview单击事件
	private OnItemClickListener mOnItemClickSixth = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int index, long id)
		{
			String deptParentid;

			int sign = 0;
			HashMap<String, Object> appInfo = (HashMap<String, Object>) listviews[5].getItemAtPosition(index);
			String type = (String) appInfo.get("type");
			String indexstr = (String) appInfo.get("index");

			if(indexstr==null) 
			{
				Toast.makeText(getActivity(),"失败代码110108", Toast.LENGTH_LONG).show();
				return;
			}
			sign = Integer.valueOf(indexstr).intValue();

			// 进入员工详细界面
			if (StringHelper.equals(type, "employee"))
			{
				EmployeeVO ep;
				if( search==1 )
				{
					ep = emplistsearch.get(index);
				}
				else  ep = employees5.get(sign);
				
				// 跳转到员工详细界面
				mainactivity.showEmployee(ep,2);
			}
			else if (StringHelper.equals(type, "department"))
			{
				curScreen++;

				// 滑动到下级页面
				mScrollLayout.scrollToScreen(curScreen);

				DepartmentVO entity = departments5.get(sign);
				deptParentid = entity.getDeptId();
				nametitles[curScreen] = entity.getDeptDisplayname();

				// 设置标题栏
				textview.setText(nametitles[curScreen]);

				// 下级列表中，员工数据
				departments6 = db.findDepartments(deptParentid);

				// 下级列表中，员工数据
				employees6 = db.findEmployeeBydept(deptParentid,6);

				// 填充数据到listview
				initListItem(listviews[curScreen], departments6, employees6);
				search = 0;
			}
		}
	};
	
	// 7级listview单击事件
	private OnItemClickListener mOnItemClickSeventh = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int index, long id)
		{
			String deptParentid;

			int sign = 0;
			HashMap<String, Object> appInfo = (HashMap<String, Object>) listviews[6].getItemAtPosition(index);
			String type = (String) appInfo.get("type");
			String indexstr = (String) appInfo.get("index");

			if(indexstr==null) 
			{
				Toast.makeText(getActivity(),"失败代码110109", Toast.LENGTH_LONG).show();
				return;
			}
			sign = Integer.valueOf(indexstr).intValue();

			// 进入员工详细界面
			if (StringHelper.equals(type, "employee"))
			{
				EmployeeVO ep;
				if( search==1 )
				{
					ep = emplistsearch.get(index);
				}
				else  ep = employees6.get(sign);
				
				// 跳转到员工详细界面
				mainactivity.showEmployee(ep,2);
			}
			else if (StringHelper.equals(type, "department"))
			{
				curScreen++;

				// 滑动到下级页面
				mScrollLayout.scrollToScreen(curScreen);

				DepartmentVO entity = departments6.get(sign);
				deptParentid = entity.getDeptId();
				nametitles[curScreen] = entity.getDeptDisplayname();

				// 设置标题栏
				textview.setText(nametitles[curScreen]);

				// 下级列表中，员工数据
				departments7 = db.findDepartments(deptParentid);

				// 下级列表中，员工数据
				employees7 = db.findEmployeeBydept(deptParentid,7);

				// 填充数据到listview
				initListItem(listviews[curScreen], departments7, employees7 );
				search = 0;
			}
		}
	};
	
	// 8级listview单击事件
	private OnItemClickListener mOnItemClickEighth = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int index, long id)
		{
			String deptParentid;

			int sign = 0;
			HashMap<String, Object> appInfo = (HashMap<String, Object>) listviews[7].getItemAtPosition(index);
			String type = (String) appInfo.get("type");
			String indexstr = (String) appInfo.get("index");

			if(indexstr==null) 
			{
				Toast.makeText(getActivity(),"失败代码110110", Toast.LENGTH_LONG).show();
				return;
			}
			sign = Integer.valueOf(indexstr).intValue();

			// 进入员工详细界面
			if (StringHelper.equals(type, "employee"))
			{
				EmployeeVO ep;
				if( search==1 )
				{
					ep = emplistsearch.get(index);
				}
				else  ep = employees7.get(sign);
				// 跳转到员工详细界面
				mainactivity.showEmployee(ep,2);
			}
			else if (StringHelper.equals(type, "department"))
			{
				curScreen++;

				// 滑动到下级页面
				mScrollLayout.scrollToScreen(curScreen);

				DepartmentVO entity = departments7.get(sign);
				deptParentid = entity.getDeptId();
				nametitles[curScreen] = entity.getDeptDisplayname();

				// 设置标题栏
				textview.setText(nametitles[curScreen]);

				// 下级列表中，员工数据
				departments8 = db.findDepartments(deptParentid);

				// 下级列表中，员工数据
				employees8 = db.findEmployeeBydept(deptParentid,8);

				// 填充数据到listview
				initListItem(listviews[curScreen], departments8, employees8 );
				search = 0;
			}
		}
	};

	// 9级listview单击事件
	private OnItemClickListener mOnItemClickNinth = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int index, long id)
		{
			String deptParentid;

			int sign = 0;
			HashMap<String, Object> appInfo = (HashMap<String, Object>) listviews[8].getItemAtPosition(index);
			String type = (String) appInfo.get("type");
			String indexstr = (String) appInfo.get("index");

			if(indexstr==null) 
			{
				Toast.makeText(getActivity(),"失败代码110111", Toast.LENGTH_LONG).show();
				return;
			}
			sign = Integer.valueOf(indexstr).intValue();

			// 进入员工详细界面
			if (StringHelper.equals(type, "employee"))
			{
				EmployeeVO ep;
				if( search==1 )
				{
					ep = emplistsearch.get(index);
				}
				else  ep = employees8.get(sign);
				
				// 跳转到员工详细界面
				mainactivity.showEmployee(ep,2);
			}
			else if (StringHelper.equals(type, "department"))
			{
				curScreen++;

				// 滑动到下级页面
				mScrollLayout.scrollToScreen(curScreen);

				DepartmentVO entity = departments8.get(sign);
				deptParentid = entity.getDeptId();
				nametitles[curScreen] = entity.getDeptDisplayname();

				// 设置标题栏
				textview.setText(nametitles[curScreen]);

				// 下级列表中，员工数据
				departments9 = db.findDepartments(deptParentid);

				// 下级列表中，员工数据
				employees9 = db.findEmployeeBydept(deptParentid,9);

				// 填充数据到listview
				initListItem(listviews[curScreen], departments9, employees9 );
				search = 0;
			}
		}
	};
		
	//填充数据到listview
	public void initListItem( ListView listview, List<DepartmentVO> department, List<EmployeeVO> employee ) 
	{
//		int sizedp = 0;
//		int sizeep = 0;
//		
//		sizedp = department.size();
//		sizeep = employee.size();
		
		// 生成动态数组，加入数据
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		
		int i = 0;
		String indexstr;
		//2级列表中，部门数据
		for (DepartmentVO entity1 : department)
		{
			indexstr = String.valueOf(i);
			// HashMap为键值对类型。第一个参数为键，第二个参数为值
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("type", "department");
			map.put("index", indexstr);
			map.put("userName", entity1.getDeptDisplayname());
			listItem.add(map);// 添加到listItem中
			i++;
		}

		if( employee.size()>0 )
		{
			//安卓6.0，申请权限
			if( Build.VERSION.SDK_INT>=23 )
			{
				int Permission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);
				if( Permission!=PackageManager.PERMISSION_GRANTED )
				{
					requestPermissions( new String[]{ Manifest.permission.CALL_PHONE}, 123);
				}
			}
		}
		
		i = 0;
		for (EmployeeVO entity2 : employee) 
		{
			String tel = entity2.getMobileIphone();
			if (ValidUtil.isNullOrEmpty(tel))
			{
				tel = entity2.getPhoneNumber();
			}

			indexstr = String.valueOf(i);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("type", "employee");
			map.put("index", indexstr);
			map.put("tel", tel);
			map.put("sms", entity2.getPhoneNumber());
			map.put("mail", entity2.getEmail());
			map.put("userName", entity2.getUserDisplayName());
			listItem.add(map);
			i++;
		}
		
		//定义适配器
		lvButtonAdapter listItemAdapter = new lvButtonAdapter( FragmentPhoneBook.this.getActivity(), listItem);

		//绑适配器 添加并显示
		listview.setAdapter(listItemAdapter);
	}
	
	public int getemployeeinfo()
	{
		return 1;
	}
	
	//开放外部接口，得到当前的页面
	public int getcurScrollScreen()
	{
		return curScreen;
	}

	//开放外部接口，回退到上级
	public int goback()
	{
		//判断搜索框是否有值
		//有值，清空，显示之前的界面
		//没值，回退到上级部门界面
		String name = searchValue.getText().toString().trim();
		if( name.length()==0 )
		{
			if(curScreen==0) return 101;
			
			curScreen--;
			mScrollLayout.scrollToScreen(curScreen);
			
			//设置标题栏
			textview.setText(nametitles[curScreen]);
		}
		else
		{
			searchValue.setText("");
		}
		
		return 1;
	}
	
	//开放外部接口，得到主activity
	public void getmainactivity( MainActivity ma )
	{
		mainactivity = ma;
	}
	
	// 文本观察者
	private class MyTextWatcher implements TextWatcher
	{
		@Override
		public void afterTextChanged(Editable s)
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
			// TODO Auto-generated method stub
		}

		// 当文本改变时候的操作
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{		
			// TODO Auto-generated method stub
			// 如果编辑框中文本的长度大于0就显示删除按钮否则不显示
			if (s.length() > 0)
			{
				//search_delete.setVisibility(View.VISIBLE);
				//emplistsearch = db.findEmployeeByName(s.toString());
				emplistsearch = db.findEmployeeByKey(s.toString());

				int i = 0;
				String indexstr;
				ArrayList<HashMap<String, Object>> empListItem = new ArrayList<HashMap<String, Object>>();
				for (EmployeeVO entity : emplistsearch)
				{
					String tel = entity.getMobileIphone();
					if (ValidUtil.isNullOrEmpty(tel))
					{
						tel = entity.getPhoneNumber();
					}

					indexstr = String.valueOf(i);
					// HashMap为键值对类型。第一个参数为键，第二个参数为值
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("type", "employee");
					map.put("index", indexstr);
					map.put("tel", tel);
					map.put("sms", entity.getPhoneNumber());
					map.put("mail", entity.getEmail());
					map.put("userName", entity.getUserDisplayName());
					empListItem.add(map);// 添加到listItem中
					i++;
				}
				
				listItemAdapter = new lvButtonAdapter(FragmentPhoneBook.this.getActivity(), empListItem);
				listItemAdapter.notifyDataSetChanged();
				//empList.setAdapter(listItemAdapter);
				listviews[curScreen].setAdapter(listItemAdapter);
				
				search = 1;
			}
			else
			{
				//search_delete.setVisibility(View.GONE);
				//initListItem(listviews[curScreen], departments[curScreen], employees[curScreen]);
				search = 0;
				gobacklist();
			}
		}
	}
	
	public void cleareditsearch() 
	{
		searchValue.setText("");
		//gobacklist();
	}
	
	public void settextview(TextView tv)
	{
		textview = tv;
	}
	
	public void gobacklist()
	{
		switch(curScreen)
		{
			case 0:
				initListItem(listviews[curScreen], departments0, employees0 );
				break;
			case 1:
				initListItem(listviews[curScreen], departments1, employees1 );
				break;
			case 2:
				initListItem(listviews[curScreen], departments2, employees2 );
				break;
			case 3:
				initListItem(listviews[curScreen], departments3, employees3 );
				break;
			case 4:
				initListItem(listviews[curScreen], departments4, employees4 );
				break;
			case 5:
				initListItem(listviews[curScreen], departments5, employees5 );
				break;
			case 6:
				initListItem(listviews[curScreen], departments6, employees6 );
				break;
			case 7:
				initListItem(listviews[curScreen], departments7, employees7 );
				break;
			case 8:
				initListItem(listviews[curScreen], departments8, employees8 );
				break;
			case 9:
				initListItem(listviews[curScreen], departments9, employees9 );
				break;
			default:
				break;
		}
	}
	
	public void getCollectList()
	{
		employees1 = db.findCollect();
		List<DepartmentVO> department = new ArrayList<DepartmentVO>();
		//填充数据到listview
		initListItem( listviews[1], department, employees1 );
		search=0;
	}
}
