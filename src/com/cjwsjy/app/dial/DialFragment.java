package com.cjwsjy.app.dial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.do1.common.util.string.StringUtil;

import com.cjwsjy.app.R;
import com.cjwsjy.app.SmApplication;
import com.cjwsjy.app.dial.view.ContactView;
import com.cjwsjy.app.dial.view.DialpadKeyButton;
import com.cjwsjy.app.main.MainActivity;
import com.cjwsjy.app.utils.ValidUtil;
import com.do1.cjmobileoa.db.DBManager;
import com.do1.cjmobileoa.db.model.EmployeeVO;
import com.do1.cjmobileoa.db.model.SUserVO;

public class DialFragment extends Fragment 
       implements View.OnClickListener,
		View.OnLongClickListener,
		TextWatcher,
		DialpadKeyButton.OnPressedListener 
		{

	private DBManager dbManagerdia;
	
	private EditText mDigits;
	private View mDelete;
	private View mBack;
	private View m_arrow;
	private TextView info_text;

	private ListView listView;

	private View mDialpad;

	private View button_dialpad;

	private final HashSet<View> mPressedDialpadKeys = new HashSet<View>(12);

	/**
	 * 系统设置中的拨号键盘触摸音效开关
	 */

	// 编写按键发声功能
	private ToneGenerator mToneGenerator;
	private final Object mToneGeneratorLock = new Object();
	private boolean mDTMFToneEnabled;

	private boolean mAdjustTranslationForAnimation = false;

	private static final int TONE_LENGTH_INFINITE = -1;
	private static final int TONE_RELATIVE_VOLUME = 80;
	private static final int DIAL_TONE_STREAM_TYPE = AudioManager.STREAM_DTMF;

	private ContactAdapter adapter;
	private MainActivity mainactivity;

	private List<SUserVO> employees = new ArrayList<SUserVO>();
	private boolean showlist = false;
	private FrameLayout fmlayout;
	
    @Override
	public void onHiddenChanged(boolean hidden) 
    {
		super.onHiddenChanged(hidden);
		/*if(!hidden){
		clearDialpad();
		mDelete.setPressed(false);
		if (showlist) 
		{
			listView.setVisibility(View.GONE);
			mDialpad.setVisibility(View.VISIBLE);
			showlist = false;
		}
		}*/
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		final View fragmentView = inflater.inflate(R.layout.dial_fragment,container, false);
		fragmentView.buildLayer();
		
		dbManagerdia = SmApplication.dbManager;
		
		fmlayout = (FrameLayout)fragmentView.findViewById(R.id.fake_action_bar);
		
		mDigits = (EditText) fragmentView.findViewById(R.id.digits);
		mDigits.setOnClickListener(this);
		mDigits.addTextChangedListener(this);
		info_text=(TextView) fragmentView.findViewById(R.id.digits_info);
		info_text.setText("");
		info_text.setOnClickListener(this);

		View oneButton = fragmentView.findViewById(R.id.one);
		if (oneButton != null) {
			setupKeypad(fragmentView);
		}

		mDelete = fragmentView.findViewById(R.id.deleteButton);
		if (mDelete != null) 
		{
			mDelete.setOnClickListener(this);
			mDelete.setOnLongClickListener(this);
		}

		mBack = fragmentView.findViewById(R.id.backButton);
		if (mBack != null) {
			mBack.setOnClickListener(this);
		}

		button_dialpad = fragmentView.findViewById(R.id.button_dialpad);
		if (button_dialpad != null) {
			button_dialpad.setOnClickListener(this);
		}

		m_arrow = fragmentView.findViewById(R.id.imagearrow);
		if (m_arrow != null) {
			m_arrow.setOnClickListener(this);
		}
		m_arrow.setVisibility(View.GONE);
		
		mDialpad = fragmentView.findViewById(R.id.dialpad);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		params.addRule(RelativeLayout.ABOVE, R.id.listview_search_contact);
		params.addRule(RelativeLayout.BELOW, R.id.digits_container);
		mDialpad.setLayoutParams(params);
		
		listView = (ListView) fragmentView.findViewById(R.id.listview_search_contact);
		adapter = new ContactAdapter(getActivity(),ContactView.Display_Mode_Search);

		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);// 一定要设置这个属性，否则ListView不会刷新 
		listView.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,int position, long id) 
			{
				// cur_pos = position;// 更新当前行
				adapter.setSelectItem(position);
	            adapter.notifyDataSetInvalidated();   
	            updateInfo2(position);
			}
		});
		
		listView.setAdapter(adapter);

		return fragmentView;
	}

	private void setupKeypad(View fragmentView) {
		final int[] buttonIds = new int[] { R.id.one, R.id.two, R.id.three,
				R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight,
				R.id.nine, R.id.star, R.id.zero, R.id.pound };

		final int[] numberIds = new int[] { R.string.dialpad_1_number,
				R.string.dialpad_2_number, R.string.dialpad_3_number,
				R.string.dialpad_4_number, R.string.dialpad_5_number,
				R.string.dialpad_6_number, R.string.dialpad_7_number,
				R.string.dialpad_8_number, R.string.dialpad_9_number,
				R.string.dialpad_star_number, R.string.dialpad_0_number,
				R.string.dialpad_pound_number };

		final int[] letterIds = new int[] { R.string.dialpad_1_letters,
				R.string.dialpad_2_letters, R.string.dialpad_3_letters,
				R.string.dialpad_4_letters, R.string.dialpad_5_letters,
				R.string.dialpad_6_letters, R.string.dialpad_7_letters,
				R.string.dialpad_8_letters, R.string.dialpad_9_letters,
				R.string.dialpad_star_letters, R.string.dialpad_0_letters,
				R.string.dialpad_pound_letters };

		final Resources resources = getResources();

		DialpadKeyButton dialpadKey;
		TextView numberView;
		TextView lettersView;

	//根据dpi设置间距
		
		DisplayMetrics metrics = new DisplayMetrics();
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		display.getMetrics(metrics);
		
		Log.d("zhq heightPixels", String.valueOf(metrics.heightPixels));
		Log.d("zhq widthPixels", String.valueOf(metrics.widthPixels));
		Log.d("zhq dpi", String.valueOf(metrics.densityDpi));
		
		int leftMargin = 0;
		int rightMargin = 0;
		int topMargin = 0;
		int bottomMargin = 0;
		
		if(metrics.heightPixels<=1280)
		{
			 leftMargin=50;
			 rightMargin=50;
			 topMargin=30;
			 bottomMargin=5;
		}
		else if(metrics.heightPixels==1920)
		{
			//1920*1080的 无虚拟按键
			 leftMargin=70;
			 rightMargin=70;
			 topMargin=70;
			 bottomMargin=10;
		}
		else if(metrics.heightPixels<1920&&metrics.heightPixels>1700)
		{
			//1920*1080的 有虚拟按键
			 leftMargin=50;
			 rightMargin=50;
			 topMargin=50;
			 bottomMargin=5;
		}
		else if(metrics.heightPixels>1920)
		{
			//2560×1440 的 无虚拟按键
//			 leftMargin=100;
//			 rightMargin=100;
//			 topMargin=120;
//			 bottomMargin=10;

			leftMargin=120;
			rightMargin=120;
			topMargin=120;
			bottomMargin=10;
		}
		
		for (int i = 0; i < buttonIds.length; i++)
		{
			dialpadKey = (DialpadKeyButton) fragmentView.findViewById(buttonIds[i]);

			FrameLayout.LayoutParams params = null;
			if (i % 3 == 0)
			{// 第一列
				params = new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.WRAP_CONTENT,
						FrameLayout.LayoutParams.WRAP_CONTENT,Gravity.RIGHT);
				
			}
			else if (i % 3 == 1)
			{// 第二列
				params = new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.WRAP_CONTENT,
						FrameLayout.LayoutParams.WRAP_CONTENT,Gravity.CENTER_HORIZONTAL);
				params.leftMargin = leftMargin;
				params.rightMargin = rightMargin;
			}
			else if (i % 3 == 2)
			{// 第三列
				params= new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.WRAP_CONTENT,
						FrameLayout.LayoutParams.WRAP_CONTENT,Gravity.LEFT);

			}
			params.topMargin = topMargin;
			params.bottomMargin = bottomMargin;
			dialpadKey.setLayoutParams(params);

			dialpadKey.setOnPressedListener(this);
			// 不知道为什么不加点击没有反应。。。
			dialpadKey.setOnLongClickListener(this);
			
			numberView = (TextView) dialpadKey.findViewById(R.id.dialpad_key_number);
			lettersView = (TextView) dialpadKey.findViewById(R.id.dialpad_key_letters);
			final String numberString = resources.getString(numberIds[i]);
			numberView.setText(numberString);
			dialpadKey.setContentDescription(numberString);
			if (lettersView != null) {
				lettersView.setText(resources.getString(letterIds[i]));
			}
		}
	}
	     
	private void keyPressed(int keyCode) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_1:
			playTone(ToneGenerator.TONE_DTMF_1, TONE_LENGTH_INFINITE);
			break;
		case KeyEvent.KEYCODE_2:
			playTone(ToneGenerator.TONE_DTMF_2, TONE_LENGTH_INFINITE);
			break;
		case KeyEvent.KEYCODE_3:
			playTone(ToneGenerator.TONE_DTMF_3, TONE_LENGTH_INFINITE);
			break;
		case KeyEvent.KEYCODE_4:
			playTone(ToneGenerator.TONE_DTMF_4, TONE_LENGTH_INFINITE);
			break;
		case KeyEvent.KEYCODE_5:
			playTone(ToneGenerator.TONE_DTMF_5, TONE_LENGTH_INFINITE);
			break;
		case KeyEvent.KEYCODE_6:
			playTone(ToneGenerator.TONE_DTMF_6, TONE_LENGTH_INFINITE);
			break;
		case KeyEvent.KEYCODE_7:
			playTone(ToneGenerator.TONE_DTMF_7, TONE_LENGTH_INFINITE);
			break;
		case KeyEvent.KEYCODE_8:
			playTone(ToneGenerator.TONE_DTMF_8, TONE_LENGTH_INFINITE);
			break;
		case KeyEvent.KEYCODE_9:
			playTone(ToneGenerator.TONE_DTMF_9, TONE_LENGTH_INFINITE);
			break;
		case KeyEvent.KEYCODE_0:
			playTone(ToneGenerator.TONE_DTMF_0, TONE_LENGTH_INFINITE);
			break;
		case KeyEvent.KEYCODE_POUND:
			playTone(ToneGenerator.TONE_DTMF_P, TONE_LENGTH_INFINITE);
			break;
		case KeyEvent.KEYCODE_STAR:
			playTone(ToneGenerator.TONE_DTMF_S, TONE_LENGTH_INFINITE);
			break;
		default:
			break;
		}
		if (mAdjustTranslationForAnimation) {
			// 如果系统设置里面选中了触摸反馈，那么就会振动，否则不振动
			mDigits.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			// 忽略系统设置中的触摸反馈设置，始终振动
			// mDigits.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY,
			// HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
		}
		KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
		
		mDigits.onKeyDown(keyCode, event);
		// If the cursor is at the end of the text we hide it.
		final int length = mDigits.length();
		if (length == mDigits.getSelectionStart()
				&& length == mDigits.getSelectionEnd()) {
			mDigits.setCursorVisible(false);
		}
	}

	public void onPressed(View view, boolean pressed) {
		if (pressed) {
			switch (view.getId()) {
			case R.id.one: {
				keyPressed(KeyEvent.KEYCODE_1);
				break;
			}
			case R.id.two: {
				keyPressed(KeyEvent.KEYCODE_2);
				break;
			}
			case R.id.three: {
				keyPressed(KeyEvent.KEYCODE_3);
				break;
			}
			case R.id.four: {
				keyPressed(KeyEvent.KEYCODE_4);
				break;
			}
			case R.id.five: {
				keyPressed(KeyEvent.KEYCODE_5);
				break;
			}
			case R.id.six: {
				keyPressed(KeyEvent.KEYCODE_6);
				break;
			}
			case R.id.seven: {
				keyPressed(KeyEvent.KEYCODE_7);
				break;
			}
			case R.id.eight: {
				keyPressed(KeyEvent.KEYCODE_8);
				break;
			}
			case R.id.nine: {
				keyPressed(KeyEvent.KEYCODE_9);
				break;
			}
			case R.id.zero: {
				keyPressed(KeyEvent.KEYCODE_0);
				break;
			}
			case R.id.pound: {
				keyPressed(KeyEvent.KEYCODE_POUND);
				break;
			}
			case R.id.star: {
				keyPressed(KeyEvent.KEYCODE_STAR);
				break;
			}
			default: {
				break;
			}
			}
			mPressedDialpadKeys.add(view);
		} else {
			view.jumpDrawablesToCurrentState();
			mPressedDialpadKeys.remove(view);
			if (mPressedDialpadKeys.isEmpty()) {
				stopTone();
			}
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// do nothing
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// do nothing
	}

	@Override
	public void afterTextChanged(Editable input) 
	{
		onQueryChanged(mDigits.getText().toString());
		updateDialAndDeleteButtonEnabledState();
		updateInfo(0);
	}

	private void updateInfo(int position) 
	{
		int size = 0;
		int count = adapter.getCount();
		if(count==0) 
		{
			//没有匹配的结果，清空数据
			info_text.setText("");
			//if(showlist==true)
			//{
				m_arrow.setVisibility(View.GONE);
			//}
			
			adapter.setSelectItem(-1);
			listView.setVisibility(View.GONE);
			mDialpad.setVisibility(View.VISIBLE);
			fmlayout.setVisibility(View.VISIBLE);
			showlist = false;
			m_arrow.setSelected(false);
			
			return;
		}
		String number=mDigits.getText().toString();
		SUserVO vo = (SUserVO) adapter.getItem(position);
		
		if(!StringUtil.isNullEmpty(number)&&number.length()>1&&count > position)
		{
			//SUserVO vo = (SUserVO) adapter.getItem(position);
			int index = position+1;
			//String info = vo.getName() + " " + vo.getMobile_type() + "("+index+"/"+ count + ")";
			String info = vo.getName() + " " + "("+index+"/"+ count + ")";
			info_text.setText(info);
			m_arrow.setVisibility(View.VISIBLE);
		}
		else
		{
			info_text.setText("");
			//if(showlist==true)
			//{
				m_arrow.setVisibility(View.GONE);
			//}
			
			adapter.setSelectItem(-1);
			listView.setVisibility(View.GONE);
			mDialpad.setVisibility(View.VISIBLE);
			showlist = false;
			m_arrow.setSelected(false);
		}
	}

	private void updateInfo2(int position) 
	{
		int size = 0;
		int count = adapter.getCount();
		String number=mDigits.getText().toString();
		SUserVO vo = (SUserVO) adapter.getItem(position);
		
		if(!StringUtil.isNullEmpty(number)&&number.length()>1&&count > position)
		{
			//SUserVO vo = (SUserVO) adapter.getItem(position);
			int index = position+1;
			String info = vo.getName() + " " + "("+index+"/"+ count + ")";
			info_text.setText(info);
			m_arrow.setVisibility(View.VISIBLE);
		}
		else
		{
			info_text.setText("");
			//if(showlist==true)
			//{
				m_arrow.setVisibility(View.GONE);
			//}
			
			adapter.setSelectItem(-1);
			listView.setVisibility(View.GONE);
			mDialpad.setVisibility(View.VISIBLE);
			showlist = false;
			m_arrow.setSelected(false);
		}
		
		EmployeeVO employee;
		String userid = vo.getUserId();
		employee = dbManagerdia.findEmployeeById(userid);
		
		//跳转到员工详细界面
		mainactivity.showEmployee(employee,1);
	}
	/**
	 * Update the enabledness of the "Dial" and "Backspace" buttons if
	 * applicable.
	 */
	private void updateDialAndDeleteButtonEnabledState() {
		if (getActivity() == null) {
			return;
		}
		final boolean digitsNotEmpty = !isDigitsEmpty();
		mDelete.setEnabled(digitsNotEmpty);
		if(!isDigitsEmpty())
		{
			mDelete.setVisibility(View.VISIBLE);
			mBack.setVisibility(View.VISIBLE);
		}
		
	}

//	@Override
//	public boolean onKey(View v, int keyCode, KeyEvent event) {
//		return false;
//	}

	@Override
	public boolean onLongClick(View v) {

		switch (v.getId()) {
		case R.id.deleteButton: {
			clearDialpad();
			mDelete.setPressed(false);
			if (showlist) {
				listView.setVisibility(View.GONE);
				mDialpad.setVisibility(View.VISIBLE);
				showlist = false;
			}
			return true;
		}
		}
		return false;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) 
		{
		case R.id.deleteButton: 
		{
			keyPressed(KeyEvent.KEYCODE_DEL);
			if (isDigitsEmpty())
			{
				mDelete.setVisibility(View.GONE);
				mBack.setVisibility(View.GONE);
				//fmlayout.setVisibility(View.VISIBLE);
				// m_arrow.setVisibility(View.GONE);
			}
			return;
		}
		case R.id.backButton: 
		{
			clearDialpad();
			mDelete.setPressed(false);
			//if (showlist) 
			{
				adapter.setSelectItem(-1);
				listView.clearDisappearingChildren();
				listView.setVisibility(View.GONE);
				mDialpad.setVisibility(View.VISIBLE);
				fmlayout.setVisibility(View.VISIBLE);
				showlist = false;
			}
			return;
		}
		case R.id.digits:  //收起拨号盘，展开下拉表
		{
			onExpand();
			return;
		}
		
		case R.id.digits_info:  //收起拨号盘，展开下拉表
		{
			onExpand();
			return;
		}
		case R.id.imagearrow:  //收起拨号盘，展开下拉表
		{
			onExpand();
			return;
		}
		case R.id.button_dialpad:
			if(adapter.getSelectedItem()!=null){
				String number =adapter.getSelectedItem().getMobile();
				makePhoneCall(number);
			}else{
				String number = getDiapadNumber();
				if (!TextUtils.isEmpty(number) && number.length() >= 3) {
					makePhoneCall(number);
				}
			}
	
			break;
		default: {
			return;
		}
		}
	}

	//收起拨号盘，展开下拉表
	private void onExpand() 
	{
		if(showlist==false)
		{
			//收起拨号盘
			if (!isDigitsEmpty()&&!employees.isEmpty()) 
			{
				listView.setVisibility(View.VISIBLE);
				mDialpad.setVisibility(View.GONE);
				fmlayout.setVisibility(View.GONE);
				showlist = true;
				m_arrow.setSelected(true);
			}
		}
		else
		{
			//展开拨号盘
			adapter.setSelectItem(-1);
			listView.setVisibility(View.GONE);
			mDialpad.setVisibility(View.VISIBLE);
			fmlayout.setVisibility(View.VISIBLE);
			showlist = false;
			m_arrow.setSelected(false);
		}

	}
	
	private boolean isDigitsEmpty() {
		return mDigits.length() == 0;
	}

	@Override
	public void onPause() {
		super.onPause();
		//stopTone();
		//mPressedDialpadKeys.clear();
	}

	@Override
	public void onResume() 
	{
		super.onResume();
		/*mPressedDialpadKeys.clear();

		final ContentResolver contentResolver = getActivity().getContentResolver();
		mDTMFToneEnabled = Settings.System.getInt(contentResolver,Settings.System.DTMF_TONE_WHEN_DIALING, 1) == 1;
		synchronized (mToneGeneratorLock) 
		{
			if (mToneGenerator == null) 
			{
				try 
				{
					mToneGenerator = new ToneGenerator(DIAL_TONE_STREAM_TYPE,TONE_RELATIVE_VOLUME);
				} 
				catch (RuntimeException e) 
				{
					mToneGenerator = null;
				}
			}
		}*/
	}

	private void playTone(int tone, int durationMs) {
		if (!mDTMFToneEnabled) {
			return;
		}

		AudioManager audioManager = (AudioManager) getActivity()
				.getSystemService(Context.AUDIO_SERVICE);
		int ringerMode = audioManager.getRingerMode();
		if ((ringerMode == AudioManager.RINGER_MODE_SILENT)
				|| (ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
			return;
		}

		synchronized (mToneGeneratorLock) {
			if (mToneGenerator == null) {
				return;
			}

			mToneGenerator.startTone(tone, durationMs);
		}
	}

	private void stopTone() {
		if (!mDTMFToneEnabled) {
			return;
		}
		synchronized (mToneGeneratorLock) {
			if (mToneGenerator == null) {
				return;
			}
			mToneGenerator.stopTone();
		}
	}

	private void clearDialpad() {
		mDigits.getText().clear();
		mDelete.setVisibility(View.GONE);
		mBack.setVisibility(View.GONE);
	}

	private String getDiapadNumber() {
		return mDigits.getText().toString();
	}

	private void onQueryChanged(final String telNum) 
	{
		int i = 0;
		int size = 0;
		boolean result;
		
		if (telNum.length() >= 2) 
		{
			if (SmApplication.filterMaps.get(telNum) != null) 
			{
				employees = SmApplication.filterMaps.get(telNum);
			} 
			else 
			{
					employees = SearchFilterUtil.filterContact(employees,telNum);
					SmApplication.filterMaps.put(telNum, employees);
			}
			
			//过滤重复的记录
			/*List<SUserVO> employees2 = new ArrayList<SUserVO>();
			//employees2 = employees;
//			HashSet h = new HashSet(employees2);
//			employees2.clear();
//			employees2.addAll(h);

			SUserVO objvo;
			size = employees.size();
			for( i=0; i<size; i++ )
			{
				objvo = employees.get(i);
				result = employees2.contains(objvo);
				if( result==false ) employees2.add(objvo);
			}*/
			
			//排序
			//Collections.sort(employees);
			
			adapter.setContacts(employees);
			adapter.setSelectItem(-1);
			adapter.notifyDataSetInvalidated();
		}
		else
		{
			employees = new ArrayList<SUserVO>();
			adapter.setContacts(employees);
			adapter.setSelectItem(-1);
			adapter.notifyDataSetInvalidated();
		}
	}

	private void makePhoneCall(String number) {
		if (TextUtils.isEmpty(number) || number.length() < 3) {
			return;
		}
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + number));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	//开放外部接口，得到主activity
	public void setmainactivity( MainActivity ma )
	{
		mainactivity = ma;
	}
}
