import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.test.JSONAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

//*************************************************************
// IGNORER CE FICHIER POUR LA CORRECTION DU TP.               *
// SOLUTION PERSONNELLE POUR VALIDER LE FONCTIONNEMENT DU TP. *
//*************************************************************

/**
 * Classe pour valider le bon fonctionnement du logiciel a partir d'une serie de fichiers JSON
 * d'entree.
 */
class ValidationTpTest {
    // --------------- Attributs

    // Modifiez uniquement les trois variables ci-dessous.

    /**
     * Le nom de la classe contenant la fonction main().
     * Important : Le nom de classe doit comprendre son/ses package(s).
     * Important (2) : La methode main() de cette classe ne doit pas faire usage de System.exit().
     *                 Si c'est le cas, remplacez les System.exit() par des return.
     */
    String CLASSE_AVEC_MAIN = "package01.package02.ClasseAvecMain";

    /**
     * Le dossier contenant les fichiers JSON d'entree sur votre ordinateur.
     */
    String DOSSIER_DONNEES_DE_TEST = "./donneesDeTest/";

    /**
     * Suffixe ajoute au nom des fichiers JSON representant le resultat (sortie) attendu.
     * Ce suffixe permet de distinguer les fichiers dans DOSSIER_DONNEES_DE_TEST qui sont des
     * entrees de ceux qui representent le resultat attendu.
     */
    String SUFFIXE_FICHIERS_RESULTAT_ATTENDU = "ResultatAttendu";



    // --------------- Methodes

    /**
     * Appelle le logiciel avec chaque fichier d'entree dans DOSSIER_DONNEES_DE_TEST.
     * Deux types de verifications sont possibles :
     *
     * 1) Si le fichier d'entree est accompagne d'un fichier de resultat attendu, le JSON
     * produit par l'appel au logiciel est compare au JSON du fichier resultat attendu. En cas de
     * difference entre les contenus, celle-ci sera clairement indiquee avec le test.
     *
     * 2) Si le fichier d'entree n'est pas accompagne d'un fichier de resultat attendu, le test
     * unitaire assume qu'une exception sera levee dans le logiciel suite a la lecture du fichier
     * d'entree. Le test assume donc que le logiciel ne produira pas de fichier de sortie et qu'il
     * affichera une erreur a la console. Ce message d'erreur sera affiche avec le test reussi.
     *
     * @return Une suite de tests unitaires a etre executes.
     */
    //@Disabled // <--- Decommenter pour desactiver les tests dans la remise de votre TP.
    @TestFactory
    Stream<DynamicTest> validerTp() {
        if (DOSSIER_DONNEES_DE_TEST.lastIndexOf("/") != DOSSIER_DONNEES_DE_TEST.length() - 1) {
            DOSSIER_DONNEES_DE_TEST += "/";
        }

        File dossierDonneesTest = new File(DOSSIER_DONNEES_DE_TEST);
        String[] fichiersEntree = new String[]{};
        // Fichier dans lequel on enverra la sortie du logiciel. Sera supprime a la fin des tests.
        String fichierSortieTemp = DOSSIER_DONNEES_DE_TEST + "zTemp.json";

        if (dossierDonneesTest.list() != null) {
            // On ne conserve que les fichiers sans le suffixe SUFFIXE_FICHIERS_RESULTAT_ATTENDU.
            fichiersEntree = List.of(dossierDonneesTest.list())
                                 .stream().filter( f -> !f.contains(SUFFIXE_FICHIERS_RESULTAT_ATTENDU) )
                                 .toArray(String[]::new);
        }

        // On retourne une liste de tests unitaires a etre executes. Chaque test est nomme selon le
        // nom du fichier d'entree.
        return List.of(fichiersEntree).stream().map(fichier -> DynamicTest.dynamicTest(fichier,
            () -> {
                String fichierResultatAttendu;

                // On construit le nom du possible fichier de resultat attendu qui pourrait
                // accompagner le fichier d'entree.
                if (fichier.contains(".")) {
                    fichierResultatAttendu = fichier.substring( 0, fichier.indexOf(".") );
                    fichierResultatAttendu += SUFFIXE_FICHIERS_RESULTAT_ATTENDU;
                    fichierResultatAttendu += fichier.substring( fichier.indexOf(".") );
                }
                else {
                    fichierResultatAttendu = fichier + SUFFIXE_FICHIERS_RESULTAT_ATTENDU;
                }
                fichierResultatAttendu = DOSSIER_DONNEES_DE_TEST + fichierResultatAttendu;

                try {
                    Files.delete( Path.of(fichierSortieTemp) );
                } catch (Exception e) {
                    // Rien.
                }


                // Appel au logiciel avec le fichier d'entree.

                // Reglages pour capturer le contenu de la console.
                ByteArrayOutputStream tamponConsole = new ByteArrayOutputStream();
                PrintStream redirectionConsole = new PrintStream(tamponConsole);
                // Sauvegarde pour restauration apres coup.
                PrintStream vraieSortieConsole = System.out;
                // On indique a Java de rediriger temporairement la sortie de la console.
                System.setOut(redirectionConsole);

                String fichierEntree = DOSSIER_DONNEES_DE_TEST + fichier;
                String[] args = new String[]{fichierEntree, fichierSortieTemp};
                Class.forName(CLASSE_AVEC_MAIN)
                     .getDeclaredMethod("main", String[].class)
                     .invoke(null, (Object) args);

                System.out.flush();
                System.setOut(vraieSortieConsole);


                // Si un fichier de resultat attendu a ete fourni, on compare son contenu
                // avec celui produit par le logiciel.
                if ( Files.exists( Path.of(fichierResultatAttendu) ) ) {
                    try {
                        String resultatAttendu = Files.readString(Path.of(fichierResultatAttendu),
                                                                  StandardCharsets.UTF_8);
                        String resultatObtenu = Files.readString(Path.of(fichierSortieTemp),
                                                                 StandardCharsets.UTF_8);
                        JSONObject jsonAttendu = (JSONObject) JSONSerializer.toJSON(resultatAttendu);
                        JSONObject jsonObtenu = (JSONObject) JSONSerializer.toJSON(resultatObtenu);

                        JSONAssert.assertEquals(jsonAttendu, jsonObtenu);
                    }
                    catch (Exception e) {
                        throw new AssertionError("\nAucun fichier de sortie n'a ete produit " +
                            "par le logiciel.\nIl semblerait qu'une exception ait ete levee " +
                            "durant l'appel au logiciel.\nVoici ce qu'a affiche votre logiciel a " +
                            "la console :\n" + tamponConsole.toString()
                        );
                    }
                }
                else {
                    boolean fichierSortieExiste = Files.exists( Path.of(fichierSortieTemp) );
                    if (fichierSortieExiste) Files.delete( Path.of(fichierSortieTemp) );
                    Assertions.assertFalse(fichierSortieExiste, "\nLe logiciel a genere un " +
                        "fichier de sortie alors qu'il aurait du lever une exception."
                    );
                    // Affichage du message que le logiciel a affiche a la console.
                    System.out.println(fichier + " : " + tamponConsole.toString());
                }
            }
        ));
    }
}
