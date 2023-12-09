package se.magictechnology.intromlkit

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import se.magictechnology.intromlkit.ui.theme.IntroMLKitTheme

class MainActivity : ComponentActivity() {
    private var result = mutableStateOf(emptyList<Recognition>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntroMLKitTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        var imageResourceId by remember { mutableStateOf(0) }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(onClick = {
                                imageResourceId = R.drawable.oscarwilde
                                runTextRecognition(resourceId = imageResourceId)
                            }) {
                                Text("Oscar Wilde")
                            }

                            Button(onClick = {
                                imageResourceId = R.drawable.postit
                                runTextRecognition(resourceId = imageResourceId)
                            }) {
                                Text("Post It")
                            }

                            Button(onClick = {
                                imageResourceId = R.drawable.roadsigns
                                runTextRecognition(resourceId = imageResourceId)
                            }) {
                                Text("Road Signs")
                            }
                        }

                        if (imageResourceId > 0) {
                            Image(
                                painter = painterResource(id = imageResourceId),
                                modifier = Modifier.padding(all = 50.dp).align(Alignment.CenterHorizontally),
                                contentScale = ContentScale.Fit,
                                contentDescription = ""
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 16.dp),
                            text = result.value.map {
                                it.asString()
                            }.joinToString(separator = "\n")
                        )
                    }
                }
            }
        }
    }

    private fun runTextRecognition(resourceId: Int) {
        var selectedImage = BitmapFactory.decodeResource(resources, resourceId)

        val image = InputImage.fromBitmap(selectedImage, 0)
        var textRecognizerOptions = TextRecognizerOptions.Builder().build()
        val recognizer = TextRecognition.getClient(textRecognizerOptions)
        //mTextButton.setEnabled(false)
        recognizer.process(image)
            .addOnSuccessListener { texts ->
                //mTextButton.setEnabled(true)
                result.value = processTextRecognitionResult(texts)
            }
            .addOnFailureListener { e -> // Task failed with an exception
                //mTextButton.setEnabled(true)
                e.printStackTrace()
                result.value = listOf(Recognition(text = "ERROR", confidence = "-"))
            }
    }

    private fun processTextRecognitionResult(texts: Text): List<Recognition> {
        val blocks: List<Text.TextBlock> = texts.getTextBlocks()
        if (blocks.size == 0) {
            //showToast("No text found")
            return emptyList()
        }
        //mGraphicOverlay.clear()

        var result = mutableListOf<Recognition>()
        for (i in blocks.indices) {
            val lines: List<Text.Line> = blocks[i].getLines()
            for (j in lines.indices) {
                val elements: List<Text.Element> = lines[j].getElements()
                for (k in elements.indices) {
                    //val textGraphic: Graphic = TextGraphic(mGraphicOverlay, elements[k])
                    //mGraphicOverlay.add(textGraphic)
                    Log.i("MLKITDEBUG",elements[k].text + " " + elements[k].confidence.toString())
                    result.add(Recognition(text = elements[k].text, confidence = elements[k].confidence.toString()))
                }
            }
        }
        return result
    }
}

private data class Recognition(
    val text: String,
    val confidence: String
)

private fun Recognition.asString(): String {
    return "\"$text\" with confidence \"$confidence\""
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IntroMLKitTheme {
        Greeting("Android")
    }
}