package com.example.mushtools.models

import com.example.mushtools.R

sealed class Items_Setas (
    val nombre: String,
    val imagen: Int,
    val descripcion: String
){
    object Item1 : Items_Setas(
        "Lentinula edodes / Shiitake",
        R.drawable.seta1,
        "Nombre: Lentinula edodes / Shiitake \n\nTipo: Comestible\n \n La seta china o shiitake (Lentinula edodes) es una seta comestible de color marrón y aroma intenso originaria de Asia Oriental."

    )
    object Item2 : Items_Setas(
        "Infundibulicybe geotropa / Platera ",
        R.drawable.seta2,
        "Nombre: Infundibulicybe geotropa / Platera o cabeza de fraile \n \nTipo: Comestible\n\nInfundibulicybe geotropa es un hongo basidiomiceto de la familia Tricholomataceae.1\u200B Es una especie muy conocida en ciertas zonas donde se recoge para cocinarlo. Crece hasta muy avanzada la estación, noviembre y diciembre."

    )
    object Item3 : Items_Setas(
        "Agaricus bisporus / Champiñon común",
        R.drawable.seta3,
        "Nombre: Agaricus bisporus / Champiñon común\n \nTipo: Comestible\n\nEl champiñón común, champiñón de París —cuyo nombre científico es Agaricus bisporus—1\u200B es una especie de hongo basidiomiceto de la familia Agaricales nativo de Europa y América del Norte, cultivado extensamente para su uso en gastronomía. Es la especie de hongo comestible más comúnmente usada para la cocina."

    )
}