package com.saksham4106.stopwatch.fxtrayicon;


import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


/*
source: https://github.com/dustinkredmond/FXTrayIcon
Couldn't Use build system because modules are a jerk
*and very slight modifications*
 */
public class FXTrayIcon {
    private final SystemTray tray;
    private Stage parentStage;
    private String appTitle;
    protected final TrayIcon trayIcon;
    private final PopupMenu popupMenu;
    private boolean addExitMenuItem;
    private boolean addTitleMenuItem;
    private boolean isMac;
    private final ActionListener stageShowListener;

    private MouseListener getPrimaryClickListener(final EventHandler<ActionEvent> e) {
        return new MouseListener() {
            public void mouseClicked(MouseEvent me) {
                Platform.runLater(() -> {
                    e.handle(new ActionEvent());
                });
            }

            public void mousePressed(MouseEvent ignored) {
            }

            public void mouseReleased(MouseEvent ignored) {
            }

            public void mouseEntered(MouseEvent ignored) {
            }

            public void mouseExited(MouseEvent ignored) {
            }
        };
    }

    public FXTrayIcon(Stage parentStage, URL iconImagePath, int iconWidth, int iconHeight) {
        this(parentStage, loadImageFromFile(iconImagePath, iconWidth, iconHeight));
    }

    public FXTrayIcon(Stage parentStage, URL iconImagePath) {
        this(parentStage, loadImageFromFile(iconImagePath));
    }

    public FXTrayIcon(Stage parentStage) {
        this(parentStage, loadImageFromFile((URL)null));
    }

    public FXTrayIcon(Stage parentStage, Image icon) {
        this.popupMenu = new PopupMenu();
        this.addExitMenuItem = false;
        this.addTitleMenuItem = false;
        this.stageShowListener = (e) -> {
            if (this.parentStage != null) {
                Stage var10000 = this.parentStage;
                Platform.runLater(var10000::show);
            }

        };
        Objects.requireNonNull(parentStage, "parentStage must not be null");
        Objects.requireNonNull(icon, "icon must not be null");
        this.ensureSystemTraySupported();
        this.tray = SystemTray.getSystemTray();
        Platform.setImplicitExit(false);
        this.attemptSetSystemLookAndFeel();
        this.parentStage = parentStage;

        this.trayIcon = new TrayIcon(icon.getScaledInstance(SystemTray.getSystemTray().getTrayIconSize().width
                , -1, Image.SCALE_SMOOTH), parentStage.getTitle(), this.popupMenu);
    }

    protected FXTrayIcon(FXTrayIcon.Builder build) {
        this(build.parentStage, build.icon);
        this.parentStage = build.parentStage;
        this.isMac = build.isMac;
        this.appTitle = build.appTitle;
        this.addExitMenuItem = build.addExitMenuItem;
        this.addTitleMenuItem = build.addTitleMenuItem;
        if (build.showTrayIcon) {
            this.show();
        }

        if (!build.tooltip.equals("")) {
            this.setTooltip(build.tooltip);
        }

        if (build.event != null) {
            this.setOnAction(build.event);
        }

        for(int x = 0; x <= build.index; ++x) {
            if (build.menuItemMap.containsKey(x)) {
                this.addMenuItem((MenuItem)build.menuItemMap.get(x));
            } else if (build.separatorIndexList.contains(x)) {
                this.addSeparator();
            }
        }

    }

    protected final TrayIcon getTrayIcon() {
        return this.trayIcon;
    }

    private void ensureSystemTraySupported() {
        if (!SystemTray.isSupported()) {
            throw new UnsupportedOperationException("SystemTray icons are not supported by the current desktop environment.");
        }
    }

    private void attemptSetSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException | ClassNotFoundException var2) {
        }

    }

    private static Image loadImageFromFile(URL iconImagePath) {
        URL defaultIconImagePath = FXTrayIcon.class.getResource("FXIconRedWhite.png");
        return isMac() ? loadImageFromFile(iconImagePath == null ? defaultIconImagePath : iconImagePath, 22, 22) : loadImageFromFile(iconImagePath == null ? defaultIconImagePath : iconImagePath, 16, 16);
    }

    private static Image loadImageFromFile(URL iconImagePath, int iconWidth, int iconHeight) {
        try {
            return ImageIO.read(iconImagePath).getScaledInstance(iconWidth, iconHeight, 4);
        } catch (IOException var4) {
            throw new IllegalStateException("Unable to read the Image at the provided path: " + iconImagePath, var4);
        }
    }

    private static boolean isMac() {
        return System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("mac");
    }

    public void show() {
        SwingUtilities.invokeLater(() -> {
            try {
                this.tray.add(this.trayIcon);
                if (this.addTitleMenuItem) {
                    String miTitle = this.appTitle != null ? this.appTitle : (this.parentStage != null && this.parentStage.getTitle() != null && !this.parentStage.getTitle().isEmpty() ? this.parentStage.getTitle() : "Show Application");
                    java.awt.MenuItem miStage = new java.awt.MenuItem(miTitle);
                    miStage.setFont(Font.decode((String)null).deriveFont(Font.BOLD));
                    miStage.addActionListener((e) -> {
                        Platform.runLater(() -> {
                            if (this.parentStage != null) {
                                this.parentStage.show();
                            }

                        });
                    });
                    this.popupMenu.insert(miStage, 0);
                }

                if (this.addExitMenuItem) {
                    java.awt.MenuItem miExit = new java.awt.MenuItem("Exit Application");
                    miExit.addActionListener((e) -> {
                        this.tray.remove(this.trayIcon);
                        Platform.exit();
                        System.exit(0);
                    });
                    this.popupMenu.add(miExit);
                }

                this.trayIcon.addActionListener(this.stageShowListener);
            } catch (AWTException var3) {
                throw new IllegalStateException("Unable to add TrayIcon", var3);
            }
        });
    }

    public void setOnAction(EventHandler<ActionEvent> e) {
        if (this.trayIcon.getMouseListeners().length >= 1) {
            this.trayIcon.removeMouseListener(this.trayIcon.getMouseListeners()[0]);
        }

        this.trayIcon.addMouseListener(this.getPrimaryClickListener(e));
    }

    public void addExitItem(boolean addExitMenuItem) {
        this.addExitMenuItem = addExitMenuItem;
    }

    public void addTitleItem(boolean addTitleMenuItem) {
        this.addTitleMenuItem = addTitleMenuItem;
    }

    public void removeMenuItem(int index) {
        EventQueue.invokeLater(() -> {
            this.popupMenu.remove(index);
        });
    }

    public void removeMenuItem(MenuItem fxMenuItem) {
        EventQueue.invokeLater(() -> {
            java.awt.MenuItem toBeRemoved = null;

            for(int i = 0; i < this.popupMenu.getItemCount(); ++i) {
                java.awt.MenuItem awtItem = this.popupMenu.getItem(i);
                if (awtItem.getLabel().equals(fxMenuItem.getText()) || awtItem.getName().equals(fxMenuItem.getText())) {
                    toBeRemoved = awtItem;
                }
            }

            if (toBeRemoved != null) {
                this.popupMenu.remove(toBeRemoved);
            }

        });
    }

    public void addSeparator() {
        EventQueue.invokeLater(this.popupMenu::addSeparator);
    }

    public void insertSeparator(int index) {
        EventQueue.invokeLater(() -> {
            this.popupMenu.insertSeparator(index);
        });
    }

    public void addMenuItem(MenuItem menuItem) {
        EventQueue.invokeLater(() -> {
            if (menuItem instanceof Menu) {
                this.addMenu((Menu)menuItem);
            } else if (this.isNotUnique(menuItem)) {
                throw new UnsupportedOperationException("Menu Item labels must be unique.");
            } else {
                this.popupMenu.add(AWTUtils.convertFromJavaFX(menuItem));
            }
        });
    }

    public void addMenuItems(MenuItem... menuItems) {
        EventQueue.invokeLater(() -> {
            MenuItem[] var2 = menuItems;
            int var3 = menuItems.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                MenuItem menuItem = var2[var4];
                if (menuItem instanceof Menu) {
                    this.addMenu((Menu)menuItem);
                    return;
                }

                if (this.isNotUnique(menuItem)) {
                    throw new UnsupportedOperationException("Menu Item labels must be unique.");
                }

                this.popupMenu.add(AWTUtils.convertFromJavaFX(menuItem));
            }

        });
    }

    public void insertMenuItem(MenuItem menuItem, int index) {
        EventQueue.invokeLater(() -> {
            if (this.isNotUnique(menuItem)) {
                throw new UnsupportedOperationException("Menu Item labels must be unique.");
            } else {
                this.popupMenu.insert(AWTUtils.convertFromJavaFX(menuItem), index);
            }
        });
    }

    public java.awt.MenuItem getMenuItem(int index) {
        return this.popupMenu.getItem(index);
    }

    public void setTrayIconTooltip(String tooltip) {
        EventQueue.invokeLater(() -> {
            this.trayIcon.setToolTip(tooltip);
        });
    }

    public void setApplicationTitle(String title) {
        this.appTitle = title;
    }

    public void hide() {
        EventQueue.invokeLater(() -> {
            this.tray.remove(this.trayIcon);
            Platform.setImplicitExit(true);
        });
    }

    public boolean isMenuShowing() {
        Iterator it = Arrays.stream(this.tray.getTrayIcons()).iterator();

        TrayIcon ti;
        do {
            if (!it.hasNext()) {
                return false;
            }

            ti = (TrayIcon)it.next();
        } while(!ti.equals(this.trayIcon));

        return ti.getPopupMenu().isEnabled();
    }

    public boolean isShowing() {
        return ((List)Arrays.stream(this.tray.getTrayIcons()).collect(Collectors.toList())).contains(this.trayIcon);
    }

    public void showInfoMessage(String title, String message) {
        if (this.isMac) {
            this.showMacAlert(title, message, "Information");
        } else {
            EventQueue.invokeLater(() -> {
                this.trayIcon.displayMessage(title, message, MessageType.INFO);
            });
        }

    }

    public void showInfoMessage(String message) {
        this.showInfoMessage((String)null, message);
    }

    public void showWarningMessage(String title, String message) {
        if (this.isMac) {
            this.showMacAlert(title, message, "Warning");
        } else {
            EventQueue.invokeLater(() -> {
                this.trayIcon.displayMessage(title, message, MessageType.WARNING);
            });
        }

    }

    public void showWarningMessage(String message) {
        this.showWarningMessage((String)null, message);
    }

    public void showErrorMessage(String title, String message) {
        if (this.isMac) {
            this.showMacAlert(title, message, "Error");
        } else {
            EventQueue.invokeLater(() -> {
                this.trayIcon.displayMessage(title, message, MessageType.ERROR);
            });
        }

    }

    public void showErrorMessage(String message) {
        this.showErrorMessage((String)null, message);
    }

    public void showMessage(String title, String message) {
        if (this.isMac) {
            this.showMacAlert(title, message, "Message");
        } else {
            EventQueue.invokeLater(() -> {
                this.trayIcon.displayMessage(title, message, MessageType.NONE);
            });
        }

    }

    public void showMessage(String message) {
        this.showMessage((String)null, message);
    }

    public void clear() {
        PopupMenu var10000 = this.popupMenu;
        EventQueue.invokeLater(var10000::removeAll);
    }

    public static boolean isSupported() {
        return Desktop.isDesktopSupported() && SystemTray.isSupported();
    }

    public int getMenuItemCount() {
        return this.popupMenu.getItemCount();
    }

    public void setGraphic(javafx.scene.image.Image img) {
        this.setGraphic((Image) SwingFXUtils.fromFXImage(img, (BufferedImage)null));
    }

    public void setGraphic(File file) {
        javafx.scene.image.Image img = new javafx.scene.image.Image(file.getAbsolutePath());
        this.setGraphic((Image)SwingFXUtils.fromFXImage(img, (BufferedImage)null));
    }

    public void setGraphic(Image img) {
        this.trayIcon.setImage(img);
    }

    public void setTooltip(String tooltip) {
        this.trayIcon.setToolTip(tooltip);
    }

    private void showMacAlert(String subTitle, String message, String title) {
        String execute = String.format("display notification \"%s\" with title \"%s\" subtitle \"%s\"", message != null ? message : "", title != null ? title : "", subTitle != null ? subTitle : "");

        try {
            Runtime.getRuntime().exec(new String[]{"osascript", "-e", execute});
        } catch (IOException var6) {
            throw new UnsupportedOperationException("Cannot run osascript with given parameters.");
        }
    }

    private void addMenu(Menu menu) {
        EventQueue.invokeLater(() -> {
            java.awt.Menu awtMenu = new java.awt.Menu(menu.getText());
            menu.getItems().forEach((subItem) -> {
                awtMenu.add(AWTUtils.convertFromJavaFX(subItem));
            });
            this.popupMenu.add(awtMenu);
        });
    }

    private boolean isNotUnique(MenuItem fxItem) {
        boolean result = true;

        for(int i = 0; i < this.popupMenu.getItemCount(); ++i) {
            if (this.popupMenu.getItem(i).getLabel().equals(fxItem.getText())) {
                result = false;
                break;
            }
        }

        return !result;
    }

    public static class Builder {
        private final Stage parentStage;
        private URL iconImagePath;
        private int iconWidth;
        private int iconHeight;
        private Image icon;
        private boolean isMac;
        private String tooltip = "";
        private String appTitle;
        private boolean addExitMenuItem = false;
        private boolean addTitleMenuItem = false;
        private EventHandler<ActionEvent> event;
        private final Map<Integer, MenuItem> menuItemMap = new HashMap();
        private final List<Integer> separatorIndexList = new ArrayList();
        private boolean showTrayIcon = false;
        private boolean useDefaultIcon = false;
        private Integer index = 0;
        private final URL defaultIconPath = this.getClass().getResource("FXIconRedWhite.png");

        public Builder(Stage parentStage, URL iconImagePath, int iconWidth, int iconHeight) {
            this.parentStage = parentStage;
            this.iconImagePath = iconImagePath;
            this.iconWidth = iconWidth;
            this.iconHeight = iconHeight;
        }

        public Builder(Stage parentStage, URL iconImagePath) {
            this.parentStage = parentStage;
            this.iconImagePath = iconImagePath;
        }

        public Builder(Stage parentStage) {
            this.parentStage = parentStage;
            this.useDefaultIcon = true;
        }

        public FXTrayIcon.Builder menuItem(String label, EventHandler<ActionEvent> eventHandler) {
            MenuItem menuItem = new MenuItem(label);
            menuItem.setOnAction(eventHandler);
            this.menuItemMap.put(this.index, menuItem);
            Integer var4 = this.index;
            Integer var5 = this.index = this.index + 1;
            return this;
        }

        public  FXTrayIcon.Builder menuItem(MenuItem menuItem) {
            this.menuItemMap.put(this.index, menuItem);
            Integer var2 = this.index;
            Integer var3 = this.index = this.index + 1;
            return this;
        }

        public  FXTrayIcon.Builder menuItems(MenuItem... menuItems) {
            MenuItem[] var2 = menuItems;
            int var3 = menuItems.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                MenuItem menuItem = var2[var4];
                this.menuItemMap.put(this.index, menuItem);
                Integer var6 = this.index;
                Integer var7 = this.index = this.index + 1;
            }

            return this;
        }

        public  FXTrayIcon.Builder separator() {
            this.separatorIndexList.add(this.index);
            Integer var1 = this.index;
            Integer var2 = this.index = this.index + 1;
            return this;
        }

        public  FXTrayIcon.Builder toolTip(String tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public  FXTrayIcon.Builder addTitleItem(boolean addTitleMenuItem) {
            this.addTitleMenuItem = addTitleMenuItem;
            return this;
        }

        public  FXTrayIcon.Builder applicationTitle(String appTitle) {
            this.appTitle = appTitle;
            this.addTitleMenuItem = true;
            return this;
        }

        public  FXTrayIcon.Builder addExitMenuItem() {
            this.addExitMenuItem = true;
            return this;
        }

        public  FXTrayIcon.Builder onAction(EventHandler<ActionEvent> event) {
            this.event = event;
            return this;
        }

        public  FXTrayIcon.Builder show() {
            this.showTrayIcon = true;
            return this;
        }

        public  FXTrayIcon build() {
            this.isMac = System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("mac");
            if (this.iconWidth == 0 || this.iconHeight == 0) {
                if (this.isMac) {
                    this.iconWidth = 22;
                    this.iconHeight = 22;
                } else {
                    this.iconWidth = 16;
                    this.iconHeight = 16;
                }
            }

            if (this.useDefaultIcon) {
                this.icon = FXTrayIcon.loadImageFromFile(this.defaultIconPath, this.iconWidth, this.iconHeight);
            } else {
                this.icon = FXTrayIcon.loadImageFromFile(this.iconImagePath, this.iconWidth, this.iconHeight);
            }

            return new FXTrayIcon(this);
        }
    }
}

