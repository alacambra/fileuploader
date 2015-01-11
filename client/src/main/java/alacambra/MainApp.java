package alacambra;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataWriter;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApp extends Application {

    private Desktop desktop = Desktop.getDesktop();
    private static String endpoint = "http://localhost:8080/files/r/file/";

    Logger logger  =Logger.getLogger(this.getClass().getName());

    @Override
    public void start(final Stage stage) {
        stage.setTitle("File uploader");

        final FileChooser fileChooser = new FileChooser();

        final Button openMultipleButton = new Button("Select files to upload.");

        final GridPane inputGridPane = new GridPane();

        GridPane.setConstraints(openMultipleButton, 1, 0);
        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        inputGridPane.addRow(0,openMultipleButton);

        final Pane rootGroup = new VBox();
        rootGroup.setMinHeight(300L);
        rootGroup.setMinWidth(300L);
        rootGroup.getChildren().addAll(inputGridPane);

        stage.setScene(new Scene(rootGroup));

        openMultipleButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        List<File> list =
                                fileChooser.showOpenMultipleDialog(stage);
                        if (list != null) {
                            for (File file : list) {
                                Label processInfo = new Label("Starting upload of " + file.getName());
                                rootGroup.getChildren().add(processInfo);
                                Response r = upload(file);
                                if(r.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                                    processInfo.setText(file.getName() + " successfully uploaded");
                                } else {
                                    processInfo.setText(file.getName() + " not uploaded " + r.getStatus() + r.readEntity(String.class));
                                }
                            }
                        }
                    }
                });
        stage.show();
    }

    private void loadEndpoint(){

    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    public Response upload(File file) {

        ResteasyClient client = new ResteasyClientBuilder().build();
        client.register(MultipartFormDataWriter.class);

        ResteasyWebTarget target = client.target(endpoint);

        MultipartFormDataOutput mdo = new MultipartFormDataOutput();

        try {
            mdo.addFormData("file", new FileInputStream(file), MediaType.APPLICATION_OCTET_STREAM_TYPE, file.getName());
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, null, e);
        }

        GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(mdo) {};
        Response r = target.request().post(Entity.entity(entity, MediaType.MULTIPART_FORM_DATA_TYPE));
        logger.log(Level.INFO, String.valueOf(r.getStatus()) + r.getHeaders());
        return r;
    }
}
