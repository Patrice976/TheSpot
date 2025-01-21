package com.example.myapplication

// Importation des classes nécessaires à l'application Android et à la gestion de l'interface utilisateur
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

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
            .baseUrl("http://192.168.1.17:6000") // URL de base de l'API Airtable
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
        Log.d("clickOnSpot", "event listner enable")
        //mémorisation de l'ID du spot selectionné
        selectedSpotId = spotId

        Log.d("clickOnSpot", "The spotID selected is ${selectedSpotId} ")

    }
}

class MainActivity : ComponentActivity() {

    // La classe MainActivity hérite de ComponentActivity, utilisée pour créer une activité avec Jetpack Compose
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Active l'affichage sans bordures inutiles (plein écran)

        // Capture globale des exceptions
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            Log.e("GlobalErrorHandler", "Erreur non interceptée : ${exception.message}", exception)
        }

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
                Log.d("connexion", "application lancée")

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
fun OneSpotHighlight(
    spotId: Int,
    title: String = "Default Surf Spot",
    imageUrl: String = "https://via.placeholder.com/400x200", // Image par défaut
    surfBreak: String = "Point Break",
    difficulty: Int = 3, // Difficulté sur 5
    startSeason: String = "April",
    endSeason: String = "September",
    address: String = "123 Surf Street, Beach City"
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .padding(16.dp)
    ) {
        Text(
            text = "Spot Highlight",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.9f))
                .padding(16.dp)
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            AsyncImage(
                model = imageUrl,
                contentDescription = "Spot Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "Surf Break: $surfBreak",
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "Difficulty:",
                    modifier = Modifier.padding(end = 8.dp)
                )
                for (i in 1..5) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star $i",
                        tint = if (i <= difficulty) Color.Yellow else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = "Season: $startSeason - $endSeason",
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Address: $address",
            )
        }
    }
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
                    .clickable { IdGetter.SpotId(index)},
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
        // État local pour stocker la valeur du champ de recherche
        var searchText by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxWidth() // Prend toute la largeur
                .wrapContentHeight(), // S'ajuste à la hauteur du contenu
            verticalArrangement = Arrangement.Top, // Aligne tout en haut
            horizontalAlignment = Alignment.CenterHorizontally // Centre horizontalement
        ) {
            TextField(
                value = searchText, // Utilise l'état local
                onValueChange = { newValue -> searchText = newValue }, // Met à jour l'état
                placeholder = { Text("Search...") },
                modifier = Modifier
                    .fillMaxWidth() // Le champ prend toute la largeur
                    .height(48.dp) // Hauteur du champ de texte
            )
        }
    }

@Composable
fun OneSpot(spot: SurfSpotRecord) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ){
        Text(
            text = spot.destination ?: "Destination inconnue",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id = R.drawable.surf_spot_1),
            contentDescription = "Image de ${spot.destination}",
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
        )

        Text(
            text = "Adresse : ${spot.address ?: "Non disponible"}",
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "Niveau de difficulté : ${spot.difficultyLevel}",
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "Surf Break : ${spot.surfBreak.joinToString() ?: "Non disponible"}",
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "Peak Begins : ${spot.peakBegins}",
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "Peak Ends : ${spot.peakEnds}",
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "Magic Seaweed Link : ${spot.magicSeaweedLink}",
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "Influenceurs : ${spot.influencers.joinToString() ?: "Non disponible"}",
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "Geocode : ${spot.geocode}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

