package com.example.smackandroid.view

import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.smackandroid.R
import com.example.smackandroid.modal.ChatMessage
import com.example.smackandroid.modal.Type
import java.io.File


class MessageAdapater(private val messageList:ArrayList<ChatMessage>):RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    companion object {
        private const val RECEIVED=1
        private const val SENT=2
        private const val IMAGE_SENT = 3
        private const val IMAGE_RECEIVED = 4
        private const val AUDIO_SENT = 5
        private const val AUDIO_RECEIVED = 6
        private const val VIDEO_SENT = 7
        private const val VIDEO_RECEIVED = 8
        private const val PDF_SENT = 9
        private const val PDF_RECEIVED = 10
        private const val OFFICE_SENT = 11
        private const val OFFICE_RECEIVED = 12
        private const val OTHER_SENT = 13
        private const val OTHER_RECEIVED = 14

        private val TAG="MessageAdapter"

    }


    override fun getItemViewType(position: Int): Int {

        if (messageList[position].type== Type.SENT){
           return   SENT
        }
        if(messageList[position].type==Type.RECEIVED){
          return   RECEIVED
        }
        if (messageList[position].type==Type.IMAGE_SENT){
            return IMAGE_SENT
        }
        if (messageList[position].type==Type.IMAGE_RECEIVED){
            return IMAGE_RECEIVED
        }
        if (messageList[position].type==Type.AUDIO_SENT){
            return AUDIO_SENT
        }
        if (messageList[position].type==Type.AUDIO_RECEIVED){
            return AUDIO_RECEIVED
        }
        if (messageList[position].type==Type.VIDEO_SENT){
            return VIDEO_SENT
        }
        if (messageList[position].type==Type.VIDEO_RECEIVED){
            return VIDEO_RECEIVED
        }
        if (messageList[position].type==Type.PDF_SENT){
            return PDF_SENT
        }
        if (messageList[position].type==Type.PDF_RECEIVED){
            return PDF_RECEIVED
        }
        if (messageList[position].type==Type.OFFICE_SENT){
            return OFFICE_SENT
        }
        if (messageList[position].type==Type.OFFICE_RECEIVED){
            return OFFICE_RECEIVED
        }
        if (messageList[position].type==Type.OTHER_SENT){
            return OTHER_SENT
        }
        if (messageList[position].type==Type.OTHER_RECEIVED){
            return OTHER_RECEIVED
        }

        return SENT

    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
              SENT -> bindMessage(holder,messageList[position])
              RECEIVED -> bindMessage(holder,messageList[position])
              IMAGE_SENT -> bindMessage(holder,messageList[position])
              IMAGE_RECEIVED -> bindMessage(holder,messageList[position])
              AUDIO_SENT -> bindMessage(holder,messageList[position])
              AUDIO_RECEIVED -> bindMessage(holder,messageList[position])
              VIDEO_SENT -> bindMessage(holder,messageList[position])
              VIDEO_RECEIVED -> bindMessage(holder,messageList[position])
              PDF_SENT -> bindMessage(holder,messageList[position])
              PDF_RECEIVED -> bindMessage(holder,messageList[position])
              OFFICE_SENT -> bindMessage(holder,messageList[position])
              OFFICE_RECEIVED -> bindMessage(holder,messageList[position])
              OTHER_SENT -> bindMessage(holder,messageList[position])
               OTHER_RECEIVED -> bindMessage(holder,messageList[position])
        }
    }

    private fun bindMessage(holder: RecyclerView.ViewHolder,message:ChatMessage){
          val type=message.type

          if (type==Type.SENT){
              val messageHolder= holder as MyMessgeVeiwHolder
              messageHolder.myMessage?.text=message.text
          }

          if (type==Type.RECEIVED){
              val messageHolder= holder as TheirMessageViewHolder
              messageHolder.theirMessage?.text=message.text
          }

          if (type==Type.IMAGE_SENT){
              val imageHolder= holder as ImageSentViewHolder
              imageHolder.myImage?.setImageResource(R.drawable.ic_profile)
          }

          if (type==Type.IMAGE_RECEIVED){
              val imageHolder= holder as ImageReceievedViewHolder
              imageHolder.theirImage?.setImageResource(R.drawable.ic_profile)
          }

          if (type==Type.VIDEO_SENT){
              val videoHolder= holder as VideoSentViewHolder
              videoHolder.myImage?.setImageResource(R.drawable.ic_profile)
          }

          if (type==Type.VIDEO_RECEIVED){
              val videoHolder= holder as VideoReceivedViewHolder
              videoHolder.theirImage?.setImageResource(R.drawable.ic_profile)
          }

         if (type==Type.AUDIO_SENT){
              val otherViewHolder= holder as OtherItemsSentViewHolder
              otherViewHolder.imageViewFileIcon?.setImageResource(R.drawable.ic_picture_as_audio_48dp)
             otherViewHolder.attachmentFileName?.text="Sent Audio File"
         }

        if (type==Type.AUDIO_RECEIVED){
            val otherViewHolder= holder as OtherItemsReceivedViewHolder
            otherViewHolder.imageViewFileIcon?.setImageResource(R.drawable.ic_picture_as_audio_48dp)
            otherViewHolder.attachmentFileName?.text="Received Audio File"
        }

        if (type==Type.OFFICE_SENT){
            val otherViewHolder= holder as OtherItemsSentViewHolder
            otherViewHolder.imageViewFileIcon?.setImageResource(R.drawable.ic_picture_as_document_48dp)
            otherViewHolder.attachmentFileName?.text="Sent Office File"
        }

        if (type==Type.OFFICE_RECEIVED){
            val otherViewHolder= holder as OtherItemsReceivedViewHolder
            otherViewHolder.imageViewFileIcon?.setImageResource(R.drawable.ic_picture_as_document_48dp)
            otherViewHolder.attachmentFileName?.text="Received Office File"
        }

        if (type==Type.PDF_SENT){
            val otherViewHolder= holder as OtherItemsSentViewHolder
            otherViewHolder.imageViewFileIcon?.setImageResource(R.drawable.ic_picture_as_pdf_black_48dp)
            otherViewHolder.attachmentFileName?.text="Sent Pdf File"
        }

        if (type==Type.PDF_RECEIVED){
            val otherViewHolder= holder as OtherItemsReceivedViewHolder
            otherViewHolder.imageViewFileIcon?.setImageResource(R.drawable.ic_picture_as_pdf_black_48dp)
            otherViewHolder.attachmentFileName?.text="Received Pdf File"
        }

        if (type==Type.OTHER_SENT){
            val otherViewHolder= holder as OtherItemsSentViewHolder
            otherViewHolder.imageViewFileIcon?.setImageResource(R.drawable.ic_picture_as_attachment_black_48dp)
            otherViewHolder.attachmentFileName?.text="Sent Other File"
        }

        if (type==Type.OTHER_RECEIVED){
            val otherViewHolder= holder as OtherItemsReceivedViewHolder
            otherViewHolder.imageViewFileIcon?.setImageResource(R.drawable.ic_picture_as_attachment_black_48dp)
            otherViewHolder.attachmentFileName?.text="Received Other File"
        }


        // For images just show the image preview
        if (type==Type.IMAGE_SENT){
             val file =File(message.attachmentPath)
             val imageHolder=holder as ImageSentViewHolder

             if (!file.exists()){
                 Log.d(TAG, "Image File does not exist")
                 imageHolder.myImage?.setImageResource(R.drawable.ic_picture_as_file_deleted_48dp)
             }else{
                 val bitmap = BitmapFactory.decodeFile(message.attachmentPath)
                 imageHolder.myImage?.setImageBitmap(bitmap)
             }
        }

        if (type==Type.IMAGE_RECEIVED){
            val file =File(message.attachmentPath)
            val imageHolder=holder as ImageReceievedViewHolder

            if (!file.exists()){
                Log.d(TAG, "Image File does not exist")
                imageHolder.theirImage?.setImageResource(R.drawable.ic_picture_as_file_deleted_48dp)
            }else{
                val bitmap = BitmapFactory.decodeFile(message.attachmentPath)
                imageHolder.theirImage?.setImageBitmap(bitmap)
            }
        }



        // For videos extract the thumbnail
        if (type==Type.VIDEO_SENT){
            val file=File(message.attachmentPath)
            val videoHolder= holder as VideoSentViewHolder
            if (!file.exists()){
                Log.d(TAG, "Video File does not exist")
                videoHolder.myImage?.setImageResource(R.drawable.ic_picture_as_file_deleted_48dp)
            }else{
                val thumbnail = ThumbnailUtils.createVideoThumbnail(
                    message.attachmentPath,
                    MediaStore.Images.Thumbnails.MINI_KIND
                )
                videoHolder.myImage?.setImageBitmap(thumbnail)
            }
        }

        if (type==Type.VIDEO_RECEIVED){
            val file=File(message.attachmentPath)
            val videoHolder= holder as VideoReceivedViewHolder
            if (!file.exists()){
                Log.d(TAG, "Video File does not exist")
                videoHolder.theirImage ?.setImageResource(R.drawable.ic_picture_as_file_deleted_48dp)
            }else{
                val thumbnail = ThumbnailUtils.createVideoThumbnail(
                    message.attachmentPath,
                    MediaStore.Images.Thumbnails.MINI_KIND
                )
                videoHolder.theirImage?.setImageBitmap(thumbnail)
            }
        }





    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder {

        var viewHolder:RecyclerView.ViewHolder?=null

        when(viewType){
            SENT ->  viewHolder= MyMessgeVeiwHolder(LayoutInflater.from(parent.context).inflate(R.layout.my_message, parent, false))
            RECEIVED ->  viewHolder = TheirMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.their_message, parent, false))
            IMAGE_SENT ->  viewHolder = ImageSentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.chat_message_image_sent, parent, false))
            IMAGE_RECEIVED ->  viewHolder = ImageReceievedViewHolder (LayoutInflater.from(parent.context).inflate(R.layout.chat_message_image_recieved, parent, false))
            VIDEO_SENT ->  viewHolder = VideoSentViewHolder (LayoutInflater.from(parent.context).inflate(R.layout.chat_message_video_sent, parent, false))
            VIDEO_RECEIVED ->  viewHolder = VideoReceivedViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.chat_message_video_recived, parent, false))
            AUDIO_SENT ->    viewHolder = OtherItemsSentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.chat_message_file_sent, parent, false))
            AUDIO_RECEIVED -> viewHolder = OtherItemsReceivedViewHolder (LayoutInflater.from(parent.context).inflate(R.layout.chat_message_file_recived, parent, false))
            PDF_SENT -> viewHolder = OtherItemsSentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.chat_message_file_sent, parent, false))
            PDF_RECEIVED -> viewHolder = OtherItemsReceivedViewHolder (LayoutInflater.from(parent.context).inflate(R.layout.chat_message_file_recived, parent, false))
            OFFICE_SENT -> viewHolder = OtherItemsSentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.chat_message_file_sent, parent, false))
            OFFICE_RECEIVED -> viewHolder = OtherItemsReceivedViewHolder (LayoutInflater.from(parent.context).inflate(R.layout.chat_message_file_recived, parent, false))
            OTHER_SENT -> viewHolder = OtherItemsSentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.chat_message_file_sent, parent, false))
            OTHER_RECEIVED -> viewHolder = OtherItemsReceivedViewHolder (LayoutInflater.from(parent.context).inflate(R.layout.chat_message_file_recived, parent, false))
        }
         return viewHolder!!
    }
}

class MyMessgeVeiwHolder(view: View):RecyclerView.ViewHolder(view){
     var myMessage:TextView?=null

    init {
         myMessage=view.findViewById(R.id.my_message_body)

    }
}


class TheirMessageViewHolder(view: View):RecyclerView.ViewHolder(view){
    var theirMessage:TextView?=null

    init {
        theirMessage=view.findViewById(R.id.their_message_body)
    }
}

class ImageSentViewHolder(view: View):RecyclerView.ViewHolder(view){
    var myImage:ImageView?=null

    init {
        myImage=view.findViewById(R.id.sent_image)

    }
}


class ImageReceievedViewHolder(view: View):RecyclerView.ViewHolder(view){
    var theirImage:ImageView?=null

    init {
        theirImage=view.findViewById(R.id.received_image)

    }
}

class VideoSentViewHolder(view: View):RecyclerView.ViewHolder(view){
    var myImage:ImageView?=null

    init {
        myImage=view.findViewById(R.id.video_sent)

    }
}

class VideoReceivedViewHolder(view: View):RecyclerView.ViewHolder(view){
    var theirImage:ImageView?=null

    init {
        theirImage=view.findViewById(R.id.video_received)

    }
}

class OtherItemsSentViewHolder(view: View):RecyclerView.ViewHolder(view){
    var attachmentFileName:TextView?=null
    var imageViewFileIcon:ImageView?=null

    init {
        attachmentFileName=view.findViewById(R.id.attachmentFileNameSent)
        imageViewFileIcon=view.findViewById(R.id.imageViewFileIconSent)
    }
}

class OtherItemsReceivedViewHolder(view: View):RecyclerView.ViewHolder(view){
    var attachmentFileName:TextView?=null
    var imageViewFileIcon:ImageView?=null

    init {
        attachmentFileName=view.findViewById(R.id.attachmentFileNameReceived)
        imageViewFileIcon=view.findViewById(R.id.imageViewFileIconReceived)
    }
}









