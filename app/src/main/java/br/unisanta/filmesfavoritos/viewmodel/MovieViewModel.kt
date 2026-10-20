package br.unisanta.filmesfavoritos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import br.unisanta.filmesfavoritos.model.Movie
import br.unisanta.filmesfavoritos.repository.MovieRepository

/** Resumo exibido no subtítulo da Toolbar. */
data class MovieSummary(val count: Int, val average: Float)

/**
 * ViewModel: concentra toda a lógica (listar, validar, adicionar, editar,
 * remover). A Activity só observa o LiveData e repassa as ações do usuário.
 * Sobrevive à rotação de tela, então a lista não se perde.
 */
class MovieViewModel : ViewModel() {

    private val repository = MovieRepository

    private val _movies = MutableLiveData(repository.getAll())
    val movies: LiveData<List<Movie>> = _movies

    val summary: LiveData<MovieSummary> = movies.map { list ->
        val avg = if (list.isEmpty()) 0f else list.map { it.rating }.average().toFloat()
        MovieSummary(list.size, avg)
    }

    /**
     * Cria (id == null) ou atualiza um filme.
     * @return false se os dados forem inválidos (nome vazio).
     */
    fun saveMovie(id: Long?, name: String, rating: Float, photoUrl: String): Boolean {
        val cleanName = name.trim()
        if (cleanName.isEmpty()) return false

        val cleanRating = rating.coerceIn(0f, 10f)
        val cleanUrl = photoUrl.trim()

        if (id == null) {
            repository.add(cleanName, cleanRating, cleanUrl)
        } else {
            repository.update(Movie(id, cleanName, cleanRating, cleanUrl))
        }
        refresh()
        return true
    }

    /** @return posição antiga do filme, para permitir desfazer. */
    fun deleteMovie(movie: Movie): Int {
        val index = repository.delete(movie.id)
        refresh()
        return index
    }

    fun restoreMovie(movie: Movie, index: Int) {
        repository.restore(movie, index)
        refresh()
    }

    private fun refresh() {
        _movies.value = repository.getAll()
    }
}
