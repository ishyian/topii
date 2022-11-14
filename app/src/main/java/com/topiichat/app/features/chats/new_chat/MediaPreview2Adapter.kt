package com.topiichat.app.features.chats.new_chat

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.yourbestigor.chat.R
import com.yourbestigor.chat.databinding.MediaPreviewBinding
import eu.siacs.conversations.persistance.FileBackend
import eu.siacs.conversations.ui.XmppActivity
import eu.siacs.conversations.ui.adapter.MediaAdapter
import eu.siacs.conversations.ui.adapter.MediaPreviewAdapter
import eu.siacs.conversations.ui.util.Attachment
import java.lang.ref.WeakReference

class MediaPreview2Adapter(private val conversationFragment: NewChatFragment) :
    RecyclerView.Adapter<MediaPreview2Adapter.MediaPreview2ViewHolder>() {

    val attachments = ArrayList<Attachment>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaPreview2ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<MediaPreviewBinding>(layoutInflater, R.layout.media_preview, parent, false)
        return MediaPreview2ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaPreview2ViewHolder, position: Int) {
        val context: Context? = conversationFragment.activity
        val attachment = attachments[position]
        if (attachment.renderThumbnail()) {
            holder.binding.mediaPreview.imageAlpha = 255
            loadPreview(attachment, holder.binding.mediaPreview)
        } else {
            cancelPotentialWork(attachment, holder.binding.mediaPreview)
            MediaAdapter.renderPreview(context, attachment, holder.binding.mediaPreview)
        }
        holder.binding.deleteButton.setOnClickListener { v: View? ->
            val pos = attachments.indexOf(attachment)
            attachments.removeAt(pos)
            notifyItemRemoved(pos)
            conversationFragment.toggleInputMethod()
        }
        holder.binding.mediaPreview.setOnClickListener { v: View? -> view(context, attachment) }
    }

    fun addMediaPreviews(attachments: List<Attachment>?) {
        this.attachments.addAll(attachments!!)
        notifyDataSetChanged()
    }

    private fun loadPreview(attachment: Attachment, imageView: ImageView) {
        if (cancelPotentialWork(attachment, imageView)) {
            val activity = conversationFragment.activity as XmppActivity?
            /*final Bitmap bm = activity.xmppConnectionService.getFileBackend().getPreviewForUri(attachment,Math.round(activity.getResources().getDimension(R.dimen.media_preview_size)),true);
            if (bm != null) {
                cancelPotentialWork(attachment, imageView);
                imageView.setImageBitmap(bm);
                imageView.setBackgroundColor(0x00000000);
            } else {
                imageView.setBackgroundColor(0xff333333);
                imageView.setImageDrawable(null);
                final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
                final AsyncDrawable asyncDrawable = new AsyncDrawable(conversationFragment.getActivity().getResources(), null, task);
                imageView.setImageDrawable(asyncDrawable);
                try {
                    task.execute(attachment);
                } catch (final RejectedExecutionException ignored) {
                }
            }*/
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

    inner class MediaPreview2ViewHolder(val binding: MediaPreviewBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    internal class AsyncDrawable(
        res: Resources?,
        bitmap: Bitmap?,
        bitmapWorkerTask: MediaPreviewAdapter.BitmapWorkerTask
    ) : BitmapDrawable(res, bitmap) {
        private val bitmapWorkerTaskReference: WeakReference<MediaPreviewAdapter.BitmapWorkerTask>
        val bitmapWorkerTask: MediaPreviewAdapter.BitmapWorkerTask?
            get() = bitmapWorkerTaskReference.get()

        init {
            bitmapWorkerTaskReference = WeakReference(bitmapWorkerTask)
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

        private fun getBitmapWorkerTask(imageView: ImageView?): MediaPreviewAdapter.BitmapWorkerTask? {
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