package com.example.mushtools.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Brightness3
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.ModeNight
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.Nightlight
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

    @Composable
    fun Tiempo() {
        var isLoading by remember { mutableStateOf(true) }
        var bajado by remember { mutableStateOf("") }
        var searchText by remember { mutableStateOf("") }
        var lugarCargado by remember { mutableStateOf("Madrid") } // Estado para almacenar el nombre del sitio cargado

        val lugares = mapOf("Barberà del Vallès" to 6926,
            "Barcelona" to 7183,
            "Barri d'Artigas" to 25101,
            "Barri Gòtic" to 25768,
            "Begues" to 7163,
            "Bellaterra" to 24806,
            "Bellprat" to 7162,
            "Berga" to 7161,
            "Bigues i Riells" to 7160,
            "Bonayre" to 22864,
            "Borredà" to 7159,
            "Breda" to 22785,
            "Cabrera d'Igualada" to 7155,
            "Cabrera de Mar" to 7154,
            "Cabrianes" to 22585,
            "Cabrils" to 7153,
            "Calaf" to 7152,
            "Calders" to 7149,
            "Caldes d'Estrac" to 7151,
            "Caldes de Montbui" to 7150,
            "Caldes Internacional Golf Course" to 121476,
            "Calella" to 7148,
            "Calldetenes" to 7146,
            "Callús" to 7145,
            "Calonge de Segarra" to 7147,
            "Camp Nou" to 25822,
            "Campdessens" to 22465,
            "Campins" to 7144,
            "Campo Público Golf Sant Joan" to 121485,
            "Can Tunis" to 25090,
            "Canet de Mar" to 7143,
            "Canovelles" to 7142,
            "Cànoves i Samalús" to 7141,
            "Canyamars" to 22344,
            "Canyelles" to 7140,
            "Capellades" to 7139,
            "Capolat" to 7138,
            "Cardedeu" to 7137,
            "Cardona" to 7136,
            "Carme" to 7135,
            "Caserío de Canaletas" to 22166,
            "Caserío de Ca’n Rosell" to 22165,
            "Casserres" to 7134,
            "Castell de l'Areny" to 7133,
            "Castelladral" to 22130,
            "Castellar de n'Hug" to 7132,
            "Castellar del Riu" to 7131,
            "Castellar del Vallès" to 7130,
            "Castellbell i el Vilar" to 7129,
            "Castellbisbal" to 7128,
            "Castellcir" to 7127,
            "Castelldefels" to 7126,
            "Castellet" to 22119,
            "Castellet i la Gornal" to 7125,
            "Castellfollit de Riubregós" to 7124,
            "Castellfollit del Boix" to 7123,
            "Castellgalí" to 7122,
            "Castellnou de Bages" to 7121,
            "Castellolí" to 7120,
            "Castellterçol" to 7119,
            "Castellví de la Marca" to 7118,
            "Castellví de Rosanes" to 7117,
            "Ca’n Bargalló" to 22396,
            "Ca’n Bros" to 22395,
            "Ca’n Margarit" to 22358,
            "Ca’n Prats" to 22353,
            "Centelles" to 7116,
            "Centre" to 25823,
            "Cercs" to 6914,
            "Cerdanyola del Vallès" to 6916,
            "Cervelló" to 7115,
            "Ciutat Vella" to 25749,
            "Clariana de Cardener" to 21734,
            "Clot del Moro" to 21731,
            "Club de Golf Can Bosch Sant Feliu" to 121479,
            "Club de Golf de Barcelona" to 121478,
            "Club de Golf La Mola" to 121480,
            "Club de Golf Llavaneras" to 121481,
            "Club de Golf Montanyá" to 121477,
            "Club de Golf Sant Cugat" to 121482,
            "Club de Golf Terramar" to 121483,
            "Club de Golf Vallromanes" to 121474,
            "Club de Golf Vilacis-Taradell" to 121475,
            "Collbató" to 7114,
            "Collblanc i La Torrassa" to 25080,
            "Collsuspina" to 7113,
            "Conanglell" to 21649,
            "Copons" to 7112,
            "Corbera de Llobregat" to 7111,
            "Cornellà de Llobregat" to 7110,
            "Corró de Vall" to 21554,
            "Cubelles" to 7109,
            "Diagonal Mar" to 25819,
            "Dosrius" to 7108,
            "Dreta de l'Eixample" to 25816,
            "Eixample" to 25743,
            "Eixample Dreta" to 25769,
            "Eixample Esquerra" to 25770,
            "El Amunt" to 21198,
            "El Arrabal de Coll d’Arbós" to 21196,
            "El Arrabal Mas" to 25074,
            "El Arrabal Torrelletas" to 25073,
            "El Bruc" to 7158,
            "El Brull" to 7157,
            "El Canyet" to 21166,
            "el Canyet" to 22343,
            "El Carmel" to 25797,
            "El Carrer de Dalt" to 21161,
            "El Carrer del Canonge" to 25071,
            "El Carrer d’Abaix" to 21162,
            "el Clot" to 25081,
            "el Coll" to 25796,
            "El Guix" to 21104,
            "El Hospital" to 21100,
            "El Mas Blau" to 24638,
            "El Masnou" to 7065,
            "El Papiol" to 7024,
            "El Pla del Penedès" to 7018,
            "El Poble Nou" to 25000,
            "El Prat de Llobregat" to 25087,
            "El Raval" to 25824,
            "El Vendrell" to 25059,
            "Esparreguera" to 7099,
            "Esplugues de Llobregat" to 7098,
            "Espunyola" to 7097,
            "Estany" to 7096,
            "Fals" to 7095,
            "Fígols" to 7094,
            "Fogars de Montclús" to 7093,
            "Fogars de la Selva" to 7092,
            "Folgueroles" to 7091,
            "Fonollosa" to 7090,
            "Font-Rubí" to 7089,
            "Font-rubí" to 22291,
            "Font-rubí" to 22292,
            "Fontpineda" to 7088,
            "Franqueses del Vallès" to 7087,
            "Gallifa" to 7086,
            "Garriga" to 7085,
            "Gavà" to 7084,
            "Gelida" to 7083,
            "Gironella" to 7082,
            "Gisclareny" to 7081,
            "Golpejar de la Tercia" to 21040,
            "Golpejar de los Pinares" to 21039,
            "Granollers" to 7079,
            "Gualba" to 7078,
            "Guardiola de Berguedà" to 7077,
            "Gurb" to 7076,
            "Igualada" to 7075,
            "Jorba" to 7074,
            "La Ametlla" to 24204,
            "La Ametlla de Mar" to 24205,
            "La Baells" to 24044,
            "La Beguda Alta" to 24212,
            "La Beguda Baja" to 24211,
            "La Beguda de Gaià" to 24210,
            "La Bisbal del Penedès" to 7072,
            "La Clua" to 24201,
            "La Colonia Rosal" to 21069,
            "La Costa" to 21068,
            "La Cresta" to 21063,
            "La Floresta" to 7058,
            "La Garriga" to 7057,
            "La Granada" to 24207,
            "La Guingueta d'Àneu" to 24208,
            "La Llacuna" to 7056,
            "La Llagosta" to 7055,
            "La Llavinera" to 24039,
            "La Lloma" to 21045,
            "La Llosa de Ranes" to 24209,
            "La Masó" to 24206,
            "La Mora" to 24048,
            "La Móra" to 24047,
            "La Móra" to 24046,
            "La Móra" to 24045,
            "La Nova Esquerra de l'Eixample" to 25817,
            "La Pineda" to 24053,
            "La Platja de Calafell" to 24052,
            "La Portella" to 24049,
            "La Puebla de Castro" to 24058,
            "La Ràpita" to 24059,
            "La Riera" to 24202,
            "La Roca del Vallès" to 7054,
            "La Roda de Bera" to 24061,
            "La Salut" to 25820,
            "La Seu d'Urgell" to 7053,
            "La Torre de Claramunt" to 7052,
            "La Torre de Fontaubella" to 7051,
            "La Torre de Oristà" to 7050,
            "La Torre del Español" to 7049,
            "La Torre del Llorito" to 7048,
            "La Torre de Lloris" to 24026,
            "La Torre de Rialb" to 7047,
            "La Torre de Riu" to 24060,
            "La Torre de Sant Pere" to 7046,
            "La Torre de Vespella" to 7045,
            "La Torre d'en Besora" to 24063,
            "La Torre d'en Doménec" to 24062,
            "La Torre d'Oristà" to 24064,
            "La Torre Solsona" to 24029,
            "La Valldan" to 21735,
            "La Vall d'Uixó" to 24065,
            "La Vallençana" to 21188,
            "La Verneda i la Pau" to 25082,
            "La Vila de Gràcia" to 25771,
            "La Vila Olímpica del Poblenou" to 25821,
            "La Vilella Alta" to 24203,
            "Les Cabanyes" to 7044,
            "Les Fonts" to 7043,
            "Les Franqueses del Vallès" to 7042,
            "Les Gunyoles" to 7041,
            "Les Planes d'Hostoles" to 7040,
            "Les Planes d'Hostoles" to 22375,
            "Les Planes de Marbella" to 24032,
            "Les Planes de Marbella" to 24031,
            "Les Planes d'Urgell" to 24030,
            "Les Roquetes" to 7039,
            "L'Hospitalet de Llobregat" to 7038,
            "Llinars del Vallès" to 7036,
            "Llissá de Munt" to 7035,
            "Llissá de Vall" to 7034,
            "Lliçà d'Amunt" to 7037,
            "Lliçà de Vall" to 7033,
            "Lluçà" to 7032,
            "Malgrat de Mar" to 7031,
            "Malla" to 7030,
            "Manlleu" to 7029,
            "Manresa" to 7028,
            "Marganell" to 7027,
            "Martorell" to 7026,
            "Martorelles" to 7025,
            "Masquefa" to 7023,
            "Matadepera" to 7022,
            "Mataró" to 7021,
            "Mediona" to 7020,
            "Miajadas" to 24141,
            "Mieres" to 7019,
            "Moia" to 7018,
            "Molins de Rei" to 7017,
            "Mollet del Vallès" to 7016,
            "Monistrol de Calders" to 7015,
            "Monistrol de Montserrat" to 7014,
            "Montagut i Oix" to 7013,
            "Montbrió del Camp" to 7012,
            "Montcada i Reixac" to 7011,
            "Montclar" to 7010,
            "Montcortal" to 24027,
            "Montferri" to 24028,
            "Montgat" to 7009,
            "Montmajor" to 7008,
            "Montmaneu" to 7007,
            "Montmeló" to 7006, "Montornès del Vallès" to 7047,
            "Montseny" to 7046,
            "Muntanyola" to 7054,
            "Mura" to 7044,
            "Navarcles" to 7043,
            "Navàs" to 7042,
            "Navès" to 17534,
            "Nou Barris" to 25127,
            "Ocata" to 25131,
            "Òdena" to 7040,
            "Olèrdola" to 7038,
            "Olesa de Bonesvalls" to 7037,
            "Olesa de Montserrat" to 7036,
            "Olivella" to 7035,
            "Olost" to 7034,
            "Olvan" to 7039,
            "Olzinellas" to 17299,
            "Ordal" to 17257,
            "Orís" to 7033,
            "Oristà" to 7032,
            "Orpí" to 7031,
            "Òrrius" to 7030,
            "Pacs del Penedès" to 7029,
            "Palafolls" to 7028,
            "Palau-solità i Plegamans" to 7027,
            "Pallejà" to 7026,
            "Palmerola" to 17014,
            "Palou" to 17006,
            "Parets del Vallès" to 7023,
            "Pedralbes" to 25795,
            "Perafita" to 7022,
            "Piera" to 7021,
            "Pineda de Mar" to 7019,
            "Plegamans" to 16528,
            "Poble Sec" to 25787,
            "Polinyà" to 7015,
            "Pontons" to 7014,
            "Portell" to 16449,
            "Prats de Lluçanès" to 7012,
            "Premià de Dalt" to 6952,
            "Premià de Mar" to 7010,
            "Provençals" to 25821,
            "Puig-reig" to 7008,
            "Puigdàlber" to 7009,
            "Puigmoltó" to 16265,
            "Pujalt" to 7007,
            "Querol" to 16215,
            "Rajadell" to 7006,
            "Real Club de Golf El Prat" to 121484,
            "Rellinars" to 7004,
            "Riells del Fai" to 15794,
            "Ripollet" to 7003,
            "Rocafort" to 15675,
            "Roda de Ter" to 7000,
            "Rubí" to 6999,
            "Rubió" to 6998,
            "Rupit i Pruit" to 6997,
            "Sabadell" to 6996,
            "Saderra" to 15516,
            "Sagàs" to 6995,
            "Sagrada Família" to 25818,
            "Saldes" to 6993,
            "Sallent" to 6992,
            "Samalús" to 24994,
            "San Adrian de Besos" to 24799,
            "San Vicenç de Torelló" to 14751,
            "Sant Adrià de Besòs" to 6989,
            "Sant Agustí de Lluçanès" to 6988,
            "Sant Andreu de la Barca" to 6987,
            "Sant Andreu de Llavaneres" to 6986,
            "Sant Andreu de Palomar" to 24992,
            "Sant Antoni de Vilamajor" to 6985,
            "Sant Bartomeu de la Quadra" to 15392,
            "Sant Bartomeu del Grau" to 6984,
            "Sant Boi de Llobregat" to 6983,
            "Sant Boi de Lluçanès" to 6982,
            "Sant Cebrià de Vallalta" to 6980,
            "Sant Celoni" to 6981,
            "Sant Climent de Llobregat" to 6979,
            "Sant Cugat del Vallès" to 6978,
            "Sant Cugat Sesgarrigues" to 6977,
            "Sant Esteve de Palautordera" to 6976,
            "Sant Esteve Sesrovires" to 6975,
            "Sant Feliu de Codines" to 6973,
            "Sant Feliu de Llobregat" to 6972,
            "Sant Feliu de Lluelles" to 15302,
            "Sant Feliu del Racó" to 15301,
            "Sant Feliu Sasserra" to 6971,
            "Sant Fost de Campsentelles" to 6974,
            "Sant Fruitós de Bages" to 6970,
            "Sant Genís" to 14852,
            "Sant Genís de Palafolls" to 15285,
            "Sant Hipòlit de Voltregà" to 6968,
            "Sant Iscle de Vallalta" to 6990,
            "Sant Jaume de Frontanyà" to 6967,
            "Sant Jaume Sas Oliveras" to 14804,
            "Sant Joan de Fàbregues" to 24988,
            "Sant Joan de Vilatorrada" to 6966,
            "Sant Joan Despí" to 6964,
            "Sant Julià de Cerdanyola" to 6963,
            "Sant Julià de Vilatorta" to 6962,
            "Sant Julià Sassorba" to 14803,
            "Sant Just Desvern" to 6961,
            "Sant Llorenç d'Hortons" to 6960,
            "Sant Llorenç Savall" to 6959,
            "Sant Martí" to 25748,
            "Sant Martí d'Albars" to 6956,
            "Sant Martí de Centelles" to 6958,
            "Sant Martí de Tous" to 6957,
            "Sant Martí Sarroca" to 6955,
            "Sant Martí Sescorts" to 24986,
            "Sant Martí Sesgueioles" to 6954,
            "Sant Mateu de Bages" to 6953,
            "Sant Maurici de la Quar" to 15153,
            "Sant Miquel de Balenyà" to 23361,
            "Sant Pau de Pinós" to 15113,
            "Sant Pau d’Ordal" to 14779,
            "Sant Pere de Reixac" to 15949,
            "Sant Pere de Ribes" to 6951,
            "Sant Pere de Riudebitlles" to 6950,
            "Sant Pere de Torelló" to 6949,
            "Sant Pere de Vilamajor" to 6948,
            "Sant Pere Sallavinera" to 6994,
            "Sant Pere, Santa Caterina i La Ribera" to 25032,
            "Sant Pol de Mar" to 6947,
            "Sant Quintí de Mediona" to 6946,
            "Sant Quirze de Besora" to 6945,
            "Sant Quirze del Vallès" to 6944,
            "Sant Quirze Safaja" to 6943,
            "Sant Romà de La Clusa" to 14777,
            "Sant Sadurní d'Anoia" to 6942,
            "Sant Sadurní d'Osormort" to 6941,
            "Sant Salvador de Guardiola" to 7085,
            "Sant Sebastià de Montmajor" to 15029,
            "Sant Vicenç de Castellet" to 6940,
            "Sant Vicenç de Montalt" to 6939,
            "Sant Vicenç de Torelló" to 6938,
            "Sant Vicenç dels Horts" to 6937,
            "Santa Cecília de Voltregà" to 6935,
            "Santa Coloma de Cervelló" to 6934,
            "Santa Coloma de Gramenet" to 6933,
            "Santa Creu dels Juglars" to 14995,
            "Santa Eugènia de Berga" to 6932,
            "Santa Eugènia del Congost" to 24983,
            "Santa Eulàlia de Puigoriol" to 14956,
            "Santa Eulàlia de Riuprimer" to 6931,
            "Santa Eulàlia de Ronçana" to 6930,
            "Santa Fe" to 14952,
            "Santa Fe del Penedès" to 6929,
            "Santa Margarida de Montbui" to 6928,
            "Santa Margarida i els Monjos" to 6927,
            "Santa Maria d'Oló" to 6920,
            "Santa Maria de Besora" to 6925,
            "Santa Maria de Corcó" to 6924,
            "Santa Maria de Martorelles" to 6922,
            "Santa Maria de Merlès" to 6923,
            "Santa Maria de Miralles" to 6921,
            "Santa Maria de Montcada" to 14914,
            "Santa Maria de Palautordera" to 6919,
            "Santa María de Villalba" to 14910,
            "Santa María del Camí" to 14922,
            "Santa Perpètua de Mogoda" to 6918,
            "Santa Susanna" to 6917,
            "Santpedor" to 6991,
            "Sants" to 24978,
            "Sants-Montjuïc" to 25747,
            "Sarrià-Sant Gervasi" to 25745,
            "Sentfores" to 14612,
            "Sentmenat" to 6915,
            "Serrateix" to 14574,
            "Seva" to 6913,
            "Sitges" to 6912,
            "Sobremunt" to 6911,
            "Sora" to 6910,
            "Subirats" to 6909,
            "Súria" to 6908,
            "Tagamanent" to 6906,
            "Talamanca" to 6905,
            "Taradell" to 6904,
            "Tavèrnoles" to 6907,
            "Tavertet" to 6902,
            "Teià" to 6901,
            "Terradelles" to 24793,
            "Terrasola" to 14065,
            "Terrassa" to 6903,
            "Tiana" to 6900,
            "Tona" to 6899,
            "Tordera" to 6898,
            "Torelló" to 6897,
            "Torrelavit" to 6895,
            "Torrelles de Foix" to 6894,
            "Torrelles de Llobregat" to 6893,
            "Torrelletas" to 13900,
            "Ullastrell" to 6892,
            "Vacarisses" to 6891,
            "Vallbona d'Anoia" to 6890,
            "Vallcarca" to 25817,
            "Vallcebre" to 6889,
            "Valldoreix" to 13392,
            "Vallgorguina" to 6888,
            "Vallirana" to 6887,
            "Vallmanya" to 13357,
            "Vallromanes" to 6886,
            "Vallvidrera" to 25794,
            "Veciana" to 6885,
            "Vespella" to 13155,
            "Vic" to 6884,
            "Vilada" to 6883,
            "Viladecans" to 6880,
            "Viladecavalls" to 6882,
            "Viladomíu" to 13083,
            "Viladomíu Vella" to 13082,
            "Viladordis" to 13078,
            "Vilafranca del Penedès" to 6881,
            "Vilalba Sasserra" to 6874,
            "Vilalleons" to 24964,
            "Vilanova de Sau" to 6879,
            "Vilanova del Camí" to 6878,
            "Vilanova del Vallès" to 6877,
            "Vilanova i la Geltrú" to 6876,
            "Vilaseca" to 13021,
            "Vilassar de Dalt" to 6969,
            "Vilassar de Mar" to 6965,
            "Vilobí del Penedès" to 6875,
            "Vinyoles" to 12279,
            "Viver" to 12237,
            "Viver i Serrateix" to 6873,
            "Zona Franca" to 25026,
            "Zona Hotelera" to 24628,
            "Zona Universitaria" to 25790
        )

        var url =
            "https://api.tutiempo.net/json/?lan=es&apid=4xTaq4qXXza9N4u&lid=3768"

        LaunchedEffect(key1 = true) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = URL(url).readText()
                bajado = response
                isLoading = false
                Log.d("MainActivity", "Datos cargados exitosamente desde URL: $url")
            }
        }


        Column(

            modifier = Modifier.fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.inversePrimary,
                    shape = RoundedCornerShape(10.dp)

                )


        ) {
            // Campo de búsqueda
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Buscar lugar") },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()

            )
            Button(
                onClick = {
                    // Obtener el número correspondiente al lugar buscado
                    val lugarNumero = lugares[searchText]
                    if (lugarNumero != null) {
                        // Construir la URL con el número del lugar
                        url = "https://api.tutiempo.net/json/?lan=es&apid=4xTaq4qXXza9N4u&lid=$lugarNumero"
                        // Volver a cargar los datos con la nueva URL
                        isLoading = true
                        CoroutineScope(Dispatchers.IO).launch {
                            val response = URL(url).readText()
                            bajado = response
                            isLoading = false
                            lugarCargado = searchText // Actualizar el nombre del sitio cargado
                            Log.d("MainActivity", "Datos cargados exitosamente para el lugar: $searchText")
                        }
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Cargar Datos")
            }

            if (isLoading) {
                Log.d("MainActivity", "Cargando datos desde URL: $url")
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else {
                Log.d("MainActivity", "Datos cargados exitosamente")
                val dadesLlegits = Gson().fromJson(bajado, InfoTiempo::class.java)
                // Obtener el número correspondiente al lugar buscado
                val lugarNumero = lugares[searchText]
                if (lugarNumero != null) {
                    // Construir la URL con el número del lugar
                    url = "https://api.tutiempo.net/json/?lan=es&apid=zwDX4azaz4X4Xqs&lid=$lugarNumero"
                }
                // Mostrar el nombre del sitio cargado
                Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.padding(start = 12.dp)) {

                    Icon(
                        Icons.Outlined.Place,
                        contentDescription = "Icono",
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Sitio: $lugarCargado",
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                // Fecha
                Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.padding(start = 12.dp)) {
                    Icon(
                        Icons.Outlined.CalendarToday,
                        contentDescription = "Icono",
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Fecha: ${dadesLlegits?.day1?.date}",
                        modifier = Modifier.padding(12.dp)
                    )
                }

                // Temperatura Máxima
                Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.padding(start = 12.dp)) {
                    Icon(
                        Icons.Outlined.Thermostat,
                        contentDescription = "Icono",
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Temperatura Máxima: ${dadesLlegits?.day1?.temperatureMax}",
                        modifier = Modifier.padding(12.dp)
                    )
                }

                // Temperatura Mínima
                Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.padding(start = 12.dp)) {
                    Icon(
                        Icons.Outlined.Thermostat,
                        contentDescription = "Icono",
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Temperatura Mínima: ${dadesLlegits?.day1?.temperatureMin}",
                        modifier = Modifier.padding(12.dp)
                    )
                }

                // Estado del Tiempo
                Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.padding(start = 12.dp)) {
                    Icon(
                        Icons.Outlined.Cloud,
                        contentDescription = "Icono",
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Estado del Tiempo: ${dadesLlegits?.day1?.text}",
                        modifier = Modifier.padding(12.dp)
                    )
                }

                // Humedad
                Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.padding(start = 12.dp)) {
                    Icon(
                        Icons.Outlined.WaterDrop,
                        contentDescription = "Icono",
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Humedad: ${dadesLlegits?.day1?.humidity}",
                        modifier = Modifier.padding(12.dp)
                    )
                }

                // Velocidad del Viento
                Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.padding(start = 12.dp)) {
                    Icon(
                        Icons.Outlined.Speed,
                        contentDescription = "Icono",
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Velocidad del Viento: ${dadesLlegits?.day1?.wind} km/h",
                        modifier = Modifier.padding(12.dp)
                    )
                }

                // Dirección del Viento
                Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.padding(start = 12.dp)) {
                    Icon(
                        Icons.Outlined.Navigation,
                        contentDescription = "Icono",
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Dirección del Viento: ${dadesLlegits?.day1?.windDirection}",
                        modifier = Modifier.padding(12.dp)
                    )
                }

                // Amanecer
                Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.padding(start = 12.dp)) {
                    Icon(
                        Icons.Outlined.WbSunny,
                        contentDescription = "Icono",
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Amanecer: ${dadesLlegits?.day1?.sunrise}",
                        modifier = Modifier.padding(12.dp)
                    )
                }

                // Atardecer
                Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.padding(start = 12.dp)) {
                    Icon(
                        Icons.Outlined.Brightness3,
                        contentDescription = "Icono",
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Atardecer: ${dadesLlegits?.day1?.sunset}",
                        modifier = Modifier.padding(12.dp)
                    )
                }

                // Salida de la Luna
                Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.padding(start = 12.dp)) {
                    Icon(
                        Icons.Outlined.Nightlight,
                        contentDescription = "Icono",
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Salida de la Luna: ${dadesLlegits?.day1?.moonrise}",
                        modifier = Modifier.padding(12.dp)
                    )
                }

                // Puesta de la Luna
                Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.padding(start = 12.dp)) {
                    Icon(
                        Icons.Outlined.NightsStay,
                        contentDescription = "Icono",
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Puesta de la Luna: ${dadesLlegits?.day1?.moonset}",
                        modifier = Modifier.padding(12.dp)
                    )
                }

                // Fase de la Luna
                Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.padding(start = 12.dp)) {
                    Icon(
                        Icons.Outlined.ModeNight,
                        contentDescription = "Icono",
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Fase de la Luna: ${dadesLlegits?.day1?.moonset}",
                        modifier = Modifier.padding(12.dp)
                    )
                }

            }
        }
    }

