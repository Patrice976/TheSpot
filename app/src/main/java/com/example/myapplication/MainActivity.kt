package com.example.myapplication

import android.health.connect.ReadRecordsResponse
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers


//LISTE DES INTERFACES

/*Etablis la structure nécessaire pour récupérer les spots
interface ApiService {
    @GET("v0/appEksYm9WhIjEtus/tblRuaa61gtDvzAt2/") //URL de l'API
    suspend fun getSpots(
        @Header("Authorization") token: String //le token passé seras envoyé dans l'en tête de la requête
    ): List<Spot>  // Retourne une liste d'objet spots */

/*LISTE DES VARIABLES
val token = " Bearer patpzSBgSr3dnevwc.a4de7204ffccf9cb98878db35d702f98de1136cb75016c8943e7691e9cc8dc53 "

val BASE_URL = "https://api.airtable.com/v0/appEksYm9WhIjEtus/tblRuaa61gtDvzAt2/"

//setup retrofit (indispensable pour effectuer un fetch de l'API)
val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

//creation d'une instance de ApiService (interface définis plus haut)
val apiService = retrofit.create(ApiService::class.java) */

/*LISTE DES FONCTIONS
//fonction asynchrone pour récupérer les spots issus de l'API
suspend fun fetchSpots() {
    try {
        val spots = apiService.getSpots(token) //Récupère la liste des spots
        Log.d("perso_log", "Spots récupérés : $spots")
    } catch (e: Exception) {
        //gestion des erreurs
        e.printStackTrace()
    }
} */

//LISTE DES CLASSES
// Element principal c'est ce qui va s'afficher sur notre mobile
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*Appel fetchSpots() dans un CoroutineScope
        CoroutineScope(Dispatchers.IO).launch {
            fetchSpots()
        }*/
        enableEdgeToEdge()
        setContent { //contenu à afficher
            MyApplicationTheme {
                // Scaffold est reponssable de la structure et de la mise en page
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { NavigationBarWithButtons() } // Ajout de la barre de navigation en bas
                ) { innerPadding ->
                    Box( //permet de superposer les éléments
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Screen() // affichage principale du contenu contient les différentes vu et le scrolling
                        // Manque du code non ? voir Tiph
                     }
                }
            }
        }
    }


RetrofitClient.apiService.getSurfSpot().enqueue(object : Callback<SurfSpotResponse> {
    override fun onResponse(call: Call<SurfSpotResponse>, response: Response<SurfSpotResponse>) {
        if (response.isSuccessful) {
            val records = response.body()?.records
            records?.forEach {
                val fields = it.fields
                println("Destination: $fields.destination}")
            }
        }
    }

}

/* data class Spot(
    val imageUrl: String,   //URL de l'image
    val name: String,   //Nom du spot
    val location: String //Lieu du spot
) */



//LISTE DES COMPOSANTS
//Composant permetant d'affichier un spot de surf il contient 3 variable img nom et lieu
@Composable
fun SpotView(modifier: Modifier = Modifier, spot : Spot = Spot (
    imageUrl = "https://dr4f7gkjfgtsc.cloudfront.net/images/contents/travel-around-australia-for-the-best-beaches-in-the-world-1565717370.jpg",
    name = "Un spot de Surf par défaut",
    location = "Quelque part pas loin de la mer"
)
){

    Box(//div qui contiendra l'image et le texte
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White.copy(alpha = 0.8f)) //Applique un fond blanc avec une opacité de 80 % (0.8f)
    ) {
        Column( // Permet d'emplier les éléments les uns en dessous des autres
            modifier = modifier
                .fillMaxWidth()
                .padding(1.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Affichage de l'image
            Image(
                painter =  rememberAsyncImagePainter(spot.imageUrl),
                contentDescription = spot.name,
                modifier = Modifier
                    .size(300.dp) // Taille de l'image
            )
            //texte propre au nom du spot
            Text(
                text=spot.name
            )
            //texte propre au lieu du spot
            Text(
                text = spot.location
            )
        }
    }
}


//composant responssable de la création de la bar de navigation et de ses bouttons
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

//composent parent qui contient le fond et la logique de scroll c'est à l'intérreieur
//de celle ci qu'on appellera nos autre composants
@Composable
fun Screen() {
    Image(
        painter = painterResource(id = R.drawable.main_background),
        contentDescription = "Une planche plantée dans du sable ",
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.fillMaxSize()
    )
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        repeat(20) {
            SpotView()
        }
    }
}
}

data class SurfSpotRecord(
    val id: String,
    val fields: SurfSpotFields
)

data class SurfSpotFields(
    val destination: String,
    val difficulty: String,
    val surfBreak: String,
    val photos: String,
    val peakBegins: String,
    val peakEnds: String,
    val magicSeaweedLink: String,
    val influencers: List<String>,
    val travellers: List<String>,
    val geocode: String
)

data class SurfSpotResponse(
    val records: List<SurfSpotRecord>
)

    interface AirTableApi {
        //endpoint ici pour recuperer les données
        @GET("v0/appEksYm9WhIjEtus/tblRuaa61gtDvzAt2/")
        @Headers("Authorization : Bearer patpzSBgSr3dnevwc.a4de7204ffccf9cb98878db35d702f98de1136cb75016c8943e7691e9cc8dc53")
        fun getSurfSpots(): Call<SurfSpotResponse>
    }
    object RetrofitClient {
      val apiService: AirTableApi by lazy {
          Retrofit.Builder()
              .baseUrl("https://api.airtable.com/")
              .addConverterFactory(GsonConverterFactory.create())
              .build()
              .create(AirTableApi::class.java)
      }
  }

// url de l'API : https://api.airtable.com/v0/appEksYm9WhIjEtus/tblRuaa61gtDvzAt2/
// token : patpzSBgSr3dnevwc.a4de7204ffccf9cb98878db35d702f98de1136cb75016c8943e7691e9cc8dc53