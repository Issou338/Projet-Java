/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MoteurDeJeu;

/**
 *
 * @author cano28
 */

import com.mycompany.projet_jeu.model.Gamedata;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationScenarioTest {

    @Test
    void doitChargerEtJouerUnScenarioComplet() throws Exception {

        // 1. Création d’un vrai scénario externe
        Path dossier = creerScenarioComplet();

        // 2. Chargement via le loader
        Gamedata jeu = Charger_Jeu.chargerJeu(dossier.toString());
        assertNotNull(jeu);

        // 3. Création du moteur
        MoteurDeJeu moteur = new MoteurDeJeu(jeu);

        assertFalse(moteur.estTermine());

        // 4. Étape 1 : bonne réponse → p2
        moteur.passerAuPuzzleSuivant("123");
        assertFalse(moteur.estTermine());

        // 5. Étape 2 : bonne réponse → victoire
        moteur.passerAuPuzzleSuivant("true");

        // 6. Vérifications finales
        assertTrue(moteur.estTermine());
        assertTrue(moteur.estVictoire());
        assertEquals(2, moteur.getNbEnigmesResolues());
    }

    private Path creerScenarioComplet() throws IOException {

        Path dossier = Files.createTempDirectory("scenario_integration");

        // dossier images
        Files.createDirectory(dossier.resolve("images"));

        // fausses images
        Files.writeString(dossier.resolve("images").resolve("img1.png"), "fake");
        Files.writeString(dossier.resolve("images").resolve("img2.png"), "fake");

        // JSON complet avec 2 étapes
        String json = """
        {
          "schemaVersion":"1.0",
          "id":"integration_test",
          "title":"Scenario complet",
          "author":"Test",
          "start":"p1",
          "puzzles":{
            "p1":{
              "type":"text",
              "prompt":"Entre le code",
              "image":"images/img1.png",
              "routes":{"123":"p2","*":"end_lose"}
            },
            "p2":{
              "type":"boolean",
              "prompt":"La réponse est-elle vraie ?",
              "image":"images/img2.png",
              "routes":{"true":"end_win","false":"end_lose"}
            }
          }
        }
        """;

        Files.writeString(dossier.resolve("manifest.json"), json);

        return dossier;
    }
}
