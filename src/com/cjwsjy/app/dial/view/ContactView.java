
package com.cjwsjy.app.dial.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjwsjy.app.R;
import com.do1.cjmobileoa.db.model.SUserVO;


public class ContactView extends FrameLayout {

    private SUserVO contact;
    public TextView nameTextView;
//    public TextView pinyinTextView;
    public TextView phoneTextView;
    public TextView phone_type;
    private Context mContext;
    
    public int Display_Mode = 0;
    
    public static final int Display_Mode_Recent = 1;
    public static final int Display_Mode_Search = 2;
    public static final int Display_Mode_Display = 3;

    public ContactView(Context context, int display) 
    {
        super(context);
        
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.dial_contact_item, this);
        nameTextView = (TextView) findViewById(R.id.text_contact_name);
//        pinyinTextView = (TextView) findViewById(R.id.text_contact_pinyin);
        phoneTextView = (TextView) findViewById(R.id.text_contact_phone);
        //phone_type= (TextView) findViewById(R.id.text_contact_type);
        
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.layout_phone);
        layout.setOnClickListener(new View.OnClickListener()
        {
        	public void onClick(View v) 
        	{
        		String phone;
        		String mobile1;
        		Intent intent;
        		
        		mobile1 = contact.getMobile();
        		phone = "tel:" + mobile1;
    			intent = new Intent(Intent.ACTION_CALL, Uri.parse(phone));
    			mContext.startActivity(intent);
            }
        });
        
        this.Display_Mode = display;
    }

    public ContactView(Context context, AttributeSet attrs) 
    {
        super(context, attrs);
    }

    public ContactView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void build() {
        boolean shouldDisplayMorePhones = true;
        
       String mobile=contact.getMobile();
       String name=contact.getName();
       String organization = contact.getOrganization();
       String py=contact.getPinyin();
       String searchSTR=contact.getSearchStr();
        
        //phone_type.setText(contact.getMobile_type());
        
        SpannableStringBuilder builder;
        int start;
        int end;
       switch (contact.getMatcher_type()) 
       {
       case SUserVO.matcher_type_mobile:
    	   builder = new SpannableStringBuilder(mobile);
    	   start=mobile.indexOf(searchSTR);
    	   end=start+searchSTR.length();
    	   builder.setSpan(new ForegroundColorSpan(Color.RED), start, end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    	   phoneTextView.setText(builder);
           nameTextView.setText(contact.getName()+"  "+contact.getOrganization());
           break;
       case SUserVO.matcher_type_nameNumber:
    	   phoneTextView.setText(mobile);
           nameTextView.setText(name+"  "+organization);
           break;
       case SUserVO.matcher_type_pinyinNumber:
    	   phoneTextView.setText(mobile);
           nameTextView.setText(name+"  "+organization);
           break;
       default:
    	   phoneTextView.setText(mobile);
           nameTextView.setText(name+"  "+organization);
         break;
       }
    }

    public void setContact(SUserVO contact) {
        this.contact = contact;
    }

    public SUserVO getContact() {
        return contact;
    }
}
