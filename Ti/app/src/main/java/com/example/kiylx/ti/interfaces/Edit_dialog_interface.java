package com.example.kiylx.ti.interfaces;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/31 10:43
 * packageName：com.example.kiylx.ti.interfaces
 * 描述：edit_dialog将编辑传入的文本，将最终结果通过接口传递
 */
public interface Edit_dialog_interface {
    /**
     * 将编辑后的结果进行传递
     * @param requestCode 标识进行的操作
     * @param request 传入edit_dialog的string
     * @param result 修改request后的结果
     */
    void setResult(int requestCode,String request,String result);

    /**
     * 其他用途，随实现类而自行决定
     */
    void message();
}
