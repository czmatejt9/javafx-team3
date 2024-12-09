package classes;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GrayscaleFilter {
    public void grayScale(Stage primaryStage) {
        // Load an image
        Image image = new Image("file:example.jpg"); // Replace with your image path
        ImageView imageView = new ImageView(image);

        // Apply grayscale filter using ColorAdjust
        ColorAdjust grayscale = new ColorAdjust();
        grayscale.setSaturation(-1); // Set saturation to -1 for grayscale effect
        imageView.setEffect(grayscale);

        // Set up the scene
        StackPane root = new StackPane(imageView);
        Scene scene = new Scene(root, 600, 400);

        primaryStage.setTitle("Grayscale Filter Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
