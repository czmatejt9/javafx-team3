module com.javafx_team3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.javafx_team3 to javafx.fxml;
    exports com.javafx_team3;
}
