package com.cjwsjy.app;

import com.cjwsjy.app.Finger.FingerprintAuthenticationDialogFragment;
import com.cjwsjy.app.main.MainActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class ActivityLogin2 extends Activity 
{  
	private int sign;
	private int m_FingerEnrolled;
	private String password;
	private Button loginBtn;
	private EditText edit_password;
	private SharedPreferences sp;

	private KeyStore mKeyStore;
	private KeyGenerator mKeyGenerator;

	private FingerprintAuthenticationDialogFragment m_fragment = null;
	private FingerprintManager m_fingerprintManager = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login2);
        
        edit_password = (EditText) findViewById(R.id.et_password);
        
        loginBtn = (Button) findViewById(R.id.btn_login);
        
        TextView textview = (TextView) findViewById(R.id.textView1);
        
        String version = SmApplication.sharedProxy.getString("curVersion", "1.0");
		String strver = "版本："+version;
		textview.setText(strver);
		
		Intent intent = getIntent();
		sign = intent.getIntExtra("sign", 0);
		  
        sp = SmApplication.sp;

		m_FingerEnrolled = sp.getInt("USERDATA.FINGER.STATE", 0);

        edit_password.setOnKeyListener(onKey);
        
		// 登录
		loginBtn.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				login(2);
			}
		});

		if(m_FingerEnrolled==1)
		{
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

			//FingerShow(1);

			Message msg;
			msg = handlerFinger2.obtainMessage();
			msg.what = 102;
			//handlerFinger.sendMessage(msg);
			handlerFinger2.sendMessageDelayed(msg,500);
		}
    }
    
    OnKeyListener onKey = new OnKeyListener() 
    {
	    @Override  
		public boolean onKey(View v, int keyCode, KeyEvent event) 
	    {
	    	// TODO Auto-generated method stub  
	    	int code1 = 0;
	    	int code2 = 0;
	    	code1 = event.getKeyCode();
	    	code2 = event.getAction();
	    	
	    	// 按下回车键后隐藏键盘
	    	if( keyCode==KeyEvent.KEYCODE_ENTER && code2==KeyEvent.ACTION_UP)
	    	{
		    	InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
		    	if(imm.isActive())
		    	{
		    		imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0 );  
		    	}
		    	
	    		login(1);
	    		
		    	return true;
	    	}
	    	return false;
		}
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@TargetApi(Build.VERSION_CODES.M)
	private int FingerShow(int debug)
	{
		m_fingerprintManager = getSystemService(FingerprintManager.class);

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
		m_fragment.setFingerObject(m_fingerprintManager);
		m_fragment.SetHandlerFinger(handlerFinger2);
		//m_fragment.SetDialogTitle("验 证");

		if (true)
		{
			m_fragment.setStage(FingerprintAuthenticationDialogFragment.Stage.FINGERPRINT);
		}
		else
		{
			m_fragment.setStage(FingerprintAuthenticationDialogFragment.Stage.PASSWORD);
		}
		m_fragment.show(getFragmentManager(), "myFragment2");

		return 1;
	}

  //系统按钮，“回退”键
  	@Override
  	public void onBackPressed()
  	{
  		super.onBackPressed();

  		//返回HOME桌面
  		Intent intent= new Intent(Intent.ACTION_MAIN);  
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        intent.addCategory(Intent.CATEGORY_HOME);  
        startActivity(intent);
  		
  		//System.exit(0);
  	}

 	//登录
	public void login(int debug)
	{
		String pwstr;
		
		password = edit_password.getText().toString();
		
		pwstr = sp.getString("USERDATA.LOGIN.PASSWORD", "");
		
		// 密码不能为空
		if ( password.trim().equals("")) 
		{
			Toast.makeText(getApplicationContext(), "密码不能为空!",Toast.LENGTH_SHORT).show();
			return;
		}
		
		//sp中缓存的密码不能为空
		if( pwstr.equals("") )
		{
			finish();
			return;
		}
		
		//if( true )
		if( password.equals(pwstr) )
		{
			//密码正确
			SmApplication.sethomekey(0,2);
			finish();
		}
		else
		{
			Toast.makeText(getApplicationContext(), "密码错误!",Toast.LENGTH_SHORT).show();
		}
	}

	@TargetApi(Build.VERSION_CODES.M)
	private boolean initCipher(Cipher cipher, String keyName) {
		try {
			mKeyStore.load(null);
			SecretKey key = (SecretKey) mKeyStore.getKey(keyName, null);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return true;
		} catch (KeyPermanentlyInvalidatedException e) {
			return false;
		} catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
				| NoSuchAlgorithmException | InvalidKeyException e) {
			throw new RuntimeException("Failed to init Cipher", e);
		}
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

	// 指纹 Handler
	Handler handlerFinger2 = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			android.util.Log.i("cjwsjy", "------msg.what="+msg.what+"-------");
			this.obtainMessage();
			switch (msg.what)
			{
				case 101:  //SUCCESS
					//m_fragment.FingerAuthenticationSucceeded();
					m_fragment.dismiss();
					SmApplication.sethomekey(0,2);
					finish();
					break;
				case 102:  //FAILED
					FingerShow(1);;
					break;
				case 103:  //ERROR
					m_fragment.dismiss();
					break;
				case 104:  //HELP
					m_fragment.dismiss();
					break;
				default:
					m_fragment.dismiss();
					break;
			}
		}
	};
	
}
