package MoteurDeJeu;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mycompany.projet_jeu.model.Gamedata;
import com.mycompany.projet_jeu.model.Puzzle;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

/**
 * Classe responsable du chargement et de la validation d’un scénario de jeu.
 *
 * <p>
 * Elle lit un fichier manifest.json, crée un objet Gamedata et vérifie que
 * toutes les contraintes du scénario sont respectées :
 * </p>
 *
 * <ul>
 *     <li>présence du dossier et du fichier manifest.json</li>
 *     <li>présence du dossier images</li>
 *     <li>validité des champs obligatoires (id, title, author, start)</li>
 *     <li>présence et cohérence des énigmes</li>
 *     <li>validité des types d’énigmes</li>
 *     <li>existence des images</li>
 *     <li>cohérence des routes entre énigmes</li>
 * </ul>
 *
 * <p>
 * Le champ prompt n’est pas obligatoire : la consigne peut être contenue
 * directement dans l’image de l’énigme.
 * </p>
 *
 * <p>
 * En cas d’erreur, une exception ChargementJeuException est levée avec un message explicite.
 * </p>
 *
 * @author Emma
 * @author Amy
 * @author cano28
 */
public class Charger_Jeu {

    /**
     * Charge un scénario de jeu à partir d’un dossier et vérifie sa validité.
     *
     * @param dossierScenario chemin vers le dossier contenant le scénario
     * @return objet Gamedata représentant le scénario chargé
     * @throws ChargementJeuException si une erreur est détectée dans la structure ou les données
     */
    public static Gamedata chargerJeu(String dossierScenario) throws ChargementJeuException {

        try {
            File dossier = new File(dossierScenario);
            File manifest = new File(dossier, "manifest.json");
            File dossierImages = new File(dossier, "images");

            // Vérification du dossier scénario
            if (!dossier.exists() || !dossier.isDirectory()) {
                throw new ChargementJeuException("Dossier scénario introuvable : " + dossierScenario);
            }

            // Vérification du fichier manifest.json
            if (!manifest.exists()) {
                throw new ChargementJeuException("Fichier manifest.json absent");
            }

            // Vérification du dossier images
            if (!dossierImages.exists()) {
                throw new ChargementJeuException("Dossier images/ introuvable");
            }

            if (!dossierImages.isDirectory()) {
                throw new ChargementJeuException("images/ existe mais n'est pas un dossier");
            }

            // Lecture du JSON
            Gson gson = new Gson();
            Gamedata jeu;

            try (FileReader reader = new FileReader(manifest)) {
                jeu = gson.fromJson(reader, Gamedata.class);
            }

            // Vérification du contenu du JSON
            if (jeu == null) {
                throw new ChargementJeuException("manifest.json vide ou invalide");
            }

            if (jeu.getSchemaVersion() == null ||
                    (!"1".equals(jeu.getSchemaVersion()) && !"1.0".equals(jeu.getSchemaVersion()))) {
                throw new ChargementJeuException("Valeur de schemaVersion inconnue : " + jeu.getSchemaVersion());
            }

            if (jeu.getId() == null || jeu.getId().isBlank()) {
                throw new ChargementJeuException("Identifiant du scénario manquant : id");
            }

            if (jeu.getTitle() == null || jeu.getTitle().isBlank()) {
                throw new ChargementJeuException("Titre du scénario manquant : title");
            }

            if (jeu.getAuthor() == null || jeu.getAuthor().isBlank()) {
                throw new ChargementJeuException("Auteur du scénario manquant : author");
            }

            if (jeu.getPuzzles() == null || jeu.getPuzzles().isEmpty()) {
                throw new ChargementJeuException("Aucune énigme définie dans manifest.json");
            }

            if (jeu.getStart() == null || jeu.getStart().isBlank()) {
                throw new ChargementJeuException("Énigme de départ absente");
            }

            if (!jeu.hasPuzzle(jeu.getStart())) {
                throw new ChargementJeuException("Énigme de départ inexistante : " + jeu.getStart());
            }

            // Vérification des énigmes
            for (Map.Entry<String, Puzzle> entry : jeu.getPuzzles().entrySet()) {

                String idPuzzle = entry.getKey();
                Puzzle puzzle = entry.getValue();

                if (puzzle == null) {
                    throw new ChargementJeuException("Puzzle nul détecté : " + idPuzzle);
                }

                if (puzzle.getType() == null || puzzle.getType().isBlank()) {
                    throw new ChargementJeuException("Type manquant pour l’énigme : " + idPuzzle);
                }

                /*
                 * Le prompt n'est pas obligatoire :
                 * la consigne peut être directement présente dans l'image.
                 */

                if (puzzle.getImage() == null || puzzle.getImage().isBlank()) {
                    throw new ChargementJeuException("Image manquante pour l’énigme : " + idPuzzle);
                }

                // Vérification de l’image
                File image = new File(dossier, puzzle.getImage());

                if (!image.exists()) {
                    throw new ChargementJeuException("Image introuvable : " + puzzle.getImage());
                }

                if (!image.isFile()) {
                    throw new ChargementJeuException("Le chemin image ne correspond pas à un fichier : " + puzzle.getImage());
                }

                String type = puzzle.getType();

                // Vérification du type d’énigme
                if (!"qcm".equals(type) && !"text".equals(type) && !"boolean".equals(type)) {
                    throw new ChargementJeuException("Type d’énigme non supporté : " + type);
                }

                // Vérification spécifique au QCM
                if ("qcm".equals(type)) {
                    if (puzzle.getChoices() == null || puzzle.getChoices().isEmpty()) {
                        throw new ChargementJeuException("Choices manquant ou vide pour le QCM : " + idPuzzle);
                    }
                }

                // Vérification des routes
                if (puzzle.getRoutes() == null || puzzle.getRoutes().isEmpty()) {
                    throw new ChargementJeuException("Aucune route définie pour l’énigme : " + idPuzzle);
                }

                for (Map.Entry<String, String> route : puzzle.getRoutes().entrySet()) {

                    String reponse = route.getKey();
                    String destination = route.getValue();

                    if (destination == null || destination.isBlank()) {
                        throw new ChargementJeuException("Destination vide dans " + idPuzzle);
                    }

                    boolean estFin = destination.startsWith("end_");

                    if (!estFin && !jeu.hasPuzzle(destination)) {
                        throw new ChargementJeuException(
                                "Route invalide : " + idPuzzle + " -> " + destination + " (énigme inexistante)"
                        );
                    }

                    if ("qcm".equals(type)
                            && !"*".equals(reponse)
                            && !puzzle.getChoices().contains(reponse)) {
                        throw new ChargementJeuException(
                                "Route invalide dans " + idPuzzle + " : " + reponse + " absente de choices"
                        );
                    }

                    if ("boolean".equals(type)
                            && !"true".equals(reponse)
                            && !"false".equals(reponse)
                            && !"*".equals(reponse)) {
                        throw new ChargementJeuException(
                                "Route invalide pour boolean dans " + idPuzzle + " : " + reponse
                        );
                    }
                }
            }

            return jeu;

        } catch (JsonSyntaxException e) {
            throw new ChargementJeuException("JSON invalide dans manifest.json");
        } catch (ChargementJeuException e) {
            throw e;
        } catch (Exception e) {
            throw new ChargementJeuException("Erreur lors du chargement du scénario : " + e.getMessage());
        }
    }
}