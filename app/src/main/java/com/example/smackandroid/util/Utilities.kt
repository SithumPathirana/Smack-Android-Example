package com.example.smackandroid.util

import com.example.smackandroid.modal.Type

class Utilities{

    companion object {

        fun getMessageTypeFromFileFullPath(fileFullPath:String,send:Boolean):Type{
                 val index=fileFullPath.lastIndexOf(".")

                 if (index==-1){
                     if (send){
                         return Type.OTHER_SENT
                     }else{
                         return Type.OTHER_RECEIVED
                     }
                 }

                  val extension=fileFullPath.substring(index)

                 if (extension==".jpg" || extension==".png" || extension==".jpeg") {
                        if (send){
                            return Type.IMAGE_SENT
                        }else{
                            return Type.IMAGE_RECEIVED
                        }
                 }else if (extension==".mp4" || extension==".mpg" || extension==".mov" || extension==".avi"){
                     if (send){
                         return Type.VIDEO_SENT
                     }else{
                         return Type.VIDEO_RECEIVED
                     }
                 }else if (extension==".doc" || extension==".docx" || extension==".ppt" || extension==".pptx" ||
                            extension==".xls" || extension==".xlsx"  ){

                     if (send){
                         return Type.OFFICE_SENT
                     }else{
                         return  Type.OFFICE_RECEIVED
                     }

                 }else if (extension==".mp3" || extension==".wav"){
                     if (send){
                           return Type.AUDIO_SENT
                     }else{
                         return Type.AUDIO_RECEIVED
                     }
                 }else if (extension==".pdf"){
                     if (send){
                         return Type.PDF_SENT
                     }else{
                         return Type.PDF_RECEIVED
                     }
                 }else{
                     if (send){
                         return Type.OTHER_SENT
                     }else{
                         return Type.OFFICE_RECEIVED
                     }
                 }
        }
    }

}