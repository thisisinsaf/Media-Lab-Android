# Manual test checklist · CSE 489 Assignment 2

Device/emulator: ___________________    Android version: __________

## Navigation drawer
- [ ] App launches without crash; Broadcast Receiver appears first.
- [ ] Hamburger icon opens drawer; back gesture closes it.
- [ ] All four drawer choices navigate to the corresponding fragment.
- [ ] Back from another tab returns to Broadcast Receiver.

## A — Broadcast Receiver
- [ ] Spinner contains **Custom broadcast receiver** and **System battery notification receiver**.
- [ ] Continue opens different activities depending on spinner choice.
- [ ] Custom: empty message displays validation.
- [ ] Custom: enter `Hello from CSE 489` and tap Send.
- [ ] Receiver screen displays exactly the message and the success label from `onReceive()`.
- [ ] Send again broadcasts again; Edit goes back to message-entry Activity 2.
- [ ] Battery: percentage matches emulator battery setting.
- [ ] Changing battery level or charging state in emulator updates the receiver screen.
- [ ] Back navigation returns to the Broadcast fragment.

## B — Image Scale
- [ ] A photo loads over HTTPS with internet available.
- [ ] Pinch gesture zooms both in and out.
- [ ] Drag pans the zoomed photo.
- [ ] Double tap toggles zoom/reset.
- [ ] Reset zoom fits the whole image.
- [ ] Bad network URL displays a failure message without crashing.

## C — Video
- [ ] Local MP4 has a visible first frame.
- [ ] Play/Pause/Resume buttons work.
- [ ] Replay seeks back to beginning.
- [ ] Playback works in airplane mode.

## D — Audio
- [ ] Local MP3 plays and is audible.
- [ ] Pause/Resume and Replay buttons work.
- [ ] Seek bar changes playback position.
- [ ] Elapsed and total time display correctly.
- [ ] Playback works in airplane mode.

### Notes / issues found

________________________________________________________________

________________________________________________________________
