package com.cjwsjy.app.Canteen;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.imagecache.ImageGazerUtil;
import com.cjwsjy.app.imagecache.LoaderImpl;
import com.cjwsjy.app.phonebook.EmployeePopupWindow;
import com.cjwsjy.app.utils.DateUtil;
import com.cjwsjy.app.utils.PhoneUtils;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.utils.ValidUtil;
import com.do1.cjmobileoa.db.DBManager;
import com.do1.cjmobileoa.db.model.DepartmentEmployeeVO;
import com.do1.cjmobileoa.db.model.DepartmentVO;
import com.do1.cjmobileoa.db.model.EmployeeVO;
import com.do1.cjmobileoa.db.model.HistoryVO;

import java.lang.ref.SoftReference;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工详细信息页面
 * 
 * 除了智能包好界面，其他 界面都在使用
 */
public class FragmentCanteen4 extends Fragment
{
	private DBManager db;
	private EmployeeVO employee;
	private ImageButton keep;
	private Context mContext;

	private Bitmap m_image = null;
	private ImageView iv_Image;

	private TextView userPost;
	private TextView iphone;
	private TextView phoneNumber;
	private TextView telephone;
	private TextView shortNumber;
	private TextView email;

	private TextView tv_username;
	private TextView tv_jobnumber;
	private TextView tv_postduty;
	private TextView tv_organization;
	private TextView tv_dept;
	private TextView tv_address;
	private TextView tv_phone;
	private TextView tv_mobile;
	private TextView tv_mobileshort;
	private TextView tv_officephone;
	private TextView tv_email;

	private static LoaderImpl impl;
	private static Map<String,SoftReference<Bitmap>> sImageCache;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		//return inflater.inflate(R.layout.fragment_employee_info, container,false);
		return inflater.inflate(R.layout.fragment_employeeinfo, container,false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		sImageCache = new HashMap<String,SoftReference<Bitmap>>();
		impl = new LoaderImpl(sImageCache);

		mContext = getActivity();

		//db = new DBManager(this.getActivity());
		db = SmApplication.dbManager;
//
//		String userId = getArguments().getString("userId");
//
//		employee = db.findEmployeeById(userId);
//
//		collectCount = db.checkCollectForUserid(employee.getUserid());

		initView();
	}

	/*
	 * 初始化联系人信息
	 */
	public void initView()
	{
		iv_Image = (ImageView)getView().findViewById(R.id.iv_user);

		tv_username = (TextView)getView().findViewById(R.id.tv_userName);
		tv_jobnumber = (TextView)getView().findViewById(R.id.tv_jobnumber);

		tv_postduty = (TextView)getView().findViewById(R.id.emp_id_postDuty);
		tv_organization = (TextView)getView().findViewById(R.id.emp_id_organization);
		tv_dept = (TextView)getView().findViewById(R.id.emp_id_dept);
		tv_address = (TextView)getView().findViewById(R.id.emp_id_address);
		tv_phone = (TextView)getView().findViewById(R.id.emp_id_phone);
		tv_mobile = (TextView) getView().findViewById(R.id.emp_id_mobile);
		tv_mobileshort = (TextView) getView().findViewById(R.id.emp_id_mobileshort);
		tv_officephone = (TextView) getView().findViewById(R.id.emp_id_officephone);
		tv_email = (TextView) getView().findViewById(R.id.emp_id_email);

		ImageView iv_Phone;  //电话
		ImageView iv_sms;  //短信
		ImageView iv_email;  //邮件
		ImageView iv_mark;  //关注
		ImageView iv_addphone;  //加到通讯录

		iv_Phone= (ImageView) getView().findViewById(R.id.iv_tel1);
		iv_sms= (ImageView) getView().findViewById(R.id.iv_tel2);
		iv_email= (ImageView) getView().findViewById(R.id.iv_tel3);
		iv_mark= (ImageView) getView().findViewById(R.id.iv_tel4);
		iv_addphone= (ImageView) getView().findViewById(R.id.iv_tel5);

	}

	// 添加历史记录
	private void addHistory()
	{
		HistoryVO historyVo = new HistoryVO();
		historyVo.setCreatetime(DateUtil.format(new Date(),
				"yyyy-MM-dd HH:mm:ss"));
		historyVo.setDepartment(employee.getDepartment());
		historyVo.setEmail(employee.getEmail());
		historyVo.setNameSpell(employee.getNameSpell());
		String tel = employee.getMobileIphone();
		if (ValidUtil.isNullOrEmpty(tel))
			tel = employee.getPhoneNumber();
		historyVo.setPhoneNumber(tel);
		historyVo.setUserid(employee.getUserid());
		historyVo.setUserDisplayName(employee.getUserDisplayName());

		db.addHistory(historyVo);

	}

	// 删除收藏数据
	private void delCollect()
	{
		db.delCollectForUserid(employee.getUserid());
	}

	public void AssignView()
	{
		int length = 0;
		String text;
		String userid;
		String orgid;
		String photoUrl;
		String position;  //岗位
		String organization;//机构
		String department;  //部门

		Bitmap image = null;

		//获得岗位
		userid = employee.getUserid();
		DepartmentEmployeeVO orgsVO = db.findOrgsbyUserid(userid);
		position = orgsVO.getuserTitle();

		//部门
		orgid = orgsVO.getorgRID();
		DepartmentVO deptVO = db.findDepartmentbyid(orgid);
		department = deptVO.getDeptDisplayname();

		//机构
		deptVO = db.findOrganizationbyid(deptVO.getDeptParentid());
		organization = deptVO.getDeptDisplayname();

		//判断岗位是否为空
		if( position==null ) length = 0;
		else length = position.length();


		//用户名
		text = employee.getUserDisplayName();
		if(length==0) tv_username.setText(text);
		else
		{
			text = text + "  " + position;
			tv_username.setText(text);
		}

		//员工编号
		text = employee.getjobNumber();
		tv_jobnumber.setText(text);

		//教授级高工
		text = employee.getpostDuty();
		if( text.length()==0 )
		{
			text = department;
		}
		else text = employee.getpostDuty()+"  "+department;
		tv_postduty.setText(text);

		//所属机构
		//text = employee.getPhoneNumber();
		tv_organization.setText(organization);

		//所在部门
		//text = employee.getPhoneNumber();
		tv_dept.setText(department);

		//办公地址
		text = employee.getOfficeAddress();
		tv_address.setText(text);

		//手机号码
		text = employee.getPhoneNumber();
		tv_phone.setText(text);

		//移动号码
		text = employee.getMobileIphone();
		tv_mobile.setText(text);

		//移动短号
		text = employee.getmobileIphoneShortNumber();
		tv_mobileshort.setText(text);

		//办公电话
		text = employee.getTelephone();
		tv_officephone.setText(text);

		//电子邮箱
		text = employee.getEmail();
		tv_email.setText(text);

		//设置头像
		text = employee.getUsername();
		//用户头像下载地址
		photoUrl = UrlUtil.USER_IMAGE+text;
		image = getimageforurl(photoUrl);
		m_image = image;

		//方图片转圆图片
		image = ImageGazerUtil.createCircleImage(image);

		//显示头像
		iv_Image.setImageBitmap(image);
	}

	private Bitmap getimageforurl(String strUrl)
	{
		String resultStr = null;
		Bitmap bitmap = null;

		//resultStr = HttpClientUtil.sendRequestFromHttpClientString2(strUrl, null, "UTF-8");

		//resultStr = "http://10.6.177.205:8080/CEGWAPServer/InterSource/getUserPicByte/ganwei";
		bitmap = impl.getBitmap(strUrl);

		return bitmap;
	}

	private void showWin(int source, EmployeeVO employee)
	{
		EmployeePopupWindow menuWindow = new EmployeePopupWindow(
				this.getActivity(), 0, source, employee);

		// 显示窗口
		menuWindow.showAtLocation(
				this.getActivity().findViewById(R.id.ft_employeeInfo),
				Gravity.NO_GRAVITY | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
	}

	public void setEmployeeVO(EmployeeVO ep)
	{
		employee = ep;
		AssignView();
	}

	private int requestPermissions2(String strpermission, int code )
	{
		//判断该权限
		int Permission = ContextCompat.checkSelfPermission(mContext, strpermission);
        if( Permission!=PackageManager.PERMISSION_GRANTED )
        {
        	//没有权限，申请权限
        	requestPermissions(new String[]{ strpermission}, code);
            return 2;
        }

		return 1;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		android.util.Log.d("cjwsjy", "------code="+requestCode+"-------");
	    switch (requestCode)
	    {
	        case 103:  //打电话
	            if( grantResults[0]==PackageManager.PERMISSION_GRANTED)
	            {
	                //同意
	            	showWin(EmployeePopupWindow.SOURCE_TEL, employee);
	            }
	            else
	            {
	                //拒绝
	                Toast.makeText(this.getActivity(), "没有权限，不能打电话", Toast.LENGTH_SHORT) .show();
	            }
	            break;
	        case 104:  //发短信
	            if( grantResults[0]==PackageManager.PERMISSION_GRANTED)
	            {
	                //同意
	            	showWin(EmployeePopupWindow.SOURCE_SMS, employee);
	            }
	            else
	            {
	                //拒绝
	                Toast.makeText(this.getActivity(), "没有权限，不能发短信", Toast.LENGTH_SHORT) .show();
	            }
	        	break;
	        case 105://通讯录
	            if( grantResults[0]==PackageManager.PERMISSION_GRANTED)
	            {
	                //同意
					if(PhoneUtils.queryContact(mContext,employee.getPhoneNumber()))
					{
						PhoneUtils.insertContact(mContext,
								employee.getUserDisplayName(),
								employee.getPhoneNumber(), employee.getMobileIphone(), employee.getmobileIphoneShortNumber(),employee.getEmail(),m_image,null);
						Builder builder = new Builder(getActivity());
						builder.setTitle("消息提示");
						builder.setMessage("添加成功！");
						builder.setPositiveButton("确定", null);
						builder.show();
					}else{
						Builder builder = new Builder(getActivity());
						builder.setTitle("消息提示");
						builder.setMessage("通讯录中存在该号码！");
						builder.setPositiveButton("确定", null);
						builder.show();
					}
	            }
	            else 
	            {
	                //拒绝
	                Toast.makeText(this.getActivity(), "没有权限，不能添加到通讯录", Toast.LENGTH_SHORT) .show();
	            }
	        	break;
	        default:
	            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	    }
	}
}