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
   public void delete(int i){

       Ti temp1=get(i-1);
       temp1.next=get(i+1);

   }
   public void delete1(){

   }
   Ti get(int i){
       Ti temp=Root;
       for(;index!=i;index++){
           temp=temp.next;
       }
       return temp;

   }
}
