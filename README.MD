# Projet d'optimisation stochastique

## Introduction 

Ce projet vise à proposer une solution au célèbre problème du voyageur de commerce : le voyageur doit passer par un ensemble de villes, avec la contrainte de ne passer qu’une seule fois par ville, de minimiser son temps de trajet et de retourner à la ville de départ.  

Nous allons implémenter un algorithme visant à résoudre ce problème de manière optimisée : le recuit simulé, qui sera détaillé par la suite, ainsi que le solveur CPLEX. Ces deux outils vont nous permettre de résoudre le problème d'une manière déterministe mais également stochastique.

## Fonctionnement

### Pré-requis

Les librairies utilisées dans ce projet sont ```CPLEX, Apache Common Maths```.

Le projet a été codé sous Eclipse : l'utilisateur devrait pouvoir lancer celui-ci en ouvrant le projet, puis en mettant le chemin de la librairie CPLEX dans le build path du projet.

### Interface du programme


L'application se présente sous cette forme : 

![alt text](https://github.com/minh-n/ProjetStocha/blob/master/detatt532.PNG?raw=true)

Sur la gauche se trouvent les boutons permettant de régler le programme. Tout d’abord, l’utilisateur doit choisir le fichier contenant les données du problème. Ces fichiers peuvent être de format XML : le programme ne donne accès qu’à ce type de fichier et pourra en rejeter certains s’il ne sait pas les traiter (un message d’erreur apparaîtra alors, listant les différents types de fichiers reconnus par notre solveur). Une checkbox ‘Get cities from a TSP file’ permet de prendre les coordonnées des villes depuis un fichier TSP de même nom que le fichier XML choisi. Cette fonctionnalité a l’intérêt d’écourter considérablement les calculs d’affichage pour des fichiers XML de plus de 1000 villes.

L’utilisateur devra indiquer si son jeu de données décrit un problème stochastique ou un problème déterministe.

Grâce à un menu déroulant, l’utilisateur pourra choisir quel algorithme utiliser pour la résolution du problème. Celui-ci peut être soit CPLEX, soit le recuit simulé. Il pourra également choisir de ne pas résoudre le problème et simplement d’afficher les villes.

La partie suivante concerne les variables que l’utilisateur peut paramétrer sur le recuit simulé et CPLEX stochastique. “Temperature coefficient” désigne le coefficient par lequel la température sera multipliée à chaque palier. “Acceptance rate” désigne le taux d’acceptation minimum. “Failure threshold” désigne le nombre d’itération maximal par palier. “Initial temp. multiplier” désigne le coefficient par lequel sera multiplié la température initiale (celle-ci étant plus élevé, le nombre de palier sera par conséquent plus élevé et la solution de meilleure qualité, en théorie). “Alpha” désigne le coefficient alpha d’un problème stochastique. 

L’utilisateur pourra alors ordonner au programme de résoudre le problème à l’aide du bouton “Lancer la résolution”. Le logiciel calculera le chemin du voyageur à travers la liste de villes données par le fichier. Les informations concernant le problème et sa résolution seront donnés dans la partie Information.

Le solveur dessinera le chemin parcouru par le voyageur dans la partie droite de l’interface. En plus du graphe, le logiciel donnera le coût associé à la solution, le temps de calcul demandé, ainsi que le pourcentage de satisfaction de la contrainte de probabilité dans le cas d’un problème stochastique.

## Auteurs 

- Adrien LAVILLONNIÈRE ([@Veados](https://github.com/Veados)) : CPLEX stochastique et déterministe, modélisation préliminaire au projet
- Corentin MANSCOUR ([@neofoetus](https://github.com/neofoetus)) : Recuit simulé stochastique et déterministe, modélisation préliminaire au projet
- Hien Minh NGUYEN ([@minh-n](https://github.com/minh-n)) : interface utilisateur, affichage du problème, revue et vérification de code, écriture des documents, modélisation préliminaire au projet


## Remerciements

Nous souhaitons remercier M. Abdel LISSER, notre professeur d'optimisation stochastique qui nous a guidé à travers ce projet. 

## Annexes

### Annexe : problème du voyageur de commerce

Le problème du voyageur est reformulé en une fonction objectif et cinq contraintes. 

Fonction objectif : elle nous indique que le but du problème est de minimiser la somme du coût de tous les arcs empruntés par le voyageur.

**Contrainte (1a)** : La somme de tous les coefficients xij correspondant à une ville doit valoir 1, et ce pour chaque ville. Dans le parcours du voyageur, une ville i ne peut donc posséder qu’un seul chemin se dirigeant vers une ville j. Cela signifie que l’on ne sort qu’une seule fois de chaque ville.
**Contrainte (1b)** : Idem : on ne rentre qu’une seule fois dans chaque ville.
**Contrainte (1c)** : Soit S un sous ensemble de ville de cardinalité |S|. Si il y a |S| arcs ou plus reliant les villes de ce sous-ensemble, alors cela veut dire que l’ensemble est un sous-tour de la solution totale, et donc que la solution n’est pas acceptable.
**Contrainte (1d)** : C’est la contrainte de probabilité. La solution donnée par CPLEX doit pouvoir être de qualité proche de la solution déterministe du problème (notée Z) au moins une certaine partie du temps (notée 𝞪).
**Contrainte (1e)** : assure le bon format des éléments manipulés dans le problème 


### Annexe : l'algorithme du recuit simulé 

L’algorithme du recuit simulé est basé sur un principe métallurgique. C’est un procédé qui se déroule comme suit : un métal est porté à une certaine température et refroidi de manière contrôlée. Ce cycle peut être répété plusieurs fois et permet de stabiliser les cristaux à l’intérieur de la matière. 

Cette technique a inspiré les mathématiciens qui en ont déduit une méthode permettant de trouver le minimum ou le maximum d’une fonction. 

L’algorithme du recuit simulé consiste en l’exploration du voisinage d’une solution de la fonction objectif, en partant d’un point de départ X0 et d’une température T0. Afin de trouver l’extremum, il faut explorer le voisinage de X0 en calculant des solutions voisines à celle-ci, que nous appellerons Xi. 

Si la solution Xi est meilleure que la solution initiale X0, alors on accepte cette nouvelle solution. Si au contraire la qualité de la solution est dégradée, il est quand même possible de retenir Xi. La décision de garder ou rejeter Xi dans ce cas là est prise aléatoirement grâce à la distribution de Gibbs-Boltzmann : exp(-ΔCoût/T). On tire un nombre généré aléatoirement entre 0 et 1, et si celui-ci est inférieur à la probabilité de Gibbs-Boltzmann, alors on conserve Xi. Plus la température est élevée, plus la chance d’accepter un X qui dégrade la solution est élevée. On doit donc observer une certaine stabilisation au fur et à mesure que la température descend.

On effectue ce procédé un certain nombre de fois, nombre qui peut être fixé de plusieurs manières. Les itérations fonctionnent par cycle, par palier. A la fin de chaque palier, la température est diminuée d’un certain coefficient. L’algorithme s’arrête lorsque nous avons effectué un certain nombre de palier prédéfini tout en ayant un taux d’acceptation faible. Cette situation pourrait se produire lorsque la température est trop basse pour qu’une solution coûteuse soit acceptée, générant ainsi un taux d’acceptation faible. 

