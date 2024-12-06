package launcher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
//import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		// using FXML
		Parent root = FXMLLoader.load(getClass().getResource("/views/javaPaint.fxml"));

		// using Java
		/*
		AnchorPane root = new AnchorPane();
		VBox vbox1 = new VBox();

		root.getChildren().add(vbox1);
		 */
		Scene scene = new Scene(root, 1100, 550);
		
		stage.setScene(scene);
		stage.setTitle("javaPaint");
		stage.setMinWidth(1000);
		stage.setMinHeight(400);
		stage.show();
	}

	public static void main(String[] args) {
		launch(new String[0]);
	}
}
