package com.example.kiylx.ti;
//一个特殊的链表，只在开头不断的放入元素。

public class WebList {
   Ti Top;
   int allnum=0;
   //元素的个数，也是最顶部的元素
    int yongyushanchu =0;

   WebList(){
       this.Top=null;
   }

   Ti getTop(){

       return Top;
   }
   int getAllnum(){
        return allnum;
    }

   void push(Ti web){
       if(Top==null){
           Top=web;
           Top.index=allnum;//空的时候放入web，那它就是list[0]
           this.allnum++;
           return;
       }else{
           web.next=Top;
           Top=web;
           this.allnum++;
           Top.index=allnum;//位置是list[allnum+1]
       }
       return;
   }

   Ti getI(int i){
       Ti current=Top;
       Ti perient;
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
           getI(i).destroy();
       }
   }

}
