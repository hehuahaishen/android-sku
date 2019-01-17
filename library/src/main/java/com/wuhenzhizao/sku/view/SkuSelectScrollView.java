package com.wuhenzhizao.sku.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.wuhenzhizao.sku.bean.Sku;
import com.wuhenzhizao.sku.bean.SkuAttribute;
import com.wuhenzhizao.sku.utils.ViewUtils;
import com.wuhenzhizao.sku.widget.SkuMaxHeightScrollView;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by wuhenzhizao on 2017/7/31.
 */
public class SkuSelectScrollView extends SkuMaxHeightScrollView implements SkuItemLayout.OnSkuItemSelectListener {

    /** 所有属性的布局 -- 包含多个SkuItemLayout  */
    private LinearLayout mSkuContainerLayout;
    private List<Sku> mSkuList;

    /** 存放 -- 当前 -- 选中属性的信息 */
    private List<SkuAttribute> mSelectedAttributeList;
    /** sku选中状态 -- 回调接口 */
    private OnSkuListener mListener;

    public SkuSelectScrollView(Context context) {
        super(context);
        init(context, null);
    }

    public SkuSelectScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setFillViewport(true);                          // 拉伸填满
        setOverScrollMode(OVER_SCROLL_NEVER);

        // 添加一个 "纵向的LinearLayout" -- 所有属性的布局（包含多个SkuItemLayout）
        mSkuContainerLayout = new LinearLayout(context, attrs);
        mSkuContainerLayout.setId(ViewUtils.generateViewId());
        mSkuContainerLayout.setOrientation(LinearLayout.VERTICAL);
        mSkuContainerLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addView(mSkuContainerLayout);
    }

    /**
     * 设置SkuView委托，MVVM + Databinding模式下使用
     *
     * @param delegate
     */
    public void setSkuViewDelegate(SkuViewDelegate delegate) {
        this.mListener = delegate.getListener();
    }


    /**
     * 设置监听接口
     *
     * @param listener {@link OnSkuListener}
     */
    public void setListener(OnSkuListener listener) {
        this.mListener = listener;
    }


    /**
     * 绑定sku数据
     *
     * @param skuList
     */
    public void setSkuList(List<Sku> skuList) {
        this.mSkuList = skuList;
        // 清空sku视图
        mSkuContainerLayout.removeAllViews();

        // 获取分组的sku集合
        Map<String, List<String>> dataMap = getSkuGroupByName(skuList);


        mSelectedAttributeList = new LinkedList<>();
        int index = 0;
        for (Iterator<Map.Entry<String, List<String>>> it = dataMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, List<String>> entry = it.next();

            // 构建sku视图 -- 每一个大属性
            SkuItemLayout itemLayout = new SkuItemLayout(getContext());
            itemLayout.setId(ViewUtils.generateViewId());
            itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            itemLayout.buildItemLayout(index++, entry.getKey(), entry.getValue());
            itemLayout.setSelectListener(this);
            mSkuContainerLayout.addView(itemLayout);

            // 初始状态下，所有属性信息设置为空
            mSelectedAttributeList.add(new SkuAttribute(entry.getKey(), ""));
        }


        // 一个sku时，默认选中
        if (skuList.size() == 1) {
            mSelectedAttributeList.clear();
            for (SkuAttribute attribute : this.mSkuList.get(0).getAttributes()) {
                mSelectedAttributeList.add(new SkuAttribute(attribute.getKey(), attribute.getValue()));
            }
        }

        // 清除所有选中状态
        clearAllLayoutStatus();
        // 设置是否可点击
        optionLayoutEnableStatus();
        // 设置选中状态
        optionLayoutSelectStatus();
    }


    /**
     * 将sku根据属性名进行分组
     *
     * @param list
     * @return 如{ "颜色": {"白色", "红色", "黑色"}, "尺寸": {"M", "L", "XL", "XXL"}}
     */
    private Map<String, List<String>> getSkuGroupByName(List<Sku> list) {
        Map<String, List<String>> dataMap = new LinkedHashMap<>();
        for (Sku sku : list) {
            for (SkuAttribute attribute : sku.getAttributes()) {
                String attributeName = attribute.getKey();
                String attributeValue = attribute.getValue();

                if (!dataMap.containsKey(attributeName)) {
                    dataMap.put(attributeName, new LinkedList<String>());
                }

                List<String> valueList = dataMap.get(attributeName);
                if (!valueList.contains(attributeValue)) {
                    dataMap.get(attributeName).add(attributeValue);
                }
            }
        }
        return dataMap;
    }


    /**
     * 重置 "所有属性" 的选中状态
     */
    private void clearAllLayoutStatus() {
        for (int i = 0; i < mSkuContainerLayout.getChildCount(); i++) {  // 一个个大属性循环
            SkuItemLayout itemLayout = (SkuItemLayout) mSkuContainerLayout.getChildAt(i);
            itemLayout.clearItemViewStatus();
        }
    }

    /**
     * 设置所有属性的Enable状态，即是否可点击
     */
    private void optionLayoutEnableStatus() {
        int childCount = mSkuContainerLayout.getChildCount();

        if (childCount <= 1) {                          /* 只有一大类属性 */
            optionLayoutEnableStatusSingleProperty();
        } else {                                        /* 有多种大属性 */
            optionLayoutEnableStatusMultipleProperties();
        }
    }

    /**
     * 只有一大类属性 -- 设置所有属性的Enable状态，即是否可点击
     */
    private void optionLayoutEnableStatusSingleProperty() {
        SkuItemLayout itemLayout = (SkuItemLayout) mSkuContainerLayout.getChildAt(0);

        // 遍历sku列表
        for (int i = 0; i < mSkuList.size(); i++) {
            // 属性值是否可点击flag
            Sku sku = mSkuList.get(i);
            List<SkuAttribute> attributeBeanList = mSkuList.get(i).getAttributes();

            if (sku.getStockQuantity() > 0) {
                String attributeValue = attributeBeanList.get(0).getValue();  // 有这个属性的商品
                itemLayout.optionItemViewEnableStatus(attributeValue);        // 设置这个属性可以点击
            }
        }
    }

    /**
     * 有多种大属性 -- 设置所有属性的Enable状态，即是否可点击
     */
    private void optionLayoutEnableStatusMultipleProperties() {

        for (int i = 0; i < mSkuContainerLayout.getChildCount(); i++) {

            SkuItemLayout itemLayout = (SkuItemLayout) mSkuContainerLayout.getChildAt(i);

            /* 遍历sku列表 */
            for (int j = 0; j < mSkuList.size(); j++) {
                // 属性值是否可点击flag
                boolean flag = false;
                Sku sku = mSkuList.get(j);
                List<SkuAttribute> attributeBeanList = sku.getAttributes();
                /* 遍历选中信息列表 */
                for (int k = 0; k < mSelectedAttributeList.size(); k++) {
                    // i = k，跳过当前属性，避免多次设置是否可点击
                    if (i == k) continue;
                    // 选中信息为空，则说明未选中，无法判断是否有不可点击的情形，跳过
                    if ("".equals(mSelectedAttributeList.get(k).getValue())) continue;
                    // 选中信息列表中不包含当前sku的属性，则sku组合不存在，设置为不可点击
                    // 库存为0，设置为不可点击
                    if (!mSelectedAttributeList.get(k).getValue().equals(attributeBeanList.get(k).getValue())
                            || sku.getStockQuantity() == 0) {
                        flag = true;
                        break;
                    }
                }

                // flag 为false时，可点击
                if (!flag) {
                    String attributeValue = attributeBeanList.get(i).getValue();
                    itemLayout.optionItemViewEnableStatus(attributeValue);
                }
            }

        }
    }

    /**
     * 设置所有属性的选中状态
     */
    private void optionLayoutSelectStatus() {
        for (int i = 0; i < mSkuContainerLayout.getChildCount(); i++) {
            SkuItemLayout itemLayout = (SkuItemLayout) mSkuContainerLayout.getChildAt(i);
            itemLayout.optionItemViewSelectStatus(mSelectedAttributeList.get(i));
        }
    }

    /**
     * 是否有sku选中
     *
     * @return
     */
    private boolean isSkuSelected() {
        for (SkuAttribute attribute : mSelectedAttributeList) {
            if (TextUtils.isEmpty(attribute.getValue())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取第一个未选中的属性名
     *
     * @return
     */
    public String getFirstUnelectedAttributeName() {
        for (int i = 0; i < mSkuContainerLayout.getChildCount(); i++) {
            SkuItemLayout itemLayout = (SkuItemLayout) mSkuContainerLayout.getChildAt(i);
            if (!itemLayout.isSelected()) {
                return itemLayout.getAttributeName();
            }
        }
        return "";
    }

    /**
     * 获取选中的Sku
     *
     * @return
     */
    public Sku getSelectedSku() {
        // 判断是否有选中的sku
        if (!isSkuSelected()) {
            return null;
        }
        for (Sku sku : mSkuList) {
            List<SkuAttribute> attributeList = sku.getAttributes();
            // 将sku的属性列表与selectedAttributeList匹配，完全匹配则为已选中sku
            boolean flag = true;
            for (int i = 0; i < attributeList.size(); i++) {
                if (!isSameSkuAttribute(attributeList.get(i), mSelectedAttributeList.get(i))) {
                    flag = false;
                }
            }
            if (flag) {
                return sku;
            }
        }
        return null;
    }

    /**
     * 设置选中的sku
     *
     * @param sku
     */
    public void setSelectedSku(Sku sku) {
        mSelectedAttributeList.clear();
        for (SkuAttribute attribute : sku.getAttributes()) {
            mSelectedAttributeList.add(new SkuAttribute(attribute.getKey(), attribute.getValue()));
        }
        // 清除所有选中状态
        clearAllLayoutStatus();
        // 设置是否可点击
        optionLayoutEnableStatus();
        // 设置选中状态
        optionLayoutSelectStatus();
    }

    /**
     * 是否为同一个SkuAttribute
     *
     * @param previousAttribute
     * @param nextAttribute
     * @return
     */
    private boolean isSameSkuAttribute(SkuAttribute previousAttribute, SkuAttribute nextAttribute) {
        return previousAttribute.getKey().equals(nextAttribute.getKey())
                && previousAttribute.getValue().equals(nextAttribute.getValue());
    }

    @Override
    public void onSelect(int position, boolean selected, SkuAttribute attribute) {
        if (selected) {
            // 选中，保存选中信息
            mSelectedAttributeList.set(position, attribute);
        } else {
            // 取消选中，清空保存的选中信息
            mSelectedAttributeList.get(position).setValue("");
        }
        clearAllLayoutStatus();
        // 设置是否可点击
        optionLayoutEnableStatus();
        // 设置选中状态
        optionLayoutSelectStatus();
        // 回调接口
        if (isSkuSelected()) {
            mListener.onSkuSelected(getSelectedSku());
        } else if (selected) {
            mListener.onSelect(attribute);
        } else {
            mListener.onUnselected(attribute);
        }
    }
}