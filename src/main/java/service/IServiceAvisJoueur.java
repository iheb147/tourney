package service;

import entities.AvisJoueur;
import entities.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface IServiceAvisJoueur {

    void ajouter(AvisJoueur av, User us) throws SQLException;

    void modifier(int id, AvisJoueur avisModifie, int idr) throws SQLException;

    void supprimer(int id) throws SQLException;

    List<AvisJoueur> recuperer() throws SQLException;

    boolean existeAvis(int id) throws SQLException;
}