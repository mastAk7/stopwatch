module com.saksham4106.stopwatch {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.media;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.desktop;
    requires javafx.swing;

    opens com.saksham4106.stopwatch to javafx.fxml;
    exports com.saksham4106.stopwatch;
}