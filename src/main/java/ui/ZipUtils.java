package ui;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Classe utilitaire permettant de gérer l’extraction des scénarios
 * au format ZIP.
 *
 * <p>
 * Elle permet :
 * - de vérifier la validité d’un fichier ZIP,
 * - d’extraire son contenu dans un dossier temporaire,
 * - de localiser automatiquement le dossier contenant le scénario
 * (celui qui possède un fichier manifest.json).
 * </p>
 *
 * <p>
 * Sécurité : une vérification est effectuée pour éviter les attaques
 * de type "Zip Slip" (chemins malveillants dans l’archive).
 * </p>
 *
 * @author cano28
 */
public class ZipUtils {

    /**
     * Extrait un fichier ZIP contenant un scénario dans un dossier temporaire.
     *
     * <p>
     * Le ZIP doit contenir un fichier {@code manifest.json} soit :
     * - à la racine,
     * - soit dans un sous-dossier.
     * </p>
     *
     * @param cheminZip chemin du fichier ZIP à extraire
     * @return chemin du dossier contenant le scénario valide
     * @throws IOException si le fichier est invalide ou si aucun scénario n’est trouvé
     */
    public static String extraireScenarioZip(String cheminZip) throws IOException {

        Path zipPath = Paths.get(cheminZip);

        // Vérification que le fichier existe et est bien un ZIP.
        if (!Files.exists(zipPath) || !cheminZip.toLowerCase().endsWith(".zip")) {
            throw new IOException("Fichier zip invalide : " + cheminZip);
        }

        // Création d’un dossier temporaire pour l’extraction.
        Path dossierTemp = Files.createTempDirectory("scenario_zip_");

        try (InputStream fis = Files.newInputStream(zipPath);
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry entry;

            // Parcours de chaque entrée du ZIP.
            while ((entry = zis.getNextEntry()) != null) {

                Path cible = dossierTemp.resolve(entry.getName()).normalize();

                // Sécurité : empêche les chemins malveillants (Zip Slip).
                if (!cible.startsWith(dossierTemp)) {
                    throw new IOException("Entrée zip invalide : " + entry.getName());
                }

                if (entry.isDirectory()) {
                    // Création des dossiers.
                    Files.createDirectories(cible);
                } else {
                    // Création du dossier parent si nécessaire.
                    if (cible.getParent() != null) {
                        Files.createDirectories(cible.getParent());
                    }
                    // Copie du fichier extrait.
                    Files.copy(zis, cible, StandardCopyOption.REPLACE_EXISTING);
                }

                zis.closeEntry();
            }
        }

        // Recherche du dossier contenant un scénario valide.
        return trouverDossierScenario(dossierTemp).toString();
    }

    /**
     * Recherche le dossier contenant un scénario valide (manifest.json).
     *
     * <p>
     * Le fichier {@code manifest.json} peut être :
     * - directement à la racine,
     * - dans un sous-dossier du ZIP.
     * </p>
     *
     * @param racineExtraction dossier temporaire contenant les fichiers extraits
     * @return chemin du dossier contenant le scénario
     * @throws IOException si aucun scénario valide n’est trouvé
     */
    private static Path trouverDossierScenario(Path racineExtraction) throws IOException {

        // Cas 1 : manifest.json à la racine.
        Path manifestDirect = racineExtraction.resolve("manifest.json");
        if (Files.exists(manifestDirect)) {
            return racineExtraction;
        }

        // Cas 2 : recherche dans les sous-dossiers.
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(racineExtraction)) {
            for (Path path : stream) {
                if (Files.isDirectory(path) && Files.exists(path.resolve("manifest.json"))) {
                    return path;
                }
            }
        }

        throw new IOException("Aucun scénario valide trouvé dans le zip.");
    }
}