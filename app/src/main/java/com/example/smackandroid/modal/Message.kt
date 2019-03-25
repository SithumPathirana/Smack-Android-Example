package com.example.smackandroid.modal

class Message(val text:String,val type:Type)

enum class Type {
    SENT, RECEIVED,
    IMAGE_SENT, IMAGE_RECEIVED,
    AUDIO_SENT, AUDIO_RECEIVED,
    VIDEO_SENT, VIDEO_RECEIVED,
    PDF_SENT, PDF_RECEIVED,
    OFFICE_SENT, OFFICE_RECEIVED,
    OTHER_SENT, OTHER_RECEIVED
}