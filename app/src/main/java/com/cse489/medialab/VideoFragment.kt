package com.cse489.medialab

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

/** Plays an original bundled MP4 with Android's VideoView / MediaPlayer backend. */
class VideoFragment : Fragment(R.layout.fragment_video) {
    private var videoView: VideoView? = null
    private var prepared = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val video = view.findViewById<VideoView>(R.id.localVideoView)
        val status = view.findViewById<TextView>(R.id.videoStatus)
        val overlay = view.findViewById<TextView>(R.id.videoOverlay)
        val play = view.findViewById<MaterialButton>(R.id.videoPlayPauseButton)
        val replay = view.findViewById<MaterialButton>(R.id.videoReplayButton)
        videoView = video

        val controls = MediaController(requireContext())
        controls.setAnchorView(video)
        video.setMediaController(controls)
        val uri = Uri.parse("android.resource://${requireContext().packageName}/${R.raw.sample_video}")
        video.setOnPreparedListener { player ->
            prepared = true
            player.isLooping = false
            status.text = "Ready · MP4 stored in res/raw"
            video.seekTo(1) // Display first frame before playback starts.
        }
        video.setOnCompletionListener {
            status.text = "Video finished. Replay whenever you like."
            play.text = "Play video"
            overlay.visibility = View.VISIBLE
        }
        video.setOnErrorListener { _, _, _ ->
            prepared = false
            status.text = "Could not play this MP4 on this device."
            play.text = "Play video"
            true
        }
        video.setVideoURI(uri)

        play.setOnClickListener {
            if (video.isPlaying) {
                video.pause()
                play.text = "Resume video"
                status.text = "Paused"
                overlay.visibility = View.VISIBLE
            } else {
                if (prepared && video.currentPosition >= video.duration - 100) video.seekTo(0)
                video.start()
                play.text = "Pause video"
                status.text = "Playing local video"
                overlay.visibility = View.GONE
            }
        }
        replay.setOnClickListener {
            video.seekTo(0)
            video.start()
            play.text = "Pause video"
            status.text = "Playing local video from start"
            overlay.visibility = View.GONE
        }
    }

    override fun onPause() {
        videoView?.pause()
        super.onPause()
    }

    override fun onDestroyView() {
        videoView?.stopPlayback()
        videoView = null
        prepared = false
        super.onDestroyView()
    }
}
