package com.example.smackandroid.modal

class ChatMessage(val text:String,val type:Type,val contactJid:String,val attachmentPath:String)

enum class Type {
    SENT, RECEIVED,
    IMAGE_SENT, IMAGE_RECEIVED,
    AUDIO_SENT, AUDIO_RECEIVED,
    VIDEO_SENT, VIDEO_RECEIVED,
    PDF_SENT, PDF_RECEIVED,
    OFFICE_SENT, OFFICE_RECEIVED,
    OTHER_SENT, OTHER_RECEIVED
}