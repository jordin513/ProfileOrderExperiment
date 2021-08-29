package com.nidroj.profileorderexperiment.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.nidroj.profileorderexperiment.R

open class ProfileAdapter(context: Context) : RecyclerView.Adapter<ProfileAdapter.Holder>() {

    private val layoutInflater = LayoutInflater.from(context)

    var items: List<ProfileSection> = ArrayList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = when (viewType) {
        TYPE_INFO -> InfoHolder(layoutInflater.inflate(R.layout.item_info, parent, false))
        TYPE_IMAGE -> ImageHolder(layoutInflater.inflate(R.layout.item_image, parent, false))
        else -> throw IllegalArgumentException("unrecognized view type, $viewType")
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        when (holder) {
            is InfoHolder -> holder.bindTo(items[position] as InfoItem)
            is ImageHolder -> holder.bindTo(items[position] as ImageItem)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type()
    }

    interface ProfileSection {
        fun type(): Int
    }

    class InfoItem(val header: String, val value: String? = null) : ProfileSection {
        override fun type(): Int = TYPE_INFO
    }


    class ImageItem(val url: String) : ProfileSection {
        override fun type(): Int = TYPE_IMAGE
    }

    abstract class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class InfoHolder(itemView: View) : Holder(itemView) {
        private val header: TextView = itemView.findViewById(R.id.header)
        private val infoValue: TextView = itemView.findViewById(R.id.value)

        fun bindTo(info: InfoItem) {
            //capitalizes first letter in header
            val sentenceCaseHeader = info.header.substring(0, 1).uppercase() + info.header.substring(1)

            header.text = sentenceCaseHeader
            info.value?.let {
                infoValue.visibility = View.VISIBLE
                infoValue.text = it;
            }
        }
    }

    class ImageHolder(itemView: View) : Holder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.image)

        /* Sets a placeholder item until the image is done loading from the url.
         * If it cant load the image, it sets an error image
         */
        fun bindTo(imageItem: ImageItem) {
            this.image.setImageResource(R.drawable.ic_placeholder_loading)

            imageItem.url.let { url ->
                Glide.with(itemView).asBitmap().load(url).listener(object : RequestListener<Bitmap> {
                    override fun onResourceReady(
                        resource: Bitmap?, model: Any?, target: Target<Bitmap>?,
                        dataSource: DataSource?, isFirstResource: Boolean
                    ): Boolean {
                        resource?.let { res ->
                            image.post {
                                image.setImageBitmap(res)
                            }
                        }
                        return true
                    }

                    override fun onLoadFailed(
                        e: GlideException?, model: Any?, target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        this@ImageHolder.image.let { iv ->
                            iv.post { iv.setImageResource(R.drawable.ic_placeholder_error) }
                        }
                        return true
                    }
                }).submit()
            }
        }
    }

    companion object {
        private const val TYPE_INFO = 0
        private const val TYPE_IMAGE = 1
    }
}