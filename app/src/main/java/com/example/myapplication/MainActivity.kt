package com.example.myapplication

import android.os.Bundle
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
import coil.compose.rememberAsyncImagePainter


//LISTE DES CLASSES
// Element principal c'est ce qui va s'afficher sur notre mobile
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                     }
                }
            }
        }
    }
}

data class Spot(
    val imageUrl: String,   //URL de l'image
    val name: String,   //Nom du spot
    val location: String //Lieu du spot
)



//LISTE DES COMPOSANTS
//Composant permetant d'affichier un spot de surf il contient 3 variable img nom et lieu
@Composable
fun SpotView(modifier: Modifier = Modifier, spot : Spot = Spot (
    imageUrl = "R.drawable.spot_idea",
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
        painter = painterResource(id= R.drawable.main_background),
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
    }}

// url de l'API : https://api.airtable.com/v0/appEksYm9WhIjEtus/tblRuaa61gtDvzAt2/
// token : patpzSBgSr3dnevwc.a4de7204ffccf9cb98878db35d702f98de1136cb75016c8943e7691e9cc8dc53