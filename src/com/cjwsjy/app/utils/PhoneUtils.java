package com.cjwsjy.app.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts.Data;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.util.Log;

import java.io.ByteArrayOutputStream;

//调用系统方法帮助类
public class PhoneUtils {

	public static void toEmail(Context context, String email) {
		String mail = "mailto:" + email;
		Intent data = new Intent(Intent.ACTION_SENDTO);
		data.setData(Uri.parse(mail));
		data.putExtra(Intent.EXTRA_SUBJECT, "这是标题");
		data.putExtra(Intent.EXTRA_TEXT, "这是内容");
		context.startActivity(data);
	}
	
	//判断通讯录是是否存在该号码
	public static boolean queryContact(Context context, String phoneNumber)
	{
		boolean flag=true;
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] PHONES_PROJECTION = new String[] {Phone.DISPLAY_NAME, Phone.NUMBER,Phone.CONTACT_ID };
		Cursor cursor = context.getContentResolver().query(uri, PHONES_PROJECTION,
				Phone.NUMBER+"= ?",new String[] {phoneNumber}, null);
		if(cursor!=null)
		{
			while(cursor.moveToNext())
			{
				String mobileNumber = cursor.getString (cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)); 
				//Log.d("TagTest", "aa11:"+mobileNumber);
				if(phoneNumber.equals(mobileNumber))
				{
					flag =false;
					break;
				}
			}
		}
		return flag;
	}
	
	public static boolean insertContact(Context context, String given_name, String mobile_number, String mobile_Iphone,
					String mobile_short, String work_email, Bitmap image, String im_qq)
	{
		try
		{
			ContentValues values = new ContentValues();

			// 下面的操作会根据RawContacts表中已有的rawContactId使用情况自动生成新联系人的rawContactId
			Uri rawContactUri = context.getContentResolver().insert(RawContacts.CONTENT_URI, values);
			long rawContactId = ContentUris.parseId(rawContactUri);

			// 向data表插入姓名数据
			if (!StringHelper.isEmpty(given_name))
			{
				values.clear();
				values.put(Data.RAW_CONTACT_ID, rawContactId);
				values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
				values.put(StructuredName.GIVEN_NAME, given_name);
				context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
			}

			// 向data表插入头像
			if( image!=null )
			{
	            ByteArrayOutputStream array = new ByteArrayOutputStream();
	            //image.compress(Bitmap.CompressFormat.JPEG, 100, array);
	            image.compress(Bitmap.CompressFormat.PNG, 100, array);
	            byte[] avatar = array.toByteArray();

				values.clear();
				values.put(Data.RAW_CONTACT_ID, rawContactId);
				values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
	            values.put(Photo.PHOTO, avatar);
	            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
			}

			// 向data表插入Iphone电话数据
			if (!StringHelper.isEmpty(mobile_Iphone))
			{
				values.clear();
				values.put(Data.RAW_CONTACT_ID, rawContactId);
				values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
				values.put(Phone.NUMBER, mobile_Iphone);
				values.put(Phone.TYPE, Phone.TYPE_MOBILE);
				context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
			}

			// 向data表插入电话数据
			if (!StringHelper.isEmpty(mobile_number))
			{
				values.clear();
				values.put(Data.RAW_CONTACT_ID, rawContactId);
				values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
				values.put(Phone.NUMBER, mobile_number);
				values.put(Phone.TYPE, Phone.TYPE_MOBILE);
				context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
			}

			// 向data表插入短号电话数据
			if (!StringHelper.isEmpty(mobile_short))
			{
				values.clear();
				values.put(Data.RAW_CONTACT_ID, rawContactId);
				values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
				values.put(Phone.NUMBER, mobile_short);
				values.put(Phone.TYPE, Phone.TYPE_MOBILE);
				context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
			}

			// 向data表插入Email数据
			if (!StringHelper.isEmpty(work_email)) {
				values.clear();
				values.put(Data.RAW_CONTACT_ID, rawContactId);
				values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
				values.put(Email.DATA, work_email);
				values.put(Email.TYPE, Email.TYPE_WORK);
				context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
			}

			// 向data表插入QQ数据
			if (!StringHelper.isEmpty(im_qq))
			{
				values.clear();
				values.put(Data.RAW_CONTACT_ID, rawContactId);
				values.put(Data.MIMETYPE, Im.CONTENT_ITEM_TYPE);
				values.put(Im.DATA, im_qq);
				values.put(Im.PROTOCOL, Im.PROTOCOL_QQ);
				context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
			}
			// 向data表插入头像数据
			// Bitmap sourceBitmap =
			// BitmapFactory.decodeResource(getResources(),
			// R.drawable.icon);
			// final ByteArrayOutputStream os = new ByteArrayOutputStream();
			// // 将Bitmap压缩成PNG编码，质量为100%存储
			// sourceBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
			// byte[] avatar = os.toByteArray();
			// values.put(Data.RAW_CONTACT_ID, rawContactId);
			// values.put(Data.MIMETYPE, Photo.CONTENT_ITEM_TYPE);
			// values.put(Photo.PHOTO, avatar);
			// getActivity().getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
			// values);
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}
}
