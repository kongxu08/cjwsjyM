package com.cjwsjy.app.custom;

import com.cjwsjy.app.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class DialogProgress extends Dialog
{
	public DialogProgress(Context context)
	{
		super(context);
		// TODO 自动生成的构造函数存根
	}

    public DialogProgress(Context context, int theme){
        super(context, theme);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_dialog);
    }
    
}
