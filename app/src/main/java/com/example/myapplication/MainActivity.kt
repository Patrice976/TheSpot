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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
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
                            HomeView()
                            HomeView2()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeView(modifier: Modifier = Modifier) {
    //Premier item
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp)
            .background(Color.Cyan),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Affichage de l'image
        Image(
            painter = painterResource(id = R.drawable.banane), // Remplace par ton image
            contentDescription = "Image de banane",
            modifier = Modifier
                .size(300.dp) // Taille de l'image
        )
        Text(
            "Coucou les petites bananes 1"
        )
    }
}

@Composable
fun HomeView2(modifier: Modifier = Modifier) {
    //Deuxième item
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp)
            .background(Color.Yellow),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.banane),
            contentDescription = "Image de banane",
            modifier = Modifier.size(300.dp)
        )
        Text(
            "Coucou les petites bananes 2",
            modifier = Modifier.padding(top = 8.dp) // Réduit l'espace entre l'image et le texte
        )
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
