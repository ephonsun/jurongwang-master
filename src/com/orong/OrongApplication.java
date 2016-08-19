package com.orong;

import java.util.ArrayList;

import org.apache.http.client.CookieStore;

import com.orong.utils.ThreadPoolService;
import com.orong.view.CustomDialog;
import com.orong.view.CustomDialog.ButtonRespond;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextPaint;
import android.widget.TextView;
import android.widget.Toast;

import com.orong.entity.UserInfo;

/**
 * @Title: CRongApplication.java
 * @Description: 应用程序的主程序
 * @author lanhaizhong
 * @date 2013年7月3日 上午10:31:30
 * @version V1.0 Copyright (c) 2013 Company,Inc. All Rights Reserved.
 */
public class OrongApplication extends Application {

	public static OrongApplication instance;// 实例化一个app
	private static ArrayList<Activity> activitystack;// activity启动栈，记录栈中的activity实例
	public static ThreadPoolService service;
	public static UserInfo user = null;
	public static CookieStore cookieStore;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		instance = this;
		
		activitystack = new ArrayList<Activity>();
		service = new ThreadPoolService();
		MyCrashHandler handler = MyCrashHandler.getInstance();
		handler.init(getApplicationContext());
		//把异常处理的handler设置到主线程里面
		Thread.setDefaultUncaughtExceptionHandler(handler);
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		// 程序安全退出
		for (Activity activity : activitystack) {
			activity.finish();
		}
		activitystack.clear();
		super.onTerminate();
	}

	/**
	 * 将应用程序的任务栈中的一activity实例添加到activitystack中
	 * 
	 * @param activity
	 *            一个activity实例
	 */
	public static void addActivity2Stack(Activity activity) {
		instance.activitystack.add(activity);
	}

	/**
	 * 经activity实例从activitystack中移除
	 * 
	 * @param activity
	 *            一个activity实例
	 */
	public static void removeActivityFromStack(Activity activity) {
		instance.activitystack.remove(activity);
	}

	public static OrongApplication getInstance() {
		return instance;
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

	public static SharedPreferences getOrSharedPrefences(Context context) {
		return context.getSharedPreferences(Constant.ORPREFERENCES, Context.MODE_PRIVATE);

	}

	public static void showResultToast(int result, Context context) {
		switch (result) {
		case Constant.NO_RESPONSE:
			Toast.makeText(context, R.string.no_response, 0).show();
			break;
		case Constant.S_EXCEPTION:
			Toast.makeText(context, R.string.server_exception, 0).show();
			break;
		case Constant.RESPONESE_EXCEPTION:
			Toast.makeText(context, R.string.responese_exception, 0).show();
			break;
		case Constant.TIMEOUT:
			Toast.makeText(context, R.string.timeout, 0).show();
			break;
		case Constant.NO_NETWORK:
			Toast.makeText(context, R.string.no_network, 0).show();
			break;
		case Constant.NULLPARAMEXCEPTION:
			Toast.makeText(context, R.string.nullparamexception, 0).show();
			break;
		case Constant.SERVER_EXCEPTION:
			Toast.makeText(context, R.string.server_exception, 0).show();
			break;
		case Constant.RELOGIN:
			CustomDialog dialog = new CustomDialog(context, new ButtonRespond() {

				@Override
				public void buttonRightRespond() {
					Activity activity = activitystack.get(0);
					activitystack.remove(0);// 把登录界面提出来
					OrongApplication.instance.onTerminate();
					activitystack.add(activity);// 重新放到栈中
				}

				@Override
				public void buttonLeftRespond() {
					// TODO Auto-generated method stub
					OrongApplication.instance.onTerminate();
				}
			});
			dialog.setDialogTitle(R.string.relogin);
			dialog.setDialogMassage(R.string.relogin_message);
			dialog.setLeftButtonText(R.string.exit_app);
			dialog.setRightButtonText(R.string.relogin);
			dialog.setCancelable(false);
			dialog.show();
			break;
		case 4005:
			Toast.makeText(context, "缺少参数", 0).show();
			break;
		case 4006:
			Toast.makeText(context, "参数值不能为空", 0).show();
			break;
		default:
			Toast.makeText(context, "请求响应失败，错误号" + result, 0).show();
			break;
		}
	}
}
