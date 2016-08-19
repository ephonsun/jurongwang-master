package com.orong.activity;

import com.orong.Constant;
import com.orong.OrongApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextPaint;
import android.view.Window;
import android.widget.TextView;

/**
 * @Title: BaseFragmentActivity.java
 * @Description: TODO
 * @author lanhaizhong
 * @date 2013年7月5日 上午10:11:26
 * @version V1.0 Copyright (c) 2013 Crong.com,Inc. All Rights Reserved.
 */
public class BaseFragmentActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		OrongApplication.addActivity2Stack(this);
		super.onCreate(arg0);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		OrongApplication.removeActivityFromStack(this);
		super.onDestroy();
	}

	/**
	 * 将中文字体设置为粗体字 android默认不支持中文
	 * 
	 * @param tv
	 */
	public static void setBoldText(TextView tv) {
		TextPaint tp2 = tv.getPaint();
		tp2.setFakeBoldText(true);
	}
	
}
