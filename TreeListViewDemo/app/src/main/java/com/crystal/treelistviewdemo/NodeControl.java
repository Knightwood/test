package com.crystal.treelistviewdemo;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/29 17:29
 */
public class NodeControl {
    private volatile static NodeControl mControl;
    private Node root;


    private NodeControl() {
        root = new Node(-1, -2, 0, "**根节点**", true);
        root.setExpand(true);
    }

    public static NodeControl getInstance() {
        if (mControl == null) {
            synchronized (NodeControl.class) {
                if (mControl == null) {
                    mControl = new NodeControl();
                }
            }
        }
        return mControl;
    }

    public Node getRoot() {
        if (root == null) throw new AssertionError();
        return root;
    }

    /**
     * @param id          节点的id
     * @param parentId    父节点的id
     * @param parentLevel 父节点的层级
     * @param name        节点名称
     * @param folder      是否是文件夹，否的话是普通节点
     */
    public void newNode(int id, int parentId, int parentLevel, String name, boolean folder) {
        Node tmp = new Node(id, parentId, parentLevel + 1, name, folder);
        insertNode(tmp);
    }

    public void insertNode(Node tmp) {
        if (tmp.isFolder()) {
            Objects.requireNonNull(findParent(root, tmp)).getChildrenList().add(tmp);
        } else {
            Objects.requireNonNull(findParent(root, tmp)).getFileList().add(tmp);
        }

    }

    /**
     * @param tmp 要被删除的普通节点
     *            获取父节点，然后从父节点的fileList中删除tmp
     */
    public void deleteNode(Node tmp) {
        Objects.requireNonNull(findParent(root, tmp)).getFileList().remove(tmp);
    }

    /**
     * @param node 要被删除的文件夹
     * @param b    是否连文件夹下的内容也一并删除
     *             先找到node的父节点，把node的两个list合并到父节点，
     *             然后从父节点删除node
     */
    public void deleteFolder(Node node, boolean b) {
        Node parent=Objects.requireNonNull(findParent(root, node));
        int level=node.getLevel();
        if (!b) {
            //不删除子文件和子文件夹
            if (node.getChildrenList() != null && !node.getChildrenList().isEmpty()) {
                //如果node的子文件夹节点列表存在且有数据,深度优先遍历，更改level值
                Stack<Node> stack = new Stack<>();
                stack.push(node);
                while (!stack.empty()) {
                    Node top = stack.pop();
                    top.setLevel(top.getLevel()-1);
                    List<Node> children = top.getChildrenList();
                    List<Node> files = top.getFileList();

                    if (files != null && files.size() > 0) {
                        for (int i = files.size() - 1; i >= 0; i--) {
                            stack.push(files.get(i));
                        }
                    }
                    if (children != null && children.size() > 0) {
                        for (int i = children.size() - 1; i >= 0; i--) {
                            stack.push(children.get(i));
                        }

                    }
                }
            }
            if (node.getChildrenList()!=null && !node.getChildrenList().isEmpty()){
                //把被删除节点的直属子节点加入被删除节点的父节点
                for (Node folder: node.getChildrenList()) {
                    parent.getChildrenList().add(folder);
                }
            }
            if (node.getFileList() != null && !node.getFileList().isEmpty()) {
                //如果node的子文件节点列表存在且有数据
                for (Node file : node.getFileList()) {
                    file.setLevel(level-1);
                    file.setParentId(parent.getId());
                    parent.getFileList().add(file);
                }
            }
        }
        parent.getChildrenList().remove(node);
}



    private Node findParent(Node root, Node node) {
        if (node.getParentId() == root.getId()) {
            //System.out.println(node.getName() + "的直接父节点->" + root.getName());
            //这里隐含的是node的level=1，root的level=0；广义的也就是node比root的level小1（node.getLevel()-1==root.getLevel()）；
            return root;
        } else {
            //这里说明node不应该插入到这个root的childrenlist中，(node.getLevel()-1>root.getLevel())
            if (root.getLevel() + 1 == node.getLevel()) {
                //root节点的子节点和node节点平级，这个root节点不是node节点的父节点
                return null;
            }
            for (Node tmp : root.getChildrenList()) {
                if (tmp.isFolder()) {
                    //System.out.println(tmp.getName() + "->");
                    //遍历根节点的childrenlist（用到了递归），如果不是node的父节点，result会被赋为null，如果是父节点，就直接返回结果
                    Node result = findParent(tmp, node);
                    if (result != null) {
                        return result;
                    }
                }

            }
        }
        return null;
    }

    /**
     * 深度优先遍历,非递归
     * <p>
     * 把根节点入栈，
     * while循环->
     * 取出栈顶，放入结果列表
     * 在栈顶（根节点）是展开的状态下，如果根节点的文件列表不为空，把文件从右往左入栈，文件夹列表也如此。
     * 然后while不停循环
     */
    public List<Node> getExpandList(Node rootNode) {
        List<Node> result = new ArrayList<>();
        Stack<Node> stack = new Stack<>();
        stack.push(rootNode);
        while (!stack.empty()) {
            Node top = stack.pop();
            result.add(top);
            List<Node> children = top.getChildrenList();
            List<Node> files = top.getFileList();
            if (top.isExpand()) {
                if (files != null && files.size() > 0) {
                    for (int i = files.size() - 1; i >= 0; i--) {
                        stack.push(files.get(i));
                    }
                }
                if (children != null && children.size() > 0) {

                    for (int i = children.size() - 1; i >= 0; i--) {
                        stack.push(children.get(i));
                    }

                }
            }

        }
        return result;
    }

    /**
     * @param node 被编辑的节点
     *             编辑节点的名称
     */
    public void editNodeName(Node node, String name) {
        node.setName(name);
    }
}
