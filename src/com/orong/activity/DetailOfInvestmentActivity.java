package com.orong.activity;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.orong.Constant;
import com.orong.R;
import com.orong.entity.Contract;
import com.orong.entity.HttpDatas;
import com.orong.entity.LoanDetail;
import com.orong.entity.ProjectInfo;
import com.orong.utils.FileUitls;
import com.orong.utils.JSONUtil;
import com.orong.utils.LoadImageRespone;
import com.orong.utils.net.NetUtils;
import com.orong.utils.net.NetUtils.DownloadCallback;
import com.orong.utils.net.HttpAsyncTask.TaskCallBack;

import android.R.color;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @Title: DetailOfInvestmentActivity.java
 * @Description: 我要投资-》要融资项目的融资详情
 * @author lanhaizhong
 * @date 2013年7月12日下午2:14:17
 * @version V1.0 Copyright (c) 2013 Company,Inc. All Rights Reserved.
 */
public class DetailOfInvestmentActivity extends BaseActivity {

	private ImageView ivProjectIC;// 项目的图标
	private TextView tvProjectTheme;// 投资项目主题
	private TextView tvProjectSum;// 项目金额
	//private CheckBox cbProjectStatus;// 项目可投资状态
	private TextView tvInvestmentInfo;// 投资信息

	private ProgressBar pbProjectProgress;// 进度条
	private TextView tvProgressText;// 进度
	private TextView tvRatePercent;// 利率
	private TextView tvTimeLimit;// 期限
	private TextView tvIndemnity;// 保障
	private TextView tvLave;// 剩余

	private LinearLayout llProjectInfo;// 项目信息
	private LinearLayout llContractInfo;// 合同信息
	private LinearLayout llRepayPlan;// 还款计划

	private Button btInvest;// 我要投资
	private LoanDetail detail;
	private String loanId;// 投资id
	private String loanName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_of_investment);
		initivReabck(this);
		setTitle(this, R.string.detailof_inve);
		initView();
		setViewValue();
	}

	/**
	 * 给view对象赋值
	 */
	private void setViewValue() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		Serializable serializable = bundle.getSerializable("LoanDetail");
		if (serializable == null) {
			Toast.makeText(this, "加载出错了", 0).show();
			finish();
		} else {
			detail = (LoanDetail) serializable;
			loanName = detail.getLoanName();
		}
		tvProjectTheme.setText(detail.getLoanName());
		tvProjectSum.setText(String.format(getString(R.string.sum), detail.getMoney()));
		tvInvestmentInfo.setText(String.format(getString(R.string.income_str), (detail.getInterestRate()* 100f)));
		pbProjectProgress.setProgress((int) detail.getSchedule());
		tvProgressText.setText(detail.getSchedule() + "%");
		tvRatePercent.setText(detail.getInterestRate() + "%");
		tvTimeLimit.setText(detail.getDeadline());
		tvIndemnity.setText(detail.getGuarantee());
		tvLave.setText(detail.getTimeRemaining());
		loanId = bundle.getString("Loanid");
		//cbProjectStatus.setChecked(detail.isInvestment());
		btInvest.setClickable(detail.isInvestment());

		String url = detail.getPicture();
		String filename = url.substring(url.lastIndexOf('/'));
		Bitmap bitmap = FileUitls.getBitmapfromSDPathByName(this, Constant.PROJECTIMG + filename);
		if (bitmap != null) {
			ivProjectIC.setImageBitmap(bitmap);
		} else {
			NetUtils.downLoadImage(url, this, Constant.PROJECTIMG, new DownloadCallback() {
				@Override
				public void loadCompleteCallback(LoadImageRespone respone) {
					ivProjectIC.setImageBitmap(respone.getBitmap());
				}

				@Override
				public void beforeDownload() {
				}
			});
		}

	}

	@Override
	public void initView() {
		super.initView();
		ivProjectIC = (ImageView) this.findViewById(R.id.iv_project_ic);
		tvProjectTheme = (TextView) this.findViewById(R.id.tv_project_theme);
		tvProjectSum = (TextView) this.findViewById(R.id.tv_project_sum);
		//cbProjectStatus = (CheckBox) this.findViewById(R.id.cb_pro_status);
		tvInvestmentInfo = (TextView) this.findViewById(R.id.tv_investment_info);

		pbProjectProgress = (ProgressBar) this.findViewById(R.id.pb_project_progress);
		tvProgressText = (TextView) this.findViewById(R.id.tv_progress_text);
		tvRatePercent = (TextView) this.findViewById(R.id.tv_rate_percent);
		tvTimeLimit = (TextView) this.findViewById(R.id.tv_time_limit);
		tvIndemnity = (TextView) this.findViewById(R.id.tv_indemnity);
		tvLave = (TextView) this.findViewById(R.id.tv_lave);

		llProjectInfo = (LinearLayout) this.findViewById(R.id.ll_project_info);
		llProjectInfo.setOnClickListener(this);
		llContractInfo = (LinearLayout) this.findViewById(R.id.ll_contract_info);
		llContractInfo.setOnClickListener(this);
		llRepayPlan = (LinearLayout) this.findViewById(R.id.ll_repayed_plan);
		llRepayPlan.setOnClickListener(this);

		btInvest = (Button) this.findViewById(R.id.bt_invest);
		btInvest.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.ll_project_info:
			getInvestmentInfo();
			break;
		case R.id.ll_contract_info:
			getContractInfo();
			break;
		case R.id.ll_repayed_plan:
			getRepayPlanInfo();
			break;
		case R.id.bt_invest:

			getInvestSumInfo();

			break;

		default:
			super.onClick(v);
			break;
		}

	}

	/**
	 * 获取可投资金额信息
	 */
	private void getInvestSumInfo() {
		HttpDatas datas = new HttpDatas(Constant.LOANAPI);
		datas.putParam("method", "GetSurplusAmout");
		datas.putParam("loanID", loanId);
		NetUtils.sendRequest(datas, DetailOfInvestmentActivity.this, getString(R.string.requesting), new TaskCallBack() {
			double bal;// 用户可用余额
			double available;// 项目可投资金额

			@Override
			public int excueHttpResponse(String respondsStr) {
				int code = 0;
				try {
					JSONObject jsonObject = new JSONObject(respondsStr);
					code = jsonObject.getInt("code");
					if (code == Constant.RESPONSE_OK) {
						bal = jsonObject.getDouble("bal");
						available = jsonObject.getDouble("available");
					}
				} catch (JSONException e) {
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
				case Constant.RESPONSE_OK:
					Intent investIntent = new Intent(DetailOfInvestmentActivity.this, InvestActivity.class);
					investIntent.putExtra("Bal", bal);
					investIntent.putExtra("Sum", available); // 所需金额
					investIntent.putExtra("LoanId", loanId);
					startActivity(investIntent);
					break;

				default:
					showResulttoast(result, DetailOfInvestmentActivity.this);
					break;
				}
			}
		});
	}

	/**
	 * 获取还款计划
	 */
	private void getRepayPlanInfo() {
		HttpDatas datas = new HttpDatas(Constant.LOANAPI);
		datas.putParam("method", "GetRefundPlan");
		datas.putParam("loanID", loanId);
		NetUtils.sendRequest(datas, DetailOfInvestmentActivity.this, getString(R.string.requesting), new TaskCallBack() {
			String jsonArrayStr;

			@Override
			public int excueHttpResponse(String respondsStr) {
				int code = 0;
				try {
					JSONObject jsonObject = new JSONObject(respondsStr);
					code = jsonObject.getInt("code");
					if (code == Constant.RESPONSE_OK) {
						jsonArrayStr = jsonObject.getJSONArray("data").toString();
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
				case Constant.RESPONSE_OK:
					Intent repayIntent = new Intent(DetailOfInvestmentActivity.this, RepayPlanActivity.class);
					repayIntent.putExtra("RefundPlanJSON", jsonArrayStr);
					repayIntent.putExtra("LoanName", loanName);
					startActivity(repayIntent);
					break;
				default:
					showResulttoast(result, DetailOfInvestmentActivity.this);
					break;
				}
			}
		});
	}

	/**
	 * 获取合同信息
	 */
	private void getContractInfo() {
		HttpDatas datas = new HttpDatas(Constant.LOANAPI);
		datas.putParam("method", "GetContract");
		datas.putParam("loanID", loanId);
		NetUtils.sendRequest(datas, DetailOfInvestmentActivity.this, getString(R.string.requesting), new TaskCallBack() {
			Contract contract;

			@Override
			public int excueHttpResponse(String respondsStr) {
				int code = 0;
				try {
					JSONObject jsonObject = new JSONObject(respondsStr);
					code = jsonObject.getInt("code");
					if (code == Constant.RESPONSE_OK) {
						contract = JSONUtil.jsonObject2Bean(jsonObject, Contract.class);
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
				case Constant.RESPONSE_OK:
					Intent contractIntent = new Intent(DetailOfInvestmentActivity.this, ContractInfoActivity.class);
					contractIntent.putExtra("Contract", contract);
					startActivity(contractIntent);
					break;

				default:
					break;
				}
			}
		});
	}

	/**
	 * 获取项目信息
	 */
	private void getInvestmentInfo() {
		HttpDatas datas = new HttpDatas(Constant.PROJECTAPI);
		datas.putParam("method", "GetProject");
		datas.putParam("projectID", detail.getProjectId());
		NetUtils.sendRequest(datas, DetailOfInvestmentActivity.this, getString(R.string.requesting), new TaskCallBack() {
			ProjectInfo info;

			@Override
			public int excueHttpResponse(String respondsStr) {
				int code = 0;
				try {
					JSONObject jsonObject = new JSONObject(respondsStr);
					code = jsonObject.getInt("code");
					if (code == Constant.RESPONSE_OK) {
						info = JSONUtil.jsonObject2Bean(jsonObject, ProjectInfo.class);
					}
				} catch (JSONException e) {
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
				case Constant.RESPONSE_OK:
					Intent projectIntent = new Intent(DetailOfInvestmentActivity.this, ProjectInfoActivity.class);
					projectIntent.putExtra("ProjectInfo", info);
					startActivity(projectIntent);
					break;

				default:
					showResulttoast(result, DetailOfInvestmentActivity.this);
					break;
				}
			}
		});
	}

}
