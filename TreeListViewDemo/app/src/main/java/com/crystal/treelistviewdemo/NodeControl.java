package com.crystal.treelistviewdemo;

import java.util.ArrayList;
import java.util.List;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/29 17:29
 */
public class NodeControl {
    public static NodeControl mControl;
    private Node root;


    private NodeControl() {
        root.setChildren(new ArrayList<Node>());
        root = new Node();
        root.setLevel(0);
        root.setFolder(true);
        root.setId(-1);
    }

    public static void getInstance() {
        if (mControl == null) {
            synchronized (NodeControl.class) {
                if (mControl == null) {
                    mControl = new NodeControl();
                }
            }
        }
    }

    /**
     * @param list 存储node节点的list
     *             初始化数据
     */
    public void initData(List<Node> list) {
        this.root.setChildren(list);
    }

    /**
     * @param node 节点
     *             根节点是第零层，也就是level==0
     */
    public void insertBrotherNode(Node node) {
        root.getChildren().add(node);
    }

    private void insertNode(Node tmp) {
        find(root,tmp).getChildren().add(tmp);
    }

    private Node find(Node root,Node node) {
        if(node.getParentId()==root.getId()){
            return root;
        }
        if (root.getLevel()<node.getLevel()){

        }
        return null;
    }

    public void newNode(int id, int parentId, int parentLevel, String name, boolean folder) {
        Node tmp = new Node(id, parentId, name);
        tmp.setFolder(folder);
        tmp.setLevel(parentLevel + 1);
        if (parentLevel == 0) {
            insertBrotherNode(tmp);
        } else {
            insertNode(tmp);
        }

    }


}
