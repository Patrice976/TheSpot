package com.example.myapplication

// Importation des classes nécessaires à l'application Android et à la gestion de l'interface utilisateur
import android.os.Bundle                 // Permet d'utiliser la classe Bundle (stockage de données d'activité)
import android.util.Log                   // Permet d'utiliser les logs pour le débogage
import android.widget.Toast
import androidx.activity.ComponentActivity  // Permet de créer une activité principale utilisant Jetpack Compose
import androidx.activity.compose.setContent  // Permet de définir le contenu de l'activité avec Jetpack Compose
import androidx.activity.enableEdgeToEdge   // Permet d'activer un design de type "edge-to-edge" (sans bordures)
import androidx.benchmark.perfetto.Row     // Importation pour les benchmarks de performance
import androidx.compose.foundation.Image    // Permet d'afficher une image dans l'UI
import androidx.compose.foundation.background // Permet d'ajouter une couleur de fond à un composant
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement // Permet de gérer l'alignement des éléments dans des conteneurs
import androidx.compose.foundation.layout.Box  // Permet de créer un conteneur flexible pour positionner des éléments
import androidx.compose.foundation.layout.Column // Permet de disposer les éléments verticalement dans une colonne
import androidx.compose.foundation.layout.Row    // Permet de disposer les éléments horizontalement dans une ligne
import androidx.compose.foundation.layout.Spacer // Permet de créer un espace vide entre les éléments
import androidx.compose.foundation.layout.fillMaxHeight // Permet de remplir la hauteur d'un conteneur
import androidx.compose.foundation.layout.fillMaxSize // Permet de remplir entièrement l'espace d'un conteneur
import androidx.compose.foundation.layout.fillMaxWidth // Permet de remplir la largeur d'un conteneur
import androidx.compose.foundation.layout.height    // Permet de spécifier une hauteur pour un composant
import androidx.compose.foundation.layout.padding   // Permet d'ajouter un espacement (padding) autour d'un composant
import androidx.compose.foundation.layout.size      // Permet de définir la taille d'un composant
import androidx.compose.foundation.layout.width     // Permet de spécifier une largeur pour un composant
import androidx.compose.foundation.lazy.LazyColumn  // Permet de créer une liste qui charge les éléments de manière paresseuse (lazy loading)
import androidx.compose.foundation.rememberScrollState // Permet de mémoriser l'état du défilement d'une liste
import androidx.compose.foundation.shape.RoundedCornerShape // Permet de créer des coins arrondis pour les composants
import androidx.compose.foundation.verticalScroll  // Permet de faire défiler les éléments verticalement
import androidx.compose.material.icons.Icons        // Permet d'utiliser les icônes Material Design
import androidx.compose.material.icons.filled.Edit  // Icône pour un bouton de modification (édition)
import androidx.compose.material.icons.filled.Home  // Icône pour un bouton d'accueil
import androidx.compose.material.icons.filled.Search // Icône pour un bouton de recherche
import androidx.compose.material3.*                // Importation des composants Material 3 pour l'UI
import androidx.compose.material3.TextField       // Permet de créer un champ de texte pour la saisie de l'utilisateur
import androidx.compose.material3.TextFieldDefaults // Permet de définir des valeurs par défaut pour un champ de texte
import androidx.compose.runtime.Composable       // Annotation utilisée pour marquer une fonction composable (UI déclarative)
import androidx.compose.runtime.mutableStateOf   // Permet de définir un état mutable dans un composable
import androidx.compose.ui.Alignment             // Permet de spécifier l'alignement des éléments dans l'UI
import androidx.compose.ui.Modifier              // Permet d'appliquer des modifications à un composant
import androidx.compose.ui.draw.clip             // Permet de rogner les bords d'un composant (par exemple, coins arrondis)
import androidx.compose.ui.graphics.Color       // Permet de définir une couleur pour un composant
import androidx.compose.ui.layout.ContentScale  // Permet de spécifier comment une image doit être mise à l'échelle
import androidx.compose.ui.res.colorResource    // Permet d'utiliser des couleurs définies dans les ressources XML
import androidx.compose.ui.res.painterResource  // Permet de charger une image depuis les ressources
import androidx.compose.ui.text.font.FontWeight // Permet de spécifier le poids de la police pour le texte
import androidx.compose.ui.unit.dp               // Permet de spécifier des dimensions (comme des marges, tailles) en dp (density-independent pixels)
import androidx.core.content.ContextCompat      // Permet d'utiliser les ressources contextuelles comme les couleurs ou les ressources matérielles
import androidx.test.core.app.ApplicationProvider
import com.example.myapplication.ui.theme.MyApplicationTheme // Permet d'appliquer le thème personnalisé de l'application
import com.google.gson.annotations.SerializedName // Permet d'annoter les champs d'un objet pour la sérialisation/desérialisation JSON avec Gson
import org.json.JSONObject                    // Permet de manipuler des objets JSON
import retrofit2.Call                         // Permet de gérer les appels réseau avec Retrofit
import retrofit2.http.GET                     // Permet de définir des requêtes HTTP GET
import retrofit2.http.Headers                // Permet de spécifier des en-têtes HTTP dans une requête
import retrofit2.http.Query                  // Permet de spécifier des paramètres de requête dans une URL
import retrofit2.Retrofit                    // Permet de créer une instance de Retrofit pour effectuer des requêtes réseau
import retrofit2.converter.gson.GsonConverterFactory // Permet de convertir les réponses en objets Java/Kotlin à l'aide de Gson
import retrofit2.Callback                    // Permet de gérer les réponses asynchrones des requêtes réseau
import retrofit2.Response                    // Permet de gérer les réponses de Retrofit à une requête

data class SurfSpotResponse(
    @SerializedName("data") val data: List<SurfSpotRecord> // La liste de records dans la clé "data"
)

data class SurfSpotRecord(
    @SerializedName("id") val id: String, // Identifiant du spot
    @SerializedName("surf_break") val surfBreak: List<String>, // Liste des types de surf break
    @SerializedName("difficulty_level") val difficultyLevel: Int, // Niveau de difficulté
    @SerializedName("destination") val destination: String, // Destination du spot
    @SerializedName("geocode") val geocode: String, // Géocode (peut-être une localisation)
    @SerializedName("influencers") val influencers: List<String>, // Liste des influenceurs
    @SerializedName("magic_seaweed_link") val magicSeaweedLink: String, // Lien vers Magic Seaweed
    @SerializedName("photos") val photos: List<Photos>, // Liste des photos
    @SerializedName("peak_surf_season_begins") val peakBegins: String, // Saison de surf commence
    @SerializedName("peak_surf_season_ends") val peakEnds: String, // Saison de surf finit
    @SerializedName("address") val address: String, // Adresse
    @SerializedName("created_time") val createdTime: String // Date de création
)

data class Photos(
    @SerializedName("id") val id: String?, // ID de la photo
    @SerializedName("url") val url: String?, // URL de la photo
    @SerializedName("filename") val filename: String?, // Nom du fichier
    @SerializedName("size") val size: Int?, // Taille de la photo
    @SerializedName("type") val type: String?, // Type de fichier (image/jpeg)
    @SerializedName("thumbnails") val thumbnails: Thumbnails? // Thumbnails des images
)

data class Thumbnails(
    @SerializedName("small") val small: Thumbnail?,
    @SerializedName("large") val large: Thumbnail?,
    @SerializedName("full") val full: Thumbnail?
)

data class Thumbnail(
    @SerializedName("url") val url: String?,
    @SerializedName("width") val width: Int?,
    @SerializedName("height") val height: Int?
)


/* interface AirtableApi {
    // Endpoint pour récupérer les données
    @GET("v0/appEksYm9WhIjEtus/tblRuaa61gtDvzAt2") // URL de l'API pour récupérer les données
    @Headers("Authorization: Bearer patpzSBgSr3dnevwc.a4de7204ffccf9cb98878db35d702f98de1136cb75016c8943e7691e9cc8dc53") // En-tête pour l'autorisation API
    fun getSurfSpot(): Call<SurfSpotResponse> // Fonction qui retourne une requête pour récupérer les spots de surf
} */

interface SurfSpotApi {
    @GET("spots")
    fun getSurfSpot(): Call<SurfSpotResponse>  // Now matches your Golang response structure exactly
}

object RetrofitClient {
    val apiService: SurfSpotApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://127.0.0.1:6000") // URL de base de l'API Airtable
            //.baseUrl("http://10.0.2.2:6000") // URL pour renvoyer le localhost de android sur le localhost du PC.
            .addConverterFactory(GsonConverterFactory.create()) // Convertisseur JSON en objet Kotlin avec Gson
            .build() // Construction de l'instance Retrofit
            .create(SurfSpotApi::class.java) // Création de l'interface AirtableApi
    }
}

object IdGetter {
    //GESTION RECUPERATION ET MEMORISATION ID SPOT
//déclaration de la varaible qui contiendra la valeur de l'id du spot
    var selectedSpotId: Int? = null
    fun SpotId(spotId: Int) {
        //mémorisation de l'ID du spot selectionné
        selectedSpotId = spotId
        //affiché à l'écrant la data récupéré
        Toast.makeText(ApplicationProvider.getApplicationContext(), "Spot sélectionné: $spotId", Toast.LENGTH_SHORT).show()
    }
}

class MainActivity : ComponentActivity() {

    // La classe MainActivity hérite de ComponentActivity, utilisée pour créer une activité avec Jetpack Compose
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Active l'affichage sans bordures inutiles (plein écran)

        // Déclaration des états réactifs pour gérer les données, le chargement et les erreurs
        val surfSpots =
            mutableStateOf<List<SurfSpotRecord>>(emptyList()) // Liste réactive des spots de surf
        val isLoading =
            mutableStateOf(true) // Variable réactive pour afficher l'indicateur de chargement
        val errorState =
            mutableStateOf<String?>(null) // Variable réactive pour l'affichage des erreurs

        // Appel à l'API pour récupérer les données via Retrofit
        RetrofitClient.apiService.getSurfSpot().enqueue(object : Callback<SurfSpotResponse> {
            override fun onResponse(
                call: Call<SurfSpotResponse>,
                response: Response<SurfSpotResponse>
            ) {
                isLoading.value = false // Fin du chargement
                if (response.isSuccessful) {
                    println("JSON brut : ${response.body()}") // Affiche les données brutes de la réponse
                    surfSpots.value = response.body()?.data
                        ?: emptyList() // Assigne les spots récupérés à `surfSpots`
                    println("Données récupérées : ${surfSpots.value}") // Affiche les données récupérées
                } else {
                    errorState.value =
                        "Erreur : ${response.code()} - ${response.message()}" // Affiche l'erreur si la réponse est un échec
                }
            }

            override fun onFailure(call: Call<SurfSpotResponse>, t: Throwable) {
                isLoading.value = false // Fin du chargement en cas d'échec
                errorState.value =
                    "Erreur : ${t.message}" // Affiche l'erreur spécifique en cas d'échec
            }
        })



        // Configuration de l'interface utilisateur avec Jetpack Compose
        setContent {
            MyApplicationTheme { // Applique le thème de l'application

                Scaffold(
                    modifier = Modifier.fillMaxSize(), // Utilise tout l'espace disponible pour l'interface
                    topBar = { SearchBar() }, // Barre de recherche en haut
                    bottomBar = { NavigationBarWithButtons() } // Barre de navigation en bas
                ) { innerPadding ->
                    Box( // Conteneur pour superposer les éléments
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                    if (isLoading.value) { // Si les données sont encore en cours de chargement, afficher un indicateur de chargement
                        println("Chargement en cours...")
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator() // Affiche un indicateur de chargement
                        }
                    } else if (errorState.value != null) { // Si une erreur est survenue, afficher le message d'erreur
                        println("Erreur détectée : ${errorState.value}")
                        Text(
                            text = "Erreur : ${errorState.value}",
                            color = Color.Red // Affiche le message d'erreur en rouge
                        )
                    } else {
                        println("Affichage des spots de surf : ${surfSpots.value}")
                        Screen(surfSpots.value) // Affiche les spots de surf
                    }
                }
            }
        }
    }
}

@Composable
fun Screen(surfSpots: List<SurfSpotRecord>) {
    Image(
        painter = painterResource(id = R.drawable.main_background), // Image de fond
        contentDescription = "Une planche planté dans du sable ", // Description de l'image
        contentScale = ContentScale.FillBounds, // L'image occupe toute la surface disponible
        modifier = Modifier.fillMaxSize() // Utilise tout l'espace disponible
    )
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) { DisplaySurfSpots(surfSpots) }
}

@Composable

fun DisplaySurfSpots(surfSpots: List<SurfSpotRecord>,) {
    // Affiche la liste des spots de surf dans une colonne
    Column(modifier = Modifier.fillMaxSize()) {
        surfSpots.forEachIndexed { index, spot -> // Parcours de chaque spot
            // Sélection aléatoire d'une image parmi une liste
            val drawableImages = listOf(
                R.drawable.surf_spot_1,
                R.drawable.surf_spot_2,
                R.drawable.surf_spot_3,
                R.drawable.surf_spot_4,
                R.drawable.surf_spot_5,
                R.drawable.surf_spot_6,
                R.drawable.surf_spot_7,
                R.drawable.surf_spot_8,
                R.drawable.surf_spot_9,
                R.drawable.surf_spot_10,
                R.drawable.surf_spot_11,
                R.drawable.surf_spot_12,
                R.drawable.surf_spot_13,
                R.drawable.surf_spot_15,
                R.drawable.surf_spot_16
            )
            val randomImage = drawableImages.random() // Choisit une image aléatoire

            // Affiche le spot de surf avec une colonne pour disposer les informations
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(80.dp))
                    .background(Color.White.copy(alpha = 0.8f))
                    .clickable { IdGetter.SpotId(index) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp), // Hauteur ajustée pour bien centrer le texte
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = spot.destination, // Affiche le nom de la destination
                        fontWeight = FontWeight.Bold, // Applique une graisse au texte
                    )
                }

                // Affichage de l'image
                Image(
                    painter = painterResource(id = randomImage), // L'image du spot de surf
                    contentDescription = "Image de ${spot.destination}", // Description de l'image
                    modifier = Modifier
                        .size(285.dp) // Définit la taille de l'image
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(56.dp))
                )

                // Affichage des informations supplémentaires sur le spot
                Text(
                    text = "adresse : ${spot.address}",
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "Difficulté : ${spot.difficultyLevel}",
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "Surf Break : ${spot.surfBreak.get(0)}",
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )
            }
        }
    }
}



@Composable
fun NavigationBarWithButtons() {
    val sable =
        colorResource(id = R.color.Sable) // Récupère la couleur "Sable" définie dans les ressources
    Log.d("Debug", sable.toString()) // Log pour vérifier la valeur de la couleur

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(sable.copy(alpha = 0.8f)) // Applique la couleur de fond avec un peu de transparence
    ) {
        NavigationBar(
            modifier = Modifier.height(90.dp) // Définit la hauteur de la barre de navigation
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), // La Row prend toute la largeur
                horizontalArrangement = Arrangement.Center, // Centre les éléments horizontalement
                verticalAlignment = Alignment.CenterVertically // Centre les éléments verticalement
            ) {
                // Bouton Home
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.Home, contentDescription = "Home")
                }

                // Espacement entre les boutons
                Spacer(modifier = Modifier.width(32.dp))

                // Bouton Edit
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit")
                }
            }
        }
    }
}

@Composable
fun SearchBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth() // Prend toute la largeur
            .fillMaxHeight(), // Prend toute la hauteur
        verticalArrangement = Arrangement.Top, // Aligne tout en haut
        horizontalAlignment = Alignment.CenterHorizontally // Centre horizontalement
    ) {
        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Search...") },
            modifier = Modifier
                .fillMaxWidth() // Le champ prend toute la largeur
                .height(48.dp) // Hauteur du champ de texte
        )
    }
}
