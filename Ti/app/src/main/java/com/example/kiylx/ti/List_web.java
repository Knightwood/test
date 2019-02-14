package com.example.kiylx.ti;


public class List_web {
   Ti Root;
   int index=0;
   //索引
   List_web(){
       this.index =0;
       this.Root=null;
   }
   void add(int i, Ti web){
       if(index==i){
           Root=web;
           Root.ii=i;
           index=0;
           return;
       }else {
           index++;
           add(i,web.next);
       }
       return;

   }
   void delete(int i){
       Ti current=get(i);
       if(i==0){
           Ti tmp1=current.next;
           current=null;
           //+此处应该调用删除
           changeNum(tmp1);
       }else if(current.next==null){
           current=null;
           //+此处应该调用删除
           Ti temp=get(i-1);
           temp.next=null;
       }else{
       Ti temp=get(i-1);
       temp.next=get(i+1);
       current=null;
       //+此处应该调用删除
           }
           return;

   }

   Ti get(int i){
       Ti temp=Root;
       for(;index!=i;index++){
           temp=temp.next;
       }
       index=0;
       return temp;

   }
   private void changeNum(Ti tmp){
       //保持webview的下标不会因为删除一个而断开
       tmp.ii-=1;
       if(tmp.next!=null){
           changeNum(tmp.next);
       }
       return;
   }
}
