// TODO: Update to match your plugin's package name.
package org.godotengine.plugin.android.video.player

import android.app.Activity
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.Toast
import androidx.media3.exoplayer.ExoPlayer
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.UsedByGodot
import org.godotengine.plugin.android.player.BuildConfig

import android.content.Context
import android.view.Surface
import android.view.SurfaceHolder
import android.view.ViewGroup
import androidx.media3.common.MediaItem

class GodotAndroidPlugin(godot: Godot): GodotPlugin(godot) {

    private var player: ExoPlayer? = null
    private var surfaceView: SurfaceView? = null

    override fun getPluginName() = BuildConfig.GODOT_PLUGIN_NAME


    override fun onMainCreate(activity: Activity?): View? {
        val context = activity ?: return null // Ensure activity is not null

        // Create a SurfaceView for video rendering
        surfaceView = SurfaceView(context)

        // Ensure UI updates happen on the main thread
        activity.runOnUiThread {
            activity.addContentView(
                surfaceView,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        }

        // Attach a callback to handle ExoPlayer when the surface is created/destroyed
        surfaceView?.holder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                initializePlayer(holder.surface)
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                releasePlayer()
            }
        })

        return surfaceView // Godot will use this View for rendering
    }

    private fun initializePlayer(surface: Surface) {
        player = ExoPlayer.Builder(activity as Context).build().apply {
            setVideoSurface(surface)
        }
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    @UsedByGodot
    fun loadVideo(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player?.setMediaItem(mediaItem)
        player?.prepare()
    }

    @UsedByGodot
    fun play() {
        player?.play()
    }

    @UsedByGodot
    fun pause() {
        player?.pause()
    }
}
