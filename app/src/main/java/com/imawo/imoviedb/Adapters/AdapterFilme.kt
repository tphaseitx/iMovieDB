package imawo.prognosis.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.imawo.veaguard.Helpers.Utils
import com.imawo.imoviedb.Models.ModelFilme
import com.imawo.imoviedb.R

class AdapterFilme(
    private val listFirme: MutableList<ModelFilme>,
    private val context: Context,
    private val clickListener: (ModelFilme, Int) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutIdForListItem = R.layout.layout_topfilme_items
        val inflater = LayoutInflater.from(context)
        val shouldAttach = false

        val view = inflater.inflate(layoutIdForListItem, parent, shouldAttach)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var item: ModelFilme = listFirme[position]

        holder?.bind(item, position, clickListener)
    }

    override fun getItemCount(): Int {
        return listFirme.size
    }
}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val mTextFilm: TextView = itemView.findViewById(R.id.text_film)
    private val mTextDescriere: TextView = itemView.findViewById(R.id.text_descriere)
    private val mTextDetalii: TextView = itemView.findViewById(R.id.text_detalii)
    private val mPosterImageView: ImageView = itemView.findViewById(R.id.item_movie_poster)

    fun bind(item: ModelFilme, position: Int, clickListener: (ModelFilme, Int) -> Unit) {

        mTextFilm.text = item.title
        mTextDescriere.text = item.overview
        mTextDetalii.text = item.releaseDate

        Glide.with(itemView)
            .load("https://image.tmdb.org/t/p/w342${item.posterPath}")
            .transform(CenterCrop())
            .into(mPosterImageView)

        itemView.setOnClickListener {
            Utils.pulsateClickView(itemView)
            clickListener(item, position)
        }
    }
}