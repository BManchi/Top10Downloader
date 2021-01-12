@file:Suppress("DEPRECATION")

package com.bmanchi.top10downloader

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.io.IOException
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate called")
        val downloadData = DownloadData()
        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=10/xml")
        Log.d(TAG, "onCreate done")
    }

    companion object {
        private class DownloadData : AsyncTask<String, Void, String>() {
            private val TAG = "DownloadData"

            /**
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
             */
            override fun doInBackground(vararg url: String?): String {
                Log.d(TAG, "doInBackground: starts with ${url[0]}")
                val rssFeed = downloadXML(url[0])
                if (rssFeed.isEmpty()) {
                    Log.e(TAG, "doInBackground: Error downloading")
                }
                return rssFeed
            }

            /**
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
             */
            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                Log.d(TAG, "onPostExcecute parameter is $result")
            }

            private fun downloadXML(urlPath: String?): String {
                val xmlResult = StringBuilder()

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
                return "" // If it gets here, there's been a problem. Return empty string
            }
        }

    }


}