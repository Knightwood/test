package com.crystal.treelistviewdemo;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @创建者 kiylx
 * @创建时间 2020/3/2 16:23
 */
public class NodeControlTest {
    NodeControl control;
    Node root1;
    Node c;
    Node d;
    Node a;
    Node b;
    Node e;
    Node a1;

    @Before
    public void setUp() throws Exception {
        a = new Node(23, -1, 1, "a文件夹", true);
        b = new Node(224, -1, 1, "b文件夹", true);
        a1 = new Node(10, -1, 1, "a1文件", false);
        c = new Node(2, 224, 2, "c文件夹", true);
        //c.setExpand(false);
        d = new Node(25, 2, 3, "d文件夹", true);
        e = new Node(256, 23, 2, "e文件", false);

        control = NodeControl.getInstance();

        control.insertNode(a);
        control.insertNode(b);
        control.insertNode(a1);

        root1 = control.getRoot();
        control.insertNode(e);
        control.insertNode(c);
        control.insertNode(d);

        control.newNode(333, 2, 2, "f文件夹", true);
        control.newNode(331, 333, 3, "m文件夹", true);
        control.newNode(332, 333, 3, "n文件", false);
        System.out.println("================");
    }

    @Test
    public void insertData() {

        c.setExpand(true);

        List<Node> result = control.getExpandList(root1);
        result.get(7).setName("mmmm");
        print();
//改变节点的展开状态，再获取所有节点。这里的是引用，所以改变list的节点，树中的节点肯定会被改变
        //result.get(6).setExpand(false);
        print();
    }
    private void print(){
        List<Node> result = control.getExpandList(root1);
        System.out.println("目录：\n");
        for (Node tmp : result) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < tmp.getLevel(); i++) {
                builder.append("  ");
            }

            System.out.println(builder.toString() + tmp.getName());

        }
    }

    @Test
    public void expandList() {
        c.setExpand(true);
        print();
    }

    @Test
    public void deleteNode(){
        print();
        control.deleteNode(a1);
        print();
        control.deleteFolder(b,false);
        print();
    }
}