package com.orong.activity;

import com.orong.Constant;
import com.orong.OrongApplication;
import com.orong.R;
import com.orong.utils.APPUltil;
import com.orong.utils.net.HttpAsyncTask;
import com.orong.view.CustomDialog;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

/**
 * 
 * @ClassName:SplashActivity
 * @Description: 欢迎界面
 * @author lanhaizhong
 * @date 2013年7月3日 下午3:02:59
 * 
 */
public class SplashActivity extends Activity {
	private APPUltil appUltil;// 版本控制
//	private CustomDialog newVersionDialog; // 新版本提示对话框
//	private HttpAsyncTask cheVersionTask; // 检测新版本 的一步任务

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		appUltil = new APPUltil(SplashActivity.this, handler);
		int versionCode = appUltil.getVersionCode();// 当前版本信息
		SharedPreferences sp = OrongApplication.getOrSharedPrefences(this);
		int saveCode=sp.getInt(Constant.SAVAVERSIONCODE, 0);//获取上一次运行时的版本信息
		if(versionCode>saveCode){//新的版本
			handler.postDelayed(runnable, 1500);
			Editor editor=sp.edit();
			editor.putInt(Constant.SAVAVERSIONCODE, versionCode);
			editor.commit();
			editor.clear();
		}else{
			Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
		}
	//cheVersionTask = appUltil.getNewstVersion(null);
		
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case 1000:
//				handler.removeCallbacks(runnable);
//				Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//				startActivity(intent);
//				finish();
//				break;
//
//			default:
//				break;
//			}
			super.handleMessage(msg);
		}
	};

	Runnable runnable = new Runnable() {
		public void run() {
//			newVersionDialog = appUltil.getDialog();
//			if (newVersionDialog == null) {
				Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
//			}
		}
	};

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (requestCode == 100) {
//			System.out.println(resultCode);
//			handler.sendEmptyMessage(1000);
//		}
//	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// if(appUltil.g)
//		if (cheVersionTask != null && !cheVersionTask.isCancelled()) {
//			cheVersionTask.cancel(true);
//		}
//		if (appUltil.getDialog() != null && appUltil.getDialog().isShowing()) {
//			appUltil.getDialog().dismiss();
//		}
		super.onDestroy();
	}
}
