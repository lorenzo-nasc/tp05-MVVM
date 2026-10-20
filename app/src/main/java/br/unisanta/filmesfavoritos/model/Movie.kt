package br.unisanta.filmesfavoritos.model

/**
 * Model: representa um filme favorito do usuário.
 * @param rating nota de 0 a 10
 * @param photoUrl URL (ou URI da galeria) da foto/pôster, carregada com o Coil
 */
data class Movie(
    val id: Long,
    val name: String,
    val rating: Float,
    val photoUrl: String
)
