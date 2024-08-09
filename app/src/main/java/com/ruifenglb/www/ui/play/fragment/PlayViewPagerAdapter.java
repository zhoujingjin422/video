package com.ruifenglb.www.ui.play.fragment;
import com.ruifenglb.www.bean.VodBean;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.ruifenglb.www.R;

import java.util.List;


public class PlayViewPagerAdapter extends FragmentPagerAdapter {
    private VodBean mVodBean;
    private String[] tabs;
    private FragmentManager fragmentManager;
    private List<Fragment> fragments;

    public PlayViewPagerAdapter(@NonNull FragmentManager fm, List<Fragment> fragments, VodBean vodBean) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragmentManager = fm;
        this.fragments = fragments;
        this.mVodBean = vodBean;
        initTabsWithCommentCount();
    }
    private void initTabsWithCommentCount() {
        int comment_num = mVodBean.getComment_num();
        tabs = new String[]{"详情", "影评(" + comment_num + ")"};
    }


    public void replace(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentTop,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public void backClose(){
        fragmentManager.popBackStack();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return tabs.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

}