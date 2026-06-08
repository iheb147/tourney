package controller;

import entities.Equipe;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import service.ServiceEquipe;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import static controller.DisplayEquipe.*;
import static controller.ShowAvis.idAvisSelct;

public class UpdateEquipe implements Initializable {

    @FXML
    ImageView image;
    @FXML
    Button upload, update, delete, home, JoinTeam;
    @FXML
    TextField Nomequipe;
    @FXML
    DatePicker Dateequipe;
    @FXML
    VBox myBox;

    Date Selcdate = DisplayEquipe.selectedDate;
    String SelcNom = DisplayEquipe.selectedNom;
    String Selcimage = DisplayEquipe.selectedImage;
    int Selcid = DisplayEquipe.selectedId;

    ServiceEquipe serviceEquipe = new ServiceEquipe();

    private String imagePath;

    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            imagePath = selectedFile.getAbsolutePath();
            Image selectedImage = new Image(new File(imagePath).toURI().toString());
            image.setImage(selectedImage);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //-------------------------Hover Side bar ---------------------------------
        FadeTransition ft = new FadeTransition(Duration.millis(300), myBox);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        myBox.setOpacity(0.0);

        myBox.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                ft.setRate(-1);
                ft.play();
            }
        });

        myBox.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                ft.setRate(3);
                ft.play();
            }
        });

        //-------------------------set data in my filed------------------------------------
        Nomequipe.setText(selectedNom);
        LocalDate local = java.sql.Date.valueOf(String.valueOf(Selcdate)).toLocalDate();
        Dateequipe.setValue(local);
        Image selectedImage = new Image(new File(Selcimage).toURI().toString());
        image.setImage(selectedImage);

        //-------------upload my image--------------
        upload.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                chooseImage();
            }
        });

        //------------------------update my team-------------
        update.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String nom = Nomequipe.getText();
                if (nom == null || nom.trim().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Validation Error");
                    alert.setContentText("Team name cannot be empty.");
                    alert.show();
                    return;
                }

                LocalDate localDate = Dateequipe.getValue();
                if (localDate == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Validation Error");
                    alert.setContentText("Please select a date.");
                    alert.show();
                    return;
                }

                Equipe equipe = new Equipe();
                equipe.setNom(nom.trim());
                java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
                equipe.setDateCreation(sqlDate);

                if (imagePath != null) {
                    equipe.setImage(imagePath);
                } else {
                    equipe.setImage(Selcimage);
                }

                try {
                    serviceEquipe.modifier(Selcid, equipe);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setContentText("Merci de modifier votre Equipe");
                    alert.show();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/DisplayEquipe.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) update.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Gestion Equipe");
                    stage.show();

                } catch (SQLException | IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Failed to update team: " + e.getMessage());
                    alert.show();
                }
            }
        });

        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Alert");
                alert.setContentText("Are you sure you want to delete this team?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {
                        serviceEquipe.supprimer(selectedId);
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DisplayEquipe.fxml"));
                        Parent root = loader.load();
                        Stage stage = (Stage) delete.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Gestion Equipe");
                        stage.show();
                    } catch (IOException | SQLException e) {
                        Alert alert2 = new Alert(Alert.AlertType.ERROR);
                        alert2.setTitle("Error");
                        alert2.setContentText("Failed to delete team: " + e.getMessage());
                        alert2.show();
                    }
                }
            }
        });

        //-----------------switch to home ---------------------
        home.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) home.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Home");
                    stage.show();
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Failed to load home: " + e.getMessage());
                    alert.show();
                }
            }
        });
    }
}