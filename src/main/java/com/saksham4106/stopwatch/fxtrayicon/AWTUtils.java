package com.saksham4106.stopwatch.fxtrayicon;


import java.awt.MenuItem;
import java.util.StringJoiner;
import javafx.application.Platform;
import javafx.event.ActionEvent;

/*
source: https://github.com/dustinkredmond/FXTrayIcon
 */
public class AWTUtils {
    public AWTUtils() {
    }

    protected static MenuItem convertFromJavaFX(javafx.scene.control.MenuItem fxItem) throws UnsupportedOperationException {
        MenuItem awtItem = new MenuItem(fxItem.getText());
        StringJoiner sj = new StringJoiner(",");
        if (fxItem.getGraphic() != null) {
            sj.add("setGraphic()");
        }

        if (fxItem.getAccelerator() != null) {
            sj.add("setAccelerator()");
        }

        if (fxItem.getCssMetaData().size() > 0) {
            sj.add("getCssMetaData().add()");
        }

        if (fxItem.getOnMenuValidation() != null) {
            sj.add("setOnMenuValidation()");
        }

        if (fxItem.getStyle() != null) {
            sj.add("setStyle()");
        }

        String errors = sj.toString();
        if (!errors.isEmpty()) {
            throw new UnsupportedOperationException(String.format("The following methods were called on the passed JavaFX MenuItem (%s), these methods are notsupported by FXTrayIcon.", errors));
        } else {
            if (fxItem.getOnAction() != null) {
                awtItem.addActionListener((e) -> {
                    Platform.runLater(() -> {
                        fxItem.getOnAction().handle(new ActionEvent());
                    });
                });
            }

            awtItem.setEnabled(!fxItem.isDisable());
            return awtItem;
        }
    }
}

