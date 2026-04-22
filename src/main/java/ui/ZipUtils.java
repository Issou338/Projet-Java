/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

/**
 *
 * @author cano28
 */

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {

    public static String extraireScenarioZip(String cheminZip) throws IOException {
        Path zipPath = Paths.get(cheminZip);

        if (!Files.exists(zipPath) || !cheminZip.toLowerCase().endsWith(".zip")) {
            throw new IOException("Fichier zip invalide : " + cheminZip);
        }

        Path dossierTemp = Files.createTempDirectory("scenario_zip_");

        try (InputStream fis = Files.newInputStream(zipPath);
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                Path cible = dossierTemp.resolve(entry.getName()).normalize();

                if (!cible.startsWith(dossierTemp)) {
                    throw new IOException("Entrée zip invalide : " + entry.getName());
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(cible);
                } else {
                    if (cible.getParent() != null) {
                        Files.createDirectories(cible.getParent());
                    }
                    Files.copy(zis, cible, StandardCopyOption.REPLACE_EXISTING);
                }

                zis.closeEntry();
            }
        }

        return trouverDossierScenario(dossierTemp).toString();
    }

    private static Path trouverDossierScenario(Path racineExtraction) throws IOException {
        Path manifestDirect = racineExtraction.resolve("manifest.json");
        if (Files.exists(manifestDirect)) {
            return racineExtraction;
        }

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