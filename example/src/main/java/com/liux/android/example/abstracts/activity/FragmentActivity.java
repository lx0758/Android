package com.liux.android.example.abstracts.activity;

import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.liux.android.abstracts.AbstractsActivity;
import com.liux.android.example.R;
import com.liux.android.example.abstracts.fragment.FourFragment;
import com.liux.android.example.abstracts.fragment.OneFragment;
import com.liux.android.example.abstracts.fragment.ThreeFragment;
import com.liux.android.example.abstracts.fragment.TwoFragment;
import com.liux.android.example.databinding.ActivityAbstractsFragmentBinding;

/**
 * Created by Liux on 2017/12/3.
 */

public class FragmentActivity extends AbstractsActivity {

    private ActivityAbstractsFragmentBinding mViewBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewBinding = ActivityAbstractsFragmentBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.vpContent.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new OneFragment();
                    case 1:
                        return new TwoFragment();
                    case 2:
                        return new ThreeFragment();
                    case 3:
                        return new FourFragment();
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        });
        mViewBinding.vpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mViewBinding.rgSelect.check(R.id.rb_one);
                        break;
                    case 1:
                        mViewBinding.rgSelect.check(R.id.rb_two);
                        break;
                    case 2:
                        mViewBinding.rgSelect.check(R.id.rb_three);
                        break;
                    case 3:
                        mViewBinding.rgSelect.check(R.id.rb_four);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewBinding.rgSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_one:
                        mViewBinding.vpContent.setCurrentItem(0);
                        break;
                    case R.id.rb_two:
                        mViewBinding.vpContent.setCurrentItem(1);
                        break;
                    case R.id.rb_three:
                        mViewBinding.vpContent.setCurrentItem(2);
                        break;
                    case R.id.rb_four:
                        mViewBinding.vpContent.setCurrentItem(3);
                        break;
                    default:
                        break;
                }
            }
        });

        mViewBinding.rgSelect.check(R.id.rb_one);
    }
}
