package com.skinfotech.dailyneeds.models;

public class MenuModel {

    public String menuName;
    public String menuId;
    public int img;
    public String imgStr;
    public int imgArrow;
    public boolean parent;
    public boolean root;

    public MenuModel(String menuName, boolean root, boolean parent, int img, int imgArrow) {
        this.menuName = menuName;
        this.root = root;
        this.parent = parent;
        this.img = img;
        this.imgArrow = imgArrow;
    }

    public MenuModel(String menuName, boolean root, boolean parent, String imgStr, int imgArrow, String id) {
        this.menuName = menuName;
        this.root = root;
        this.parent = parent;
        this.imgStr = imgStr;
        this.imgArrow = imgArrow;
        this.menuId = id;
    }


}
