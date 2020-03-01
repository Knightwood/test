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
     * */
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
     */
        private int level;
    /**
     * 是否是展开状态
     */
        private boolean isExpand =false;
    /**
     * 图标的id
     */
        private int icon;
        private List<Node> children=new ArrayList<>();
        private Node parent;
    public Node()
    {
    }

    public Node(int id, int parentId, String name)
    {
        super();
        this.id = id;
        this.parentId = parentId;
        this.name = name;
    }

    public int getIcon()
    {
        return icon;
    }

    public void setIcon(int icon)
    {
        this.icon = icon;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getParentId()
    {
        return parentId;
    }

    public void setParentId(int parentId)
    {
        this.parentId = parentId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public boolean isExpand()
    {
        return isExpand;
    }

    public List<Node> getChildren()
    {
        return children;
    }

    public void setChildren(List<Node> children)
    {
        this.children = children;
    }

    public Node getParent()
    {
        return parent;
    }

    public void setParent(Node parent)
    {
        this.parent = parent;
    }

    /**
     * 是否为跟节点
     *
     * @return
     */
    public boolean isRoot()
    {
        return parent == null;
    }

    /**
     * 判断父节点是否展开
     *
     * @return
     */
    public boolean isParentExpand()
    {
        if (parent == null)
            return false;
        return parent.isExpand();
    }

    /**
     * 是否是叶子界点
     *
     * @return
     */
    public boolean isLeaf()
    {
        return children.size() == 0;
    }

    /**
     * 获取level
     */
    public int getLevel()
    {
        return parent == null ? 0 : parent.getLevel() + 1;
    }

    /**
     * 设置展开
     *
     * @param isExpand
     */
    public void setExpand(boolean isExpand)
    {
        this.isExpand = isExpand;
        if (!isExpand)
        {

            for (Node node : children)
            {
                node.setExpand(isExpand);
            }
        }
    }


}
