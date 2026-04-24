package MoteurDeJeu;

/**
 * Exception spécifique levée lors du chargement d’un scénario.
 *
 * <p>
 * Cette exception est utilisée lorsque :
 * - le fichier manifest.json est absent ou invalide,
 * - le dossier images est manquant,
 * - une énigme est mal définie,
 * - une image est introuvable,
 * - une route est incorrecte,
 * - ou toute autre erreur liée à la structure du scénario.
 * </p>
 *
 * <p>
 * Elle permet de fournir un message d’erreur clair et compréhensible
 * pour l’utilisateur ou le développeur.
 * </p>
 *
 * @author cano28
 */
public class ChargementJeuException extends Exception {

    /**
     * Construit une exception avec un message décrivant l’erreur. 
     *
     * @param message description de l’erreur rencontrée
     */
    public ChargementJeuException(String message) {
        super(message);
    }
}