module JavaPaint {
    requires transitive javafx.graphics;
    requires java.base;
    requires java.logging;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;
    requires org.jfxtras.styles.jmetro;
    requires java.desktop;
    requires javafx.swing;

    exports launcher;
    exports controllers;
    opens controllers to javafx.fxml;

}