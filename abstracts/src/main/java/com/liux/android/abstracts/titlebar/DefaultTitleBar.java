package com.liux.android.abstracts.titlebar;

import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.liux.android.abstracts.R;
import com.liux.android.abstracts.util.TitleBarUtil;

/**
 * 默认的自定义{@link TitleBar}实现 <br>
 * 处理默认的Toolbar,并填充一个自定义TitleBar <br>
 *
 * http://blog.csdn.net/yewei02538/article/details/60979075
 */
public class DefaultTitleBar extends TitleBar<DefaultTitleBar> {
    private View mRoot, mBack, mMore;
    private TextView mTitle, mBackText, mMoreText;
    private ImageView mBackImage, mMoreImage;

    private OnTitleBarListener mOnTitleBarListener;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.view_titlebar_default_back) {
                if (mOnTitleBarListener == null || !mOnTitleBarListener.onBack()) {
                    getActivity().onBackPressed();
                }
            } else if (i == R.id.view_titlebar_default_more) {
                if (mOnTitleBarListener == null || !mOnTitleBarListener.onMore()) {

                }
            }
        }
    };

    public DefaultTitleBar(AppCompatActivity activity) {
        super(activity);
        mRoot = LayoutInflater.from(getActivity()).inflate(
                R.layout.view_titlebar_default,
                null,
                false
        );

        mBack = mRoot.findViewById(R.id.view_titlebar_default_back);
        mMore = mRoot.findViewById(R.id.view_titlebar_default_more);
        mTitle = mRoot.findViewById(R.id.view_titlebar_default_title);
        mBackText = mRoot.findViewById(R.id.view_titlebar_default_back_text);
        mMoreText = mRoot.findViewById(R.id.view_titlebar_default_more_text);
        mBackImage = mRoot.findViewById(R.id.view_titlebar_default_back_image);
        mMoreImage = mRoot.findViewById(R.id.view_titlebar_default_more_image);

        mBack.setOnClickListener(mOnClickListener);
        mMore.setOnClickListener(mOnClickListener);
    }

    @Override
    public void setup(ActionBar actionBar) {
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(
                mRoot,
                new ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );
        TitleBarUtil.operationToolbar(mRoot);
    }

    @Override
    public DefaultTitleBar setTitle(CharSequence title) {
        mTitle.setText(title);
        return this;
    }

    @Override
    public DefaultTitleBar setTitleColor(int color) {
        mTitle.setTextColor(color);
        return this;
    }

    public DefaultTitleBar hasBack(boolean has) {
        mBack.setVisibility(has ? View.VISIBLE : View.GONE);
        return this;
    }

    public DefaultTitleBar hasMore(boolean has) {
        mMore.setVisibility(has ? View.VISIBLE : View.GONE);
        return this;
    }

    public DefaultTitleBar setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(color);
        }
        return this;
    }

    public DefaultTitleBar setTitleBarColor(int color) {
        mRoot.setBackgroundColor(color);
        return this;
    }

    public DefaultTitleBar setOnTitleBarListener(OnTitleBarListener listener) {
        mOnTitleBarListener = listener;
        return this;
    }

    public View getView() {
        return mRoot;
    }

    public View getBack() {
        return mBack;
    }

    public View getMore() {
        return mMore;
    }

    public ImageView getBackIcon() {
        return mBackImage;
    }

    public ImageView getMoreIcon() {
        return mMoreImage;
    }

    public TextView getTitleText() {
        return mTitle;
    }

    public TextView getBackText() {
        return mBackText;
    }

    public TextView getMoreText() {
        return mMoreText;
    }
}
