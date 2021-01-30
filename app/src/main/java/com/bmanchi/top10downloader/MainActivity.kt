@file:Suppress("DEPRECATION")

package com.bmanchi.top10downloader

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"
private const val URL_CONTENTS = "URL contents"
private const val LIMIT_CONTENTS = "URL limit contents"

class FeedEntry {
    var name: String = ""
    var artist: String = ""
    var releaseDate: String =""
    var summary: String = ""
    var imageURL: String = ""

//    override fun toString(): String {
//        return """
//            name = " $name
//            artist = $artist
//            releaseDate = $releaseDate
//            imageURL = $imageURL
//        """.trimIndent()
//    }
}
class MainActivity : AppCompatActivity() {


//    private val downloadData: DownloadData by lazy { DownloadData(this, findViewById(R.id.xmlListView)) }
//
//    Moved to FeedViewModel
//    private var downloadData: DownloadData? = null

//    private var feedCachedUrl = "INVALIDATED"

    private var feedUrl: String ="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
    private var feedLimit = 10

    private val feedViewModel by lazy { ViewModelProviders.of(this).get(FeedViewModel::class.java)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate called")

        val feedAdapter = FeedAdapter(this, R.layout.list_record, EMPTY_FEED_LIST)
        xmlListView.adapter = feedAdapter

        if (savedInstanceState != null) {
            feedLimit = savedInstanceState.getInt(LIMIT_CONTENTS)
            feedUrl = savedInstanceState.getString(URL_CONTENTS).toString()
        }


        feedViewModel.feedEntries.observe(this,
        Observer < List<FeedEntry>> { feedEntries -> feedAdapter.setFeedList(feedEntries ?: EMPTY_FEED_LIST) })

        feedViewModel.downloadURL(feedUrl.format(feedLimit))
        Log.d(TAG, "onCreate done")
    }
//    private fun downloadURL(feedURL: String) {
//        if (feedURL != feedCachedUrl) {
//            Log.d(TAG, "downloadURL staring AsyncTask")
//            downloadData= DownloadData(this, findViewById(R.id.xmlListView))
//            downloadData?.execute(feedURL)
//            Log.d(TAG, "downloadURL done")
//            feedCachedUrl = feedURL
//        } else {
//            Log.d(TAG, "downloadURL: URL not changed")
//        }
//    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     *
     *
     * This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * [.onPrepareOptionsMenu].
     *
     *
     * The default implementation populates the menu with standard system
     * menu items.  These are placed in the [Menu.CATEGORY_SYSTEM] group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     *
     *
     * You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     *
     *
     * When you add items to the menu, you can implement the Activity's
     * [.onOptionsItemSelected] method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     *
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     *
     * @see .onPrepareOptionsMenu
     *
     * @see .onOptionsItemSelected
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feeds_menu, menu)

        if (feedLimit ==10) {
            menu?.findItem(R.id.mnu10)?.isChecked = true
        } else {
            menu?.findItem(R.id.mnu25)?.isChecked = true
        }
        return true
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     *
     *
     * Derived classes should call through to the base class for it to
     * perform the default menu handling.
     *
     * @param item The menu item that was selected.
     *
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     *
     * @see .onCreateOptionsMenu
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.mnuFree ->
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
            R.id.mnuPaid ->
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"
            R.id.mnuSongs ->
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"
            R.id.mnu10, R.id.mnu25 -> {
                if  (!item.isChecked) {
                    item.isChecked = true
                    feedLimit =35 - feedLimit
                    Log.d(TAG, "onOptionsItemsSelected: ${item.title} setting feedLimit to ${feedLimit}")
                } else {
                    Log.d(TAG, "onOptionsItemsSelected: ${item.title} setting feedLimit unchanged")
                }
            }
            R.id.mnuRefresh -> feedViewModel.invalidate()
            else ->
                return super.onOptionsItemSelected(item)
        }

        feedViewModel.downloadURL(feedUrl.format(feedLimit))
        return true
    }

    /**
     * This method is called after [.onStart] when the activity is
     * being re-initialized from a previously saved state, given here in
     * <var>savedInstanceState</var>.  Most implementations will simply use [.onCreate]
     * to restore their state, but it is sometimes convenient to do it here
     * after all of the initialization has been done or to allow subclasses to
     * decide whether to use your default implementation.  The default
     * implementation of this method performs a restore of any view state that
     * had previously been frozen by [.onSaveInstanceState].
     *
     *
     * This method is called between [.onStart] and
     * [.onPostCreate]. This method is called only when recreating
     * an activity; the method isn't invoked if [.onStart] is called for
     * any other reason.
     *
     * @param savedInstanceState the data most recently supplied in [.onSaveInstanceState].
     *
     * @see .onCreate
     *
     * @see .onPostCreate
     *
     * @see .onResume
     *
     * @see .onSaveInstanceState
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        feedLimit = savedInstanceState.getInt(LIMIT_CONTENTS)
        feedUrl = savedInstanceState.getString(URL_CONTENTS).toString()

    }

    /**
     * This is the same as [.onSaveInstanceState] but is called for activities
     * created with the attribute [android.R.attr.persistableMode] set to
     * `persistAcrossReboots`. The [android.os.PersistableBundle] passed
     * in will be saved and presented in [.onCreate]
     * the first time that this activity is restarted following the next device reboot.
     *
     * @param outState Bundle in which to place your saved state.
     * @param outPersistentState State which will be saved across reboots.
     *
     * @see .onSaveInstanceState
     * @see .onCreate
     *
     * @see .onRestoreInstanceState
     * @see .onPause
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(URL_CONTENTS, feedUrl)
        outState.putInt(LIMIT_CONTENTS, feedLimit)
    }
//    override fun onDestroy() {
//        super.onDestroy()
//        downloadData?.cancel(true)
//    }

    companion object {
        /*private class DownloadData(context: Context, listView: ListView) : AsyncTask<String, Void, String>() {
            private val TAG = "DownloadData"

            var propContext: Context by Delegates.notNull()
            var propListView: ListView by Delegates.notNull()

            init {
                propContext= context
                propListView = listView
            }

            *//**
             * Override this method to perform a computation on a background thread. The
             * specified parameters are the parameters passed to [.execute]
             * by the caller of this task.
             *
             * This will normally run on a background thread. But to better
             * support testing frameworks, it is recommended that this also tolerates
             * direct execution on the foreground thread, as part of the [.execute] call.
             *
             * This method can call [.publishProgress] to publish updates
             * on the UI thread.
             *
             * @param url The parameters of the task.
             *
             * @return A result, defined by the subclass of this task.
             *
             * @see .onPreExecute
             * @see .onPostExecute
             *
             * @see .publishProgress
             *//*
            override fun doInBackground(vararg url: String?): String {
//                Log.d(TAG, "doInBackground: starts with ${url[0]}")
                val rssFeed = downloadXML(url[0])
                if (rssFeed.isEmpty()) {
                    Log.e(TAG, "doInBackground: Error downloading")
                }
                return rssFeed
            }

            *//**
             *
             * Runs on the UI thread after [.doInBackground]. The
             * specified result is the value returned by [.doInBackground].
             * To better support testing frameworks, it is recommended that this be
             * written to tolerate direct execution as part of the execute() call.
             * The default version does nothing.
             *
             *
             * This method won't be invoked if the task was cancelled.
             *
             * @param result The result of the operation computed by [.doInBackground].
             *
             * @see .onPreExecute
             *
             * @see .doInBackground
             *
             * @see .onCancelled
             *//*
            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
//                Log.d(TAG, "onPostExcecute parameter is $result")
                val parseApplications = ParseApplications()
                parseApplications.parse(result)

               *//* // Old adapter
                val arrayAdapter = ArrayAdapter<FeedEntry>(propContext, R.layout.list_item, parseApplications.applications)
                propListView.adapter = arrayAdapter*//*

                // New custom adapter
                val feedAdapter = FeedAdapter(propContext, R.layout.list_record, parseApplications.applications)
                propListView.adapter = feedAdapter
            }

            private fun downloadXML(urlPath: String?): String {
                *//*val xmlResult = StringBuilder()

                try {
                    val url = URL(urlPath)
                    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                    val response = connection.responseCode
                    Log.d(TAG, "downloadXML: The response code was $response")

//            val inputStream = connection.inputStream
//            val inputStreamReader = InputStreamReader(inputStream)
//            val reader = BufferedReader(inputStreamReader)
//                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
//
//                    val inputBuffer = CharArray(500)
//                    var charsRead = 0
//                    while (charsRead <= 0) {
//                        charsRead = reader.read(inputBuffer)
//                        if (charsRead > 0) {
//                            xmlResult.append(String(inputBuffer, 0, charsRead))
//                        }
//                    }
//                    reader.close()

//                    val stream = connection.inputStream
                    connection.inputStream.buffered().reader().use { xmlResult.append(it.readText()) }

                    Log.d(TAG, "Received ${xmlResult.length} bytes")
                    return xmlResult.toString()

//                } catch (e:MalformedURLException) {
//                    Log.e(TAG, "downloadXML: Invalid URL ${e.message}")
//                } catch (e:IOException) {
//                    Log.e(TAG, "downloadXML: IO Exception reading data: ${e.message}")
//                } catch (e:SecurityException){
//                    Log.e(TAG, "downloadXML: Security exception. Needs permissions? ${e.message}")
//                } catch (e:Exception) {
//                    Log.e(TAG, "Unknown error: ${e.message}")
//                }

                } catch (e: Exception) {
                    val errorMessage: String
                    when (e) {
                        is MalformedURLException -> errorMessage =
                            "downloadXML: Invalid URL ${e.message}"
                        is IOException -> errorMessage = "downloadXML: Invalid URL ${e.message}"
                        is SecurityException -> {
                            e.printStackTrace()
                            errorMessage =
                                "downloadXML: Security Exception. Needs permission? ${e.message}"
                        }
                    }
                }
                return "" // If it gets here, there's been a problem. Return empty string*//*

                // More Idiomatic Kotlin
                return URL(urlPath).readText()

            }

        }*/

    }
}