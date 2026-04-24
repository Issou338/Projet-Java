package MoteurDeJeu;

import com.google.gson.Gson;
import com.mycompany.projet_jeu.model.Gamedata;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de tests unitaires du moteur de jeu {@link MoteurDeJeu}.
 * <p>
 * Ces tests vérifient le comportement du moteur indépendamment du chargement
 * de scénario :
 * - gestion de la victoire,
 * - gestion de la défaite,
 * - abandon de la partie,
 * - gestion des routes invalides.
 * </p>
 *
 * <p>
 * Les scénarios sont créés directement en mémoire via du JSON afin de tester
 * uniquement la logique métier du moteur.
 * </p>
 *
 * @author cano28
 */
public class MoteurDeJeuTest {

    /**
     * Vérifie que le moteur termine correctement une partie en victoire
     * lorsque l’utilisateur fournit la bonne réponse.
     */
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
              "image":"images/img.png",
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

    /**
     * Vérifie que le moteur termine en défaite lorsque la réponse
     * ne correspond à aucune route spécifique et utilise la route par défaut (*).
     */
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
              "image":"images/img.png",
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

    /**
     * Vérifie que le moteur gère correctement l’abandon de la partie.
     */
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
              "image":"images/img.png",
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

    /**
     * Vérifie qu’une exception est levée lorsque la réponse fournie
     * ne correspond à aucune route définie et qu’aucune route par défaut (*) n’existe.
     */
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
              "image":"images/img.png",
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