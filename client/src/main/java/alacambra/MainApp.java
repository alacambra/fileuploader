package alacambra;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApp extends Application {

    private Desktop desktop = Desktop.getDesktop();
    private static final String endpoint = "http://localhost:8080/files/r/file/";

    Logger logger  =Logger.getLogger(this.getClass().getName());

    @Override
    public void start(final Stage stage) {
        stage.setTitle("File uploader");

        final FileChooser fileChooser = new FileChooser();

        final Button openMultipleButton = new Button("Select files to upload.");

        openMultipleButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        List<File> list =
                                fileChooser.showOpenMultipleDialog(stage);
                        if (list != null) {
                            for (File file : list) {
                                upload(file);
                            }
                        }
                    }
                });


        final GridPane inputGridPane = new GridPane();

        GridPane.setConstraints(openMultipleButton, 1, 0);
        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        inputGridPane.getChildren().addAll(openMultipleButton);

        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputGridPane);
        rootGroup.setPadding(new Insets(12, 12, 12, 12));

        stage.setScene(new Scene(rootGroup));
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException e) {
            logger.log(Level.SEVERE, null, e);
        }
    }

    public void upload(File file) {

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(endpoint);

        MultipartFormDataOutput mdo = new MultipartFormDataOutput();

        try {
            mdo.addFormData("file", new FileInputStream(file), MediaType.APPLICATION_OCTET_STREAM_TYPE, file.getName());
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, null, e);
        }

        GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(mdo) {};
        Response r = target.request().post(Entity.entity(entity, MediaType.MULTIPART_FORM_DATA_TYPE));
        logger.log(Level.SEVERE, String.valueOf(r.getStatus()) + r.getHeaders());
    }
}
