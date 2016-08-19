package com.orong.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.ScrollView;

/**
 * @Title: MainPaperAdapter.java
 * @Description: 主页面的PaperAdapter
 * @author lanhaizhong
 * @date 2013年7月5日 下午4:06:30
 * @version V1.0 Copyright (c) 2013 Crong.com,Inc. All Rights Reserved.
 */
public class MainPaperAdapter extends FragmentPagerAdapter {

	private ArrayList<Fragment> fragmentlist;
	public MainPaperAdapter(FragmentManager fm,
			ArrayList<Fragment> fragmentlist) {

		super(fm);
		this.fragmentlist = fragmentlist;
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int index) {
	
		return (fragmentlist == null ? null : fragmentlist.get(index));
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return (fragmentlist == null ? 0 : fragmentlist.size());
	}


}
