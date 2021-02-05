import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Box;
import javafx.stage.Stage;

public class Viewer extends Application {
  @Override
  public void start(Stage stage) {
    Box box = new Box(5, 5, 5);
    Group root = new Group(box);
    Scene scene = new Scene(root, 600, 300);
    stage.setTitle("Chunk");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String args[]) {
    launch(args);
  }
}
