package controller;

import entities.AvisJoueur;
import esprit.project.tools.MyDB;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.Rating;
import service.ServiceAvisJoueur;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShowAvis implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(ShowAvis.class.getName());

    ServiceAvisJoueur serviceAvisJoueur = ServiceAvisJoueur.getInstance();
    public static ResultSet rs;
    public static Integer idSelct;
    public static Integer idAvisSelct;
    public static String commSelect;
    public static float noteSelect;
    public static String dateSelec;

    @FXML
    private VBox mainContainer;

    @FXML
    private AnchorPane Pane;

    public void getGrid() {
        List<AvisJoueur> avisList;
        try {
            avisList = serviceAvisJoueur.recuperer();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving avis list", e);
            return;
        }

        for (AvisJoueur avis : avisList) {
            TitledPane titledPane = new TitledPane();
            titledPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                        int clickedIdAvis = avis.getIdAvis();
                        LOGGER.log(Level.INFO, "Clicked on TitledPane with idAvis: {0}", clickedIdAvis);

                        try (Connection connection1 = MyDB.getInsatnce().getConnection();
                             PreparedStatement pre = connection1.prepareStatement("SELECT * FROM avisjoueur WHERE idAvis = ?")) {
                            pre.setInt(1, clickedIdAvis);
                            try (ResultSet resultSet = pre.executeQuery()) {
                                if (resultSet.next()) {
                                    idAvisSelct = resultSet.getInt("idAvis");
                                    LOGGER.log(Level.INFO, "idAvisSelct: {0}", idAvisSelct);
                                    idSelct = resultSet.getInt("idJoueur");
                                    LOGGER.log(Level.INFO, "idSelct: {0}", idSelct);
                                    commSelect = resultSet.getString("commentaire");
                                    LOGGER.log(Level.INFO, "commSelect: {0}", commSelect);
                                    noteSelect = resultSet.getFloat("note");
                                    LOGGER.log(Level.INFO, "noteSelect: {0}", noteSelect);
                                    dateSelec = resultSet.getString("dateAvis");
                                    LOGGER.log(Level.INFO, "dateSelec: {0}", dateSelec);

                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateAvis.fxml"));
                                    Parent root = loader.load();
                                    Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                                    stage.setScene(new Scene(root));
                                    stage.setTitle("Gestion Avis");
                                    stage.show();
                                }
                            }
                        } catch (SQLException e) {
                            LOGGER.log(Level.SEVERE, "Database error while fetching avis details", e);
                        } catch (IOException e) {
                            LOGGER.log(Level.SEVERE, "Error loading UpdateAvis.fxml", e);
                        }
                    }
                }
            });

            String css = "-fx-font-size: 16px; -fx-font-family: 'Helvetica Neue'; -fx-text-fill: #333333; -fx-background-color: #FFFFFF; -fx-padding: 10px; -fx-border-radius: 5px; -fx-background-radius: 5px; ";
            titledPane.setText("Avis " + avis.getIdAvis());
            titledPane.setStyle(css);

            GridPane gridPane = new GridPane();
            gridPane.setStyle(css);

            Label labelCommentaire = new Label("Commentaire : " + avis.getCommentaire());
            labelCommentaire.setStyle(css);
            gridPane.add(labelCommentaire, 0, 1);

            Rating rating = new Rating();
            rating.setRating(avis.getNote());
            rating.setDisable(true);
            rating.setPrefSize(50, 25);
            gridPane.add(new Label(""), 1, 2);
            gridPane.add(rating, 1, 2);

            Label labelJoueur = new Label("Joueur : " + avis.getUser().getName());
            labelJoueur.setStyle(css);
            gridPane.add(labelJoueur, 0, 3);

            Label labelDate = new Label("Date : " + avis.getDateAvis());
            labelDate.setStyle(css);
            gridPane.add(labelDate, 0, 4);

            titledPane.setContent(gridPane);
            mainContainer.getChildren().add(titledPane);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getGrid();
    }
}