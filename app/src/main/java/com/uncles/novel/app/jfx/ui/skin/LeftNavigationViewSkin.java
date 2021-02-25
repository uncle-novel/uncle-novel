package com.uncles.novel.app.jfx.ui.skin;

import com.uncles.novel.app.jfx.ui.components.LeftNavigation;
import com.uncles.novel.app.jfx.ui.components.LeftNavigationMenuItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author blog.unclezs.com
 * @since 2021/02/25 17:11
 */
public class LeftNavigationViewSkin extends SkinBase<LeftNavigation> {
    private static final Map<LeftNavigationMenuItem, ChangeListener<Boolean>> MENU_ITEM_LISTENER = new ConcurrentHashMap<>();

    /**
     * Constructor for all SkinBase instances.
     *
     * @param leftNavigation The control for which this Skin should attach to.
     */
    public LeftNavigationViewSkin(LeftNavigation leftNavigation) {
        super(leftNavigation);
        this.getChildren().setAll(getSkinnable().getMenuItems());
        this.addSelectionIfAbsent(leftNavigation);
        this.initializeListener(leftNavigation);
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double width, double height) {
        List<LeftNavigationMenuItem> actionItems = (this.getSkinnable()).getMenuItems();
        List<LeftNavigationMenuItem> managedActionItems = new ArrayList();
        double selectionDelta = 0.0D;
        double effectiveHeight = (this.getSkinnable()).prefHeight(width) - contentY - this.snappedBottomInset();
        double effectiveWidth = width - this.snappedLeftInset() - this.snappedRightInset();
        Iterator var18 = actionItems.iterator();

        while(var18.hasNext()) {
            LeftNavigationMenuItem actionItem = (LeftNavigationMenuItem)var18.next();
            if (actionItem.isManaged()) {
                managedActionItems.add(actionItem);
            }
        }

        double currentX;
        double prefNodeWidth;
        if (effectiveWidth < 168.0D * (double)managedActionItems.size()) {
            prefNodeWidth = (effectiveWidth - selectionDelta) / (double)(managedActionItems.isEmpty() ? 1 : managedActionItems.size());
            currentX = contentX;
        } else {
            prefNodeWidth = 168.0D;
            currentX = contentX + (effectiveWidth - 168.0D * (double)managedActionItems.size()) / 2.0D;
        }

        double actualNodeWidth;
        for(Iterator var22 = managedActionItems.iterator(); var22.hasNext(); currentX += actualNodeWidth) {
            LeftNavigationMenuItem button = (LeftNavigationMenuItem)var22.next();
            actualNodeWidth = prefNodeWidth;
            button.resizeRelocate(currentX, contentY, actualNodeWidth, effectiveHeight);
        }
    }
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        double prefHeight = 0.0D;
        Iterator var13 = this.getChildren().iterator();

        while(var13.hasNext()) {
            Node node = (Node)var13.next();
            if (node.isManaged()) {
                prefHeight = Math.max(node.prefHeight(width), prefHeight);
            }
        }

        return topInset + prefHeight + bottomInset;
    }

    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        double prefWidth = 0.0D;
        double spacing = 10.0D;
        Iterator var15 = this.getChildren().iterator();

        while(var15.hasNext()) {
            Node node = (Node)var15.next();
            if (node.isManaged()) {
                prefWidth += node.prefWidth(height) + spacing;
            }
        }

        return leftInset + prefWidth + rightInset;
    }
    private void initializeListener(LeftNavigation leftNavigation) {
        for (LeftNavigationMenuItem menuItem : leftNavigation.getMenuItems()) {
            addSelectionListener(menuItem);
        }
    }

    private void addSelectionListener(LeftNavigationMenuItem leftNavigationMenuItem) {
        MenuItemSelectedListener changeListener = new MenuItemSelectedListener(leftNavigationMenuItem);
        leftNavigationMenuItem.selectedProperty().addListener(changeListener);
        MENU_ITEM_LISTENER.put(leftNavigationMenuItem, changeListener);
    }

    private void removeSelectionListener(LeftNavigationMenuItem leftNavigationMenuItem) {
        ChangeListener<Boolean> changeListener = MENU_ITEM_LISTENER.get(leftNavigationMenuItem);
        if (changeListener != null) {
            leftNavigationMenuItem.selectedProperty().removeListener(changeListener);
        }
        MENU_ITEM_LISTENER.remove(leftNavigationMenuItem);
    }

    /**
     * 默认选中
     *
     * @param leftNavigation
     */
    private void addSelectionIfAbsent(LeftNavigation leftNavigation) {
        ObservableList<LeftNavigationMenuItem> menu = leftNavigation.getMenuItems();
        Iterator<LeftNavigationMenuItem> iterator = menu.iterator();
        boolean selected = false;
        while (iterator.hasNext()) {
            if (iterator.next().isSelected()) {
                selected = true;
                break;
            }
        }
        if (!selected && !menu.isEmpty()) {
            menu.get(0).setSelected(true);
        }
    }

    private class MenuItemSelectedListener implements ChangeListener<Boolean> {
        private final LeftNavigationMenuItem menuItem;

        public MenuItemSelectedListener(LeftNavigationMenuItem menuItem) {
            this.menuItem = menuItem;
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue) {
                // 设置选中状态
                getSkinnable().getMenuItems().forEach(menuItem -> menuItem.setSelected(menuItem.equals(this.menuItem)));
                if (getSkinnable().getOnMenuItemSelectedAction() != null) {
                    getSkinnable().getOnMenuItemSelectedAction().accept(menuItem);
                }
            }
        }
    }

}
