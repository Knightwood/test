package com.example.kiylx.ti;
//一个特殊的链表，只在开头不断的放入元素。

import android.webkit.WebView;

public class WebList {

    class node{
        WebView t;
        node next;
        int id;
        node(WebView v){
            this.t=v;
            this.next=null;
        }
    }

    node Top;
    int num=0;
    //元素的个数，也是最顶部的元素
    int yongyushanchu =0;
    WebList(){
       this.Top=null;
       this.num=0;
   }

    WebView getTop(){
       return this.Top.t;
   }
    int getAllnum(){
        return num;
    }

    void push(WebView web){
        this.Top=add(this.Top,web);
    }

    private node add(node ro,WebView web){
       if(ro==null){
           ro=new node(web);
           ro.id=num;//空的时候放入web，那它就是list[0]
           this.num++;
           return ro;
       }else{
           ro.next=add(ro.next,web);
           this.num++;
           ro.id=num;//位置是list[num+1]
       }
       return ro;
   }

    node getI(int i){
       node current=Top;
       node perient;
       while (i!=yongyushanchu){
           if(yongyushanchu<i){

           }else if(yongyushanchu>i){

           }
       }
       return current;
   }

    void delete(int i){
       int j=0;
       while(i-1==j){
           getI(j).next=getI(i+1);
           getI(i).t.destroy();
       }
   }

     public WebView get(int position) {
       int i=0;
       node temp=Top;
       while(i!=position){
           temp=temp.next;
           i++;
       }
       return temp.t;
    }
}
