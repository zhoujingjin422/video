package com.ruifenglb.www.ad;

import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.Fragment;

import com.app.ad.biddingsdk.AdFragmentListener;
import com.app.ad.biddingsdk.AdUtils;
import com.ruifenglb.www.R;
import com.ruifenglb.www.base.BaseMainFragment;


public class ADFragment extends BaseMainFragment {

    Fragment contentFragment ;

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_ad;
    }
    public static ADFragment newInstance() {
        Bundle args = new Bundle();
        ADFragment fragment = new ADFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void Show(){
        if(contentFragment==null){
            AdUtils.GetsContentPageAD(getActivity(), new AdFragmentListener() {
                @Override
                public void onShow(Fragment fragment) {
                    contentFragment = fragment;
                    getChildFragmentManager().beginTransaction().replace(R.id.rootView,
                            contentFragment).commitAllowingStateLoss();
                }
            });
        }
        Log.e("test","show");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
       /* if(hidden){
            if(contentFragment!=null){
                getChildFragmentManager().beginTransaction().remove(
                        contentFragment).commitAllowingStateLoss();
                contentFragment = null;
            }
        }*/
    }


}
