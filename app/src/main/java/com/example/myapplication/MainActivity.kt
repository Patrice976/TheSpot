package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.benchmark.perfetto.Row
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Callback
import retrofit2.Response


data class SurfSpotRecord(
    val id: String, // Identifiant unique du spot de surf
    val fields: SurfSpotFields, // Contient les informations détaillées sur le spot (via la classe SurfSpotFields)
)

data class SurfSpotFields(
    @SerializedName("Destination") val destination: String?, // Nom de la destination (facultatif, peut être null)
    @SerializedName("Difficulty Level") val difficulty: Int, // Niveau de difficulté du spot
    @SerializedName("Surf Break") val surfBreak: List<String>?, // Liste des endroits où les vagues se brisent (facultatif)
    @SerializedName("photos") val photos: Photos, // Informations sur les photos liées au spot
    @SerializedName("peakBegins") val peakBegins: String?, // Heure à laquelle les vagues commencent à être meilleures (facultatif)
    @SerializedName("peakEnds") val peakEnds: String?, // Heure à laquelle les vagues sont les meilleures (facultatif)
    @SerializedName("magicSeaweedLink") val magicSeaweedLink: String?, // Lien vers la plateforme Magic Seaweed pour plus d'infos
    @SerializedName("Influencers") val influencers: List<String>?, // Liste des influenceurs associés à ce spot (facultatif)
    @SerializedName("Travellers") val travellers: List<String>?, // Liste des voyageurs associés à ce spot (facultatif)
    @SerializedName("Geocode") val geocode: String?, // Géocode du spot (facultatif)
    @SerializedName("Destination State/Country") val address: String? // Adresse complète du spot de surf
)

data class Photos(
    val id: String?, // Identifiant de la photo (facultatif)
    val width: Int?, // Largeur de la photo (facultatif)
    val height: Int?, // Hauteur de la photo (facultatif)
    val url: String?, // URL de la photo (facultatif)
    val filename: String?, // Nom du fichier de la photo (facultatif)
    val size: Int?, // Taille du fichier de la photo (facultatif)
    val type: String?, // Type de fichier de la photo (facultatif)
    val thumbnails: Thumbnails? // Thumbnails de la photo (facultatif)
)

data class Thumbnails(
    val small: Thumbnail?, // Version réduite de la photo
    val large: Thumbnail?, // Version large de la photo
    val full: Thumbnail? // Version complète de la photo
)

data class Thumbnail(
    val url: String?, // URL de l'image miniature (facultatif)
    val width: Int?, // Largeur de la miniature (facultatif)
    val height: Int? // Hauteur de la miniature (facultatif)
)

data class SurfSpotResponse(
    val records: List<SurfSpotRecord> // Liste des enregistrements de spots de surf
)

interface AirtableApi {
    // Endpoint pour récupérer les données
    @GET("v0/appEksYm9WhIjEtus/tblRuaa61gtDvzAt2") // URL de l'API pour récupérer les données
    @Headers("Authorization: Bearer patpzSBgSr3dnevwc.a4de7204ffccf9cb98878db35d702f98de1136cb75016c8943e7691e9cc8dc53") // En-tête pour l'autorisation API
    fun getSurfSpot(): Call<SurfSpotResponse> // Fonction qui retourne une requête pour récupérer les spots de surf
}

object RetrofitClient {
    val apiService: AirtableApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.airtable.com/") // URL de base de l'API Airtable
            .addConverterFactory(GsonConverterFactory.create()) // Convertisseur JSON en objet Kotlin avec Gson
            .build() // Construction de l'instance Retrofit
            .create(AirtableApi::class.java) // Création de l'interface AirtableApi
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
                    surfSpots.value = response.body()?.records
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
fun DisplaySurfSpots(surfSpots: List<SurfSpotRecord>) {
    // Affiche la liste des spots de surf dans une colonne
    Column(modifier = Modifier.fillMaxSize()) {
        surfSpots.forEach { spot -> // Parcours de chaque spot
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
                    .background(Color.White.copy(alpha = 0.8f)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp), // Hauteur ajustée pour bien centrer le texte
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${spot.fields.destination}", // Affiche le nom de la destination
                        fontWeight = FontWeight.Bold, // Applique une graisse au texte
                    )
                }

                // Affichage de l'image
                Image(
                    painter = painterResource(id = randomImage), // L'image du spot de surf
                    contentDescription = "Image de ${spot.fields.destination}", // Description de l'image
                    modifier = Modifier
                        .size(285.dp) // Définit la taille de l'image
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(56.dp))
                )

                // Affichage des informations supplémentaires sur le spot
                Text(
                    text = "adresse : ${spot.fields.address}",
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "Difficulté : ${spot.fields.difficulty}",
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "Surf Break : ${spot.fields.surfBreak?.get(0)}",
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
