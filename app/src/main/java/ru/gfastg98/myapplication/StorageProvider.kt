package ru.gfastg98.myapplication

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import kotlinx.coroutines.InternalCoroutinesApi
import ru.gfastg98.myapplication.room.AppDatabase
import ru.gfastg98.myapplication.room.Word


class StorageProvider : ContentProvider() {

    //@Inject lateinit var db : AppDatabase

    private var db : AppDatabase? = context?.let { AppDatabase.create(it) }

    private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    companion object{
        const val CONTENT_AUTHORITY = "ru.gfastg98.myapplication.provider.StorageProvider"
        const val WORDS_PATH = "words"
        const val WORD = 100
        const val WORD_ID = 101
        const val DELETE_ALL = 102
    }

    init {
        sUriMatcher.addURI(CONTENT_AUTHORITY, WORDS_PATH, WORD)
        sUriMatcher.addURI(CONTENT_AUTHORITY, "$WORDS_PATH/#", WORD_ID)
        sUriMatcher.addURI(CONTENT_AUTHORITY, "$WORDS_PATH/deleteAll", DELETE_ALL)
    }

    override fun onCreate(): Boolean {
        //_db = AppDatabase.create(context!!)

        db = AppDatabase.create(context!!)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val match = sUriMatcher.match(uri)

        return when(match){
            WORD -> {
                db?.query("SELECT ${projection?.joinToString()?:"*"} FROM words ORDER BY word", emptyArray())
            }

            WORD_ID  -> {
                val id = ContentUris.parseId(uri)
                db?.query("SELECT ${projection?.toString()?:"*"} FROM words WHERE id = $id ORDER BY word", emptyArray())
            }
            DELETE_ALL->{
                db?.wordDao?.deleteAll()?:0
                null
            }
            else -> throw IllegalArgumentException("Cannot query unknown URI $uri")
        }
    }

    override fun getType(uri: Uri): String {
        return when (sUriMatcher.match(uri)) {
            WORD -> "vnd.android.cursor.table/$CONTENT_AUTHORITY.$WORDS_PATH"
            WORD_ID -> "vnd.android.cursor.item/$CONTENT_AUTHORITY.$WORDS_PATH"
            else -> throw IllegalStateException("Unknown URI: $uri")
        }
    }

    @OptIn(InternalCoroutinesApi::class)
    override fun insert(uri: Uri, values: ContentValues?): Uri {
        return when (sUriMatcher.match(uri)){
            WORD->{

                val id : Long = values?.let {
                    db?.wordDao?.insert(
                        Word(
                            values.getAsInteger("id"),
                            values.getAsString("word"),
                            values.getAsBoolean("isSelected")
                        )
                    )
                }?:-1L

                context?.contentResolver?.notifyChange(uri,null)
                ContentUris.withAppendedId(uri,id)
            }
            WORD_ID-> throw IllegalArgumentException("Invalid URI, cannot insert with ID: $uri")
            else -> throw IllegalArgumentException("Invalid URI: $uri")
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
        /*return when (sUriMatcher.match(uri)){
            WORD->{
                db?.wordDao?.deleteAll()?:0
            }
            WORD_ID-> throw IllegalArgumentException("Invalid URI, cannot insert with ID: $uri")
            else -> throw IllegalArgumentException("Invalid URI: $uri")
        }*/
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        TODO("Not yet implemented")
    }
}