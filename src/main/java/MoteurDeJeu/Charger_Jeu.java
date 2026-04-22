/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MoteurDeJeu;

/**
 *
 * @author cano28
 */

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mycompany.projet_jeu.model.Gamedata;
import com.mycompany.projet_jeu.model.Puzzle;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

public class Charger_Jeu {

    public static Gamedata chargerJeu(String dossierScenario) throws ChargementJeuException {
        try {
            File dossier = new File(dossierScenario);
            File manifest = new File(dossier, "manifest.json");

            if (!manifest.exists()) {
                throw new ChargementJeuException("Fichier manifest.json absent");
            }

            Gson gson = new Gson();
            FileReader reader = new FileReader(manifest);
            Gamedata jeu = gson.fromJson(reader, Gamedata.class);
            reader.close();

            if (jeu == null) {
                throw new ChargementJeuException("manifest.json vide ou invalide");
            }

            if (jeu.getSchemaVersion() == null ||
                (!"1".equals(jeu.getSchemaVersion()) && !"1.0".equals(jeu.getSchemaVersion()))) {
                throw new ChargementJeuException("Valeur de schemaVersion inconnue : " + jeu.getSchemaVersion());
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

            for (Map.Entry<String, Puzzle> entry : jeu.getPuzzles().entrySet()) {
                String idPuzzle = entry.getKey();
                Puzzle puzzle = entry.getValue();

                if (puzzle == null) {
                    throw new ChargementJeuException("Puzzle nul détecté : " + idPuzzle);
                }

                if (puzzle.getType() == null || puzzle.getType().isBlank()) {
                    throw new ChargementJeuException("Type manquant pour l’énigme : " + idPuzzle);
                }

                if (puzzle.getPrompt() == null || puzzle.getPrompt().isBlank()) {
                    throw new ChargementJeuException("Consigne manquante pour l’énigme : " + idPuzzle);
                }

                String type = puzzle.getType();

                if (!"qcm".equals(type) && !"text".equals(type) && !"boolean".equals(type)) {
                    throw new ChargementJeuException("Type d’énigme non supporté : " + type);
                }

                if (puzzle.getImage() != null && !puzzle.getImage().isBlank()) {

                    File image = new File(dossier, puzzle.getImage());

                    if (!image.exists()) {

                        throw new ChargementJeuException("Image introuvable : " + puzzle.getImage());

                    }

                }

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
                            && (puzzle.getChoices() == null || !puzzle.getChoices().contains(reponse))) {
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