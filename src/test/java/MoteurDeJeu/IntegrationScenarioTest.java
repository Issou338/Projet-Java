package MoteurDeJeu;

import com.mycompany.projet_jeu.model.Gamedata;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test d’intégration du moteur de jeu.
 * <p>
 * Ce test vérifie le fonctionnement complet de l’application :
 * - chargement d’un scénario externe valide,
 * - progression dans les énigmes,
 * - validation des réponses,
 * - détection de la fin de partie et de la victoire.
 * </p>
 *
 * <p>
 * Contrairement aux tests unitaires, ce test couvre plusieurs composants
 * simultanément : le chargeur de scénario et le moteur de jeu.
 * </p>
 *
 * @author cano28
 */
public class IntegrationScenarioTest {

    /**
     * Vérifie qu’un scénario complet peut être chargé et joué
     * jusqu’à une victoire en suivant les bonnes réponses.
     *
     * @throws Exception si une erreur survient lors du chargement ou de l’exécution du scénario
     */
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

    /**
     * Crée dynamiquement un scénario complet utilisé pour le test d’intégration.
     * <p>
     * Le scénario contient :
     * - deux énigmes (une text et une boolean),
     * - un dossier images obligatoire,
     * - des fichiers images simulés,
     * - un fichier manifest.json conforme au format attendu.
     * </p>
     *
     * @return chemin du dossier temporaire contenant le scénario
     * @throws IOException si une erreur survient lors de la création des fichiers
     */
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