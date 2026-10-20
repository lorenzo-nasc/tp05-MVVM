package br.unisanta.filmesfavoritos.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import br.unisanta.filmesfavoritos.R
import br.unisanta.filmesfavoritos.databinding.ActivityMainBinding
import br.unisanta.filmesfavoritos.databinding.DialogEditMovieBinding
import br.unisanta.filmesfavoritos.model.Movie
import br.unisanta.filmesfavoritos.viewmodel.MovieViewModel
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

/**
 * View: apenas exibe o estado vindo do ViewModel e repassa as ações.
 * Todos os elementos de layout são acessados via View Binding.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MovieViewModel by viewModels()

    private val adapter = MovieAdapter(
        onEdit = { movie -> showEditDialog(movie) },
        onDelete = { movie -> deleteMovie(movie) }
    )

    // Binding do diálogo aberto (para receber a foto escolhida na galeria)
    private var dialogBinding: DialogEditMovieBinding? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) dialogBinding?.etPhotoUrl?.setText(uri.toString())
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        binding.rvMovies.adapter = adapter
        binding.fabAdd.setOnClickListener { showEditDialog(null) }

        // Observa o ViewModel
        viewModel.movies.observe(this) { list ->
            adapter.submitList(list)
            binding.tvEmpty.isVisible = list.isEmpty()
        }
        viewModel.summary.observe(this) { s ->
            binding.toolbar.subtitle =
                resources.getQuantityString(R.plurals.summary, s.count, s.count, s.average)
        }
    }

    private fun deleteMovie(movie: Movie) {
        val index = viewModel.deleteMovie(movie)
        Snackbar.make(binding.root, getString(R.string.movie_removed, movie.name), Snackbar.LENGTH_LONG)
            .setAnchorView(binding.fabAdd)
            .setAction(R.string.undo) { viewModel.restoreMovie(movie, index) }
            .show()
    }

    /** movie == null → novo filme; caso contrário → edição. */
    private fun showEditDialog(movie: Movie?) {
        val db = DialogEditMovieBinding.inflate(LayoutInflater.from(this))
        dialogBinding = db

        // Preenche os campos
        db.etName.setText(movie?.name.orEmpty())
        db.sliderRating.value = movie?.rating ?: 5f
        db.tvRatingLabel.text = getString(R.string.rating_label, db.sliderRating.value)
        db.etPhotoUrl.setText(movie?.photoUrl.orEmpty())
        loadPreview(db, movie?.photoUrl.orEmpty())

        db.sliderRating.addOnChangeListener { _, value, _ ->
            db.tvRatingLabel.text = getString(R.string.rating_label, value)
        }
        db.etPhotoUrl.doAfterTextChanged { loadPreview(db, it.toString()) }
        db.etName.doAfterTextChanged { db.tilName.error = null }
        db.btnPickImage.setOnClickListener {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(if (movie == null) R.string.new_movie else R.string.edit_movie)
            .setView(db.root)
            .setPositiveButton(R.string.save, null) // listener definido abaixo (não fecha se inválido)
            .setNegativeButton(R.string.cancel, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val saved = viewModel.saveMovie(
                    id = movie?.id,
                    name = db.etName.text.toString(),
                    rating = db.sliderRating.value,
                    photoUrl = db.etPhotoUrl.text.toString()
                )
                if (saved) dialog.dismiss()
                else db.tilName.error = getString(R.string.error_name)
            }
        }
        dialog.setOnDismissListener { dialogBinding = null }
        dialog.show()
    }

    private fun loadPreview(db: DialogEditMovieBinding, url: String) {
        db.imgPreview.load(url.trim().ifBlank { null }) {
            crossfade(true)
            placeholder(R.drawable.ic_movie_placeholder)
            error(R.drawable.ic_movie_placeholder)
        }
    }
}
