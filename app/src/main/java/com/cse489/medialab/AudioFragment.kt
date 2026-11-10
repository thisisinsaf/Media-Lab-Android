package com.cse489.medialab

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import java.util.Locale

/** Plays a bundled MP3 via MediaPlayer with pause, replay and seek support. */
class  AudioFragment : Fragment(R.layout.fragment_audio) {
    private var player: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var seeking = false
    private var seekBar: SeekBar? = null
    private var elapsed: TextView? = null
    private var duration: TextView? = null
    private var status: TextView? = null
    private var playButton: MaterialButton? = null

    private val updateProgress = object : Runnable {
        override fun run() {
            val media = player ?: return
            if (!seeking) seekBar?.progress = media.currentPosition
            elapsed?.text = formatMs(media.currentPosition)
            handler.postDelayed(this, 250)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        seekBar = view.findViewById(R.id.audioSeekBar)
        elapsed = view.findViewById(R.id.audioElapsed)
        duration = view.findViewById(R.id.audioDuration)
        status = view.findViewById(R.id.audioStatus)
        playButton = view.findViewById(R.id.audioPlayPauseButton)

        player = MediaPlayer.create(requireContext(), R.raw.sample_audio)
        val media = player
        if (media == null) {
            status?.text = "Audio file could not be opened."
            playButton?.isEnabled = false
            return
        }
        seekBar?.max = media.duration
        duration?.text = formatMs(media.duration)
        media.setOnCompletionListener {
            it.seekTo(0)
            playButton?.text = "Play audio"
            status?.text = "Finished · ready to replay"
            seekBar?.progress = 0
        }

        playButton?.setOnClickListener {
            if (media.isPlaying) {
                media.pause()
                playButton?.text = "Resume audio"
                status?.text = "Paused"
            } else {
                media.start()
                playButton?.text = "Pause audio"
                status?.text = "Now playing"
            }
        }
        view.findViewById<MaterialButton>(R.id.audioReplayButton).setOnClickListener {
            media.seekTo(0)
            media.start()
            playButton?.text = "Pause audio"
            status?.text = "Now playing from start"
        }
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(bar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) elapsed?.text = formatMs(progress)
            }
            override fun onStartTrackingTouch(bar: SeekBar?) { seeking = true }
            override fun onStopTrackingTouch(bar: SeekBar?) {
                media.seekTo(bar?.progress ?: 0)
                seeking = false
            }
        })
        handler.post(updateProgress)
    }

    override fun onPause() {
        if (player?.isPlaying == true) {
            player?.pause()
            playButton?.text = "Resume audio"
            status?.text = "Paused"
        }
        super.onPause()
    }

    override fun onDestroyView() {
        handler.removeCallbacks(updateProgress)
        player?.release()
        player = null
        seekBar = null
        elapsed = null
        duration = null
        status = null
        playButton = null
        super.onDestroyView()
    }

    private fun formatMs(ms: Int): String {
        val seconds = (ms / 1000).coerceAtLeast(0)
        return String.format(Locale.US, "%02d:%02d", seconds / 60, seconds % 60)
    }
}
