package MoteurDeJeu;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author cano28
 */

import com.google.gson.Gson;
import com.mycompany.projet_jeu.model.Gamedata;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MoteurDeJeuTest {

    @Test
    void doitTerminerEnVictoire() {
        String json = """
        {
          "schemaVersion":"1.0",
          "id":"test",
          "title":"Test",
          "author":"Moi",
          "start":"p1",
          "puzzles":{
            "p1":{
              "type":"text",
              "prompt":"Code ?",
              "image":"img.png",
              "routes":{"123":"end_win","*":"end_lose"}
            }
          }
        }
        """;

        Gamedata jeu = new Gson().fromJson(json, Gamedata.class);
        MoteurDeJeu moteur = new MoteurDeJeu(jeu);

        moteur.passerAuPuzzleSuivant("123");

        assertTrue(moteur.estTermine());
        assertTrue(moteur.estVictoire());
        assertEquals(1, moteur.getNbEnigmesResolues());
    }

    @Test
    void doitTerminerEnDefaiteAvecRouteParDefaut() {
        String json = """
        {
          "schemaVersion":"1.0",
          "id":"test",
          "title":"Test",
          "author":"Moi",
          "start":"p1",
          "puzzles":{
            "p1":{
              "type":"text",
              "prompt":"Code ?",
              "image":"img.png",
              "routes":{"123":"end_win","*":"end_lose"}
            }
          }
        }
        """;

        Gamedata jeu = new Gson().fromJson(json, Gamedata.class);
        MoteurDeJeu moteur = new MoteurDeJeu(jeu);

        moteur.passerAuPuzzleSuivant("999");

        assertTrue(moteur.estTermine());
        assertTrue(moteur.estDefaite());
        assertEquals(1, moteur.getNbEnigmesResolues());
    }

    @Test
    void doitAbandonnerLaPartie() {
        String json = """
        {
          "schemaVersion":"1.0",
          "id":"test",
          "title":"Test",
          "author":"Moi",
          "start":"p1",
          "puzzles":{
            "p1":{
              "type":"text",
              "prompt":"Code ?",
              "image":"img.png",
              "routes":{"123":"end_win","*":"end_lose"}
            }
          }
        }
        """;

        Gamedata jeu = new Gson().fromJson(json, Gamedata.class);
        MoteurDeJeu moteur = new MoteurDeJeu(jeu);

        moteur.abandonner();

        assertTrue(moteur.estTermine());
        assertTrue(moteur.estAbandonne());
    }

    @Test
    void doitLeverUneExceptionSiAucuneRouteValide() {
        String json = """
        {
          "schemaVersion":"1.0",
          "id":"test",
          "title":"Test",
          "author":"Moi",
          "start":"p1",
          "puzzles":{
            "p1":{
              "type":"text",
              "prompt":"Code ?",
              "image":"img.png",
              "routes":{"123":"end_win"}
            }
          }
        }
        """;

        Gamedata jeu = new Gson().fromJson(json, Gamedata.class);
        MoteurDeJeu moteur = new MoteurDeJeu(jeu);

        assertThrows(IllegalArgumentException.class, () -> moteur.passerAuPuzzleSuivant("999"));
    }
}