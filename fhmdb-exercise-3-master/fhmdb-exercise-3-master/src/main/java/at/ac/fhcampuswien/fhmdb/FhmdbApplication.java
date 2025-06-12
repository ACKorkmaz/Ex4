package at.ac.fhcampuswien.fhmdb;

import at.ac.fhcampuswien.fhmdb.enums.UIComponent;
import at.ac.fhcampuswien.fhmdb.ui.ControllerFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class FhmdbApplication extends Application {
    @Override
    public void start(Stage stage) {
        // 1) Factory erstellen
        ControllerFactory factory = new ControllerFactory();

        // 2) FXMLLoader initialisieren und Factory setzen
        FXMLLoader fxmlLoader = new FXMLLoader(
                FhmdbApplication.class.getResource(UIComponent.HOME.path)
        );
        fxmlLoader.setControllerFactory(factory);

        try {
            // 3) FXML laden
            Parent root = fxmlLoader.load();

            // 4) Scene & Stylesheet wie gehabt
            Scene scene = new Scene(root, 890, 620);
            scene.getStylesheets().add(
                    Objects.requireNonNull(
                            FhmdbApplication.class.getResource("/styles/styles.css")
                    ).toExternalForm()
            );

            // 5) Stage vorbereiten und anzeigen
            stage.setTitle("FHMDb!");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Cannot load scene from " + UIComponent.HOME.path);
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("Path to stylesheet may be corrupt.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
