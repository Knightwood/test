package com.example.kiylx.ti.webview32;

import android.webkit.WebView;

import com.example.kiylx.ti.Xapplication;
import com.example.kiylx.ti.webview32.nestedjspack.Suggestion;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.example.kiylx.ti.webview32.nestedjspack.NestedJsCode.getSuggestCode;

/**
 * 创建者 kiylx
 * 创建时间 2020/7/13 22:31
 */
public class JsManager {
    private static JsManager Instance;
    String suggestCode = "";

    public static JsManager getInstance() {
        if (Instance == null) {
            synchronized (JsManager.class) {
                if (Instance == null) {
                    Instance = new JsManager();
                }
            }
        }
        return Instance;
    }

    public JsManager() {

    }

    public static String readJsFile(String fileName) {
        String str = "";
        try {
            InputStream inputStream = Xapplication.getInstance().getAssets().open(fileName);
            byte buff[] = new byte[1024];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while (inputStream.read(buff) != -1) {
                byteArrayOutputStream.write(buff);
            }
            str = byteArrayOutputStream.toString();
            inputStream.close();
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 添加js与java的映射
     */
    public void addJSInterface(CustomAWebView webview) {
       webview.addJavascriptInterface(new Suggestion(), "SuggestJS");//搜索建议
        //webview.addJavascriptInterface(new ActionSelectInterface(this), "JSInterface");//action菜单

    }

    /**
     * 把js代码注入网页
     * @param view
     */
    public void injectJsCode(WebView view) {
        view.evaluateJavascript("javascript:"+getSuggestCode(),null);//搜索建议

    }

    /**
     * 自动执行注入的代码
     * @param view
     */
    public void autoExeJsCode(WebView view) {

    }
    public void exeJsCode(WebView view,String s){
        view.loadUrl("javascript:getSuggest('"+s +"')");//搜索建议
    }


}
