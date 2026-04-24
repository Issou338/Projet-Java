package MoteurDeJeu;

import com.mycompany.projet_jeu.model.Gamedata;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de tests unitaires de la classe {@link Charger_Jeu}.
 * <p>
 * Elle vérifie le chargement d’un scénario valide ainsi que le refus
 * des scénarios invalides : manifest absent, image absente, dossier
 * images absent, route invalide, type non supporté et image manquante
 * dans une énigme.
 * </p>
 *
 * @author cano28
 */
public class Charger_JeuTest {

    /**
     * Vérifie qu’un scénario correctement structuré est chargé sans erreur.
     *
     * @throws Exception si une erreur survient lors de la création ou du chargement du scénario
     */
    @Test
    void doitChargerUnScenarioValide() throws Exception {
        Path dossier = creerScenarioValide();

        Gamedata jeu = Charger_Jeu.chargerJeu(dossier.toString());

        assertNotNull(jeu);
        assertEquals("test", jeu.getId());
        assertEquals("p1", jeu.getStart());
        assertNotNull(jeu.getPuzzle("p1"));
    }

    /**
     * Vérifie que le chargement échoue lorsque le fichier manifest.json est absent.
     *
     * @throws IOException si une erreur survient lors de la création du dossier temporaire
     */
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

    /**
     * Vérifie que le chargement échoue lorsqu’une image référencée
     * dans le manifest.json est absente du dossier images.
     *
     * @throws IOException si une erreur survient lors de la création des fichiers temporaires
     */
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

    /**
     * Vérifie que le chargement échoue lorsqu’une route pointe vers
     * une énigme inexistante.
     *
     * @throws IOException si une erreur survient lors de la création des fichiers temporaires
     */
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

    /**
     * Vérifie que le chargement échoue lorsqu’un scénario contient
     * un type d’énigme non supporté par le moteur.
     *
     * @throws IOException si une erreur survient lors de la création des fichiers temporaires
     */
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

    /**
     * Vérifie que le chargement échoue lorsque le dossier obligatoire images
     * est absent du scénario.
     *
     * @throws IOException si une erreur survient lors de la création des fichiers temporaires
     */
    @Test
    void doitRefuserDossierImagesAbsent() throws IOException {
        Path dossier = Files.createTempDirectory("scenario_test");

        String json = """
        {
          "schemaVersion":"1.0",
          "id":"test",
          "title":"Scenario sans images",
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

        assertTrue(exception.getMessage().contains("Dossier images"));
    }

    /**
     * Vérifie que le chargement échoue lorsqu’une énigme ne possède
     * pas de champ image dans le manifest.json.
     *
     * @throws IOException si une erreur survient lors de la création des fichiers temporaires
     */
    @Test
    void doitRefuserImageManquanteDansPuzzle() throws IOException {
        Path dossier = Files.createTempDirectory("scenario_test");
        Files.createDirectory(dossier.resolve("images"));

        String json = """
        {
          "schemaVersion":"1.0",
          "id":"test",
          "title":"Scenario image manquante",
          "author":"Moi",
          "start":"p1",
          "puzzles":{
            "p1":{
              "type":"text",
              "prompt":"Code ?",
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

        assertTrue(exception.getMessage().contains("Image manquante"));
    }

    /**
     * Crée un scénario temporaire valide utilisé par les tests.
     *
     * @return le chemin du dossier temporaire contenant le scénario valide
     * @throws IOException si une erreur survient lors de la création des fichiers
     */
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