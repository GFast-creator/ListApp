package ru.gfastg98.myapplication

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.gfastg98.myapplication.module.CONSTANTS.NOTIFICATION_ID
import ru.gfastg98.myapplication.room.Word
import ru.gfastg98.myapplication.room.WordViewModel
import ru.gfastg98.myapplication.ui.theme.ItemGreen
import ru.gfastg98.myapplication.ui.theme.ItemRed
import ru.gfastg98.myapplication.ui.theme.MyApplicationTheme
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel : WordViewModel by viewModels()

    @Inject lateinit var notificationManager: NotificationManager
    @Inject lateinit var vibrationManager: VibratorManager
    @Inject lateinit var notification : NotificationCompat.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                Extracted()
            }
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun WordCardPreview() {
        val context = LocalContext.current
        MyApplicationTheme {
            WordCard(Word(word = context.resourceString(R.string.hello_world)),
                selected = true,
                selectedForDelete = false,
                onClick = {},
                onLongClick = {})
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    private fun Extracted(
    ) {
        var text by rememberSaveable { mutableStateOf("") }

        var words by remember {
            mutableStateOf(listOf<Word>())
        }

        var selectedWords by remember {
            mutableStateOf(listOf<Word>())
        }

        var isDialog by remember {
            mutableStateOf(false)
        }

        var selectedForDelete by remember {
            mutableStateOf(listOf<Word>())
        }

        viewModel.words.onEach {
            words = it
        }.launchIn(lifecycleScope)

        if (isDialog) {
            var newWord by rememberSaveable {
                mutableStateOf("")
            }

            AlertDialog(onDismissRequest = { isDialog = false },
                title = { Text(stringResource(R.string.adding_element)) },
                text = {
                    Column {
                        if (words.any { p -> p.word == newWord }) {
                            Text(stringResource(R.string.word_exist), color = Color.Red)
                        }
                        TextField(
                            value = newWord,
                            onValueChange = { newWord = it },
                            label = {
                                Text(
                                    stringResource(R.string.new_word)
                                )
                            },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (newWord.isNotBlank() && words.none { p -> p.word == newWord }) {
                            viewModel.save(Word(word = newWord))
                            isDialog = false
                        }
                    }) {
                        Text(text = stringResource(R.string.ok))
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        isDialog = false
                    }) {
                        Text(text = stringResource(R.string.cancel))
                    }
                }
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = resourceString(R.string.app_name)
                        )
                    },
                    actions = {
                        if (selectedForDelete.isNotEmpty()) {
                            IconButton(onClick = {
                                selectedForDelete = emptyList()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(id = R.string.cancel)
                                )
                            }
                            IconButton(onClick = {
                                selectedForDelete = words
                            }) {
                                Icon(
                                    ImageVector
                                        .vectorResource(id = R.drawable.baseline_select_all_24),
                                    contentDescription = stringResource(id = R.string.select_all),
                                )
                            }
                            IconButton(onClick = {
                                viewModel.delete(*selectedForDelete.toTypedArray())
                                selectedWords =
                                    selectedWords.filter { w -> w !in selectedForDelete }
                                selectedForDelete = emptyList()
                                updateFoundation(selectedWords, text)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.delete_selected)
                                )
                            }
                        }
                    }
                )
            }
        ) { padding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding), color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = text,
                        onValueChange = { s ->
                            text = s
                            updateFoundation(selectedWords, text)
                        },
                        label = { Text(stringResource(R.string.search)) })

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {

                        item {
                            Row {
                                IconButton(onClick = { isDialog = true }) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = stringResource(R.string.add)
                                    )
                                }

                                /*Button(
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ItemRed
                                    ),
                                    onClick = {
                                        selectedWords = listOf()
                                        viewModel.deleteAll()
                                        updateFoundation(selectedWords, text)
                                    }) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = stringResource(R.string.delete_all)
                                    )
                                    Text(stringResource(id = R.string.delete_all))
                                }*/
                            }
                        }

                        items(words.size, key = { it }) { index ->

                            WordCard(
                                words[index],
                                selected = words[index] in selectedWords,
                                selectedForDelete = words[index] in selectedForDelete,
                                onClick = {
                                    if (selectedForDelete.isNotEmpty()) {

                                        if (words[index] !in selectedForDelete)
                                            selectedForDelete += words[index]
                                        else
                                            selectedForDelete -= words[index]

                                        vibrate(VibrationEffect.EFFECT_CLICK)


                                    } else {

                                        if (words[index] !in selectedWords) selectedWords += words[index]
                                        else selectedWords -= words[index]

                                        updateFoundation(selectedWords, text)
                                    }
                                },
                                onLongClick = {
                                    if (selectedForDelete.isEmpty()) {
                                        selectedForDelete += words[index]
                                        vibrate(VibrationEffect.EFFECT_HEAVY_CLICK)
                                    }
                                })
                        }

                    }
                }
            }
        }
    }

    private fun vibrate(effectId: Int) {
        vibrationManager.vibrate(
            CombinedVibration.createParallel(
                VibrationEffect.createPredefined(
                    effectId
                )
            )
        )
    }

    //Карточка для слова
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun WordCard(
        wordObj: Word,
        selected: Boolean,
        selectedForDelete: Boolean,
        onClick: () -> Unit,
        onLongClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                .wrapContentSize()
                .selectable(
                    selected = selected,
                    onClick = onClick
                )
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                ),
            colors = CardDefaults.run {
                if (selectedForDelete) {
                    cardColors(
                        containerColor = ItemRed
                    )
                } else if (selected) {
                    cardColors(
                        containerColor = ItemGreen
                    )
                } else cardColors()
            }

        ) {
            // Тело карточки
            Row {
                Text(
                    wordObj.word,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .align(Alignment.CenterVertically)
                )
                /*Button(
                    onClick = onClickDelete,
                    Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Кнопка удалить")
                }*/
            }
        }
    }

    private fun updateFoundation(
        selectedWords: List<Word>,
        text: String
    ) {
        notificationManager.cancel(NOTIFICATION_ID)

        val founded = selectedWords.filter { p -> text.contains(p.word) }
        if (founded.isEmpty()) return

        notification.setContentText(
            getString(
                R.string.founded_elements)
                    + founded.joinToString(separator = ",\n") {
                        it.word
                    }
        )

        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

}

fun Context.resourceString(res: Int): String {
    return this.resources.getString(res)
}
