package com.crystal.customview.bottomview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.crystal.customview.R;

import com.crystal.customview.liteviewpager.LiteViewPager;

public class MyBottomSheetActivity extends AppCompatActivity {
LiteViewPager mViewGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bottom_sheet);
        mViewGroup=findViewById(R.id.litepage);
    }

    public void moveToTop(View target) {
        //先确定现在在哪个位置
        int startIndex = mViewGroup.indexOfChild(target);
        //计算一共需要几次交换，就可到达最上面
        int count = mViewGroup.getChildCount() - 1 - startIndex;
        for (int i = 0; i < count; i++) {
            //更新索引
            int fromIndex = mViewGroup.indexOfChild(target);
            //目标是它的上层
            int toIndex = fromIndex + 1;
            //获取需要交换位置的两个子View
            View from = target;
            View to = mViewGroup.getChildAt(toIndex);

            //先把它们拿出来
            mViewGroup.detachViewFromParent(toIndex);
            mViewGroup.detachViewFromParent(fromIndex);

            //再放回去，但是放回去的位置(索引)互换了
            mViewGroup.attachViewToParent(to, fromIndex, to.getLayoutParams());
            mViewGroup.attachViewToParent(from, toIndex, from.getLayoutParams());
        }
        //刷新
        mViewGroup.invalidate();
    }
}
