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
import java.io.FileReader;
import com.mycompany.projet_jeu.model.Gamedata;
import com.mycompany.projet_jeu.model.Puzzle;
import java.io.File;
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

            if (!"1.0".equals(jeu.getSchemaVersion())) {
                throw new ChargementJeuException("Valeur de schemaVersion inconnue : " + jeu.getSchemaVersion());
            }

            if (!jeu.hasPuzzle(jeu.getStart())) {
                throw new ChargementJeuException("Énigme de départ inexistante : " + jeu.getStart());
            }

            for (Map.Entry<String, Puzzle> entry : jeu.getPuzzles().entrySet()) {
                String idPuzzle = entry.getKey();
                Puzzle puzzle = entry.getValue();

                if (!puzzle.getType().equals("qcm") && !puzzle.getType().equals("input")) {
                    throw new ChargementJeuException("Type d’énigme non supporté : " + puzzle.getType());
                }

                if (puzzle.getImage() != null) {
                    File image = new File(dossier, "images/" + puzzle.getImage());
                    if (!image.exists()) {
                        throw new ChargementJeuException("Image introuvable : images/" + puzzle.getImage());
                    }
                }

                if (puzzle.getRoutes() != null) {
                    for (String reponse : puzzle.getRoutes().keySet()) {
                        String destination = puzzle.getRoutes().get(reponse);

                        if (!jeu.hasPuzzle(destination)) {
                            throw new ChargementJeuException(
                                "Route invalide : " + idPuzzle + " -> " + destination + " (énigme inexistante)"
                            );
                        }

                        if (puzzle.getType().equals("qcm")
                                && !reponse.equals("*")
                                && !puzzle.getChoices().contains(reponse)) {
                            throw new ChargementJeuException(
                                "Route invalide dans " + idPuzzle + " : " + reponse + " absente de choices"
                            );
                        }
                    }
                }
            }

            return jeu;

        } catch (JsonSyntaxException e) {
            throw new ChargementJeuException("JSON invalide dans manifest.json");
        } catch (ChargementJeuException e) {
            throw e;
        } catch (Exception e) {
            throw new ChargementJeuException("Erreur lors du chargement du scénario");
        }
    }
}
//test branche clems