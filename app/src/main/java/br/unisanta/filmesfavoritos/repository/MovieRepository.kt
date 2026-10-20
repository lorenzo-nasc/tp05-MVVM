package br.unisanta.filmesfavoritos.repository

import br.unisanta.filmesfavoritos.model.Movie

/**
 * Fonte de dados (em memória). Fica separada do ViewModel para que,
 * futuramente, possa ser trocada por Room ou uma API sem mexer na View.
 */
object MovieRepository {

    private const val TMDB = "https://image.tmdb.org/t/p/w500"

    private val movies = mutableListOf(
        Movie(1, "Interstellar", 9.5f, "$TMDB/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg"),
        Movie(2, "O Poderoso Chefão", 10f, "$TMDB/3bhkrj58Vtu7enYsRolD1fZdja1.jpg"),
        Movie(3, "Um Sonho de Liberdade", 9f, "$TMDB/q6y0Go1tsGEsmtFryDOJo3dEmqu.jpg"),
        Movie(4, "A Origem", 8.5f, "$TMDB/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg")
    )

    private var nextId = 5L

    fun getAll(): List<Movie> = movies.toList()

    fun add(name: String, rating: Float, photoUrl: String) {
        movies.add(Movie(nextId++, name, rating, photoUrl))
    }

    fun update(movie: Movie) {
        val index = movies.indexOfFirst { it.id == movie.id }
        if (index != -1) movies[index] = movie
    }

    /** Remove e devolve a posição antiga (usada no "Desfazer"). */
    fun delete(id: Long): Int {
        val index = movies.indexOfFirst { it.id == id }
        if (index != -1) movies.removeAt(index)
        return index
    }

    fun restore(movie: Movie, index: Int) {
        movies.add(index.coerceIn(0, movies.size), movie)
    }
}
