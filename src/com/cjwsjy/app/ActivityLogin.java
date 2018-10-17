package com.cjwsjy.app;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface.OnClickListener;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.cjwsjy.app.Finger.FingerprintAuthenticationDialogFragment;
import com.cjwsjy.app.custom.CustomDialog;
import com.cjwsjy.app.encrypt.Decrypt;
import com.cjwsjy.app.imagecache.LoaderImpl;
import com.cjwsjy.app.main.MainActivity;
import com.cjwsjy.app.utils.HttpClientUtil;
import com.cjwsjy.app.utils.StringHelper;
import com.cjwsjy.app.utils.ThreadUtils;
import com.cjwsjy.app.utils.UrlUtil;
import com.cjwsjy.app.vehicle.UrlManager;
import com.do1.cjmobileoa.db.DBManager;
import com.do1.cjmobileoa.db.GetServiceData;
import com.do1.cjmobileoa.db.model.EmployeeVO;

import com.wholeally.qysdk.QYSDK;
import com.wholeally.qysdk.QYSession;
import com.wholeally.qysdk.QYSession.OnViewerLogin;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.SoftReference;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static android.telephony.TelephonyManager.SIM_STATE_READY;

public class ActivityLogin extends Activity
{
    public String sp_loginName = "USERDATA.LOGIN.NAME";
    public String sp_password = "USERDATA.LOGIN.PASSWORD";
    public String sp_DisplayName = "USERDATA.DISPLAY.NAME";  //USERDATA.USER.NAME
    public String sp_userid = "USERDATA.USER.ID";
    public String sp_userJobNumber = "USERDATA.USER.JOBNUMBER";
	public String sp_baobei = "USERDATA.BAOBEI.ID";

	private int m_contacts;
	private int m_contactsnull;  //通讯录为空，第一次必须更新通讯录，没有选择是和否
	private int m_finish_phone;
	private int m_finish_login;
	private int m_Mutex;
	private int m_MutexProgress;

	private int m_FingerHardware;
	private int m_FingerEnrolled = 0;
	private int m_FingerCancel = 0;
	private int m_KeyguardSecure = 0;

	private String m_ContactsVer;

    private String chuchaistr;
    private String qianbaostr;
	private String password;
	private String userid;
	private String m_displayname;
	private String loginName;
	private String appUrl;
	private String m_isOpen;
	private String m_isMeeting;
	private String m_isZhibo;
	private String m_ChannelName;
	private String m_ChannelUrl;
	private String m_IP_in;
	private String m_Port_in;
	private String m_IP_out;
	private String m_Port_out;
	private String m_Auth;
	private String m_zhibopic;
	private String m_dahuipic1;
	private String m_versionapp;
	private String m_phonesim;
	private String m_baobei;
	private String m_SDCardPath;
	private String m_str_deviceToken;

	private Button loginBtn;
	private EditText edit_user;
	private EditText edit_password;
	private SharedPreferences sp;
	private ProgressDialog mDialog;

	private FingerprintAuthenticationDialogFragment m_fragment = null;
	private FingerprintManager m_fingerprintManager = null;
	private FingerprintManagerCompat fingerManager = null;

	private KeyStore mKeyStore;
	private KeyGenerator mKeyGenerator;

	//private CallbackFinger myAuthCallback = null;

	private DBManager db;
	private SmApplication m_App;
	private List<String> lstFile = new ArrayList<String>();  //结果 List

	private int sign = 0;
	private int signaddress = 0;  //标记是否下载通讯录
	private int progress = 0;

	CheckBox cb;

	private CancellationSignal cancellationSignal = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login3);

        int sdk = 0;
		int length = 0;
        String version = "";
		m_baobei = "";
		m_contacts = 0;
		m_Mutex = 0;
        userid = "";
		m_finish_phone = 0;
		m_finish_login = 0;
		m_MutexProgress = 0;
		m_contactsnull = 0;
		m_FingerCancel = 0;
		m_KeyguardSecure = 0;
		m_ContactsVer = "";

		android.util.Log.i("cjwsjy", "------onCreate-----1--ActivityLogin");

        TextView textview = (TextView) findViewById(R.id.tv_version);

		m_phonesim = "";
        edit_user = (EditText) findViewById(R.id.et_usertel);
        edit_password = (EditText) findViewById(R.id.et_password);

        loginBtn = (Button) findViewById(R.id.btn_login);

		m_isOpen = "";
        m_isMeeting = "0";
        db = SmApplication.dbManager;
		sp = SmApplication.sp;

        //得到android的sdk的版本
        m_App = (SmApplication) getApplication();
        sdk = Build.VERSION.SDK_INT;   //SDK版本号
        version=String.valueOf(sdk);
        m_App.setsdk(version);

        String username =sp.getString("USERDATA.LOGIN.NAME", "");
        String password =sp.getString("USERDATA.LOGIN.PASSWORD", "");
		m_versionapp = SmApplication.sharedProxy.getString("curVersion", "1.0");  //APP版本
		String strver = "版本："+m_versionapp;
		textview.setText(strver);

        edit_user.setOnKeyListener(onKey);
        edit_password.setOnKeyListener(onKey);

		loginName = username;
		userid = sp.getString("USERDATA.USER.ID", "");

        edit_user.setText(username);
        edit_password.requestFocus();

        appUrl = UrlUtil.HOST;
        UrlUtil.g_count = 0;

		android.util.Log.i("cjwsjy", "------onCreate-----2--ActivityLogin");

		//删除附件
		DeleteAttachment();

		//showTips();

		// 检查网络
		//CheckNetworkState();

		//通讯录有新版本，是否更新
		GetContactsVersion();

        //开线程，下载通讯录数据
        //Thread phoneThread = new Thread(new ThreadDownPhone());
        //phoneThread.start();

		// 登录
		loginBtn.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				int ret = requestPermissionsM2(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,103);
				if(ret!=1) return;

				login(1);
			}
		});

		if(Build.VERSION.SDK_INT < 23) return;

		//初始化指纹
		Fingerinit();

		//锁屏状态
		FingerKeyguardSecure();


		length = username.length();
		if( length!=0 && m_FingerEnrolled==1 && m_KeyguardSecure==1 )
		{
			//FingerShow(1);

			Message msg;
			msg = handlerFinger.obtainMessage();
			msg.what = 102;
			handlerFinger.sendMessageDelayed(msg,600);
		}

		if(length!=0 && m_FingerEnrolled==1 && m_KeyguardSecure==0)
		{
			Toast.makeText( ActivityLogin.this, "屏幕锁未打开", Toast.LENGTH_LONG).show();
		}

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

    @Override
    protected void onDestroy()
    {
        try
        {
            mDialog.dismiss();
        }
        catch (Exception e)
        {
            System.out.println("myDialog取消，失败！");
        }
        super.onDestroy();
    }

	protected int GetContactsVersion()
	{
		Thread phoneThread = new Thread(new ThreadGetContacts());
		phoneThread.start();

//		if(m_contactsnull==1)
//		{
//			if (m_Mutex == 0)
//			{
//				//下载通讯录
//				signaddress = 1;
//				Thread DownThread = new Thread(new ThreadDownPhone());
//				DownThread.start();
//			}
//		}

		return 1;
	}

	public void FileTest()
	{
		boolean result = false;
		String FileName = Environment.getExternalStorageDirectory() + "/Download" +"/com.cjwsjy.app/attachment/1013.txt";
		File file = new File(FileName);
		result = file.exists();
		if( result==false ) { testCreateWrite(FileName); }
		//if( result==true ) { file.delete(); }

		//创建文件
		//testCreateWrite(FileName);
		//向文件末尾追加内容
		testAppendWrite(FileName);
		//通过RandomAccessFile读取文件
		testRead(FileName);
	}


	//若“file.txt”不存在的话，则新建文件，并向文件中写入内容
	private void testCreateWrite(String FileName)
	{
		try
		{
			// 创建文件“file.txt”对应File对象
			File file = new File(FileName);
			// 创建文件“file.txt”对应的RandomAccessFile对象
			RandomAccessFile raf = new RandomAccessFile(file, "rw");

			// 向“文件中”写入26个字母+回车
			raf.writeChars("abcdefghijklmnopqrstuvwxyz\n");
			// 向“文件中”写入"9876543210"+回车
			raf.writeChars("9876543210\n");

			raf.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}


	//向文件末尾追加内容
	private static void testAppendWrite(String FileName)
	{
		try {
			// 创建文件“file.txt”对应File对象
			File file = new File(FileName);
			// 创建文件“file.txt”对应的RandomAccessFile对象
			RandomAccessFile raf = new RandomAccessFile(file, "rw");

			// 获取文件长度
			long fileLen = raf.length();
			// 将位置定位到“文件末尾”
			raf.seek(fileLen);

			// 以下向raf文件中写数据
			raf.writeBoolean(true); // 占1个字节
			raf.writeByte(0x41);    // 占1个字节
			raf.writeChar('a');     // 占2个字节
			raf.writeShort(0x3c3c); // 占2个字节
			raf.writeInt(0x75);     // 占4个字节
			raf.writeLong(0x1234567890123456L); // 占8个字节
			raf.writeFloat(4.7f);  // 占4个字节
			raf.writeDouble(8.256);// 占8个字节
			raf.writeUTF("UTF严"); // UTF-8格式写入
			raf.writeChar('\n');   // 占2个字符。“换行符”

			raf.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	//通过RandomAccessFile读取文件
	private static void testRead(String FileName)
	{
		try {
			// 创建文件“file.txt”对应File对象
			File file = new File(FileName);
			// 创建文件“file.txt”对应的RandomAccessFile对象，以只读方式打开
			RandomAccessFile raf = new RandomAccessFile(file, "r");

			// 读取一个字符
			char c1 = raf.readChar();
			System.out.println("c1="+c1);
			// 读取一个字符
			char c2 = raf.readChar();
			System.out.println("c2="+c2);

			// 跳过54个字节。
			raf.seek(54);

			// 测试read(byte[] buffer, int byteOffset, int byteCount)
			byte[] buf = new byte[20];
			raf.read(buf, 0, buf.length);
			System.out.println("buf="+(new String(buf)));

			raf.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}


	//删除附件
	//比较月份，每个月清除一次
	public void DeleteAttachment()
	{
		int oldmouth = 0;
		int oldday = 0;
		String mouth;
		String number = "";
		Editor editor = sp.edit();

		oldday = sp.getInt("DATA_DAY", 0);
		oldmouth = sp.getInt("DATA_MOUTH", 0);

		int mHour;
		int mMinute;
		int mYear;
		int mMonth;
		int mDay;

		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR); //获取当前年份
		mMonth = c.get(Calendar.MONTH);//获取当前月份
		mDay = c.get(Calendar.DAY_OF_MONTH);//获取当前月份的日期号码
		mHour = c.get(Calendar.HOUR_OF_DAY);//获取当前的小时数
		mMinute = c.get(Calendar.MINUTE);//获取当前的分钟数

		android.util.Log.i("cjwsjy", "------oldmouth="+oldmouth+"--mouth="+mMonth+"-----");

		if(oldmouth==mMonth && oldday==mDay )
		{
			editor.putInt("DATA_MOUTHDAY", 1);
		}
		else editor.putInt("DATA_MOUTHDAY", 2);

		editor.commit();

		if(oldmouth==mMonth&&oldday==mDay)
		{
			return;
		}

		String sdPath = Environment.getExternalStorageDirectory() + "/Download" +"/com.cjwsjy.app/attachment/";
		File file1 = new File(sdPath);
		DeleteAllFiles(file1);

		editor.putInt("DATA_MOUTH", mMonth);
		editor.putInt("DATA_DAY", mDay);

		editor.commit();
	}

	protected void DeleteAllFiles(File root)
	{
		File files[] = root.listFiles();
		if (files == null) return;

		for (File f : files)
		{
			if (f.isDirectory())
			{ // 判断是否为文件夹
				DeleteAllFiles(f);
				try
				{
					f.delete();
				}
				catch (Exception e)
				{
				}
			}
			else
			{
				if (f.exists())
				{ // 判断是否存在
					DeleteAllFiles(f);
					try
					{
						f.delete();
					}
					catch (Exception e)
					{
					}
				}
			}
		}
	}

 // 检查网络状态
 	public void CheckNetworkState()
 	{
 		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
 		State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
 		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

 		// 如果3G、wifi、2G等网络状态是连接的，则退出，否则显示提示信息进入网络设置界面
 		if (mobile == State.CONNECTED || mobile == State.CONNECTING)
 			return;
 		if (wifi == State.CONNECTED || wifi == State.CONNECTING)
 			return;

 		showTips();
 	}

 	//网络不可用
 	private void showTips()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle("没有可用网络");
		builder.setMessage("当前网络不可用，是否设置网络？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// 如果没有网络连接，则进入网络设置界面
				startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
				ActivityLogin.this.finish();
			}
		});

		builder.create();
		builder.show();
	}

 	Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progress = progress + 5;
            if (SVProgressHUD.getProgressBar(ActivityLogin.this).getMax() != SVProgressHUD.getProgressBar(ActivityLogin.this).getProgress()) {
                SVProgressHUD.getProgressBar(ActivityLogin.this).setProgress(progress);
                SVProgressHUD.setText(ActivityLogin.this, "进度 "+progress+"%");

                mHandler.sendEmptyMessageDelayed(0,500);
            }
            else{
                SVProgressHUD.dismiss(ActivityLogin.this);
                SVProgressHUD.getProgressBar(ActivityLogin.this).setProgress(0);
            }
        }
    };

	//申请1个权限
	private int requestPermissionsM(String strpermission, int code )
	{
		if( Build.VERSION.SDK_INT<23 ) return 1;

		//判断该权限
		int Permission = ContextCompat.checkSelfPermission(this, strpermission);
		if( Permission!= PackageManager.PERMISSION_GRANTED )
		{
			//没有权限，申请权限
			ActivityCompat.requestPermissions(this,new String[]{ strpermission}, code);
			return 1013;
		}

		return 1;
	}

	//申请2个权限
	private int requestPermissionsM2(String strPermission1, String strPermission2,int code )
	{
		boolean bret;
		int nPermission1;
		int nPermission2;

		if( Build.VERSION.SDK_INT<23 ) return 1;

		//判断权限
		nPermission1 = ContextCompat.checkSelfPermission(this, strPermission1);
		nPermission2 = ContextCompat.checkSelfPermission(this, strPermission2);
		if( nPermission1!= PackageManager.PERMISSION_GRANTED && nPermission2!= PackageManager.PERMISSION_GRANTED )
		{
			String [] permissions = { strPermission1, strPermission2 };

			//没有权限，申请权限
			ActivityCompat.requestPermissions( this, permissions, 1031 );
			return 1013;
		}
		else if(nPermission1!= PackageManager.PERMISSION_GRANTED)
		{
//			bret = ActivityCompat.shouldShowRequestPermissionRationale(ActivityLogin.this,Manifest.permission.ACCESS_COARSE_LOCATION);
//			if( bret==true )
//			{
				//相机需要获得您的授权才能正常运行
//				showNoticeDialog2("院app集成的高德SDK，可以查看地图，高德SDK需要位置权限");
//			}

			ActivityCompat.requestPermissions( this,new String[]{ strPermission1 }, 1032 );
			return 1013;
		}
		else if(nPermission2!= PackageManager.PERMISSION_GRANTED)
		{
			ActivityCompat.requestPermissions( this,new String[]{ strPermission2 }, 1032 );
			return 1013;
		}

		return 1;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		int length;
		length = grantResults.length;
		//android.util.Log.i("cjwsjy", "------length="+length+"-------Permissions");

		switch (requestCode)
		{
			case 1031:
				if( grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED )
				{
					// 同意
					login(3);
				}
				else
				{
					// 拒绝
					Toast.makeText(ActivityLogin.this, "权限申请失败！请到\"设置\"->\"应用程序\"中打开权限", Toast.LENGTH_SHORT).show();
				}
				break;
			case 1032:
				if( grantResults[0]==PackageManager.PERMISSION_GRANTED )
				{
					// 同意
					login(4);
				}
				else
				{
					// 拒绝
					Toast.makeText(ActivityLogin.this, "权限申请失败！请到\"设置\"->\"应用程序\"中打开权限", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	private int showdialog2(String strtext)
	{
		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setMessage(strtext);
		builder.setTitle("提示");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				//设置你的操作事项
				ActivityLogin.this.finish();
			}
		});

		builder.create().show();
		return 1;
	}

	private void showNoticeDialog2(String strtext)
	{
		Dialog noticeDialog;
		AlertDialog.Builder builder = new Builder(ActivityLogin.this);
		builder.setTitle("说明");
		builder.setMessage(strtext);
		builder.setPositiveButton("确定", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				//showDownloadDialog();
			}
		});

		noticeDialog = builder.create();
		noticeDialog.show();
	}

	private int GetPhoneState()
	{
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		//获取Sim卡状态
		int state = telephonyManager.getSimState();
		if( state!=SIM_STATE_READY ) return 1013;

		//获取手机支持网络制式
		//一般就GSM、CDMA两种，如果没有获取到则是NONE
		int PhoneType = telephonyManager.getPhoneType();
		int NetworkType = telephonyManager.getNetworkType();
		String operator = telephonyManager.getNetworkOperator();
		try
		{
			String mcc = operator.substring(0, 3);
			String mnc = operator.substring(3);

			m_phonesim = "_("+PhoneType+"_"+NetworkType+"_"+mcc+"_"+mnc+"_";

			int cid = 0;
			int lac = 0;
			int sid = 0;

			int ret = requestPermissionsM2(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,103);
			if(ret!=1) return 1013;

			int nPermission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
			int nPermission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

			CellLocation location = telephonyManager.getCellLocation();

			//获取当前LAC、CID
			//if( PhoneType==TelephonyManager.PHONE_TYPE_CDMA )
			if (location instanceof CdmaCellLocation)
			{
				CdmaCellLocation cdmaCellLocation = (CdmaCellLocation)location;
				lac = cdmaCellLocation.getNetworkId(); //获取cdma网络编号NID
				cid = cdmaCellLocation.getBaseStationId(); //获取cdma基站识别标号 BID
				sid = cdmaCellLocation.getSystemId(); //用谷歌API的话cdma网络的mnc要用这个getSystemId()取得→SID

				//m_phonesim = "_("+PhoneType+"_"+NetworkType+"_"+mcc+"_"+mnc+"_"+lac+"_"+cid+"_"+sid+")";
				m_phonesim = m_phonesim + lac+"_"+cid+"_"+sid;
			}
			else if(location instanceof GsmCellLocation)
			{
				GsmCellLocation gsmCellLocation = (GsmCellLocation)location;
				if(gsmCellLocation==null) return 1013;

				lac = gsmCellLocation.getLac(); //获取gsm网络编号
				cid = gsmCellLocation.getCid(); //获取gsm基站识别标号

				//m_phonesim = "_("+PhoneType+"_"+NetworkType+"_"+mcc+"_"+mnc+"_"+lac+"_"+cid+")";
				m_phonesim = m_phonesim + lac+"_"+cid;
			}
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}

		m_phonesim = m_phonesim + ")";
		//m_phonesim = "_(PhoneType="+PhoneType+"_NetworkType="+NetworkType+"_mcc="+mcc+"_mnc="+mnc+"_LAC="+lac+"_CID="+cid+")";
		return 1;
	}

	public int getRealHeight(Activity activity)
	{
		int heightPixels = 0;
		Display display = activity.getWindowManager().getDefaultDisplay();
		final int VERSION = Build.VERSION.SDK_INT;

		if( VERSION<13 )
		{
			display.getHeight();
		}
		else if ( VERSION==13 )
		{
			try
			{
				heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
			}
			catch (Exception e)
			{
			}
		}
		else
		{
			Point realSize = new Point();
			try
			{
				Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
				heightPixels = realSize.y;
			}
			catch (Exception e)
			{
			}
		}
		return heightPixels;
	}

	public void login_zhaidai(int debug)
	{
		QYSession session;

		int nport = 0;
		int retState;
		String IPaddress;
		String ports;
		String appid;
		String auth;

		QYSDK.InitSDK(5);//初始化SDK
		session = QYSDK.CreateSession(getApplicationContext());//创建session会话

		//IPaddress = "117.28.255.16";
		IPaddress = "218.106.125.147";
		ports = "39100";  //39100  18081

		if(!"".equals(IPaddress)&&null!=IPaddress &&!"".equals(ports)&&null!=ports)
		{
			nport = Integer.valueOf(ports);
			appid = "wholeally";
			auth = "czFYScb5pAu+Ze7rXhGh/+KgxMFQtw/4EkOwlq9xjm9GZ87T7kinBeiME6d8w4HzZNmnJIpQUIK7ZEM8xbu+utMwoI8ilfYVP3APq+CfVaHkMAVHncHZlw==";
			//auth = "czFYScb5pAu+Ze7rXhGh/+DZG+EYxzXl6mU9JCMJ/F/rvEuURddsP/hN/Xzyf48WRweoOAiaI9vmlrBaCMrtn9FAt75ccbDU";

			//dialog = ProgressDialog.show(this, "加载中...", "正  在  获  取  数  据 ...");
			retState = session.SetServer(IPaddress,nport);//连接服务器大于或等于0为成功  否则为失败
			if (retState >= 0)
			{
				//session会话登录 第二个参数为回调函数 ret大于或等于0为成功  否则为失败
				// 测试用： wholeally    czFYScb5pAu+Ze7rXhGh/zURoROEIJ5JZnqf1q9hjlNQpfpixq+tzaIuQmoa2qa0Vgd/r1TPf+IQy3AED5xjo9iSSMjZjGIKZv8EsCI3VJc=
				session.ViewerLogin(appid,auth, new OnViewerLogin()
				{
					@Override
					public void on(int ret)
					{
						System.out.println("===ret==:"+ret);
						if (ret >= 0)
						{
							android.util.Log.i("cjwsjy", "------登录成功-------7");
							//dialog.cancel();
							//showToast("登录成功");
							//Intent intent = new Intent(QyLoginActivity.this, QyDeviceActivity.class);
							//startActivity(intent);
						}
						else
						{
							android.util.Log.i("cjwsjy", "------登录失败-------7");
							//dialog.cancel();
							//showToast("登录失败:"+String.valueOf(ret)+";或者ViewerLogin第一个或第二个参数错误");
						}
					}
				});
			}
			else
			{
				//dialog.cancel();
				//showToast("服务器连接失败:"+String.valueOf(retState));
			}
		}
		else
		{
			//showToast("不能为空,请输入");
		}
	}

	//登录
	public void login(int debug)
	{
		//初始化指纹
		//Fingerinit();

		//隐藏键盘
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

		//第一次必须更新通讯录,不弹是否更新通讯录对话框
		//这里必须重复下载，防止第一次默认下载失败
		if(m_contactsnull==1)
		{
			if(m_Mutex==0)
			{
				//下载通讯录
				signaddress = 1;
				Thread phoneThread = new Thread(new ThreadDownPhone());
				phoneThread.start();
			}

			loginuser(1);
			return;
		}

		//选择是否更新通讯录
		//=============
		/*if( m_contacts==1 )
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("更新");
			builder.setMessage("通讯录有新版本，是否同步？");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					if(m_Mutex==0) {
						//下载通讯录
						signaddress = 1;
						Thread phoneThread = new Thread(new ThreadDownPhone());
						phoneThread.start();
					}

					loginuser(1);
				}
			});

			builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.cancel();
					m_finish_phone = 1;
					loginuser(1);
				}
			});

			builder.create();
			builder.show();
		}
		else
		{
			m_finish_phone = 1;
			loginuser(1);
		}//=======*/

		m_finish_phone = 1;
		loginuser(1);
	}

	@TargetApi(Build.VERSION_CODES.M)
	private int FingerShow(int debug)
	{
		android.util.Log.i("cjwsjy", "------FingerShow-------");

		String DEFAULT_KEY_NAME = "default_key";
		String KEY_NAME_NOT_INVALIDATED = "key_not_invalidated";

		createKey(DEFAULT_KEY_NAME, true);
		createKey(KEY_NAME_NOT_INVALIDATED, false);

		Cipher defaultCipher;
		Cipher cipherNotInvalidated;
		try
		{
			defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
					+ KeyProperties.BLOCK_MODE_CBC + "/"
					+ KeyProperties.ENCRYPTION_PADDING_PKCS7);
			cipherNotInvalidated = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
					+ KeyProperties.BLOCK_MODE_CBC + "/"
					+ KeyProperties.ENCRYPTION_PADDING_PKCS7);
		}
		catch (NoSuchAlgorithmException | NoSuchPaddingException e)
		{
			throw new RuntimeException("Failed to get an instance of Cipher", e);
		}

		initCipher(defaultCipher, DEFAULT_KEY_NAME);

		m_fragment = new FingerprintAuthenticationDialogFragment();
		m_fragment.setCryptoObject(new FingerprintManager.CryptoObject(defaultCipher));
		//m_fragment.setCryptoObject(new FingerprintManagerCompat.CryptoObject(defaultCipher));
		m_fragment.setFingerObject(m_fingerprintManager);
		m_fragment.SetHandlerFinger(handlerFinger);
		//m_fragment.setFingerObjectCompat(fingerManager,myAuthCallback);
		//myAuthCallback.setFramentObject(m_fragment);

		if (true)
		{
			m_fragment.setStage(FingerprintAuthenticationDialogFragment.Stage.FINGERPRINT);
		}
		else
		{
			m_fragment.setStage(FingerprintAuthenticationDialogFragment.Stage.PASSWORD);
		}
		m_fragment.show(getFragmentManager(), "myFragment");

		return 1;
	}

	//原生的做法
	private int FingerShow2(int debug)
	{
		int error = 0;
		// init fingerprint.
		fingerManager = FingerprintManagerCompat.from(this);

		if (!fingerManager.isHardwareDetected())
		{
			//是否有指纹硬件
			// no fingerprint sensor is detected, show dialog to tell user.
			error = 1;
		}
		else if (!fingerManager.hasEnrolledFingerprints())
		{
			//是否录入至少一个指纹
			// no fingerprint image has been enrolled.
			error = 1;
		}
		else
		{
//			try
//			{
//				myAuthCallback = new CallbackFinger(handlerFinger);
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
		}

		return 1;
	}

	public void loginuser(int debug)
	{
        //指纹登录
		//if(false)
//		if( m_FingerCancel==0 && m_FingerEnrolled==1 )
//        {
//            FingerShow(1);
//            return;
//        }

		loginName = edit_user.getText().toString().trim();
		password = edit_password.getText().toString();

		//获得屏幕的分辨率
		//getRealHeight(ActivityLogin.this);

		String model = Build.MODEL;//手机型号
		int version = Build.VERSION.SDK_INT;//SDK版本号
		String versionos = Build.VERSION.RELEASE;//Firmware/OS 版本号

		//String model2 = model.replaceAll( " ", "%20");
		String model2 = model.replaceAll( " ", "_");
		String strbuf = "Android-"+versionos+"-"+model2+"-"+m_versionapp;

		// userid
		//EmployeeVO data;
		//data = db.getEmployeeByName(loginName);
		//userid = data.getUserid();

		//这个时候userid可能还拿不到
		userid = "E5B80A2A-CA38-4C63-8823-963408CA7E4D";

		//保存日志,debug级别日志
		//String phone = "Android-"+versionos+"-"+model2+"-"+m_versionapp+"-debug";
		//SaveLog(loginName,userid,phone);

		//隐藏键盘
		//InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		//imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

		// 判断账号和用户名不能为空
		if (loginName.trim().equals("") || password.trim().equals(""))
		{
			Toast.makeText(getApplicationContext(), "用户名或者密码不能为空!",Toast.LENGTH_SHORT).show();
		}
		else
		{
			if(signaddress==1)
			{
				//下载通讯录，显示进度条
				mDialog = new ProgressDialog(ActivityLogin.this);
				mDialog.setTitle("登录");
				mDialog.setMessage("正在登录服务器，请稍后...");
				mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				mDialog.setProgress(100);
				mDialog.setCancelable(true);  // 设置是否可以通过点击Back键取消
				mDialog.setCanceledOnTouchOutside(false);  // 设置在点击Dialog外是否取消Dialog进度条
				mDialog.show();

				if( m_MutexProgress==0 ) {
					Threadprogress();
				}
			}
			else
			{
				mDialog = new ProgressDialog(ActivityLogin.this);
				mDialog.setTitle("登录");
				mDialog.setMessage("正在登录服务器，请稍后...");
				mDialog.setCancelable(true);  // 设置是否可以通过点击Back键取消
				mDialog.setCanceledOnTouchOutside(false);  // 设置在点击Dialog外是否取消Dialog进度条
				mDialog.show();
			}

			//登录验证用户密码
			Thread loginThread = new Thread(new ThreadLogin());
			loginThread.start();
		}
	}

	OnKeyListener onKey = new OnKeyListener() 
    {
	    @Override  
		public boolean onKey(View v, int keyCode, KeyEvent event) 
	    {
	    	int code = 0;
	    	code = event.getAction();
	    	
	    	//回车键
	    	if(keyCode == KeyEvent.KEYCODE_ENTER && code==KeyEvent.ACTION_UP)
	    	{
		    	InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
		    	if(imm.isActive())
		    	{
		    		//隐藏键盘
		    		imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0 );  
		    	}

				int ret = requestPermissionsM2(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,103);
				if(ret!=1) return false;

	    		login(2);
	    		
		    	return true;
	    	}
	    	return false;
		}
    };
	
	//当前使用的保存日志
	public int SaveLog(String loginName,String userid,String phone)
	{
		String strcontent = "";

		if(userid==null) userid = "";

//		if(loginName.length()==0) return 2;
//		if(userid.length()==0) return 3;
//		if(phone.length()==0) return 4;

		strcontent = "登录"+m_phonesim;

        ThreadUtils emperor1 = ThreadUtils.getInstance();  // 得到单例对象
		emperor1.setparm1(loginName,userid,phone);
		emperor1.setparm2("提交", "登录-远程校验", strcontent);
        emperor1.writelog();
        
		return 1;
	}

	//日志 指纹登录
	public int LogLoginFinger(String loginName,String userid)
	{
		String strcontent = "";

		android.util.Log.i("cjwsjy", "------LogLoginFinger-------");

		String model = Build.MODEL;//手机型号
		int version = Build.VERSION.SDK_INT;//SDK版本号
		String versionos = Build.VERSION.RELEASE;//Firmware/OS 版本号

		//model2 = model.replaceAll( " ", "%20");
		String model2 = model.replaceAll( " ", "_");
		String strbuf = "Android-"+versionos+"-"+model2+"-"+m_versionapp;

		if(userid==null) userid = "";

		strcontent = "登录-指纹"+m_phonesim;

		ThreadUtils emperor1 = ThreadUtils.getInstance();  // 得到单例对象
		emperor1.setparm1(loginName,userid,strbuf);
		emperor1.setparm2("提交", "登录-本地校验", strcontent);
		emperor1.writelog();

		return 1;
	}
	
	// 登录，验证用户密码
	class ThreadLogin implements Runnable
	{
		@Override
		public void run()
		{
			Message msg;
			int result = 0;
			int version = 0;
			int length = 0;
			String url = "";
			String strurl2 = "";
			String resultStr = "";
			//String resultStr2 = "";
			String resultVehicle = "";
			String strbuf = "";
			String model;
			String model2 = "";
			String versionos = "";
			String cryptkey = "";
			String cryptname = "";
			String cryptpwd = "";
			String cryptbuf = "";
			boolean bret;
			
			model = Build.MODEL;//手机型号
	    	version = Build.VERSION.SDK_INT;//SDK版本号
	    	versionos = Build.VERSION.RELEASE;//Firmware/OS 版本号
	    	
	    	//model2 = model.replaceAll( " ", "%20");
			model2 = model.replaceAll( " ", "_");
	    	strbuf = "Android-"+versionos+"-"+model2+"-"+m_versionapp;

			// userid
			EmployeeVO data;
			data = db.getEmployeeByName(loginName);
			userid = data.getUserid();

	    	cryptkey = "cjwsjyhsinfo1234";
			try
			{
				cryptname = Decrypt.Encrypt(loginName, cryptkey);
				cryptpwd = Decrypt.Encrypt(password, cryptkey);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}

			//检查标准管理的数据库完整性
			Checkbiaozhun();

			//是否有报备权限
//			url = appUrl+"/OutWeb/outExtraMenuType/"+userid;
//			m_baobei = HttpClientUtil.HttpUrlConnectionGet(url, "UTF-8");

			//出差权限
			CheckOutOffice();

			//是否参会人员
			//MeetingIsUser();

	    	//校验用户密码
	    	url = appUrl+"/CEGWAPServer/login/AES/"+cryptname+"/"+cryptpwd+"/"+strbuf;
			android.util.Log.i("cjwsjy", "------login="+url+"-------");
	    	resultStr = HttpClientUtil.HttpUrlConnectionGet(url, "UTF-8");

			//是否为调度员
			Map<String, String> dataMap = new HashMap<String, String>();
			dataMap.put("account", loginName);
			strurl2 = UrlManager.appRemoteUrl+UrlManager.IS_DISPATCHER;
			resultVehicle = HttpClientUtil.HttpUrlConnectionGet2(strurl2, dataMap,"UTF-8");

			bret = StringHelper.isEmpty(resultStr);
			if(bret==true)
			{
				strbuf = "连接失败：无法连接服务器，请检查您的网络连接";
				//用户名或者密码错误
				msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = strbuf;
				handler.sendMessage(msg);
				return;
			}

			//resultStr = nopassword(loginName);
			//resultStr = "成功";
			try
			{
				if ( resultStr.equals("成功"))
				{
					//保存用户密码到本地缓存
					cashe();
		
					//保存日志
					SaveLog(loginName,userid,strbuf);
					
					msg = handler.obtainMessage();
					msg.what = 4;
					handler.sendMessage(msg);

					m_finish_login = 1;
				}
				else
				{
					if(resultStr.length()==0)
					{
						strbuf = "连接失败：无法连接服务器，请检查您的网络连接";
					}
					else strbuf = resultStr;
					
					//用户名或者密码错误
					msg = handler.obtainMessage();
					msg.what = 1;
					msg.obj = strbuf;
					handler.sendMessage(msg);
				}

				//是否为调度员
				length = resultVehicle.length();
				if( length!=0 )
				{
					Editor editor = sp.edit();
					JSONObject jsonObj = new JSONObject(resultVehicle);
					String isFlag = jsonObj.get("data").toString();
					//isFlag = "True";  //测试
					if ("True".equals(isFlag))
					{
						editor.putBoolean("isDispatcher", true);
						editor.commit();
					}
					else
					{
						editor.putBoolean("isDispatcher", false);
						editor.commit();
					}
				}
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				android.util.Log.i("cjwsjy", "------catch="+e.getLocalizedMessage()+"-------");

				//异常错误
				msg = handler.obtainMessage();
				msg.what = 6;
				handler.sendMessage(msg);
			}
		}
	}

	class ThreadGetContacts implements Runnable
	{
		@Override
		public void run()
		{
			Message msg;
			String url;
			String versionold = "";
			String versionnew = "";
			boolean bresult1;
			boolean bresult2;
			boolean bresult3;

			//获得服务器通讯录的版本
			url = appUrl+"/CEGWAPServer/TXL/getTXLCode";
			versionnew = HttpClientUtil.HttpUrlConnectionGet(url, HttpClientUtil.DEFAULTENC);
			m_ContactsVer = versionnew;

			//是否参会人员
			MeetingIsUser();

			//获得本地通讯录的版本
			versionold = sp.getString("PHONE_BOOK_VERSION", "");
			//版本号是否为空
			bresult1 = versionold.equals("");
			//比较通讯录版本
			bresult2 = versionold.equals(versionnew);
			//通讯录更新没有完成
			bresult3 = sp.getBoolean("SERVICE_INIT_DATA", false);

			android.util.Log.i("cjwsjy", "------old="+versionold+"-------");
			//android.util.Log.i("cjwsjy", "------new="+versionnew+"-------");
			//android.util.Log.i("cjwsjy", "------init="+bresult3+"-------");

			//第一次直接更新通讯录
			if( bresult1==true )
			{
				m_contactsnull = 1;
				DownContact();
			}

			if( versionold.length()==0)
			{
				m_contacts = 1;
				return;
			}

			//if( true )
			if( bresult1==true || bresult2==false || bresult3==false)
			{
				//版本数据为空，表示是第一次，直接下载数据
				//版本号不一样，表示有新数据，更新数据
				//数据库初始化失败，重新下载数据
				//更新;
				m_contacts = 1;
			}
		}
	}

	//下载通讯录
	class ThreadDownPhone implements Runnable
	{
		@Override
		public void run()
		{
			DownContact();
		}
	}

	private int DownContact()
	{
		Message msg;

		m_Mutex = 1;

		if( true )
		{
			//版本数据为空，表示是第一次，直接下载数据
			//版本号不一样，表示有新数据，更新数据
			//数据库初始化失败，重新下载数据
			//更新

			android.util.Log.i("cjwsjy", "--------DownContact-------");

			Editor editor = sp.edit();
			editor.putBoolean("SERVICE_INIT_DATA", false);
			editor.commit();

			Boolean suss = initServiceData();
			if(suss)
			{
				//下载成功
				editor.putBoolean("SERVICE_INIT_DATA", true);
				editor.commit();
			}
			else
			{
				//下载失败，下载失败的处理暂时和成功一样，后面需要再修改
				editor.putBoolean("SERVICE_INIT_DATA", false);
				editor.commit();
			}

			android.util.Log.i("cjwsjy", "------savephone="+m_ContactsVer+"-------DownContact");
			//保存通讯录版本
			editor.putString("PHONE_BOOK_VERSION", m_ContactsVer);
			editor.commit();
		}

		msg = handler.obtainMessage();
		msg.what = 3;
		handler.sendMessage(msg);

		m_Mutex = 0;

		return 1;
	}

	private int Checkbiaozhun()
	{
		int result = 0;
		int nlenth = 0;
		String filename = "";
		m_SDCardPath = Environment.getExternalStorageDirectory().toString();

		filename = m_SDCardPath + "/Download/com.cjwsjy.app/attachment3/";
		//遍历文件里的文件
		File dir = new File(filename);
		File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
		if(files==null) return 1013;

		nlenth = files.length;
		if( files != null )
		{
			for(int i = 0; i<nlenth; i++)
			{
				filename = files[i].getName();

				//在数据库中查找是否存在该文件
				result = db.findBiaozhunByName(filename);
				if( result==0 )
				{
					//添加到数据库
					db.insertBiaozhun(filename);
				}
			}
		}

		return 1;
	}

	private int CheckOutOffice()
	{
		int length = 0;
		int nLeader = 0;
		int nUser = 0;
		int nOutExtra = 0;

		String url;
		String strresult;
		String Permissionstr;
		JSONObject jsonObj;

		try
		{
			//出差权限
			url = appUrl+"/OutWeb/EmployeeDynamicState/checkPermission/"+userid;
			strresult = HttpClientUtil.HttpUrlConnectionGet(url, "UTF-8");

			android.util.Log.i("cjwsjy", "------url="+url+"-------outoffice");

			//为空
			if (strresult == null)
			{
				//没有权限
				return 1013;
			}

			//为0
			length = strresult.length();
			if (length == 0)
			{
				//没有权限
				return 1014;
			}

			jsonObj = new JSONObject(strresult);
			Permissionstr = jsonObj.getString("showLeader");
			if (Permissionstr.equals("1"))
			{
				//有权限
				nLeader = 1;
			}

			Permissionstr = jsonObj.getString("showCompanyUser");
			if (Permissionstr.equals("1"))
			{
				//有权限
				nUser = 1;
			}

			Permissionstr = jsonObj.getString("outExtraMenuType");
			if (Permissionstr.equals("1"))
			{
				//有权限
				nOutExtra = 1;
			}

			Editor editor = sp.edit();
			editor.putInt("isLeader", nLeader);
			editor.putInt("isUser", nUser);
			editor.putInt("isOutExtra", nOutExtra);
			editor.commit();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return 1;
	}

	public List<File> getFileList(String strPath)
	{
		int result = 0;
		int nlenth = 0;
		String fileName = "";
		List<File> lstFile = new ArrayList<File>();  //结果 List

		File dir = new File(strPath);
		File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
		nlenth = files.length;
		if( files != null )
		{
			for(int i = 0; i<nlenth; i++)
			{
				fileName = files[i].getName();

				//在数据库中查找是否存在该文件
				result = db.findBiaozhunByName(fileName);
				if( result==0 )
				{
					//添加到数据库
					db.insertBiaozhun(fileName);
				}

				if (files[i].isDirectory())
				{ // 判断是文件还是文件夹
					getFileList(files[i].getAbsolutePath()); // 获取文件绝对路径
				}
				else if (fileName.endsWith("avi"))
				{ // 判断文件名是否以.avi结尾
					String strFileName = files[i].getAbsolutePath();
					System.out.println("---" + strFileName);
					lstFile.add(files[i]);
				}
				else
				{
					continue;
				}
			}
		}
		return lstFile;
	}

	public void GetFiles(String Path, String Extension, boolean IsIterative)  //搜索目录，扩展名，是否进入子文件夹
	{
		File[] files = new File(Path).listFiles();

		for (int i = 0; i < files.length; i++)
		{
			File f = files[i];
			if (f.isFile())
			{
				if (f.getPath().substring(f.getPath().length() - Extension.length()).equals(Extension))  //判断扩展名
					lstFile.add(f.getPath());

				if (!IsIterative)
					break;
			}
			else if (f.isDirectory() && f.getPath().indexOf("/.") == -1)  //忽略点文件（隐藏文件/文件夹）
				GetFiles(f.getPath(), Extension, IsIterative);
		}
	}

	public String nopassword(String name)
	{
		int result = 0;
		String str1;
		str1 = "用户名错误";

		//关闭日志
		ThreadUtils emperor1 = ThreadUtils.getInstance();  // 得到单例对象
		emperor1.setturnoff(1);

		//本地数据库中查找
		result = db.findEmployeeByName2(name);
		if(result==1) str1 = "成功";

		return str1;
	}

	//2017年会议
	private int MeetingIsUser()
	{
		boolean bresult;
		int nresult = 0;

		//请求会议权限
		nresult = MeetingIsUserPost();
		if( nresult==0 ) return 1015;

//		m_isMeeting="0";
//		m_isZhibo="1";

		//写入本地数据
		Editor editor = sp.edit();
		editor.putString("isOpen", m_isOpen);
		editor.putString("isMeeting", m_isMeeting);
		editor.putString("isZhibo", m_isZhibo);
		editor.putString("ChannelName", m_ChannelName);
		editor.putString("IP_in", m_IP_in);
		editor.putString("Port_in", m_Port_in);
		editor.putString("IP_out", m_IP_out);
		editor.putString("Port_out", m_Port_out);
		editor.putString("Auth", m_Auth);
		editor.putString("zhibopic", m_zhibopic);
		editor.putString("dahuipic1", m_dahuipic1);
		editor.commit();

		//下载首页会议图片
		//是否开启会议
		bresult = m_isOpen.equals("1");
		if( bresult==true )
		{
			bresult = m_isMeeting.equals("1");
			if(bresult==true)  //参会人员
			{
				//下载图片
				if(m_dahuipic1!=null)
				{
					if( m_dahuipic1.length()!=0 )
					{
						GetBitmapFromUrl(m_dahuipic1);
					}
				}
			}
			else  //非参会人员
			{
				bresult = m_isZhibo.equals("1");
				if(bresult==true)
				{
					if(m_dahuipic1!=null)
					{
						if( m_dahuipic1.length()!=0 )
						{
							GetBitmapFromUrl(m_zhibopic);
						}
					}
				}
			}
		}

		return 1;
	}

	private int PostInformation(String str_machine,String str_os,String str_appversion)
	{
		int i = 0;
		int length = 0;
		boolean bresult;
		String url = "";
		String key;
		String str_createDate;
		String jsonStr1 = "";

		Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR); //获取当前年份
		int mMonth = c.get(Calendar.MONTH);//获取当前月份
		int mDay = c.get(Calendar.DAY_OF_MONTH);//获取当前月份的日期号码

		str_createDate = mYear+"-"+mMonth+"-"+mDay;

		Map<String, String> map = new HashMap<String, String>();

		map.put("userId", userid);
		map.put("userName", loginName);
		map.put("userDisplayName", loginName);
		map.put("devicesId", loginName);
		map.put("devicesType", "android");
		map.put("createDate", str_createDate);
		map.put("machine", str_machine);
		map.put("os", str_os);
		map.put("appversion", str_appversion);

		url = appUrl+"/CEGWAPServer/AppPushController/hasUserDevices";
		jsonStr1 = HttpClientUtil.HttpUrlConnectionPost6(url, map, "UTF-8", 106);
		if(jsonStr1.length()==0) return 0;

		try
		{
			JSONObject jsonObj = new JSONObject(jsonStr1);
		}
		catch (JSONException e)
		{
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

		return 1;
	}

	private int PostInformation2(String str_machine, String str_os, String str_appversion)
	{
		int result = 0;
		long time = 0;
		String url = "";
		String strtime = "";
		String jsonstr = "";
		String appUrl = "";
		String str_createDate = "";
		JSONArray jsonArray;
		JSONObject jsonObject;

		appUrl = UrlUtil.HOST;

		jsonArray = new JSONArray();
		jsonObject = new JSONObject();
		time = System.currentTimeMillis();
		strtime = Long.toString(time);

		Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR); //获取当前年份
		int mMonth = c.get(Calendar.MONTH)+1;//获取当前月份
		int mDay = c.get(Calendar.DAY_OF_MONTH);//获取当前月份的日期号码

		str_createDate = mYear+"-"+mMonth+"-"+mDay;

		String android_id = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);

		EmployeeVO data;
		data = db.getEmployeeByName(loginName);

		//用户名 汉字
		m_displayname = data.getUserDisplayName();

		m_str_deviceToken = SmApplication.GetDeviceToken();

		try
		{
			jsonObject.put("userId", userid);
			jsonObject.put("userName", loginName);
			jsonObject.put("userDisplayName", m_displayname);
			jsonObject.put("devicesId", m_str_deviceToken);
			jsonObject.put("devicesType", "android");
			jsonObject.put("createDate", str_createDate);
			jsonObject.put("machine", str_machine);
			jsonObject.put("os", str_os);
			jsonObject.put("appversion", str_appversion);

			jsonstr = jsonObject.toString();

			url = appUrl+"/CEGWAPServer/AppPushController/userDevices";

			HttpClientUtil.HttpUrlConnectionPostLog(url, jsonstr);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		return 1;
	}

	private int MeetingIsUserPost()
	{
		int i = 0;
		int length = 0;
		boolean bresult;
		String url = "";
		String key;
		String value;
		String jsonStr1 = "";

		url = "http://moa.cispdr.com:8087/HYServer/mvc/meeting/isMeeting/"+loginName;
		jsonStr1 = HttpClientUtil.HttpUrlConnectionGet(url, "UTF-8");
		if(jsonStr1.length()==0) return 0;

		try
		{
			JSONObject jsonObj = new JSONObject(jsonStr1);
			Iterator iter = jsonObj.keys();
			while( iter.hasNext() )
			{
				// 获得key
				key = iter.next().toString();
				// 根据key获得value, value也可以是JSONObject,JSONArray,使用对应的参数接收即可
				value = jsonObj.getString(key).toString();

				bresult = key.equals("isOpen");
				if( bresult==true )
				{
					m_isOpen = value;
					continue;
				}

				bresult = key.equals("isMeeting");
				if( bresult==true )
				{
					m_isMeeting = value;
					continue;
				}

				bresult = key.equals("isZhibo");
				if( bresult==true )
				{
					m_isZhibo = value;
					continue;
				}

				bresult = key.equals("ChannelName");
				if( bresult==true )
				{
					m_ChannelName = value;
					continue;
				}

				bresult = key.equals("IP_in");
				if( bresult==true )
				{
					m_IP_in = value;
					continue;
				}

				bresult = key.equals("Port_in");
				if( bresult==true )
				{
					m_Port_in = value;
					continue;
				}

				bresult = key.equals("IP_out");
				if( bresult==true )
				{
					m_IP_out = value;
					continue;
				}

				bresult = key.equals("Port_out");
				if( bresult==true )
				{
					m_Port_out = value;
					continue;
				}

				bresult = key.equals("Auth");
				if( bresult==true )
				{
					m_Auth = value;
					continue;
				}

				bresult = key.equals("zhibopic");
				if( bresult==true )
				{
					m_zhibopic = value;
					continue;
				}

				bresult = key.equals("dahuipic1");
				if( bresult==true )
				{
					m_dahuipic1 = value;
					continue;
				}
			}
		}
		catch (JSONException e)
		{
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

		return 1;
	}

	private Bitmap GetBitmapFromUrl(String strUrl)
	{
		Bitmap bitmap;

		LoaderImpl impl;
		Map<String,SoftReference<Bitmap>> sImageCache;
		sImageCache = new HashMap<String,SoftReference<Bitmap>>();
		impl = new LoaderImpl(sImageCache);

		bitmap = impl.getBitmap(strUrl);

		return bitmap;
	}

	//初始化指纹_兼容代码
	@TargetApi(Build.VERSION_CODES.M)
	private int Fingerinit()
	{
		if(Build.VERSION.SDK_INT < 23) return 1013;

		// init fingerprint.
		m_fingerprintManager = getSystemService(FingerprintManager.class);

		if (!m_fingerprintManager.isHardwareDetected())
		{
			//是否有指纹硬件
			// no fingerprint sensor is detected, show dialog to tell user.
			m_FingerHardware = 0;
		}
		else
		{
			m_FingerHardware = 1;

			if (!m_fingerprintManager.hasEnrolledFingerprints())
			{
				//是否录入至少一个指纹
				// no fingerprint image has been enrolled.
				m_FingerEnrolled = 0;

				Toast.makeText(ActivityLogin.this, "没有录入指纹", Toast.LENGTH_LONG).show();
			}
			else
			{
				m_FingerEnrolled = 1;

				Editor editor = sp.edit();
				editor.putInt("USERDATA.FINGER.STATE", m_FingerEnrolled);
				editor.commit();

//				try
//				{
//					myAuthCallback = new CallbackFinger(handlerFinger);
//				}
//				catch (Exception e)
//				{
//					e.printStackTrace();
//				}
			}
		}

		try {
			mKeyStore = KeyStore.getInstance("AndroidKeyStore");
		} catch (KeyStoreException e) {
			throw new RuntimeException("Failed to get an instance of KeyStore", e);
		}

		try {
			mKeyGenerator = KeyGenerator
					.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
		}

		return 1;
	}

	//初始化指纹_兼容代码
	private int FingerinitCompat()
	{
		// init fingerprint.
		fingerManager = FingerprintManagerCompat.from(this);

		if (!fingerManager.isHardwareDetected())
		{
			//是否有指纹硬件
			// no fingerprint sensor is detected, show dialog to tell user.
			m_FingerHardware = 0;
		}
		else
		{
			m_FingerHardware = 1;

			if (!fingerManager.hasEnrolledFingerprints())
			{
				//是否录入至少一个指纹
				// no fingerprint image has been enrolled.
				m_FingerEnrolled = 0;

				Toast.makeText(ActivityLogin.this, "没有录入指纹", Toast.LENGTH_LONG).show();
			}
			else
			{
				m_FingerEnrolled = 1;

				Editor editor = sp.edit();
				editor.putInt("USERDATA.FINGER.STATE", m_FingerEnrolled);
				editor.commit();

//				try
//				{
//					myAuthCallback = new CallbackFinger(handlerFinger);
//				}
//				catch (Exception e)
//				{
//					e.printStackTrace();
//				}
			}
		}

		return 1;
	}

	@TargetApi(Build.VERSION_CODES.M)
	public void FingerKeyguardSecure()
	{
		KeyguardManager keyguardManager = getSystemService(KeyguardManager.class);

		boolean bret =  keyguardManager.isKeyguardSecure();
		if( bret==false )
		{
			Toast.makeText( ActivityLogin.this, "屏幕锁未打开", Toast.LENGTH_LONG).show();
		}
		else m_KeyguardSecure = 1;
	}

	@TargetApi(Build.VERSION_CODES.M)
	private boolean initCipher(Cipher cipher, String keyName)
	{
		try
		{
			mKeyStore.load(null);
			SecretKey key = (SecretKey) mKeyStore.getKey(keyName, null);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return true;
		}
		catch (KeyPermanentlyInvalidatedException e)
		{
			return false;
		}
		catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
				| NoSuchAlgorithmException | InvalidKeyException e)
		{
			throw new RuntimeException("Failed to init Cipher", e);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		//return true;
	}

	@TargetApi(Build.VERSION_CODES.M)
	public void createKey(String keyName, boolean invalidatedByBiometricEnrollment)
	{
		// The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
		// for your flow. Use of keys is necessary if you need to know if the set of
		// enrolled fingerprints has changed.
		try {
			mKeyStore.load(null);
			// Set the alias of the entry in Android KeyStore where the key will appear
			// and the constrains (purposes) in the constructor of the Builder

			KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
					KeyProperties.PURPOSE_ENCRYPT |
							KeyProperties.PURPOSE_DECRYPT)
					.setBlockModes(KeyProperties.BLOCK_MODE_CBC)
					// Require the user to authenticate with a fingerprint to authorize every use
					// of the key
					.setUserAuthenticationRequired(true)
					.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

			// This is a workaround to avoid crashes on devices whose API level is < 24
			// because KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment is only
			// visible on API level +24.
			// Ideally there should be a compat library for KeyGenParameterSpec.Builder but
			// which isn't available yet.
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
			}
			mKeyGenerator.init(builder.build());
			mKeyGenerator.generateKey();
		} catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
				| CertificateException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	@TargetApi(Build.VERSION_CODES.M)
	public void onPurchased(boolean withFingerprint, FingerprintManager.CryptoObject cryptoObject)
	{
		if (withFingerprint)
		{
			// If the user has authenticated with fingerprint, verify that using cryptography and
			// then show the confirmation message.
			assert cryptoObject != null;
			tryEncrypt(cryptoObject.getCipher());
		}
		else
		{
			// Authentication happened with backup password. Just show the confirmation message.
			//showConfirmation(null);
		}
	}

	private void tryEncrypt(Cipher cipher)
	{
		String SECRET_MESSAGE = "Very secret message";
		try
		{
			byte[] encrypted = cipher.doFinal(SECRET_MESSAGE.getBytes());
			//showConfirmation(encrypted);
		}
		catch (BadPaddingException | IllegalBlockSizeException e)
		{
			Toast.makeText(this, "Failed to encrypt the data with the generated key. "
					+ "Retry the purchase", Toast.LENGTH_LONG).show();
			//Log.e(TAG, "Failed to encrypt the data with the generated key." + e.getMessage());
		}
	}

	//初始数据库
	private boolean initServiceData() 
	{
		return GetServiceData.fillAllServiceData(db);
	}

	// 指纹 Handler
	Handler handlerFinger = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			this.obtainMessage();
			switch (msg.what)
			{
				case 101:  //SUCCESS
					//m_fragment.FingerAuthenticationSucceeded();
					//LogLoginFinger(loginName,userid);//==
					m_fragment.dismiss();
					gotoMainActivity(1);
					break;
				case 102:  //FAILED
					FingerShow(1);//==
					break;
				case 103:  //ERROR
					m_fragment.dismiss();
					break;
				case 104:  //HELP
					m_fragment.dismiss();
					break;
				case 105:  //取消
					m_FingerCancel = 1;
					loginBtn.setText("登  录");
					break;
				default:
					m_fragment.dismiss();
					break;
			}
		}
	};

	// Handler
	Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			this.obtainMessage();
			switch (msg.what)
			{
				case 0:
					mDialog.cancel();
					gotoMainActivity(1);
					break;
				case 1:  //登录时，用户密码错误
					Toast.makeText(getApplicationContext(), msg.obj.toString(),Toast.LENGTH_SHORT).show();
					mDialog.cancel();
					break;
				case 2:
					mDialog.cancel();
					Toast.makeText(getApplicationContext(), "网络连接超时",Toast.LENGTH_SHORT).show();
					break;
				case 3: //通讯录下载完成
					android.util.Log.i("cjwsjy", "------通讯录下载完成-------");
					if(m_finish_login==1)
					{
						mDialog.dismiss();
						gotoMainActivity(2);
					}
					break;
				case 4:  //登录成功
					android.util.Log.i("cjwsjy", "------登录成功-------");
					if(m_finish_phone==1)
					{
						mDialog.dismiss();
						gotoMainActivity(3);
					}
					else
					{
						//Threadprogress();
					}
					break;
				case 5:
					if( mDialog!=null ) { mDialog.cancel(); }

					Toast.makeText(getApplicationContext(), "通讯录下载失败，失败类型1035",Toast.LENGTH_SHORT).show();
					break;
				case 6:
					if( mDialog!=null ) { mDialog.cancel(); }

					Toast.makeText(getApplicationContext(), "登录失败，失败类型1036",Toast.LENGTH_SHORT).show();
					break;
				default:
					if( mDialog!=null ) { mDialog.cancel(); }

					Toast.makeText(getApplicationContext(), "登录失败，失败类型1037",Toast.LENGTH_SHORT).show();
					break;
			}
		}
	};

	//刷新进度条线程
	private void Threadprogress() 
	{
		new Thread()
        {
            public void run()
            {
                try
                {
					m_MutexProgress = 1;
                    while( UrlUtil.g_count<100 )
                    {
                        // 由线程来控制进度。
                    	mDialog.setProgress(UrlUtil.g_count);
                        Thread.sleep(100);
                    }
					m_MutexProgress = 0;
                }
                catch (InterruptedException e)
                {
                	e.printStackTrace();
                }
            }
        }.start();
	}
	
	private void cashe() 
	{
		int nlen = 0;
		String displayname = "";
		String number = "";
		Editor editor = sp.edit();

		//SD卡路径
		editor.putString("USERDATA.SDCARD.PATH", m_SDCardPath);

		//用户，密码
		editor.putBoolean("SAVE_INFO", true);
		editor.putString(sp_loginName, loginName);
		editor.putString(sp_password, password);

		EmployeeVO data;
		data = db.getEmployeeByName(loginName);

		//用户名 汉字
		m_displayname = data.getUserDisplayName();
		editor.putString(sp_DisplayName, m_displayname);

		// userid
		userid = data.getUserid();
		editor.putString(sp_userid, userid);

		//出差审批里的报备权限
		nlen = m_baobei.length();
		if( m_baobei==null || nlen==0 )
		{
			m_baobei = "0";
		}
		editor.putString(sp_baobei, m_baobei);

		//员工编号
		number = data.getjobNumber();
		editor.putString(sp_userJobNumber, number);
		
		editor.commit();
	}
	
	private void gotoMainActivity(int debug)
	{
		GetPhoneState();//==

		//跳转
		chuchaistr = "";
		qianbaostr = "";
		Intent intent = new Intent();
		intent.setClass(ActivityLogin.this, MainActivity.class);
		intent.putExtra("chuchai", chuchaistr);
		intent.putExtra("qianbao", qianbaostr);
		startActivity(intent);
		
		finish();
	}
}
