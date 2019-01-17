package com.wuhenzhizao.sku.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wuhenzhizao.sku.R;
import com.wuhenzhizao.sku.bean.SkuAttribute;
import com.wuhenzhizao.sku.utils.ScreenUtils;
import com.wuhenzhizao.sku.utils.ViewUtils;
import com.wuhenzhizao.sku.widget.FlowLayout;

import java.util.List;

/**
 * Created by wuhenzhizao on 2017/7/31.
 */

public class SkuItemLayout extends LinearLayout {
    /** 这类属性的总称 -- 颜色/尺寸 */
    private TextView mAttributeNameTv;
    /** 存放所有属性的"流式列表" */
    private FlowLayout mAttributeValueLayout;
    /** 属性的点击事件 */
    private OnSkuItemSelectListener mSelectListener;

    public SkuItemLayout(Context context) {
        super(context);
        init(context);
    }

    public SkuItemLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SkuItemLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);

        /* 属性的总称文本 */
        mAttributeNameTv = new TextView(context);
        mAttributeNameTv.setId(ViewUtils.generateViewId());
        mAttributeNameTv.setTextColor(context.getResources().getColor(R.color.comm_text_gray_dark));
        mAttributeNameTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        mAttributeNameTv.setIncludeFontPadding(false);
        LayoutParams attributeNameParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        attributeNameParams.leftMargin = ScreenUtils.dp2PxInt(context, 15);
        attributeNameParams.topMargin = ScreenUtils.dp2PxInt(context, 15);
        mAttributeNameTv.setLayoutParams(attributeNameParams);
        addView(mAttributeNameTv);

        /* 存放所有属性的"流式列表" */
        mAttributeValueLayout = new FlowLayout(context);
        mAttributeValueLayout.setId(ViewUtils.generateViewId());
        mAttributeValueLayout.setMinimumHeight(ScreenUtils.dp2PxInt(context, 25));
        mAttributeValueLayout.setChildSpacing(ScreenUtils.dp2PxInt(context, 15));
        mAttributeValueLayout.setRowSpacing(ScreenUtils.dp2PxInt(context, 15));
        LayoutParams attributeValueParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        attributeValueParams.leftMargin = ScreenUtils.dp2PxInt(context, 15);
        attributeValueParams.rightMargin = ScreenUtils.dp2PxInt(context, 15);
        attributeValueParams.topMargin = ScreenUtils.dp2PxInt(context, 15);
        attributeValueParams.bottomMargin = ScreenUtils.dp2PxInt(context, 10);
        mAttributeValueLayout.setLayoutParams(attributeValueParams);
        addView(mAttributeValueLayout);

        /* 底下的横线 */
        View line = new View(context);
        line.setBackgroundResource(R.color.comm_line);
        LayoutParams lineParams = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
        lineParams.leftMargin = ScreenUtils.dp2PxInt(context, 15);
        lineParams.rightMargin = ScreenUtils.dp2PxInt(context, 15);
        lineParams.topMargin = ScreenUtils.dp2PxInt(context, 5);
        line.setLayoutParams(lineParams);
        addView(line);
    }

    public void setSelectListener(OnSkuItemSelectListener selectListener) {
        this.mSelectListener = selectListener;
    }


    /**
     * 根据传递过来的 "属性列表"， 为每个项生成对应的"控件"再添加到"流式列表"中
     * @param position
     * @param attributeName
     * @param attributeValueList
     */
    public void buildItemLayout(int position, String attributeName, List<String> attributeValueList) {
        mAttributeNameTv.setText(attributeName);
        mAttributeValueLayout.removeAllViewsInLayout();

        for (int i = 0; i < attributeValueList.size(); i++) {
            SkuItemView itemView = new SkuItemView(getContext());
            itemView.setId(ViewUtils.generateViewId());
            itemView.setAttributeValue(attributeValueList.get(i));
            itemView.setOnClickListener(new ItemClickListener(position, itemView));
            itemView.setLayoutParams(new FlowLayout.LayoutParams(
                    FlowLayout.LayoutParams.WRAP_CONTENT,
                    ScreenUtils.dp2PxInt(getContext(), 25)));
            mAttributeValueLayout.addView(itemView);
        }
    }

    /**
     * 清空是否可点击，选中状态
     * 不选中
     * 不可点击
     */
    public void clearItemViewStatus() {
        for (int i = 0; i < mAttributeValueLayout.getChildCount(); i++) {
            SkuItemView itemView = (SkuItemView) mAttributeValueLayout.getChildAt(i);
            itemView.setSelected(false);
            itemView.setEnabled(false);
        }
    }

    /**
     * 设置 -- "指定属性"为"可点击状态"
     *
     * @param attributeValue
     */
    public void optionItemViewEnableStatus(String attributeValue) {
        for (int i = 0; i < mAttributeValueLayout.getChildCount(); i++) {
            SkuItemView itemView = (SkuItemView) mAttributeValueLayout.getChildAt(i);
            if (attributeValue.equals(itemView.getAttributeValue())) {
                itemView.setEnabled(true);
            }
        }
    }

    /**
     * 设置 -- "指定属性"为"选中状态"
     *
     * @param selectValue
     */
    public void optionItemViewSelectStatus(SkuAttribute selectValue) {
        for (int i = 0; i < mAttributeValueLayout.getChildCount(); i++) {
            SkuItemView itemView = (SkuItemView) mAttributeValueLayout.getChildAt(i);
            if (selectValue.getValue().equals(itemView.getAttributeValue())) {
                itemView.setEnabled(true);
                itemView.setSelected(true);
            }
        }
    }

    /**
     * "当前属性集合" -- 是否有"选中项"
     * @return
     */
    public boolean isSelected() {
        for (int i = 0; i < mAttributeValueLayout.getChildCount(); i++) {
            SkuItemView itemView = (SkuItemView) mAttributeValueLayout.getChildAt(i);
            if (itemView.isSelected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取 -- "属性名称"
     * @return
     */
    public String getAttributeName() {
        return mAttributeNameTv.getText().toString();
    }


    /**
     * item 被点击后 -- 组合下数据 通过"接口"传递
     * @param position
     * @param view
     */
    private void onSkuItemClicked(int position, SkuItemView view) {
        boolean selected = !view.isSelected();

        SkuAttribute attribute = new SkuAttribute();
        attribute.setKey(mAttributeNameTv.getText().toString());
        attribute.setValue(view.getAttributeValue());

        mSelectListener.onSelect(position, selected, attribute);
    }



    private class ItemClickListener implements OnClickListener {
        private int position;
        private SkuItemView view;

        /**
         *
         * @param position   流式瀑布（大属性分类id）的下标
         * @param view       点击的属性
         */
        ItemClickListener(int position, SkuItemView view) {
            this.position = position;
            this.view = view;
        }

        @Override
        public void onClick(View v) {
            onSkuItemClicked(position, view);
        }
    }


    /**
     * 传递数据的接口
     */
    interface OnSkuItemSelectListener {
        void onSelect(int position, boolean select, SkuAttribute attribute);
    }
}
