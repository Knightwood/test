package com.crystal.treelistviewdemo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/29 17:29
 */
public class NodeControl {
    private volatile static NodeControl mControl;
    private Node root;


    private NodeControl() {
        root = new Node(-1, -2, 0, "**根节点**", true);
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
     *             先找到node的父节点，把node的两个list合并到父节点，
     *             然后从父节点删除node
     */
    public void deleteFolder(Node node) {
        Node rootNode = Objects.requireNonNull(findParent(root, node));
        rootNode.addFolder(node.getChildrenList());
        rootNode.addFile(node.getFileList());
        rootNode.deleteFolder(node);
    }



    private Node findParent(Node root, Node node) {
        if (node.getParentId() == root.getId()) {
            System.out.println(node.getName() + "的直接父节点->" + root.getName());
            //这里隐含的是node的level=1，root的level=0；广义的也就是node比root的level小1（node.getLevel()-1==root.getLevel()）；
            return root;
        } else {
            //这里说明node不应该插入到这个root的childrenlist中，(node.getLevel()-1>root.getLevel())
            if (root.getLevel() + 1 == node.getLevel()) {
                //root节点的子节点和node节点平级，这个root节点不是node节点的父节点
                return null;
            }
            //一
            for (Node tmp : root.getChildrenList()) {
                if (tmp.isFolder()) {
                    System.out.println(tmp.getName() + "->");
                    //遍历根节点的childrenlist（用到了递归），如果不是node的父节点，result会被赋为null，如果是父节点，就直接返回结果
                    Node result = findParent(tmp, node);
                    if (result != null) {
                        return result;
                    }
                }

            }
            //二
            /*获取根节点的childrenlist，而且是排除了子节点属性是非文件夹的节点
             * 如果这个节点的childrenlist是不存在的，要么是非文件夹属性的节点，要么就是叶子结点，而如果是叶子节点，到这一步就不会再往下查找了，
             * 因为叶子结点可能就是要查找的父节点*/
            /*for (int i=0;i<root.getChildrenList().size();i++){
                Node tmp=root.getChildrenList().get(i);
                if (tmp.isFolder()){ //不遍历非文件夹的节点
                    System.out.println(tmp.getName()+"->");
                    //遍历根节点的childrenlist（用到了递归），如果不是node的父节点，result会被赋为null，如果是父节点，就直接返回结果
                    Node result = findParent(tmp, node);
                    if (result != null){
                        return result;
                    }
                }
            }*/
        }
        return null;
    }
    /**
     * 深度优先遍历
     * */
    public List<Node> find(){
        return null;
    }

    /**
     * @param node 被编辑的节点
     *             编辑节点的名称
     */
    public void editfolderName(Node node,String name) {

    }
    /**
     * @param node 被编辑的节点
     *             编辑节点的名称
     */
    public void editFileName(Node node,String name){

    }
    /**
     * @return 返回应展示的的list
     */
    public List<Node> getList(){
        return null;
    }

    /**
     * @param root 根节点
     * @param b 获取根节点的所有展开的list
     * @return 应展示的list
     */
    public List<Node> getExpandList(Node root,boolean b) {
        List<Node> result = new ArrayList<>();

        return result;
    }

    /**
     * @param root 根节点
     *             操作展开根节点和关闭根节点
     */
    public List<Node> expandList(Node root) {
        if (root.isExpand()){
           return getExpandList(root,false);
        }else{
           return getExpandList(root,true);
        }
    }


}
