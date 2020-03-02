package com.crystal.treelistviewdemo;

import java.util.ArrayList;
import java.util.List;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/29 17:29
 */
public class Node {
    /**
     * id
     */
    private int id;
    /**
     * 指向父节点的id
     */
    private int parentId;
    /**
     * 名称
     */
    private String name;
    /**
     * 层级，层级越大，视图左侧的空白越长
     * 我控制在最多3层
     */
    private int level;
    /**
     * 是否是展开状态
     */
    private boolean isExpand = false;
    /**
     * 图标的id
     */
    private int icon;
    private List<Node> childrenList;//存储文件夹类型的节点
    private List<Node> fileList;//存储普通节点（非文件夹类型的节点）
    private boolean folder;

    public Node() {}

    public Node(int id, int parentId, int level, String name, boolean folder) {
        super();
        this.id = id;
        this.parentId = parentId;
        this.level = level;
        this.name = name;
        this.folder = folder;
        if (folder){
            childrenList = new ArrayList<>();
            fileList = new ArrayList<>();
        }else {
            childrenList=null;
            fileList=null;
        }
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public List<Node> getChildrenList() {
        return childrenList;
    }

    public void setChildrenList(List<Node> childrenList) {
        this.childrenList = childrenList;
    }


    /**
     * 获取level
     */
    public int getLevel() {
        return level;
    }

    /**
     * 设置展开
     *
     * @param isExpand
     */
    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
        if (!isExpand) {

            for (Node node : childrenList) {
                node.setExpand(isExpand);
            }
        }
    }

    /**
     * @return 是不是文件夹
     */
    public boolean isFolder() {
        return folder;
    }

    /**
     * @return 返回普通节点的列表
     */
    public List<Node> getFileList() {
        return fileList;
    }

    /**
     * @param list 列表
     *             把传入的文件夹节点的list合并到这个节点的childrenList（）文件夹列表
     */
    public void addFolder(List<Node> list){
        this.childrenList.addAll(list);
    }

    /**
     * @param list 列表
     *             把传入的普通节点的list合并到这个节点的fileList
     */
    public void addFile(List<Node> list){
        this.fileList.addAll(list);
    }

    /**
     * @param node 文件夹节点
     *             从文件夹节点中删除node
     */
    public void deleteFolder(Node node) {
        this.childrenList.remove(node);
    }
}
