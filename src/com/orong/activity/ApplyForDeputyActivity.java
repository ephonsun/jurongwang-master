package com.orong.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.orong.Constant;
import com.orong.R;
import com.orong.entity.HttpDatas;
import com.orong.utils.net.HttpAsyncTask.TaskCallBack;
import com.orong.utils.net.NetUtils;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @Title: ApplyForDeputyActivity.java
 * @Description: 申请成为投资者代表的申请页面
 * @author lanhaizhong
 * @date 2013年7月10日下午2:13:38
 * @version V1.0 Copyright (c) 2013 Company,Inc. All Rights Reserved.
 */
public class ApplyForDeputyActivity extends BaseActivity {

	private EditText etApplyInfo;// 申请成为投资者代表填写的申请信息
	private Button btApply;// 提交申请的按钮

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apply_for_deputy);
		setTitle(this, R.string.become_deputy);
		initivReabck(this);
		etApplyInfo = (EditText) this.findViewById(R.id.et_apply_info);
		btApply = (Button) this.findViewById(R.id.bt_apply);
		btApply.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.bt_apply) {
			String reason = etApplyInfo.getText().toString().trim();
			int length = reason.length();
			if (length < 5) {
				Toast.makeText(getApplicationContext(), R.string.reasonTooSimple, 0).show();//字数太少
				return;
			}
			if(length>100){
				Toast.makeText(getApplicationContext(), R.string.reason100Error, 0).show();//超出100字限制
				return;
			}
			HttpDatas datas = new HttpDatas(Constant.USERAPI);
			datas.putParam("method", "BecomeDeputy");
			datas.putParam("reason", reason);
			NetUtils.sendRequest(datas, ApplyForDeputyActivity.this, getString(R.string.uploading_message), new TaskCallBack() {

				@Override
				public int excueHttpResponse(String respondsStr) {
					int code = 0;
					try {
						JSONObject jsonObject = new JSONObject(respondsStr);
						code = jsonObject.getInt("code");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return code;
				}

				@Override
				public void beforeTask() {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTask(int result) {
					switch (result) {
					case Constant.RESPONSE_OK://成功
						Toast.makeText(getApplicationContext(), R.string.requestApplySuccess, Toast.LENGTH_LONG).show();
						btApply.setClickable(false);
						break;
					case 3008://已经是投资代表
						Toast.makeText(getApplicationContext(), R.string.was_deputy, 0).show();
						btApply.setBackgroundResource(R.drawable.cor6_rounded_rectangle);
						btApply.setClickable(false);
						break;
					case 3009://正在审核中
						Toast.makeText(getApplicationContext(), R.string.auditting, 0).show();
						btApply.setBackgroundResource(R.drawable.cor6_rounded_rectangle);
						btApply.setClickable(false);
						break;
					default:
						showResulttoast(result, ApplyForDeputyActivity.this);
						break;
					}
				}
			});
		}
	}
}
