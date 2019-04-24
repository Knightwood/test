package com.example.kiylx.ti;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    /* 三个函数：

　　push webview：进入应用后首先执行的操作，初始化一个webview铺到页面上，根据偏好设定，
                  载入网址或是铺展一个空的webview。

　　search bar：监听搜索框输入内容，当按下回车键后调用search执行搜索，还要记录下当前搜索框内容。

　　search：执行搜索，把获得的内容用上下文获得的当前webview执行搜索操作。

　　当执行新建标签页时：首先，新建webview铺到页面上，之前的webview就要暂停并隐藏。这时搜索框内容也要清空。

　　多窗口页面：这要在我上面添加webview时就要添加到list里，并且铺展到这个多窗口页面（gridwiew），
               这时的webview都是暂停状态，且是隐藏的。当按下多窗口键时，隐藏当前webview，展示所有webview。

　　切换页面：按到相应的webview，就要隐藏多窗口页面，展示按到的webview.
             且，要根据保存起来的信息，把网址，当时的搜索框内容重新填充。


　　

　　最顶部展示网页网址或者标题，按下时复制到剪贴板
*/


}