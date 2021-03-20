package org.android.framework.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import org.android.framework.R;

import java.util.Calendar;
import java.util.Date;

/**
 * 多功能日期时间选择器
 * 2020-3-27 by Liux
 */

public class DateTimeDialog extends AppCompatDialog {

    public static Builder with(Context context) {
        return new Builder(context);
    }

    private Builder builder;
    private TextView tvTitle, tvRightNow;
    private ViewGroup flContent;

    private Core core;

    private DateTimeDialog(Builder builder) {
        super(builder.context);
        this.builder = builder;
        getDelegate().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setContentView(R.layout.dialog_date_time);

        tvTitle = findViewById(R.id.tv_titile);
        tvRightNow = findViewById(R.id.tv_right_now);
        flContent = findViewById(R.id.fl_content);

        tvTitle.setText(builder.title);
        tvRightNow.setVisibility(builder.hasRightNow ? View.VISIBLE :View.GONE);

        core = builder.type.factory.create(builder, flContent);
        flContent.addView(core.getView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        tvRightNow.setOnClickListener(v -> {
            builder.listener.onRightNow();
            dismiss();
        });
        findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (builder.listener != null) {
                    int[] datetime = core.getDateTime();
                    if (!builder.listener.onSelectBefore(datetime[0], datetime[1], datetime[2], datetime[3], datetime[4], datetime[5])) return;
                    builder.listener.onSelect(datetime[0], datetime[1], datetime[2], datetime[3], datetime[4], datetime[5]);
                }
                dismiss();
            }
        });
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     * 日期时间选择器建造者
     */
    public static class Builder {

        public static int DEFAULT_MIN_YEAR = 1500;
        public static int DEFAULT_MAX_YEAR = 2500;

        Context context;
        String title;
        boolean hasRightNow;
        TYPE type;
        Date now, min, max;
        OnSelectListener listener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder type(TYPE type) {
            this.type = type;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder hasRightNow(boolean hasRightNow) {
            this.hasRightNow = hasRightNow;
            return this;
        }

        public Builder min(Date min) {
            this.min = min;
            return this;
        }

        public Builder max(Date max) {
            this.max = max;
            return this;
        }

        public Builder now(Date now) {
            this.now = now;
            return this;
        }

        public Builder listener(OnSelectListener listener) {
            this.listener = listener;
            return this;
        }

        public DateTimeDialog build() {
            if (title == null) title = "请选择";
            if (type == null) type = TYPE.yyyyMMddHHmmss;

            if (now == null) now = new Date();
            if (min == null) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, DEFAULT_MIN_YEAR);
                calendar.set(Calendar.MONTH, 0);
                calendar.set(Calendar.DATE, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                min = calendar.getTime();
            }
            if (max == null) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, DEFAULT_MAX_YEAR);
                calendar.set(Calendar.MONTH, 0);
                calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                max = calendar.getTime();
            }

            if (min.after(max)) {
                min = max;
                max = min;
            }

            if (now.before(min)) now = min;
            if (now.after(max)) now = max;

            return new DateTimeDialog(this);
        }
    }

    /**
     * 选择类型
     */
    public enum TYPE {
        yyyyMMddHHmmss((builder, viewGroup) -> {
            return new Core(builder, viewGroup, R.layout.dialog_date_time_1) {
                NumberPicker year, month, day, hour, minute, second;
                @Override
                void initView(View view) {
                    year = view.findViewById(R.id.wv_year);
                    month = view.findViewById(R.id.wv_month);
                    day = view.findViewById(R.id.wv_day);
                    hour = view.findViewById(R.id.wv_hour);
                    minute = view.findViewById(R.id.wv_minute);
                    second = view.findViewById(R.id.wv_second);

                    configNumberPicker(year, "年", false);
                    configNumberPicker(month, "月", false);
                    configNumberPicker(day, "日", false);
                    configNumberPicker(hour, "时", true);
                    configNumberPicker(minute, "分", true);
                    configNumberPicker(second, "秒", true);

                    refreshYear();
                    year.setValue(preNowYear);
                    refreshMonth();
                    month.setValue(preNowMonth);
                    refreshDay();
                    day.setValue(preNowDay);
                    refreshHour();
                    hour.setValue(preNowHour);
                    refreshMinute();
                    minute.setValue(preNowMinute);
                    refreshSecond();
                    second.setValue(preNowSecond);

                    year.setOnValueChangedListener((picker, oldVal, newVal) -> refreshMonth());
                    month.setOnValueChangedListener((picker, oldVal, newVal) -> refreshDay());
                    day.setOnValueChangedListener((picker, oldVal, newVal) -> refreshHour());
                    hour.setOnValueChangedListener((picker, oldVal, newVal) -> refreshMinute());
                    minute.setOnValueChangedListener((picker, oldVal, newVal) -> refreshSecond());
                    second.setOnValueChangedListener((picker, oldVal, newVal) -> {});
                }

                @Override
                int[] getDateTime() {
                    return new int[] {
                            year.getValue(),
                            month.getValue(),
                            day.getValue(),
                            hour.getValue(),
                            minute.getValue(),
                            second.getValue()
                    };
                }

                private void refreshYear() {
                    int minYear = preMinYear;
                    int maxYear = preMaxYear;

                    year.setMinValue(minYear);
                    year.setMaxValue(maxYear);

                    refreshMonth();
                }

                private void refreshMonth() {
                    int minMonth = 1;
                    int maxMonth = 12;

                    int yearValue = year.getValue();
                    if (yearValue == preMinYear) minMonth = preMinMonth;
                    if (yearValue == preMaxYear) maxMonth = preMaxMonth;

                    month.setMinValue(minMonth);
                    month.setMaxValue(maxMonth);

                    refreshDay();
                }

                private void refreshDay() {
                    int minDay = 1;
                    int maxDay = 31;

                    int yearValue = year.getValue();
                    int monthValue = month.getValue();
                    if (yearValue == preMinYear &&
                            monthValue == preMinMonth) minDay = preMinDay;
                    if (yearValue == preMaxYear &&
                            monthValue == preMaxMonth) maxDay = preMaxDay;

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(yearValue, monthValue - 1, 1);
                    int normalMaxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    maxDay = Math.min(maxDay, normalMaxDay);

                    day.setMinValue(minDay);
                    day.setMaxValue(maxDay);

                    refreshHour();
                }

                private void refreshHour() {
                    int minHour = 0;
                    int maxHour = 23;

                    int yearValue = year.getValue();
                    int monthValue = month.getValue();
                    int dayValue = day.getValue();
                    if (yearValue == preMinYear &&
                            monthValue == preMinMonth &&
                            dayValue == preMinDay) minHour = preMinHour;
                    if (yearValue == preMaxYear &&
                            monthValue == preMaxMonth &&
                            dayValue == preMaxDay) maxHour = preMaxHour;

                    hour.setMinValue(minHour);
                    hour.setMaxValue(maxHour);

                    refreshMinute();
                }

                private void refreshMinute() {
                    int minMinute = 0;
                    int maxMinute = 59;

                    int yearValue = year.getValue();
                    int monthValue = month.getValue();
                    int dayValue = day.getValue();
                    int hourValue = hour.getValue();
                    if (yearValue == preMinYear &&
                            monthValue == preMinMonth &&
                            dayValue == preMinDay &&
                            hourValue == preMinHour) minMinute = preMinMinute;
                    if (yearValue == preMaxYear &&
                            monthValue == preMaxMonth &&
                            dayValue == preMaxDay &&
                            hourValue == preMaxHour) maxMinute = preMaxMinute;

                    minute.setMinValue(minMinute);
                    minute.setMaxValue(maxMinute);

                    refreshSecond();
                }

                private void refreshSecond() {
                    int minSecond = 0;
                    int maxSecond = 59;

                    int yearValue = year.getValue();
                    int monthValue = month.getValue();
                    int dayValue = day.getValue();
                    int hourValue = hour.getValue();
                    int minuteValue = minute.getValue();
                    if (yearValue == preMinYear &&
                            monthValue == preMinMonth &&
                            dayValue == preMinDay &&
                            hourValue == preMinHour &&
                            minuteValue == preMinMinute) minSecond = preMinSecond;
                    if (yearValue == preMaxYear &&
                            monthValue == preMaxMonth &&
                            dayValue == preMaxDay &&
                            hourValue == preMaxHour &&
                            minuteValue == preMaxMinute) maxSecond = preMaxSecond;

                    second.setMinValue(minSecond);
                    second.setMaxValue(maxSecond);
                }
            };
        }),
        yyyyMMddHHmm((builder, viewGroup) -> {
            return new Core(builder, viewGroup, R.layout.dialog_date_time_1) {
                NumberPicker year, month, day, hour, minute;
                @Override
                void initView(View view) {
                    year = view.findViewById(R.id.wv_year);
                    month = view.findViewById(R.id.wv_month);
                    day = view.findViewById(R.id.wv_day);
                    hour = view.findViewById(R.id.wv_hour);
                    minute = view.findViewById(R.id.wv_minute);
                    view.findViewById(R.id.wv_second).setVisibility(View.GONE);


                    configNumberPicker(year, "年", false);
                    configNumberPicker(month, "月", false);
                    configNumberPicker(day, "日", false);
                    configNumberPicker(hour, "时", true);
                    configNumberPicker(minute, "分", true);

                    refreshYear();
                    year.setValue(preNowYear);
                    refreshMonth();
                    month.setValue(preNowMonth);
                    refreshDay();
                    day.setValue(preNowDay);
                    refreshHour();
                    hour.setValue(preNowHour);
                    refreshMinute();
                    minute.setValue(preNowMinute);

                    year.setOnValueChangedListener((picker, oldVal, newVal) -> refreshMonth());
                    month.setOnValueChangedListener((picker, oldVal, newVal) -> refreshDay());
                    day.setOnValueChangedListener((picker, oldVal, newVal) -> refreshHour());
                    hour.setOnValueChangedListener((picker, oldVal, newVal) -> refreshMinute());
                    minute.setOnValueChangedListener((picker, oldVal, newVal) -> {});
                }

                @Override
                int[] getDateTime() {
                    return new int[] {
                            year.getValue(),
                            month.getValue(),
                            day.getValue(),
                            hour.getValue(),
                            minute.getValue(),
                            0
                    };
                }

                private void refreshYear() {
                    int minYear = preMinYear;
                    int maxYear = preMaxYear;

                    year.setMinValue(minYear);
                    year.setMaxValue(maxYear);

                    refreshMonth();
                }

                private void refreshMonth() {
                    int minMonth = 1;
                    int maxMonth = 12;

                    int yearValue = year.getValue();
                    if (yearValue == preMinYear) minMonth = preMinMonth;
                    if (yearValue == preMaxYear) maxMonth = preMaxMonth;

                    month.setMinValue(minMonth);
                    month.setMaxValue(maxMonth);

                    refreshDay();
                }

                private void refreshDay() {
                    int minDay = 1;
                    int maxDay = 31;

                    int yearValue = year.getValue();
                    int monthValue = month.getValue();
                    if (yearValue == preMinYear &&
                            monthValue == preMinMonth) minDay = preMinDay;
                    if (yearValue == preMaxYear &&
                            monthValue == preMaxMonth) maxDay = preMaxDay;

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(yearValue, monthValue - 1, 1);
                    int normalMaxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    maxDay = Math.min(maxDay, normalMaxDay);

                    day.setMinValue(minDay);
                    day.setMaxValue(maxDay);

                    refreshHour();
                }

                private void refreshHour() {
                    int minHour = 0;
                    int maxHour = 23;

                    int yearValue = year.getValue();
                    int monthValue = month.getValue();
                    int dayValue = day.getValue();
                    if (yearValue == preMinYear &&
                            monthValue == preMinMonth &&
                            dayValue == preMinDay) minHour = preMinHour;
                    if (yearValue == preMaxYear &&
                            monthValue == preMaxMonth &&
                            dayValue == preMaxDay) maxHour = preMaxHour;

                    hour.setMinValue(minHour);
                    hour.setMaxValue(maxHour);

                    refreshMinute();
                }

                private void refreshMinute() {
                    int minMinute = 0;
                    int maxMinute = 59;

                    int yearValue = year.getValue();
                    int monthValue = month.getValue();
                    int dayValue = day.getValue();
                    int hourValue = hour.getValue();
                    if (yearValue == preMinYear &&
                            monthValue == preMinMonth &&
                            dayValue == preMinDay &&
                            hourValue == preMinHour) minMinute = preMinMinute;
                    if (yearValue == preMaxYear &&
                            monthValue == preMaxMonth &&
                            dayValue == preMaxDay &&
                            hourValue == preMaxHour) maxMinute = preMaxMinute;

                    minute.setMinValue(minMinute);
                    minute.setMaxValue(maxMinute);
                }
            };
        }),
        yyyyMMddHH((builder, viewGroup) -> {
            return new Core(builder, viewGroup, R.layout.dialog_date_time_1) {
                NumberPicker year, month, day, hour;
                @Override
                void initView(View view) {
                    year = view.findViewById(R.id.wv_year);
                    month = view.findViewById(R.id.wv_month);
                    day = view.findViewById(R.id.wv_day);
                    hour = view.findViewById(R.id.wv_hour);
                    view.findViewById(R.id.wv_minute).setVisibility(View.GONE);
                    view.findViewById(R.id.wv_second).setVisibility(View.GONE);

                    configNumberPicker(year, "年", false);
                    configNumberPicker(month, "月", false);
                    configNumberPicker(day, "日", false);
                    configNumberPicker(hour, "时", true);

                    refreshYear();
                    year.setValue(preNowYear);
                    refreshMonth();
                    month.setValue(preNowMonth);
                    refreshDay();
                    day.setValue(preNowDay);
                    refreshHour();
                    hour.setValue(preNowHour);

                    year.setOnValueChangedListener((picker, oldVal, newVal) -> refreshMonth());
                    month.setOnValueChangedListener((picker, oldVal, newVal) -> refreshDay());
                    day.setOnValueChangedListener((picker, oldVal, newVal) -> refreshHour());
                    hour.setOnValueChangedListener((picker, oldVal, newVal) -> {});
                }

                @Override
                int[] getDateTime() {
                    return new int[] {
                            year.getValue(),
                            month.getValue(),
                            day.getValue(),
                            hour.getValue(),
                            0,
                            0
                    };
                }

                private void refreshYear() {
                    int minYear = preMinYear;
                    int maxYear = preMaxYear;

                    year.setMinValue(minYear);
                    year.setMaxValue(maxYear);

                    refreshMonth();
                }

                private void refreshMonth() {
                    int minMonth = 1;
                    int maxMonth = 12;

                    int yearValue = year.getValue();
                    if (yearValue == preMinYear) minMonth = preMinMonth;
                    if (yearValue == preMaxYear) maxMonth = preMaxMonth;

                    month.setMinValue(minMonth);
                    month.setMaxValue(maxMonth);

                    refreshDay();
                }

                private void refreshDay() {
                    int minDay = 1;
                    int maxDay = 31;

                    int yearValue = year.getValue();
                    int monthValue = month.getValue();
                    if (yearValue == preMinYear &&
                            monthValue == preMinMonth) minDay = preMinDay;
                    if (yearValue == preMaxYear &&
                            monthValue == preMaxMonth) maxDay = preMaxDay;

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(yearValue, monthValue - 1, 1);
                    int normalMaxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    maxDay = Math.min(maxDay, normalMaxDay);

                    day.setMinValue(minDay);
                    day.setMaxValue(maxDay);

                    refreshHour();
                }

                private void refreshHour() {
                    int minHour = 0;
                    int maxHour = 23;

                    int yearValue = year.getValue();
                    int monthValue = month.getValue();
                    int dayValue = day.getValue();
                    if (yearValue == preMinYear &&
                            monthValue == preMinMonth &&
                            dayValue == preMinDay) minHour = preMinHour;
                    if (yearValue == preMaxYear &&
                            monthValue == preMaxMonth &&
                            dayValue == preMaxDay) maxHour = preMaxHour;

                    hour.setMinValue(minHour);
                    hour.setMaxValue(maxHour);
                }
            };
        }),
        yyyyMMdd((builder, viewGroup) -> {
            return new Core(builder, viewGroup, R.layout.dialog_date_time_2) {
                NumberPicker year, month, day;
                @Override
                void initView(View view) {
                    year = view.findViewById(R.id.wv_year);
                    month = view.findViewById(R.id.wv_month);
                    day = view.findViewById(R.id.wv_day);

                    configNumberPicker(year, "年", false);
                    configNumberPicker(month, "月", false);
                    configNumberPicker(day, "日", false);

                    refreshYear();
                    year.setValue(preNowYear);
                    refreshMonth();
                    month.setValue(preNowMonth);
                    refreshDay();
                    day.setValue(preNowDay);

                    year.setOnValueChangedListener((picker, oldVal, newVal) -> refreshMonth());
                    month.setOnValueChangedListener((picker, oldVal, newVal) -> refreshDay());
                    day.setOnValueChangedListener((picker, oldVal, newVal) -> {});
                }

                @Override
                int[] getDateTime() {
                    return new int[] {
                            year.getValue(),
                            month.getValue(),
                            day.getValue(),
                            0,
                            0,
                            0
                    };
                }

                private void refreshYear() {
                    int minYear = preMinYear;
                    int maxYear = preMaxYear;

                    year.setMinValue(minYear);
                    year.setMaxValue(maxYear);

                    refreshMonth();
                }

                private void refreshMonth() {
                    int minMonth = 1;
                    int maxMonth = 12;

                    int yearValue = year.getValue();
                    if (yearValue == preMinYear) minMonth = preMinMonth;
                    if (yearValue == preMaxYear) maxMonth = preMaxMonth;

                    month.setMinValue(minMonth);
                    month.setMaxValue(maxMonth);

                    refreshDay();
                }

                private void refreshDay() {
                    int minDay = 1;
                    int maxDay = 31;

                    int yearValue = year.getValue();
                    int monthValue = month.getValue();
                    if (yearValue == preMinYear &&
                            monthValue == preMinMonth) minDay = preMinDay;
                    if (yearValue == preMaxYear &&
                            monthValue == preMaxMonth) maxDay = preMaxDay;

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(yearValue, monthValue - 1, 1);
                    int normalMaxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    maxDay = Math.min(maxDay, normalMaxDay);

                    day.setMinValue(minDay);
                    day.setMaxValue(maxDay);
                }
            };
        }),
        yyyyMM((builder, viewGroup) -> {
            return new Core(builder, viewGroup, R.layout.dialog_date_time_2) {
                NumberPicker year, month;
                @Override
                void initView(View view) {
                    year = view.findViewById(R.id.wv_year);
                    month = view.findViewById(R.id.wv_month);
                    view.findViewById(R.id.wv_day).setVisibility(View.GONE);

                    configNumberPicker(year, "年", false);
                    configNumberPicker(month, "月", false);

                    refreshYear();
                    year.setValue(preNowYear);
                    refreshMonth();
                    month.setValue(preNowMonth);

                    year.setOnValueChangedListener((picker, oldVal, newVal) -> refreshMonth());
                    month.setOnValueChangedListener((picker, oldVal, newVal) -> {});
                }

                @Override
                int[] getDateTime() {
                    return new int[] {
                            year.getValue(),
                            month.getValue(),
                            0,
                            0,
                            0,
                            0
                    };
                }

                private void refreshYear() {
                    int minYear = preMinYear;
                    int maxYear = preMaxYear;

                    year.setMinValue(minYear);
                    year.setMaxValue(maxYear);

                    refreshMonth();
                }

                private void refreshMonth() {
                    int minMonth = 1;
                    int maxMonth = 12;

                    int yearValue = year.getValue();
                    if (yearValue == preMinYear) minMonth = preMinMonth;
                    if (yearValue == preMaxYear) maxMonth = preMaxMonth;

                    month.setMinValue(minMonth);
                    month.setMaxValue(maxMonth);
                }
            };
        }),

        MMdd((builder, viewGroup) -> {
            return new Core(builder, viewGroup, R.layout.dialog_date_time_2) {
                NumberPicker month, day;
                @Override
                void initView(View view) {
                    view.findViewById(R.id.wv_year).setVisibility(View.GONE);
                    month = view.findViewById(R.id.wv_month);
                    day = view.findViewById(R.id.wv_day);

                    configNumberPicker(month, "月", false);
                    configNumberPicker(day, "日", false);

                    refreshMonth();
                    month.setValue(preNowMonth);
                    refreshDay();
                    day.setValue(preNowDay);
                }

                @Override
                int[] getDateTime() {
                    return new int[] {
                            0,
                            month.getValue(),
                            day.getValue(),
                            0,
                            0,
                            0
                    };
                }

                private void refreshMonth() {
                    int minMonth = 1;
                    int maxMonth = 12;

                    month.setMinValue(minMonth);
                    month.setMaxValue(maxMonth);
                }

                private void refreshDay() {
                    int minDay = 1;
                    int maxDay = 31;

                    day.setMinValue(minDay);
                    day.setMaxValue(maxDay);
                }
            };
        }),

        HHmmss((builder, viewGroup) -> {
            return new Core(builder, viewGroup, R.layout.dialog_date_time_3) {
                NumberPicker hour, minute, second;
                @Override
                void initView(View view) {
                    hour = view.findViewById(R.id.wv_hour);
                    minute = view.findViewById(R.id.wv_minute);
                    second = view.findViewById(R.id.wv_second);

                    configNumberPicker(hour, "时", true);
                    configNumberPicker(minute, "分", true);
                    configNumberPicker(second, "秒", true);

                    refreshHour();
                    hour.setValue(preNowHour);
                    refreshMinute();
                    minute.setValue(preNowMinute);
                    refreshSecond();
                    second.setValue(preNowSecond);
                }

                @Override
                int[] getDateTime() {
                    return new int[] {
                            0,
                            0,
                            0,
                            hour.getValue(),
                            minute.getValue(),
                            second.getValue()
                    };
                }

                private void refreshHour() {
                    int minHour = 0;
                    int maxHour = 23;

                    hour.setMinValue(minHour);
                    hour.setMaxValue(maxHour);
                }

                private void refreshMinute() {
                    int minMinute = 0;
                    int maxMinute = 59;

                    minute.setMinValue(minMinute);
                    minute.setMaxValue(maxMinute);
                }

                private void refreshSecond() {
                    int minSecond = 0;
                    int maxSecond = 59;

                    second.setMinValue(minSecond);
                    second.setMaxValue(maxSecond);
                }
            };
        }),
        HHmm((builder, viewGroup) -> {
            return new Core(builder, viewGroup, R.layout.dialog_date_time_3) {
                NumberPicker hour, minute;
                @Override
                void initView(View view) {
                    hour = view.findViewById(R.id.wv_hour);
                    minute = view.findViewById(R.id.wv_minute);
                    view.findViewById(R.id.wv_second).setVisibility(View.GONE);

                    configNumberPicker(hour, "时", true);
                    configNumberPicker(minute, "分", true);

                    refreshHour();
                    hour.setValue(preNowHour);
                    refreshMinute();
                    minute.setValue(preNowMinute);
                }

                @Override
                int[] getDateTime() {
                    return new int[] {
                            0,
                            0,
                            0,
                            hour.getValue(),
                            minute.getValue(),
                            0
                    };
                }

                private void refreshHour() {
                    int minHour = 0;
                    int maxHour = 23;

                    hour.setMinValue(minHour);
                    hour.setMaxValue(maxHour);
                }

                private void refreshMinute() {
                    int minMinute = 0;
                    int maxMinute = 59;

                    minute.setMinValue(minMinute);
                    minute.setMaxValue(maxMinute);
                }
            };
        }),

        mmss((builder, viewGroup) -> {
            return new Core(builder, viewGroup, 0) {
                NumberPicker minute, second;
                @Override
                void initView(View view) {
                    view.findViewById(R.id.wv_hour).setVisibility(View.GONE);
                    minute = view.findViewById(R.id.wv_minute);
                    second = view.findViewById(R.id.wv_second);

                    configNumberPicker(minute, "分", true);
                    configNumberPicker(second, "秒", true);

                    refreshMinute();
                    minute.setValue(preNowMinute);
                    refreshSecond();
                    second.setValue(preNowSecond);
                }

                @Override
                int[] getDateTime() {
                    return new int[] {
                            0,
                            0,
                            0,
                            0,
                            minute.getValue(),
                            second.getValue()
                    };
                }

                private void refreshMinute() {
                    int minMinute = 0;
                    int maxMinute = 59;

                    minute.setMinValue(minMinute);
                    minute.setMaxValue(maxMinute);
                }

                private void refreshSecond() {
                    int minSecond = 0;
                    int maxSecond = 59;

                    second.setMinValue(minSecond);
                    second.setMaxValue(maxSecond);
                }
            };
        }),

        yyyy((builder, viewGroup) -> {
            return new Core(builder, viewGroup, R.layout.dialog_date_time_4) {
                NumberPicker year;
                @Override
                void initView(View view) {
                    year = view.findViewById(R.id.wv_value);

                    configNumberPicker(year, "年", false);

                    refreshYear();
                    year.setValue(preNowYear);
                }

                @Override
                int[] getDateTime() {
                    return new int[] {
                            year.getValue(),
                            0,
                            0,
                            0,
                            0,
                            0
                    };
                }

                private void refreshYear() {
                    int minYear = preMinYear;
                    int maxYear = preMaxYear;

                    year.setMinValue(minYear);
                    year.setMaxValue(maxYear);
                }
            };
        }),
        MM((builder, viewGroup) -> {
            return new Core(builder, viewGroup, R.layout.dialog_date_time_4) {
                NumberPicker month;
                @Override
                void initView(View view) {
                    month = view.findViewById(R.id.wv_value);

                    configNumberPicker(month, "月", false);

                    refreshMonth();
                    month.setValue(preNowMonth);
                }

                @Override
                int[] getDateTime() {
                    return new int[] {
                            0,
                            month.getValue(),
                            0,
                            0,
                            0,
                            0
                    };
                }

                private void refreshMonth() {
                    int minMonth = 1;
                    int maxMonth = 12;

                    month.setMinValue(minMonth);
                    month.setMaxValue(maxMonth);
                }
            };
        }),
        dd((builder, viewGroup) -> {
            return new Core(builder, viewGroup, R.layout.dialog_date_time_4) {
                NumberPicker day;
                @Override
                void initView(View view) {
                    day = view.findViewById(R.id.wv_value);

                    configNumberPicker(day, "日", false);

                    refreshDay();
                    day.setValue(preNowDay);
                }

                @Override
                int[] getDateTime() {
                    return new int[] {
                            0,
                            0,
                            day.getValue(),
                            0,
                            0,
                            0
                    };
                }

                private void refreshDay() {
                    int minDay = 1;
                    int maxDay = 31;

                    day.setMinValue(minDay);
                    day.setMaxValue(maxDay);
                }
            };
        }),
        HH((builder, viewGroup) -> {
            return new Core(builder, viewGroup, R.layout.dialog_date_time_4) {
                NumberPicker hour;
                @Override
                void initView(View view) {
                    hour = view.findViewById(R.id.wv_value);

                    configNumberPicker(hour, "时", true);

                    refreshHour();
                    hour.setValue(preNowHour);
                }

                @Override
                int[] getDateTime() {
                    return new int[] {
                            0,
                            0,
                            0,
                            hour.getValue(),
                            0,
                            0
                    };
                }

                private void refreshHour() {
                    int minHour = 0;
                    int maxHour = 23;

                    hour.setMinValue(minHour);
                    hour.setMaxValue(maxHour);
                }
            };
        }),
        mm((builder, viewGroup) -> {
            return new Core(builder, viewGroup, R.layout.dialog_date_time_4) {
                NumberPicker minute;
                @Override
                void initView(View view) {
                    minute = view.findViewById(R.id.wv_value);

                    configNumberPicker(minute, "分", true);

                    refreshMinute();
                    minute.setValue(preNowMinute);
                }

                @Override
                int[] getDateTime() {
                    return new int[] {
                            0,
                            0,
                            0,
                            0,
                            minute.getValue(),
                            0
                    };
                }

                private void refreshMinute() {
                    int minMinute = 0;
                    int maxMinute = 59;

                    minute.setMinValue(minMinute);
                    minute.setMaxValue(maxMinute);
                }
            };
        }),
        ss((builder, viewGroup) -> {
            return new Core(builder, viewGroup, R.layout.dialog_date_time_4) {
                NumberPicker second;
                @Override
                void initView(View view) {
                    second = view.findViewById(R.id.wv_value);

                    configNumberPicker(second, "秒", true);

                    refreshSecond();
                    second.setValue(preNowSecond);
                }

                @Override
                int[] getDateTime() {
                    return new int[] {
                            0,
                            0,
                            0,
                            0,
                            0,
                            second.getValue()
                    };
                }

                private void refreshSecond() {
                    int minSecond = 0;
                    int maxSecond = 59;

                    second.setMinValue(minSecond);
                    second.setMaxValue(maxSecond);
                }
            };
        }),
        ;

        Core.Factory factory;
        TYPE(Core.Factory factory) {
            this.factory = factory;
        }

        /**
         * 设置 NumberPicker 属性
         * @param numberPicker
         * @param suffix
         * @param addZero
         */
        private static void configNumberPicker(NumberPicker numberPicker, String suffix, boolean addZero) {
            numberPicker.setWrapSelectorWheel(false);
            numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            numberPicker.setFormatter(value -> {
                if (value < 10 && addZero) {
                    return "0" + value + suffix;
                } else {
                    return value + suffix;
                }
            });
            // 处理 NumberPicker 设置 Formatter 第一次不生效问题
            if (numberPicker.getChildCount() > 0) {
                View editView = numberPicker.getChildAt(0);
                if (editView instanceof EditText) ((EditText) editView).setFilters(new InputFilter[0]);
            }
        }
    }

    /**
     * 选择监听器
     */
    public interface OnSelectListener {

        /**
         * 选择前回调
         * @param year
         * @param month
         * @param day
         * @param hour
         * @param minute
         * @param second
         * @return
         */
        boolean onSelectBefore(int year, int month, int day, int hour, int minute, int second);

        /**
         * 选择的日期时间
         * @param year
         * @param month
         * @param day
         * @param hour
         * @param minute
         * @param second
         */
        void onSelect(int year, int month, int day, int hour, int minute, int second);

        /**
         * 至今被选择
         */
        void onRightNow();
    }

    /**
     * 日期时间选择监听
     */
    public static abstract class OnDateSelectListener implements OnSelectListener {

        @Override
        public void onSelect(int year, int month, int day, int hour, int minute, int second) {
            onDateSelect(transform(year, month, day, hour, minute, second));
        }

        @Override
        public boolean onSelectBefore(int year, int month, int day, int hour, int minute, int second) {
            return onDateSelectBefore(transform(year, month, day, hour, minute, second));
        }

        @Override
        public void onRightNow() {

        }

        public abstract void onDateSelect(Date date);

        public boolean onDateSelectBefore(Date date) {
            return true;
        }

        private Date transform(int year, int month, int day, int hour, int minute, int second) {
            if (month == 0) month = 1;
            if (day == 0) day = 1;
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month - 1, day, hour, minute, second);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTime();
        }
    }

    /**
     * 日期时间选择核心抽象
     */
    private static abstract class Core {

        /**
         * 核心工厂
         */
        interface Factory {

            /**
             * 创建核心
             * @param builder
             * @param viewGroup
             * @return
             */
            Core create(Builder builder, ViewGroup viewGroup);
        }

        Builder builder;
        View view;
        int preNowYear, preNowMonth, preNowDay, preNowHour, preNowMinute, preNowSecond;
        int preMinYear, preMinMonth, preMinDay, preMinHour, preMinMinute, preMinSecond;
        int preMaxYear, preMaxMonth, preMaxDay, preMaxHour, preMaxMinute, preMaxSecond;

        Core(Builder builder, ViewGroup viewGroup, int layoutRes) {
            Calendar calendar = Calendar.getInstance();

            this.builder = builder;
            view = LayoutInflater.from(builder.context).inflate(layoutRes, viewGroup, false);

            calendar.setTime(builder.now);
            preNowYear = calendar.get(Calendar.YEAR);
            preNowMonth = calendar.get(Calendar.MONTH) + 1;
            preNowDay = calendar.get(Calendar.DATE);
            preNowHour = calendar.get(Calendar.HOUR_OF_DAY);
            preNowMinute = calendar.get(Calendar.MINUTE);
            preNowSecond = calendar.get(Calendar.SECOND);

            calendar.setTime(builder.min);
            preMinYear = calendar.get(Calendar.YEAR);
            preMinMonth = calendar.get(Calendar.MONTH) + 1;
            preMinDay = calendar.get(Calendar.DATE);
            preMinHour = calendar.get(Calendar.HOUR_OF_DAY);
            preMinMinute = calendar.get(Calendar.MINUTE);
            preMinSecond = calendar.get(Calendar.SECOND);

            calendar.setTime(builder.max);
            preMaxYear = calendar.get(Calendar.YEAR);
            preMaxMonth = calendar.get(Calendar.MONTH) + 1;
            preMaxDay = calendar.get(Calendar.DATE);
            preMaxHour = calendar.get(Calendar.HOUR_OF_DAY);
            preMaxMinute = calendar.get(Calendar.MINUTE);
            preMaxSecond = calendar.get(Calendar.SECOND);

            initView(view);
        }

        View getView() {
            return view;
        }

        abstract void initView(View view);

        abstract int[] getDateTime();
    }
}
