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
    private List<Node> oringe;
    Node c;
    Node d;
    Node a;
    Node b;
    Node a1;

    @Before
    public void setUp() throws Exception {
        oringe = new ArrayList<>();
        a = new Node(23, -1, 1, "a文件夹lv1", true);
        b = new Node(224, -1, 1, "b文件夹lv1", true);
        a1 = new Node(10, -1, 1, "a1文件lv1", false);

        c = new Node(2, 224, 2, "c文件夹lv2", true);
        d = new Node(25, 2, 3, "d文件夹lv3", true);

        oringe.add(a);
        oringe.add(b);
        oringe.add(a1);

    }

    @Test
    public void insertNode() {
        NodeControl control = NodeControl.getInstance();

        control.insertNode(a);
        control.insertNode(b);
        control.insertNode(a1);
        Node root1 = control.getRoot();
        System.out.println("================");
        for (Node result : root1.getChildrenList()
        ) {
            System.out.println(result.getName());

        }
        System.out.println("=====================================");
        control.insertNode(c);
        System.out.println("=====================================");
        control.insertNode(d);


    }
}