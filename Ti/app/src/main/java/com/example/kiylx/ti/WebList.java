package com.example.kiylx.ti;


public class WebList {
   Ti Top;
   int allnum=0;
   //索引

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
           this.allnum++;
           return;
       }else{
           web.next=Top;
           Top=web;
           this.allnum++;
       }
       return;
   }


   void delete(){

   }

}
