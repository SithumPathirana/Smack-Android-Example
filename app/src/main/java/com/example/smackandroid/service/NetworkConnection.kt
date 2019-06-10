package com.example.smackandroid.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.preference.PreferenceManager
import android.util.Log
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import java.lang.Exception
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smackx.httpfileupload.HttpFileUploadManager
import org.jivesoftware.smackx.httpfileupload.UploadProgressListener
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.stringprep.XmppStringprepException
import java.net.URL
import com.example.smackandroid.modal.ChatMessage
import com.example.smackandroid.modal.Type
import com.example.smackandroid.util.MimeUtils
import com.example.smackandroid.util.Utilities
import org.jivesoftware.smack.*
import org.jivesoftware.smack.filter.PresenceTypeFilter
import org.jivesoftware.smack.filter.StanzaFilter
import org.jivesoftware.smack.filter.StanzaTypeFilter
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.Stanza
import org.jivesoftware.smack.roster.RosterEntry
import org.jivesoftware.smackx.bytestreams.ibb.packet.Close
import org.jivesoftware.smackx.filetransfer.*
import org.jxmpp.jid.EntityFullJid
import org.jxmpp.util.XmppStringUtils
import org.jxmpp.util.cache.LruCache
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException


class NetworkConnection(context: Context):ConnectionListener {

    private val TAG = "NetworkConnection"

    private var mApplicationContext: Context? = null
    private var mUsername: String? = null
    private var mPassword: String? = null
    private var mServiceName: String? = null
//    private var mConnection2:XMPPTCPConnection?=null
    private var uiThreadMessageREciever:BroadcastReceiver?=null
    var httpFileUploadManager:HttpFileUploadManager?=null
    var fileTransferManager1:FileTransferManager?=null
    var fileTransferManager2:FileTransferManager?=null
    var outgoingFileTransfer:OutgoingFileTransfer?=null
    var stanzaFilter:StanzaFilter?=null


    companion object {
        var mConnection: XMPPTCPConnection?=null
        val ENTITY_FULLJID_CACHE = LruCache<String,EntityFullJid>(100)
        var roster:Roster?=null
    }

    enum class ConnectionState {
        CONNECTED, AUTHENTICATED, CONNECTING, DISCONNECTING, DISCONNECTED
    }

    enum class LoggedInState {
        LOGGED_IN, LOGGED_OUT
    }

   inner class FileUploadTask:AsyncTask<String,Long,URL>(){



       private var fileFullPath:String?=null
       private var counterPartJid:String?=null

        override fun doInBackground(vararg params: String?): URL? {
            fileFullPath =params[0]
            counterPartJid=params[1]

            try {
               return httpFileUploadManager!!.uploadFile(File(fileFullPath),object :UploadProgressListener{
                   override fun onUploadProgress(uploadedBytes: Long, totalBytes: Long) {
                        publishProgress((uploadedBytes/totalBytes)*100)
                   }
               })
            }catch (e:InterruptedException){
                 e.printStackTrace()
                return null
            }catch (e:XMPPException.XMPPErrorException){
                e.printStackTrace()
                return null
            }catch (e:SmackException){
                e.printStackTrace()
                return null
            }catch (e:IOException){
                e.printStackTrace()
                return null
            }

        }

       override fun onPostExecute(result: URL?) {
           if (result!=null){
               Log.d("FileUploadTask","File upload is done,File get Url is : $result" +
                       ",File full path is : $fileFullPath"+
                       ",ConterpartJid is : $counterPartJid"

               )

               val chatMessage=ChatMessage(result.toString(),Utilities.getMessageTypeFromFileFullPath(fileFullPath!!,true),counterPartJid!!,fileFullPath!!)

               if (sendMessage(chatMessage)){
                   Log.d(
                       "UploadTask ", "File message : "  +
                               " successfully sent to " + chatMessage.contactJid
                   )
               }else{
                   Log.d(
                       "UploadTask ", "Something went wrong while sending message : " +
                               " to " + chatMessage.contactJid
                   )
               }
           }else{
               Log.d("UploadTask", "File upload Failed.")
           }
           super.onPostExecute(result)
       }

       override fun onProgressUpdate(vararg values: Long?) {
           Log.d("FileUploadTask","Progress : ${values[0]}")
           super.onProgressUpdate(*values)
       }
    }


    inner class  FileDownloadTask:AsyncTask<String,Int,String>(){

        var inputUrl:String?=null
        var inputFileName:String?=null
        var inputRootPathString:String?=null
        var inputContactJid:String?=null

        override fun doInBackground(vararg params: String?): String? {
            inputUrl=params[0]
            inputFileName=params[1]
            inputRootPathString=params[2]
            inputContactJid=params[3]

            var input:InputStream?=null
            var output:OutputStream?=null
            var connection:HttpURLConnection?=null


            try {
                val url=URL(params[0])
                connection=url.openConnection() as HttpURLConnection
                connection.connect()

                // To Avoid Saving Error Report Instead Of the file

                if (connection.responseCode != HttpURLConnection.HTTP_OK){
                    val error="Server returned HTTP ${connection.responseCode}  ${connection.responseMessage}"
                    Log.d("DownloadFileTask",error)
                    return null
                }

                // Will be able to get the download percentage
                val fileLength=connection.contentLength

                // Download the file
                input=connection.inputStream
                Log.d(
                    "DownloadFileTask",
                    " Inside doInbackground ,File will be saved to : $inputRootPathString/$inputFileName"
                )
                output=FileOutputStream("$inputRootPathString/$inputFileName")

                val data = ByteArray(4096)
                var total: Long = 0
                var count=0
                while (count  != -1) {
                    count=input.read(data)

                    if (count==-1){
                        break
                    }
                    // allow canceling with back button
                    if (isCancelled) {
                        input.close()
                        return null
                    }
                    total += count.toLong()
                    // publishing the progress....
                    if (fileLength > 0)
                    // only if total length is known
                        publishProgress((total * 100 / fileLength).toInt())
                    output.write(data, 0, count)
                }
            }catch (e:Exception){
                return null
            }finally {
                try {
                     if (output!=null){
                         output.close()
                     }
                     if (input!=null){
                         input.close()
                     }
                }catch (ignored:IOException){

                }

                if (connection!=null){
                      connection.disconnect()
                }
            }
            return "$inputRootPathString/$inputFileName"
        }

        override fun onProgressUpdate(vararg values: Int?) {
            Log.d("DownloadFileTask","Progress : ${values[0]}")
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: String?) {

            if (result!=null){
                Log.d(
                    "DownloadFileTask",
                    " File download was successful, file saved to :$inputRootPathString/$inputFileName"
                )

                val file=File("$inputRootPathString/$inputFileName")

                if (file.exists()){
                    Log.d("DownloadFileTask", " File exists :$inputRootPathString/$inputFileName")
                    val message=ChatMessage(
                        inputUrl!!,
                        Utilities.getMessageTypeFromFileFullPath("$inputRootPathString/$inputFileName", false)
                        ,inputContactJid!!
                        ,"$inputRootPathString/$inputFileName")

                    informChatViewRecycler(message)

                    //Cause a scan for the image to show up in gallery,only videos and images
                    if (message.type === Type.VIDEO_RECEIVED || message.type === Type.IMAGE_RECEIVED) {
                        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                        val f = File("$inputRootPathString/$inputFileName")
                        val contentUri = Uri.fromFile(f)
                        mediaScanIntent.data = contentUri
                        mApplicationContext?.sendBroadcast(mediaScanIntent)
                    }


                }else{
                    Log.d("DownloadFileTask", "File does not exist :$inputRootPathString/$inputFileName")
                    return
                }
            }else{
                Log.d("DownloadFileTask", "Something went wrong while downloading file :$inputUrl")
            }


           // super.onPostExecute(result)
        }

    }

    inner class DownloadFileFromByteStreams:AsyncTask<IncomingFileTransfer,Int,String>(){

        override fun doInBackground(vararg params: IncomingFileTransfer?): String? {
            val transferRequest=params[0]
            try {
              //  val receiveIncommingFile=transferRequest?.receiveFile()
                Log.d(TAG,"File tranfer request accepted")
                val os=ByteArrayOutputStream()
                var nRead:Int?=null
                val buf=ByteArray(1024)
//                while ( nRead!=-1 ){
//                    nRead=receiveIncommingFile?.read(buf,0,buf.size)
//                    os.write(buf,0,nRead!!)
//
//                }
//                os?.flush()
//                val a=os.toByteArray()
//                return "File Downloaded Successfully"
                return "dfsf"

            }catch (e:Exception){
                e.printStackTrace()
                return null
            }
        }
        override fun onProgressUpdate(vararg values: Int?) {
            Log.d("DownloadFileTask","Progress : ${values[0]}")
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }
    }


    inner  class  FileTransferTask:AsyncTask<String,Long,String>(){

        override fun doInBackground(vararg params: String?): String {
            val fileFullPath=params[0]
            var contactJid=params[1]

            Log.d(TAG,fileFullPath)
            Log.d(TAG,contactJid);

             fileTransferManager1= FileTransferManager.getInstanceFor(mConnection)
             outgoingFileTransfer=fileTransferManager1!!.createOutgoingFileTransfer(JidCreate.entityFullFrom(contactJid))
            try {
                outgoingFileTransfer!!.sendFile(File(fileFullPath),"Just a normal file")
            }catch (e:XMPPException){
               e.printStackTrace()
            }


            return  "File uploaded succesfully"
        }

        override fun onPostExecute(result: String?) {
            Log.d("FileTransferTask","File transfer process is completed")
            super.onPostExecute(result)
        }

        override fun onProgressUpdate(vararg values: Long?) {
            Log.d("FileTransferTask","Progress : ${values[0]}")
            super.onProgressUpdate(*values)
        }

    }

    inner  class  LogoutUser:AsyncTask<String,Int,String>(){

        override fun doInBackground(vararg params: String?): String {
             mConnection?.disconnect()
             return  "User logout successfully"
        }
    }


    init {
        Log.d(TAG,"NetworkConnection Constructor called.")
        mApplicationContext=context.applicationContext
        val jid=PreferenceManager.getDefaultSharedPreferences(mApplicationContext).getString("xmpp_jid",null)
        mPassword=PreferenceManager.getDefaultSharedPreferences(mApplicationContext).getString("xmpp_password",null)

        if (jid!=null){
            mUsername=jid.split("@")[0]
            mServiceName=jid.split("@")[1]
        }else{
            mUsername=""
            mServiceName=""
        }
    }

    @Throws(IOException::class, XMPPException::class, SmackException::class)
    fun connect() {
        Log.d(TAG, "Connecting to server $mServiceName")
        val builder = XMPPTCPConnectionConfiguration.builder()
        val serviceName = JidCreate.domainBareFrom(mServiceName)
        builder.setXmppDomain(serviceName)
        builder.setUsernameAndPassword(mUsername, mPassword)
        builder.setResource("SmackAndroid")
        //Set up the ui thread broadcast message receiver.
        setUpUiThreadBroadcastMessageReciever()



        mConnection = XMPPTCPConnection(builder.build())
        mConnection?.addConnectionListener(this)
         roster=Roster.getInstanceFor(mConnection)
         roster?.isRosterLoadedAtLogin=true


        try {
            Log.d(TAG, "Calling connect")
            mConnection?.setUseStreamManagement(true)
            mConnection?.setUseStreamManagementResumption(true)
            mConnection?.connect()





            stanzaFilter = StanzaTypeFilter(Presence::class.java)
            val collector= mConnection!!.createStanzaCollector(stanzaFilter)
            mConnection!!.addAsyncStanzaListener(object : StanzaListener{
                override fun processStanza(packet: Stanza?) {

                    if(packet is Presence){
                       if (packet.type == Presence.Type.subscribe){

                           Log.d(TAG,"Received subscribe stanza from ${packet.from}")
                           Log.d(TAG,"Sending subscribed response to ${packet.from}")

                           val subscribed = Presence(Presence.Type.subscribed)
                           subscribed.to=JidCreate.from(packet.from)
                           mConnection?.sendStanza(subscribed)


                       }
                    }


                }
            },stanzaFilter)

            mConnection?.login(mUsername,mPassword)
            roster!!.subscriptionMode= Roster.SubscriptionMode.manual






//            builder.setResource(("SmackReceiver"))
//            mConnection2= XMPPTCPConnection(builder.build())
//            mConnection2?.connect()
//            mConnection2?.login(mUsername,mPassword)

            Log.d(TAG, " login() Called ")


        }catch (e:InterruptedException){
            e.printStackTrace()
        }
          httpFileUploadManager= HttpFileUploadManager.getInstanceFor(mConnection)
          fileTransferManager1= FileTransferManager.getInstanceFor(mConnection)


        ChatManager.getInstanceFor(mConnection).addIncomingListener(object :IncomingChatMessageListener{
            override fun newIncomingMessage(from: EntityBareJid?, message: Message?, chat: Chat?) {

                Log.d(TAG,"message.getBody() :${message?.body}")
                Log.d(TAG,"message.getFrom() :${message?.from}")

                val from = message?.from.toString()
                var contactJid = ""
                if ( from.contains("/") ){
                    contactJid=from.split("/")[0]
                    Log.d(TAG,"The real jid is :$contactJid")
                    Log.d(TAG,"The message is from :$from")
                }else{
                     contactJid=from
                }

                // Check if the received file is a Url
                if (Utilities.isStringFileUrl(message!!.body)){

                    downloadFileFromServer(message.body,contactJid)

                }else{
                    // Bundle up the intent and send broadcast
                    val intent = Intent(NetworkConnectionService.NEW_MESSAGE)
                    intent.setPackage(mApplicationContext?.packageName)
                    intent.putExtra(NetworkConnectionService.BUNDLE_FROM_JID,contactJid)
                    intent.putExtra(NetworkConnectionService.BUNDLE_MESSAGE_BODY,message?.body)
                    mApplicationContext?.sendBroadcast(intent)
                    Log.d(TAG,"Received message from :$contactJid broadcast sent.")
                    ///ADDED
                }
            }
        })

        fileTransferManager1?.addFileTransferListener(object :FileTransferListener{
            override fun fileTransferRequest(request: FileTransferRequest?) {
                // println("File transfer request received : ${request?.fileName}")
                Log.d(TAG,"File transfer request received : filename : ${request?.fileName} filesize :  ${request?.streamID}")

                val transfer=request?.accept()
                getFileFromStream(transfer!!)
            }
        })

        var reconnectionManager = ReconnectionManager.getInstanceFor(mConnection)
        ReconnectionManager.setEnabledPerDefault(true)
        reconnectionManager.enableAutomaticReconnection()

    }

    fun disconnect() {
        Log.d(TAG, "Disconnecting from serser $mServiceName")
        try {
            if (mConnection != null) {
                mConnection?.disconnect()
            }

        } catch (e: SmackException.NotConnectedException) {
            NetworkConnectionService.sConnectionState = ConnectionState.DISCONNECTED
            e.printStackTrace()

        }


    }

    private fun showContactListActivityWhenAuthenticated(){
        val intent=Intent(NetworkConnectionService.UI_AUTHENTICATED)
        intent.setPackage(mApplicationContext?.packageName)
        mApplicationContext?.sendBroadcast(intent)
        Log.d(TAG,"Sent the broadcast that we are authenticated")
    }

    private fun setUpUiThreadBroadcastMessageReciever(){
        uiThreadMessageREciever=object :BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val action=intent?.action
                if (action==NetworkConnectionService.SEND_MESSAGE){
                    // Sends the message to the server
                    sendMessage(intent.getStringExtra(NetworkConnectionService.BUNDLE_MESSAGE_BODY),
                        intent.getStringExtra(NetworkConnectionService.BUNDLE_TO))
                }

            }
        }
        val filter=IntentFilter()
        filter.addAction(NetworkConnectionService.SEND_MESSAGE)
        mApplicationContext?.registerReceiver(uiThreadMessageREciever,filter)
    }

    private fun sendMessage(body:String,toJid:String){

        Log.d(TAG,"Sending message to $toJid")

        var jid: EntityBareJid? = null
        val chatManager=ChatManager.getInstanceFor(mConnection)

        try {
            jid=JidCreate.entityBareFrom(toJid)

        }catch (e:XmppStringprepException) {

            e.printStackTrace()
        }

        val chat=chatManager.chatWith(jid)

        try {
            val message=Message(jid,Message.Type.chat)
            message.body=body
            chat.send(message)
            Log.d(TAG,"Message sent successfully to ${jid.toString()}")

        }catch (e:SmackException.NotConnectedException){
            e.printStackTrace()
        }catch (e: InterruptedException){
            e.printStackTrace()
        }
    }


    private fun sendMessage(chatMessage:ChatMessage):Boolean{
         Log.d(TAG,"Sending message to ${chatMessage.contactJid}")

        var jid:EntityBareJid?=null
        val chatManager=ChatManager.getInstanceFor(mConnection)

        try {
             jid=JidCreate.entityBareFrom(chatMessage.contactJid)
        }catch (e:XmppStringprepException){
            e.printStackTrace()
            return false
        }

        val chat=chatManager.chatWith(jid)
        try {
            val message=Message(jid,Message.Type.chat)
            message.body=chatMessage.text
            chat.send(message)
            informChatViewRecycler(chatMessage)
            return true
        }catch (e:SmackException.NotConnectedException){
            e.printStackTrace()
            return false
        }catch (e:InterruptedException){
            e.printStackTrace()
            return false
        }
    }

    private fun informChatViewRecycler(chatMessage: ChatMessage){
            val intent=Intent(NetworkConnectionService.UI_NEW_MESSAGE_FLAG)
            intent.setPackage(mApplicationContext?.packageName)
            intent.putExtra(NetworkConnectionService.BUNDLE_MESSAGE_TYPE,chatMessage.type.toString())
            intent.putExtra(NetworkConnectionService.BUNDLE_MESSAGE_ATTACHMENT_PATH,chatMessage.attachmentPath)
            mApplicationContext?.sendBroadcast(intent)
    }


    fun sendFile(fileFullPath:String,counterPartJid:String){
          Log.d(TAG,"Send file called")
          val fileUploadTask=FileUploadTask()
          fileUploadTask.execute(fileFullPath,counterPartJid)


    }

    fun transferFile(fileFullPath:String,contactJid: String){
        Log.d(TAG,"Transfer file called")
        val fileTransferTask=FileTransferTask()
        fileTransferTask.execute(fileFullPath,contactJid)
    }

    fun logoutUser(){
        val logoutUserTask=LogoutUser()
        logoutUserTask.execute()
    }

    fun downloadFileFromServer(fileUrl:String,contactJid:String){
            var url:URL?=null

            try {
                url= URL(fileUrl)
            }catch (e:MalformedURLException){
                e.printStackTrace()
                return
            }

            val extension=MimeUtils.extractRelevantExtension(url)
            val fileName=fileUrl.substring(fileUrl.lastIndexOf('/')+1)

           if(extension!=null){
                 Log.d(TAG,"Received extension is not null,Filename is : $fileName")

               // Create the filepath on the device
               var rootPath:File?=null

               if (MimeUtils.isFileAudio(extension)){
                   rootPath= File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),"SmackAndroidPlus")

               }else if (MimeUtils.isFileAudio(extension)){
                   rootPath=File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),"SmackAndroidPlus")
               }else if (MimeUtils.isFileImage(extension)){
                   rootPath= File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"SmacAndroidPlus")
               }else if (MimeUtils.isFileDocument(extension)){
                   rootPath= File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"SmacAndroidPlus")
               }else{
                   rootPath= File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"SmacAndroidPlus")
               }

               // Create a root directory if it is not there
               if(!rootPath.exists()){
                   if (rootPath.mkdirs()){
                       Log.d(TAG,"Files directory created successfully : ${rootPath.absolutePath}" )
                   }else{
                       Log.d(TAG,"Could not create files directory : ${rootPath.absolutePath}" )
                   }
               }

               // Pass over to the Async task to download
               val downloadFileTask=FileDownloadTask()
               downloadFileTask.execute(fileUrl,fileName,rootPath.absolutePath,contactJid)
           }
    }


    fun  getFileFromStream(fileTransferRequest:IncomingFileTransfer){
         Log.d(TAG,"getFileFromStream Called")
         val downloadFileFromByteStreamTask=DownloadFileFromByteStreams()
         downloadFileFromByteStreamTask.execute(fileTransferRequest)
    }




    override fun authenticated(connection: XMPPConnection?, resumed: Boolean) {
        NetworkConnectionService.sConnectionState=ConnectionState.CONNECTED
        Log.d(TAG,"Authenticated Successfully")
        showContactListActivityWhenAuthenticated()
    }

    override fun connected(connection: XMPPConnection?) {
        NetworkConnectionService.sConnectionState=ConnectionState.CONNECTED
        Log.d(TAG,"Connected Successfully")
    }

    override fun connectionClosed() {
        NetworkConnectionService.sConnectionState=ConnectionState.DISCONNECTED
        Log.d(TAG,"Connection closed")
    }

    override fun connectionClosedOnError(e: Exception?) {
        NetworkConnectionService.sConnectionState=ConnectionState.DISCONNECTED
        Log.d(TAG,"ConnectionClosedOnError, error "+ e.toString())
    }





    override fun reconnectingIn(seconds: Int) {
        NetworkConnectionService.sConnectionState = ConnectionState.CONNECTING
        Log.d(TAG,"ReconnectingIn")
    }



    override fun reconnectionFailed(e: Exception?) {
        NetworkConnectionService.sConnectionState = ConnectionState.DISCONNECTED
        Log.d(TAG,"ReconnectionFailed()")
    }

    override fun reconnectionSuccessful() {
        NetworkConnectionService.sConnectionState = ConnectionState.CONNECTED
        Log.d(TAG,"ReconnectionSuccessful")
    }
}