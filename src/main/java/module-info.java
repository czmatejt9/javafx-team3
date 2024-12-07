module jabaPaint {
    requires transitive javafx.graphics;
    requires java.base;
    requires java.logging;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;
    requires java.desktop;
    requires javafx.swing;

    exports launcher;
    exports controllers;
    opens controllers to javafx.fxml;

}