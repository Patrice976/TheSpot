package com.example.myapplication

import android.os.Bundle
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
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
    val id: String,
    val fields: SurfSpotFields
)

data class SurfSpotFields(
    @SerializedName("Destination") val destination: String?,
    @SerializedName("Difficulty") val difficulty: String?,
    @SerializedName("surfBreak") val surfBreak: String?,
    @SerializedName("photos") val photos: Photos,
    @SerializedName("peakBegins") val peakBegins: String?,
    @SerializedName("peakEnds") val peakEnds: String?,
    @SerializedName("magicSeaweedLink") val magicSeaweedLink: String?,
    @SerializedName("Influencers") val influencers: List<String>?,
    @SerializedName("Travellers") val travellers: List<String>?,
    @SerializedName("Geocode") val geocode: String?
)

data class Photos(
    val id: String?,
    val width: Int?,
    val height: Int?,
    val url: String?,
    val filename: String?,
    val size: Int?,
    val type: String?,
    val thumbnails: Thumbnails?
)

data class Thumbnails(
    val small: Thumbnail?,
    val large: Thumbnail?,
    val full: Thumbnail?
)

data class Thumbnail(
    val url: String?,
    val width: Int?,
    val height: Int?
)

data class SurfSpotResponse(
    val records: List<SurfSpotRecord>
)

interface AirtableApi {
    // Endpoint pour récupérer les données
    @GET("v0/appEksYm9WhIjEtus/tblRuaa61gtDvzAt2")
    @Headers("Authorization: Bearer patpzSBgSr3dnevwc.a4de7204ffccf9cb98878db35d702f98de1136cb75016c8943e7691e9cc8dc53")
    fun getSurfSpot(): Call<SurfSpotResponse>
}

object RetrofitClient {
    val apiService: AirtableApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.airtable.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AirtableApi::class.java)
    }
}

class MainActivity : ComponentActivity() {
    // La classe MainActivity hérite de ComponentActivity, une classe utilisée pour les applications Jetpack Compose.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Appelle une fonction pour activer le mode "edge-to-edge" (affichage plein écran sans bordures inutiles).

        // Déclaration des états réactifs pour gérer les données, le chargement et les erreurs.
        val surfSpots = mutableStateOf<List<SurfSpotRecord>>(emptyList())
        // `surfSpots` est une liste mutable réactive qui stockera les données des spots de surf récupérées depuis l'API.

        val isLoading = mutableStateOf(true)
        // `isLoading` est une variable réactive pour indiquer si les données sont en cours de chargement.

        val errorState = mutableStateOf<String?>(null)
        // `errorState` est une variable réactive pour stocker un message d'erreur, ou `null` s'il n'y a pas d'erreur.

        // Appel à l'API pour récupérer les données via Retrofit.
        RetrofitClient.apiService.getSurfSpot().enqueue(object : Callback<SurfSpotResponse> {
            override fun onResponse(call: Call<SurfSpotResponse>, response: Response<SurfSpotResponse>) {
                isLoading.value = false
                // Une réponse a été reçue, donc on arrête d'indiquer que le chargement est en cours.

                if (response.isSuccessful) {
                    println("JSON brut : ${response.body()}")

                    surfSpots.value = response.body()?.records ?: emptyList()
                    println("Données récupérées : ${surfSpots.value}")
                    // Si la réponse est réussie, on extrait les données des spots de surf et on les assigne à `surfSpots`.
                } else {
                    errorState.value = "Erreur : ${response.code()} - ${response.message()}"
                    // Si la réponse contient une erreur, on met à jour `errorState` avec le code et le message d'erreur.
                }
            }

            override fun onFailure(call: Call<SurfSpotResponse>, t: Throwable) {
                isLoading.value = false
                // Si la requête échoue (par exemple, problème de réseau), on arrête d'indiquer que le chargement est en cours.

                errorState.value = "Erreur : ${t.message}"
                // On met à jour `errorState` avec le message de l'exception.
            }
        })

        // Configuration de l'interface utilisateur avec Jetpack Compose.
        setContent {
            MyApplicationTheme {
                // Applique le thème de l'application pour styliser l'interface.

                if (isLoading.value) {
                    // Si les données sont encore en cours de chargement, afficher un indicateur de chargement.
                    println("Chargement en cours...")
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (errorState.value != null) {
                    println("Erreur détectée : ${errorState.value}")
                    Text(
                        text = "Erreur : ${errorState.value}",
                        color = Color.Red
                    )
                } else {
                    println("Affichage des spots de surf : ${surfSpots.value}")
                    // Si les données ont été chargées avec succès, afficher l'écran principal avec les spots de surf.
                    Screen(surfSpots.value)
                    // `MainScreen` est une fonction Composable qui reçoit les données des spots de surf et les affiche.
                }
            }
        }

    }
}

/*@Composable
fun MainScreen(surfSpots: List<SurfSpotRecord>) {
    // Utilisation du Scaffold pour une gestion correcte de la barre de navigation
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { NavigationBarWithButtons() } // Ajout de la barre de navigation en bas
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(50.dp))
                Screen()
                DisplaySurfSpots(surfSpots)
            }
        }
    }
}*/

@Composable
fun Screen(surfSpots: List<SurfSpotRecord>) {
        Image(
            painter = painterResource(id= R.drawable.main_background),
            contentDescription = "Une planche planté dans du sable ",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Column (modifier = Modifier.verticalScroll(rememberScrollState())){DisplaySurfSpots(surfSpots)}
    }

@Composable
fun DisplaySurfSpots(surfSpots: List<SurfSpotRecord>) {
    // Utilisation de Column pour afficher les éléments
    Column(modifier = Modifier.fillMaxSize()) {
        // Parcours de la liste des spots avec forEach
        surfSpots.forEach { spot ->
            // Affichage de chaque élément avec une Column pour une disposition verticale
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.White.copy(alpha = 0.8f)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Affichage de l'image
                Image(
                    painter = rememberAsyncImagePainter(spot.fields.photos),  // Assurez-vous que spot.fields.photos est une image valide
                    contentDescription = "Image de ${spot.fields.destination}",
                    modifier = Modifier
                        .size(300.dp) // Définit la taille de l'image
                        .padding(bottom = 8.dp) // Espacement en bas de l'image
                )

                // Affichage du texte
                Text(
                    text = "Destination : ${spot.fields.destination}",
                    modifier = Modifier.padding(top = 8.dp) // Espacement en haut du texte
                )
            }
        }
    }
    }


@Composable
fun NavigationBarWithButtons() {
    NavigationBar {
        // Row pour gérer l'alignement des boutons
        Row(
            modifier = Modifier.fillMaxWidth(), // La Row prend toute la largeur
            horizontalArrangement = Arrangement.Center, // Centrer les boutons horizontalement
            verticalAlignment = Alignment.CenterVertically // Alignement centré verticalement
        ) {
            // Bouton Home
            IconButton(onClick = {}) {
                Icon(Icons.Filled.Home, contentDescription = "Home")
            }

            // Spacer pour espacer les boutons
            Spacer(modifier = Modifier.width(32.dp)) // Espacement personnalisé entre les boutons

            // Bouton Edit
            IconButton(onClick = {}) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit")
            }
        }
    }
}

