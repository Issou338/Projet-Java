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

public class Charger_JeuTest {

    @Test
    void doitChargerUnScenarioValide() throws Exception {
        Path dossier = creerScenarioValide();

        Gamedata jeu = Charger_Jeu.chargerJeu(dossier.toString());

        assertNotNull(jeu);
        assertEquals("test", jeu.getId());
        assertEquals("p1", jeu.getStart());
        assertNotNull(jeu.getPuzzle("p1"));
    }

    @Test
    void doitRefuserManifestAbsent() throws IOException {
        Path dossier = Files.createTempDirectory("scenario_test");
        Files.createDirectory(dossier.resolve("images"));

        ChargementJeuException exception = assertThrows(
                ChargementJeuException.class,
                () -> Charger_Jeu.chargerJeu(dossier.toString())
        );

        assertTrue(exception.getMessage().contains("manifest.json"));
    }

    @Test
    void doitRefuserImageAbsente() throws IOException {
        Path dossier = Files.createTempDirectory("scenario_test");
        Files.createDirectory(dossier.resolve("images"));

        String json = """
        {
          "schemaVersion":"1.0",
          "id":"test",
          "title":"Scenario image absente",
          "author":"Moi",
          "start":"p1",
          "puzzles":{
            "p1":{
              "type":"text",
              "prompt":"Code ?",
              "image":"images/img.png",
              "routes":{"123":"end_win","*":"end_lose"}
            }
          }
        }
        """;

        Files.writeString(dossier.resolve("manifest.json"), json);

        ChargementJeuException exception = assertThrows(
                ChargementJeuException.class,
                () -> Charger_Jeu.chargerJeu(dossier.toString())
        );

        assertTrue(exception.getMessage().contains("Image introuvable"));
    }

    @Test
    void doitRefuserRouteVersEnigmeInexistante() throws IOException {
        Path dossier = Files.createTempDirectory("scenario_test");
        Files.createDirectory(dossier.resolve("images"));
        Files.writeString(dossier.resolve("images").resolve("img.png"), "fake");

        String json = """
        {
          "schemaVersion":"1.0",
          "id":"test",
          "title":"Scenario invalide",
          "author":"Moi",
          "start":"p1",
          "puzzles":{
            "p1":{
              "type":"text",
              "prompt":"Code ?",
              "image":"images/img.png",
              "routes":{"123":"p99","*":"end_lose"}
            }
          }
        }
        """;

        Files.writeString(dossier.resolve("manifest.json"), json);

        ChargementJeuException exception = assertThrows(
                ChargementJeuException.class,
                () -> Charger_Jeu.chargerJeu(dossier.toString())
        );

        assertTrue(exception.getMessage().contains("Route invalide"));
    }

    @Test
    void doitRefuserTypeNonSupporte() throws IOException {
        Path dossier = Files.createTempDirectory("scenario_test");
        Files.createDirectory(dossier.resolve("images"));
        Files.writeString(dossier.resolve("images").resolve("img.png"), "fake");

        String json = """
        {
          "schemaVersion":"1.0",
          "id":"test",
          "title":"Scenario invalide",
          "author":"Moi",
          "start":"p1",
          "puzzles":{
            "p1":{
              "type":"slide",
              "prompt":"Code ?",
              "image":"images/img.png",
              "routes":{"123":"end_win","*":"end_lose"}
            }
          }
        }
        """;

        Files.writeString(dossier.resolve("manifest.json"), json);

        ChargementJeuException exception = assertThrows(
                ChargementJeuException.class,
                () -> Charger_Jeu.chargerJeu(dossier.toString())
        );

        assertTrue(exception.getMessage().contains("Type d’énigme non supporté")
                || exception.getMessage().contains("Type d'énigme non supporté"));
    }

    private Path creerScenarioValide() throws IOException {
        Path dossier = Files.createTempDirectory("scenario_test");
        Files.createDirectory(dossier.resolve("images"));
        Files.writeString(dossier.resolve("images").resolve("img.png"), "fake");

        String json = """
        {
          "schemaVersion":"1.0",
          "id":"test",
          "title":"Scenario valide",
          "author":"Moi",
          "start":"p1",
          "puzzles":{
            "p1":{
              "type":"text",
              "prompt":"Code ?",
              "image":"images/img.png",
              "routes":{"123":"end_win","*":"end_lose"}
            }
          }
        }
        """;

        Files.writeString(dossier.resolve("manifest.json"), json);
        return dossier;
    }
}