# Outil pour la validation du TP du cours INF2050

Cet outil permet de valider le bon fonctionnement de votre TP en appelant votre programme contre
une série de fichiers JSON d'entrée. Vous n'avez rien à programmer; vous indiquez seulement où se
trouve le dossier contenant vos fichiers JSON d'entrée de test. L'outil se charge du reste.

## Mise en place de l'outil

1. L'outil est un "TestFactory" JUnit 5. Vous devez donc simplement copier/ajouter le fichier
*[ValidationTpTest.java](ValidationTpTest.java)* au dossier "src/test/java" de votre projet IntelliJ.

2. Créez un nouveau dossier dans votre projet IntelliJ. Ce dossier contiendra vos fichiers JSON
d'entrée qui serviront pour les tests.  
Vous pouvez télécharger le fichier *[donneesDeTest.zip](donneesDeTest.zip)* qui est inclus dans
ce dépôt GIT. Il contient 35 fichiers de test. Ajoutez ces fichiers au dossier créé plus tôt.

3. En fonction de votre projet, modifiez les trois variables suivantes dans le fichier
*[ValidationTpTest.java](ValidationTpTest.java)* :

Variables | Explications
:--- | :---
CLASSE_AVEC_MAIN | Le nom de la classe contenant la fonction main() dans projet. **Le nom de classe doit comprendre son/ses package(s)**. Le main() ne doit pas faire usage de System.exit(). Si c'est le cas, remplacez les System.exit() par des return.
DOSSIER_DONNEES_DE_TEST | Le chemin vers le dossier des fichiers JSON d'entrée.
SUFFIXE_FICHIERS_RESULTAT_ATTENDU | Suffixe ajouté au nom d'un fichier d'entrée et qui donnera le nom du fichier de résultat attendu.<br />Exemple : Vous avez un fichier d'entrée "test01.json" et votre suffixe est réglé à "_resultat_attendu". Le fichier de sortie attendu pour ce fichier devra être nommé "test01_resultat_attendu.json".

## Dossier des fichiers JSON d'entrée

Imaginons le contenu suivant pour un dossier :
* fichier01.json
* fichier01**ResultatAttendu**.json
* fichier02.json
* fichier02**ResultatAttendu**.json
* invalideDateEmbauche.json

L'outil choisira entre deux types de vérification :

1. Si le fichier d'entrée est accompagné d'un fichier "résultat attendu", le test assume que votre programme créera un fichier de sortie JSON et ce dernier sera comparé au fichier "résultat attendu". En cas de différence entre les contenus, celle-ci sera indiquée avec le test.

2. Si le fichier d'entrée n'est pas accompagné d'un fichier "résultat attendu", le test unitaire assume qu'une exception sera levée dans votre programme suite à la lecture du fichier d'entrée. Le test assume donc que votre programme ne produira pas de fichier de sortie et qu'il affichera un message d'erreur à la console. Ce message sera affiché avec le test réussi.