package com.bmanchi.top10downloader

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

private const val TAG = "FeedViewModel"
val EMPTY_FEED_LIST: List<FeedEntry> = Collections.emptyList()

class FeedViewModel: ViewModel(), DownloadData.DownloaderCallBack {

    private var downloadData: DownloadData? = null
    private var feedCachedUrl = "INVALIDATED"

    private val feed = MutableLiveData<List<FeedEntry>>()
    val feedEntries: LiveData<List<FeedEntry>>
        get() = feed

    init {
        feed.postValue(EMPTY_FEED_LIST)
    }

    fun downloadURL(feedURL: String) {
        Log.d(TAG, "downloadURL: called with url $feedURL")
        if (feedURL != feedCachedUrl) {
            Log.d(TAG, "downloadURL staring AsyncTask")
            downloadData= DownloadData(this)
            downloadData?.execute(feedURL)
            Log.d(TAG, "downloadURL done")
            feedCachedUrl = feedURL
        } else {
            Log.d(TAG, "downloadURL: URL not changed")
        }
    }

    fun invalidate() {
        feedCachedUrl = "INVALIDATE"
    }

    override fun onDataAvailable(data: List<FeedEntry>) {
        Log.d(TAG, "onDataAvailable called")
        feed.value = data
        Log.d(TAG, "onDataAvailable ends")
    }

    /**
     * This method will be called when this ViewModel is no longer used and will be destroyed.
     *
     *
     * It is useful when ViewModel observes some data and you need to clear this subscription to
     * prevent a leak of this ViewModel.
     */
    override fun onCleared() {
        Log.d(TAG, "onCleared: cancelling pending downloads")
        downloadData?.cancel(true)
    }
}