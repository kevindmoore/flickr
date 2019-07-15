package com.mastertechsoftware.flickr.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import com.mastertechsoftware.flickr.GlideApp
import com.mastertechsoftware.flickr.models.Photo
import com.mastertechsoftware.flickr.ui.ViewHolder
import kotlinx.android.synthetic.main.photo_item.*
import kotlinx.android.synthetic.main.search_layout.*

fun View.pxToDp(pixels: Int): Int {
    return (pixels / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}

/**
 * Fragment for searching for images
 */
class SearchFragment : Fragment() {
    lateinit var viewModel: SearchViewModel
    val adapter = PhotoAdapter()
    lateinit var layoutManager: LinearLayoutManager
    var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            viewModel = ViewModelProviders.of(it).get(SearchViewModel::class.java)
        }
        displayWidth = resources.displayMetrics.widthPixels
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        photos.layoutManager = layoutManager
        photos.adapter = adapter
        searchField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrBlank()) {
                    val currentText = s.toString()
                    if (currentText.length > 2) {
                        adapter.clear()
                        viewModel.searchPhotos(currentText) {
                            if (it != null) {
                                adapter.photos = it
                            }
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        photos.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (recyclerView.scrollState != SCROLL_STATE_DRAGGING) {
                    return
                }
                // Scrolling down
                if (dy > 0) {
                    val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
                    val nextPage = lastVisiblePosition + SearchViewModel.PAGE_SIZE
                    if (!loading
                        && adapter.photos.size < nextPage
                    ) {
                        loading = true
                        viewModel.searchNextPhotos {

                            loading = false
                            adapter.addPhotos(it)
                        }
                    }

                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            com.mastertechsoftware.flickr.R.layout.search_layout,
            container,
            false
        )
    }

    class PhotoAdapter : RecyclerView.Adapter<ViewHolder>() {
        var photos: List<Photo> = arrayListOf()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        private var layoutInflator: LayoutInflater? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            if (layoutInflator == null) {
                layoutInflator = LayoutInflater.from(parent.context)
            }
            return ViewHolder(
                layoutInflator?.inflate(
                    com.mastertechsoftware.flickr.R.layout.photo_item,
                    parent,
                    false
                )!!
            )
        }

        override fun getItemCount(): Int = photos.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val photo = photos[position]

            holder.title.text = photo.title
            val width = holder.photo.pxToDp(photo.width.toInt())
            val height = holder.photo.pxToDp(photo.height.toInt())
            val photoAspectRatio = (width / height.toFloat())
            val params = holder.photo.layoutParams as ConstraintLayout.LayoutParams
            params.width = displayWidth
            params.height = (displayWidth / photoAspectRatio).toInt()
            holder.photo.layoutParams = params
            GlideApp.with(holder.containerView.context).load(photo.url)
                .override(params.width, params.height)
                .into(holder.photo)
        }

        fun clear() {
            (photos as? ArrayList<Photo>)?.clear()
            notifyDataSetChanged()
        }

        fun addPhotos(newPhotos: List<Photo>?) {
            if (newPhotos.isNullOrEmpty()) {
                return
            }
            notifyItemRangeChanged(photos.size, (photos.size + newPhotos.size - 1))
            (photos as? ArrayList<Photo>)?.addAll(newPhotos)
        }

    }

    companion object {
        var displayWidth = 100
    }
}