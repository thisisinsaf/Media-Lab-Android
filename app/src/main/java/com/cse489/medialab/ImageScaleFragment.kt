package com.cse489.medialab

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

/** Downloads an image on a worker thread, then allows touch zoom on the main thread. */
class ImageScaleFragment : Fragment(R.layout.fragment_image) {
    private val requestId = AtomicInteger(0)
    private val executor = Executors.newSingleThreadExecutor()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val input = view.findViewById<TextInputEditText>(R.id.imageUrlInput)
        val image = view.findViewById<ZoomableImageView>(R.id.zoomImage)
        val progress = view.findViewById<ProgressBar>(R.id.imageProgress)
        val placeholder = view.findViewById<TextView>(R.id.imagePlaceholder)
        val status = view.findViewById<TextView>(R.id.imageStatus)
        val load = view.findViewById<MaterialButton>(R.id.loadImageButton)

        load.setOnClickListener {
            val url = input.text?.toString()?.trim().orEmpty()
            if (!url.startsWith("https://", ignoreCase = true)) {
                status.text = "Please enter a valid HTTPS image URL."
                return@setOnClickListener
            }
            val request = requestId.incrementAndGet()
            load.isEnabled = false
            progress.visibility = View.VISIBLE
            placeholder.visibility = View.GONE
            status.text = "Downloading image over the internet…"

            executor.execute {
                val response = runCatching { downloadBitmap(url) }
                view.post {
                    if (!isAdded || this.view !== view || request != requestId.get()) return@post
                    load.isEnabled = true
                    progress.visibility = View.GONE
                    response.onSuccess { bitmap ->
                        image.setImageBitmap(bitmap)
                        placeholder.visibility = View.GONE
                        status.text = "Image loaded! Pinch to zoom, drag to pan, or double-tap."
                    }.onFailure { error ->
                        if (image.drawable == null) placeholder.visibility = View.VISIBLE
                        status.text = "Could not load the image: ${error.message ?: "network error"}"
                    }
                }
            }
        }

        view.findViewById<MaterialButton>(R.id.resetZoomButton).setOnClickListener {
            image.resetZoom()
        }
        // A default URL is already populated: loading automatically demonstrates internet use.
        load.performClick()
    }

    private fun downloadBitmap(urlString: String): Bitmap {
        val connection = URL(urlString).openConnection() as HttpURLConnection
        connection.connectTimeout = 10_000
        connection.readTimeout = 12_000
        connection.instanceFollowRedirects = true
        connection.setRequestProperty("User-Agent", "CSE489MediaLab/1.0")
        try {
            if (connection.responseCode !in 200..299) {
                error("HTTP ${connection.responseCode}")
            }
            val contentType = connection.contentType.orEmpty().lowercase()
            require(contentType.startsWith("image/")) { "URL did not return an image" }
            val data = ByteArrayOutputStream()
            connection.inputStream.use { stream ->
                val buffer = ByteArray(8192)
                while (true) {
                    val count = stream.read(buffer)
                    if (count < 0) break
                    require(data.size() + count <= 8 * 1024 * 1024) { "Image file exceeds 8 MB" }
                    data.write(buffer, 0, count)
                }
            }
            val bytes = data.toByteArray()
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
            require(bounds.outWidth > 0 && bounds.outHeight > 0) { "Unsupported image file" }
            var sample = 1
            while (bounds.outWidth / sample > 2400 || bounds.outHeight / sample > 2400) {
                sample *= 2
            }
            return BitmapFactory.decodeByteArray(
                bytes, 0, bytes.size, BitmapFactory.Options().apply { inSampleSize = sample }
            ) ?: error("Unable to decode image")
        } finally {
            connection.disconnect()
        }
    }

    override fun onDestroyView() {
        requestId.incrementAndGet() // Do not modify views after navigation or rotation.
        super.onDestroyView()
    }

    override fun onDestroy() {
        executor.shutdownNow()
        super.onDestroy()
    }
}
