package com.crystal.customview.fileindexview;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.crystal.customview.R;


/**
 * 创建者 kiylx
 * 创建时间 2020/4/7 17:04
 */
public class SelectFolderFragment extends DialogFragment {
    private static final String TAG = "下载设置-选择文件夹";
    private View dialogRoot;
    private TextView positiveButton;
    private TextView backButton;
    private TextView pathView;//指示当前路径的视图
    private IndexView indexView;//文件夹路径视图
    private static String path;//始终是当前路径
    private SendPath sendPath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(SelectFolderFragment.STYLE_NORMAL,R.style.DialogThemes);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialogRoot = inflater.inflate(R.layout.dialog_select_dir, container, false);

        positiveButton = dialogRoot.findViewById(R.id.save_folder);
        backButton = dialogRoot.findViewById(R.id.back_folder);
        pathView = dialogRoot.findViewById(R.id.folder_path);

        indexView = dialogRoot.findViewById(R.id.folder_index);
        indexView.setClickAfter(new IndexView.ClickAfter() {
            //这里是after接口的实现
            @Override
            public void after(String path) {
                //指示当前的路径
                SelectFolderFragment.path = path;
                pathView.setText(path);
            }
        });

        path=indexView.getCurrentPath();//刚打开这个dialogFragment时初始化为当前路径（也就是根路径）
        pathView.setText(path);//显示当前的路径

        //确认按钮
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+path);
                if (sendPath!=null){
                    sendPath.send(path);
                }
                dismiss();
            }
        });
        //上一层级按钮
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indexView.goBack();
            }
        });

        return dialogRoot;
    }

    @Override
    public void onStart() {
        super.onStart();
        dialogStyle();
    }


    public void dialogStyle() {
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            //指定显示位置
            //layoutParams.gravity = Gravity.BOTTOM;
            /*WindowManager manager = getActivity().getWindowManager();
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);*/

            //指定显示大小
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height =WindowManager.LayoutParams.MATCH_PARENT;
            //显示消失动画
            window.setWindowAnimations(R.style.animate_dialog);
            //让属性设置生效
            window.setAttributes(layoutParams);
            //设置点击外部可以取消对话框
            setCancelable(true);
        }
    }

    /**
     * 此接口用于：在当前dialog中选择了文件夹后，
     * 当点击确定时，将会调用此接口，把选择的文件夹的路径传给实现接口的类
     */
    public interface SendPath{
        void send(String path);
    }
    public void setSendPath(SendPath sendPath){
        this.sendPath=sendPath;
    }


}
