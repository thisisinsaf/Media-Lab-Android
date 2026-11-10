# Implementation decisions

The assignment text distinguishes three custom-broadcast activities and one battery path. The app preserves that structure while using fragments for the four drawer destinations:

- **Activity 1** (the Broadcast fragment hosted by `MainActivity`): spinner selector and Continue button.
- **Activity 2** (`CustomMessageActivity`): text-entry screen in custom mode. In battery mode, `BatteryStatusActivity` listens for system battery changes instead.
- **Activity 3** (`BroadcastResultActivity`): custom broadcast receiver registered during `onStart`; input text is passed from Activity 2 and dispatched via `sendBroadcast` once registration is active. The UI displays the value inside the receiver callback.

We chose a **context-registered app-scoped receiver** rather than a manifest-exported receiver, so unrelated apps cannot inject arbitrary messages into the UI. For the sticky system battery broadcast, registration lasts only while its activity is visible.

The images are downloaded without third-party networking libraries using an executor and `HttpURLConnection`; 8 MB size limit, HTTPS-only URLs, media type checks and downsampling reduce accidental large allocations. A custom `ImageView` implements a touch matrix with pan, pinch, double tap and bounds clamping.

Audio and video files are intentionally bundled in `res/raw` rather than loaded from external sites. `VideoView` handles embedded MP4 playback. `MediaPlayer` handles the MP3 and updates the seek bar on the main thread. Both release their playback resources when navigating away.
