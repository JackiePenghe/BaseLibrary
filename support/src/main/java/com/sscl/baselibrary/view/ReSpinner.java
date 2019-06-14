package com.sscl.baselibrary.view;

import android.content.Context;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.widget.AdapterView;


/**
 * 可重复选择同一选项并触发监听的Spinner
 *
 * @author jackie
 */
public class ReSpinner extends AppCompatSpinner {

    /*---------------------------------------成员变量---------------------------------------*/

    /**
     * 标志下拉列表是否正在显示
     */
    public boolean isDropDownMenuShown = false;

    /*---------------------------------------构造方法---------------------------------------*/

    public ReSpinner(Context context) {
        super(context);
    }

    public ReSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /*---------------------------------------重写父类方法---------------------------------------*/

    @Override
    public void setSelection(int position, boolean animate) {
        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position, animate);
        if (sameSelected) {
            OnItemSelectedListener onItemSelectedListener = getOnItemSelectedListener();
            if (onItemSelectedListener != null) {
                // 如果选择项是Spinner当前已选择的项,则 OnItemSelectedListener并不会触发,因此这里手动触发回调
                onItemSelectedListener.onItemSelected(this, getSelectedView(), position, getSelectedItemId());
            }
        }
    }

    @Override
    public boolean performClick() {
        this.isDropDownMenuShown = true;
        return super.performClick();
    }

    @Override
    public void
    setSelection(int position) {
        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position);
        if (sameSelected) {
            OnItemSelectedListener onItemSelectedListener = getOnItemSelectedListener();
            if (onItemSelectedListener != null) {
                onItemSelectedListener.onItemSelected(this, getSelectedView(), position, getSelectedItemId());
            }
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /*---------------------------------------公开方法---------------------------------------*/

    public boolean isDropDownMenuShown() {
        return isDropDownMenuShown;
    }

    public void setDropDownMenuShown(boolean isDropDownMenuShown) {
        this.isDropDownMenuShown = isDropDownMenuShown;
    }
}
