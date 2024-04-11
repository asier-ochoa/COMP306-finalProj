import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class PuzzleApplication extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws Exception {
        var startMenuXml = FXMLLoader.load(new File("src/main/resources/StartMenu.fxml").toURI().toURL());
        stage.setScene(new Scene((Parent)startMenuXml));
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
