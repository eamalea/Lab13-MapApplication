# MapApplication – Localisation GPS avec OpenStreetMap et PHP/MySQL

Application Android qui récupère la position GPS, l’envoie à un serveur PHP et affiche tous les points enregistrés sur une carte OpenStreetMap.

## Fonctionnalités

- Récupération de la localisation GPS (latitude, longitude)
- Envoi périodique au serveur (POST) via Volley
- Stockage MySQL avec architecture orientée objet (PHP)
- Affichage des positions sur une carte interactive (OSMDroid)
- Gestion des permissions Android (localisation)
- Centrage sur la dernière position enregistrée

## Installation

### Backend

1. Copier le dossier `map_project/` dans `htdocs` (XAMPP) ou `www` (WAMP)
2. Importer le script SQL pour créer la base `map_project` et la table `positions`
3. Vérifier que les URLs dans `MainActivity.java` et `MapsActivity.java` pointent vers votre serveur (ex: `http://10.0.2.2/map_project/...`)

### Android

1. Ouvrir le projet dans Android Studio
2. Ajouter votre icône `marker.png` dans `res/drawable/`
3. Modifier l’IP du serveur dans les deux activités
4. Lancer l’application sur un émulateur ou un téléphone réel (API 24+)

## Améliorations apportées

- ViewBinding (optionnel, non utilisé ici mais structure prête)
- Singleton Volley pour optimiser les requêtes
- Gestion complète du cycle de vie de la carte (`onResume`/`onPause`)
- Dialogue pour activer le GPS si désactivé
- Architecture PHP orientée objet (DAO, Service, Singleton)

