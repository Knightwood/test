package com.crystal.treelistviewdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    NodeControl control;
    Node root1;
    Node c;
    Node d;
    Node a;
    Node b;
    Node e;
    Node a1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        print();
        List<Node> result = control.getExpandList(root1);
        RecyclerView recyclerView=findViewById(R.id.treecontainer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TreeViewAdapter adapter =new TreeViewAdapter(result,this);

        recyclerView.setAdapter(adapter);

    }

    void initData() {
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
    }

    private void print() {
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
}
