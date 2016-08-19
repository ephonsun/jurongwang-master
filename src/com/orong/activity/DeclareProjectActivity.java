package com.orong.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.orong.Constant;
import com.orong.R;
import com.orong.entity.Commission;
import com.orong.entity.HttpDatas;
import com.orong.utils.JSONUtil;
import com.orong.utils.net.NetUtils;
import com.orong.utils.net.HttpAsyncTask.TaskCallBack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * @Title: DeclareProjectActivity.java
 * @Description: 有偿申报项目界面
 * @author lanhaizhong
 * @date 2013年7月10日上午11:25:04
 * @version V1.0 Copyright (c) 2013 Company,Inc. All Rights Reserved.
 */
public class DeclareProjectActivity extends BaseRecommendActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recom_declare);
		initView(this);
		initivReabck(this);

		setTitle(this, R.string.my_declared_project);
		tvRecommendedRules.setText(R.string.the_project_declared_rules);
		setTextOfBtRecommend(getString(R.string.declare_project));

		rlRecommendedRules.setOnClickListener(this);
		btRecommend.setOnClickListener(this);
		
		LoadCommission();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_recommended_rules:
			startActivity(new Intent(this, RulesOfRecProjectActivity.class));
			break;
		case R.id.bt_recommend:
			startActivity(new Intent(this, DoRecProjectActivity.class));
			break;
		case R.id.iv_option_menu:
			showPopupWindow(DeclareProjectActivity.this);
			break;
		default:
			super.onClick(v);
			break;
		}
	}
	

	/**
	 * 加载佣金
	 */
	private void LoadCommission() {
		HttpDatas datas = new HttpDatas(Constant.USERAPI);
		datas.putParam("method", "GetProjectCommission");
		NetUtils.sendRequest(datas, DeclareProjectActivity.this, getString(R.string.loading), new TaskCallBack() {
			Commission commission;//佣金
			@Override
			public int excueHttpResponse(String respondsStr) {
				int code = 0;
				try {
					JSONObject jsonObject = new JSONObject(respondsStr);
					code = jsonObject.getInt("code");
					if (code == 2000) {
						commission = JSONUtil.jsonObject2Bean(jsonObject, Commission.class);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return code;
			}

			@Override
			public void beforeTask() {
			}

			@Override
			public void afterTask(int result) {

				switch (result) {
				case 2000:
					setTextOfTvHasRecommendedNum(getString(R.string.project_has_declared), commission.getCount());
					setTextOfTvHasGotBrokerage(commission.getEarned());
					setTextOfTvWillHaveGotBrokerage(commission.getWithout());
					break;
				default:
					showResulttoast(result, DeclareProjectActivity.this);
					break;
				}
			
			}
		});
	}

}
