# com.cameleon.photo.manager


En 1er lieu créer l'application sur dans la console Firebase : https://firebase.google.com/

Obtenir le server_client_id et le client_secret
Connectez-vous à la Google Cloud Console [Google Cloud Console](https://console.cloud.google.com/)

Utiliser le projet existant (qui a du être créer par Firebase)

Si vous n'avez pas encore de projet, créez-en un en cliquant sur "Créer un projet".
Si un projet existe déjà, sélectionnez-le dans le menu déroulant en haut de la page.
Activer les APIs nécessaires :

* Allez dans API et services > Bibliothèque.
* Recherchez et activez les API suivantes :
* Google Photos Library API.

Allez dans API et services > Identifiants.
Cliquez sur Créer des identifiants > Identifiant OAuth 2.0.
Choisissez Type d'application : Application Android.
Entrez les informations nécessaires :
Nom du package : Le nom du package de votre application (ex. com.example.googlephotosapp).
Empreinte SHA-1 :
Pour obtenir l'empreinte SHA-1, utilisez la commande suivante dans votre terminal :
...
keytool -list -v -keystore <votre_chemin_de_keystore> -alias <votre_alias>
...
Remplacez <votre_chemin_de_keystore> et <votre_alias> par les valeurs correctes.

Exemple :
...
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android
...

Cliquez sur Créer.
Obtenir le server_client_id et client_secret :

Une fois les identifiants créés, vous verrez un écran avec ces informations :
* Client ID OAuth 2.0 (server_client_id) : Identifiant unique pour votre application.
* Client secret (client_secret) : La clé secrète associée au client ID.
Vous pouvez également télécharger ces informations dans un fichier JSON en cliquant sur Télécharger les identifiants.

* Où trouver le access_token ?
Le token d'accès (access_token) est généré dynamiquement lors de l'authentification de l'utilisateur via Google Sign-In. Vous ne le trouverez pas directement dans la console, car il est généré par l'API OAuth 2.0.

Étapes pour obtenir le access_token :
- Lorsque l'utilisateur se connecte avec Google Sign-In, vous obtenez un auth_code.
- Utilisez cet auth_code pour échanger un access_token avec la méthode décrite précédemment :
- Appelez l'API OAuth 2.0 https://oauth2.googleapis.com/token.
- Fournissez les paramètres nécessaires : auth_code, client_id, client_secret, etc.

Résumé des clés et tokens :
server_client_id : Trouvé dans la section Identifiants de Google Cloud Console (dans le Client OAuth 2.0 créé).
client_secret : Disponible dans la même section que le server_client_id.
access_token : Géré dynamiquement en échangeant un auth_code avec l'API Google.

403
Connectez-vous à la Google Cloud Console [Google Cloud Console](https://console.cloud.google.com/)
Sélectionnez le projet dans le menu déroulant en haut de la page

Aller dans le menu : Google Auth Platform / Audience
https://console.cloud.google.com/auth/audience?inv=1&invt=Abp-7w&project=photo-manager-87e76
Ajouter les mail des utilisateurs testers
