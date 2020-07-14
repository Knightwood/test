package com.example.kiylx.ti.webview32.nestedjspack;

import android.webkit.JavascriptInterface;

/**
 * 创建者 kiylx
 * 创建时间 2020/7/14 10:40
 */
public  class NestedJsCode {
    public static String jsmenu = "(function getSelectedText() {" +
            "var txt;" +
            "var title = \"%s\";" +
            "if (window.getSelection) {" +
            "txt = window.getSelection().toString();" +
            "} else if (window.document.getSelection) {" +
            "txt = window.document.getSelection().toString();" +
            "} else if (window.document.selection) {" +
            "txt = window.document.selection.createRange().text;" +
            "}" +
            "JSInterface.callback(txt,title);" +
            "})()";

    public static String getSuggestCode(String str) {
        String suggestCode = "function getSuggest() {" +

                //组装查询地址
                "var sugurl = \"http://suggestion.baidu.com/su?wd=#content#&cb=window.baidu.sug\";" +
                "var str = \"" + str + "\";" +
                "sugurl = sugurl.replace(\"#content#\", str);" +

                //定义回调函数
                "window.baidu = {" +
                "sug: function(json) {" +
                " var x=[];" +
                "for (i = 0; i < json.s.length; i++) {" +
                "var str=json.s[i];" +
                "console.log(str);"+
                "x.push(str);" +
                "}" +
                "SuggestJS.giveSuggest(x);" +
                "}" +
                "};" +//该死的分号

                //动态添加JS脚本
                "var script = document.createElement(\"script\");"+
                "script.src = sugurl;"+
                "document.body.appendChild(script);"+
                "};";
        return suggestCode;
    }


}
