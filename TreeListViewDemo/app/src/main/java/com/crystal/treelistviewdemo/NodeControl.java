package com.crystal.treelistviewdemo;

import java.util.ArrayList;
import java.util.List;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/29 17:29
 */
public class NodeControl {
    public static NodeControl mControl;
    private List<Node> allNode;
    private NodeControl(){
        allNode=new ArrayList<>();
    }
    public static void getInstance(){
        if (mControl==null){
            synchronized(NodeControl.class){
                if (mControl==null){
                    mControl=new NodeControl();
                }
            }
        }
    }

   // public void
}
