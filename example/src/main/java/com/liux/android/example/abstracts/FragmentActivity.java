package com.liux.android.example.abstracts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.RadioGroup;

import com.liux.android.abstracts.AbstractsActivity;
import com.liux.android.example.R;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Liux on 2017/12/3.
 */

public class FragmentActivity extends AbstractsActivity {

    @BindView(R.id.vp_content)
    ViewPager vpContent;
    @BindView(R.id.rg_select)
    RadioGroup rgSelect;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_fragment);

        ButterKnife.bind(this);

        vpContent.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
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
        vpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        rgSelect.check(R.id.rb_one);
                        break;
                    case 1:
                        rgSelect.check(R.id.rb_two);
                        break;
                    case 2:
                        rgSelect.check(R.id.rb_three);
                        break;
                    case 3:
                        rgSelect.check(R.id.rb_four);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        rgSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_one:
                        vpContent.setCurrentItem(0);
                        break;
                    case R.id.rb_two:
                        vpContent.setCurrentItem(1);
                        break;
                    case R.id.rb_three:
                        vpContent.setCurrentItem(2);
                        break;
                    case R.id.rb_four:
                        vpContent.setCurrentItem(3);
                        break;
                    default:
                        break;
                }
            }
        });

        rgSelect.check(R.id.rb_one);
    }
}
