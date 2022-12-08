package com.topiichat.chat.chat.adapter

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.topiichat.chat.chat.ChatFragment
import com.yourbestigor.chat.R
import com.yourbestigor.chat.databinding.MediaPreviewBinding
import eu.siacs.conversations.persistance.FileBackend
import eu.siacs.conversations.ui.XmppActivity
import eu.siacs.conversations.ui.adapter.MediaAdapter
import eu.siacs.conversations.ui.util.Attachment
import java.lang.ref.WeakReference
import java.util.concurrent.RejectedExecutionException
import kotlin.math.roundToInt

class MediaPreviewAdapter(private val conversationFragment: ChatFragment) :
    RecyclerView.Adapter<MediaPreviewAdapter.MediaPreviewViewHolder>() {
    val attachments = ArrayList<Attachment>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaPreviewViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<MediaPreviewBinding>(layoutInflater, R.layout.media_preview, parent, false)
        return MediaPreviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaPreviewViewHolder, position: Int) {
        val attachment = attachments[position]
        holder.bind(attachment, conversationFragment.requireContext())
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addMediaPreviews(attachments: List<Attachment>?) {
        this.attachments.addAll(attachments!!)
        notifyDataSetChanged()
    }

    private fun loadPreview(attachment: Attachment, imageView: ImageView) {
        if (cancelPotentialWork(attachment, imageView)) {
            val activity = conversationFragment.activity as XmppActivity?
            val bm = activity!!.xmppConnectionService.fileBackend.getPreviewForUri(
                attachment, activity.resources.getDimension(R.dimen.media_preview_size).roundToInt(), true
            )
            if (bm != null) {
                cancelPotentialWork(attachment, imageView)
                imageView.setImageBitmap(bm)
                imageView.setBackgroundColor(0x00000000)
            } else {
                imageView.setBackgroundColor(-0xcccccd)
                imageView.setImageDrawable(null)
                val task = BitmapWorkerTask(imageView)
                val asyncDrawable = AsyncDrawable(
                    conversationFragment.requireActivity().resources, null, task
                )
                imageView.setImageDrawable(asyncDrawable)
                try {
                    task.execute(attachment)
                } catch (ignored: RejectedExecutionException) {
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return attachments.size
    }

    fun hasAttachments(): Boolean {
        return attachments.size > 0
    }

    fun clearPreviews() {
        attachments.clear()
    }

    inner class MediaPreviewViewHolder(val binding: MediaPreviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(attachment: Attachment, context: Context) = with(binding) {
            if (attachment.renderThumbnail()) {
                mediaPreview.imageAlpha = 255
                loadPreview(attachment, mediaPreview)
            } else {
                cancelPotentialWork(attachment, binding.mediaPreview)
                MediaAdapter.renderPreview(itemView.context, attachment, mediaPreview)
            }
            deleteButton.setOnClickListener {
                val pos = attachments.indexOf(attachment)
                attachments.removeAt(pos)
                notifyItemRemoved(pos)
                conversationFragment.toggleInputMethod()
            }
            mediaPreview.setOnClickListener {
                view(context, attachment)
            }
        }
    }

    internal class AsyncDrawable(res: Resources?, bitmap: Bitmap?, bitmapWorkerTask: BitmapWorkerTask) :
        BitmapDrawable(res, bitmap) {
        private val bitmapWorkerTaskReference: WeakReference<BitmapWorkerTask>
        val bitmapWorkerTask: BitmapWorkerTask?
            get() = bitmapWorkerTaskReference.get()

        init {
            bitmapWorkerTaskReference = WeakReference(bitmapWorkerTask)
        }
    }

    @Suppress("DEPRECATION")
    class BitmapWorkerTask constructor(imageView: ImageView) : AsyncTask<Attachment?, Void?, Bitmap?>() {
        private val imageViewReference: WeakReference<ImageView>
        var attachment: Attachment? = null

        override fun doInBackground(vararg params: Attachment?): Bitmap? {
            attachment = params[0]
            val activity = XmppActivity.find(imageViewReference) ?: return null
            return activity.xmppConnectionService.fileBackend.getPreviewForUri(
                attachment,
                activity.resources.getDimension(R.dimen.media_preview_size).roundToInt(),
                false
            )
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            if (bitmap != null && !isCancelled) {
                val imageView = imageViewReference.get()
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap)
                    imageView.setBackgroundColor(0x00000000)
                }
            }
        }

        init {
            imageViewReference = WeakReference(imageView)
        }
    }

    companion object {
        private fun view(context: Context?, attachment: Attachment) {
            val view = Intent(Intent.ACTION_VIEW)
            val uri = FileBackend.getUriForUri(context, attachment.uri)
            view.setDataAndType(uri, attachment.mime)
            view.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                context!!.startActivity(view)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, R.string.no_application_found_to_open_file, Toast.LENGTH_SHORT).show()
            } catch (e: SecurityException) {
                Toast.makeText(context, R.string.sharing_application_not_grant_permission, Toast.LENGTH_SHORT).show()
            }
        }

        private fun cancelPotentialWork(attachment: Attachment, imageView: ImageView): Boolean {
            val bitmapWorkerTask = getBitmapWorkerTask(imageView)
            if (bitmapWorkerTask != null) {
                val oldAttachment = bitmapWorkerTask.attachment
                if (oldAttachment == null || oldAttachment != attachment) {
                    bitmapWorkerTask.cancel(true)
                } else {
                    return false
                }
            }
            return true
        }

        private fun getBitmapWorkerTask(imageView: ImageView?): BitmapWorkerTask? {
            if (imageView != null) {
                val drawable = imageView.drawable
                if (drawable is AsyncDrawable) {
                    return drawable.bitmapWorkerTask
                }
            }
            return null
        }
    }
}