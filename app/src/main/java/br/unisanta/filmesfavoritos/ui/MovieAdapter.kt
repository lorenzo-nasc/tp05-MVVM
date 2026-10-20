package br.unisanta.filmesfavoritos.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.unisanta.filmesfavoritos.R
import br.unisanta.filmesfavoritos.databinding.ItemMovieBinding
import br.unisanta.filmesfavoritos.model.Movie
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder

class MovieAdapter(
    private val onEdit: (Movie) -> Unit,
    private val onDelete: (Movie) -> Unit
) : ListAdapter<Movie, MovieAdapter.MovieViewHolder>(DIFF) {

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) = with(binding) {
            tvName.text = movie.name
            tvRating.text = root.context.getString(R.string.rating_value, movie.rating)
            ratingBar.rating = movie.rating / 2f // nota 0–10 → 0–5 estrelas

            // Coil: carrega a foto a partir da URL/URI
            imgPoster.load(movie.photoUrl.ifBlank { null }) {
                crossfade(true)
                placeholder(R.drawable.ic_movie_placeholder)
                error(R.drawable.ic_movie_placeholder)
            }

            root.setOnClickListener { onEdit(movie) }
            btnDelete.setOnClickListener { onDelete(movie) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(old: Movie, new: Movie) = old.id == new.id
            override fun areContentsTheSame(old: Movie, new: Movie) = old == new
        }
    }
}
