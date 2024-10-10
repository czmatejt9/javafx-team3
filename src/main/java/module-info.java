module com.javafx_team3 {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.javafx_team3 to javafx.fxml;
    exports com.javafx_team3;
}
