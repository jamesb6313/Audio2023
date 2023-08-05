package com.example.audio2023

import SampleData
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.res.Configuration
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.audio2023.ui.theme.Audio2023Theme
import java.io.File


//object ListHolder {
//    private val list = mutableListOf<AudioSongs>()
//
//    fun addItems(vararg items: AudioSongs) {
//        list.addAll(items)
//    }
//
//    fun addItem(item: AudioSongs) {
//        list.add(item)
//    }
//
//    fun removeAll() {
//        list.clear()
//    }
//
//    fun removeItem(item: AudioSongs) = list.remove(item)
//
//    fun getSongs(): List<AudioSongs> = list
//}

//SEE: https://developer.android.com/media/implement/playback-app
class MainActivity : ComponentActivity() {

    var audioList: ArrayList<AudioSongs>? = null
    //val audioList = ListHolder.getSongs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        audioList = ArrayList<AudioSongs>()
        loadAudio()

        setContent {
            Audio2023Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Greeting("Android")
                    //MessageCard(Message("Android","Jetpack Compose"))
                    Conversation(SampleData.conversationSample)
                }
            }
        }
    }

    @SuppressLint("Range")
    fun myNewGetAudioFileCount(): Int {
//See:https://stackoverflow.com/questions/11982195/how-to-access-music-files-from-android-programatically
//See:https://stackoverflow.com/questions/21403221/how-to-get-all-audio-video-files?rq=4

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.ArtistColumns.ARTIST
        )
        val selection = MediaStore.Audio.Media.IS_MUSIC + " !=0 "
        val cursor = contentResolver.query(
            uri,
            projection,
            selection,
            null,
            null
        )

        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val title =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val album =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                val artist =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))


// data = /storage/emulated/0/Music/LinkinPark2.mp3
                // displayName = /LinkinPark2.mps
                // RelPath = Music/
                // Save to audioList
                audioList!!.add(AudioSongs(title, album, artist))
            }
            audioList!!.shuffle()
        }

        val songCount = cursor!!.count
        myShowErrorDlg("Songs found = $songCount")
        cursor.close()

        return songCount
    }

    fun myShowErrorDlg(errMsg: String) {
        // build alert dialog
        val dialogBuilder = AlertDialog.Builder(this)

        // set message of alert dialog
        dialogBuilder.setMessage("Populate Music Folder with MP3 songs and try again.")
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("Close App", DialogInterface.OnClickListener {
                    _, _ -> //finish()
            })
        // negative button text and action
//            .setNegativeButton("Continue", DialogInterface.OnClickListener {
//                    dialog, id -> dialog.cancel()
//            })

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle(errMsg)
        // show alert dialog
        alert.show()
    }

    //loadAudio
    //////////////////////////////////////////////////////////
    fun loadAudio() {
        try {
            myNewGetAudioFileCount()
        } catch (e: Exception) {
            myShowErrorDlg("Error = " + e.message)
// Cannot use Toast in catch stmt - Toast.makeText(this, " Error = " + e.message, Toast.LENGTH_SHORT).show()
        }
    }
}

data class Message(val author: String, val body: String)

@Composable
fun Conversation(messages: List<Message>) {
    LazyColumn{ 
        items(messages) { message ->
            MessageCard(message)
        }
    }
}

@Preview
@Composable
fun PreviewConversation() {
    Audio2023Theme {
        Conversation(messages = (SampleData.conversationSample))
    }
}

@Composable
fun MessageCard(msg: Message) {
    Row (modifier = Modifier.padding(all = 8.dp)) {
        Image(
            painter = painterResource(R.drawable.profile_picture),
            contentDescription = "Contact profile picture",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        var isExpanded by remember { mutableStateOf((false)) }

        // surfaceColor will be updated gradually from one color to the other
        val surfaceColor by animateColorAsState(
            if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            label = "",
        )

        Column(modifier = Modifier.clickable { isExpanded = !isExpanded}) {
            Text(text = msg.author,
                color = MaterialTheme.colorScheme.secondary,
                style =  MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(4.dp))

            Surface(shape =  MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                color = surfaceColor,
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
            )
            {
                Text(text = msg.body,
                    modifier = Modifier.padding(all = 4.dp),
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    style =  MaterialTheme.typography.bodyMedium)
            }
        }
    }





}




//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}

@Preview(name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground =  true,
    name = "Dark Mode"
)
//@Composable
//fun GreetingPreview() {
//    Audio2023Theme {
//        Greeting("Android")
//    }
//}

@Composable
fun PreviewMessageCard() {
    Audio2023Theme {
        Surface {
            MessageCard(
                msg = Message("Lexi","Hey, tal a look at JetPack"))
        }
    }
}