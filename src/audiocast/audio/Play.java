package audiocast.audio;

import java.util.concurrent.BlockingQueue;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public final class Play extends Thread {

	final AudioTrack stream;
	final BlockingQueue<byte[]> queue;	
	
	public Play(int sampleHz, BlockingQueue<byte[]> queue) {
		this.queue = queue;
		
		int bufsize = AudioTrack.getMinBufferSize(
				sampleHz, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
		Log.i("Audiocast","initialised player with buffer length "+ bufsize);		
		
		stream = new AudioTrack( 
					AudioManager.STREAM_VOICE_CALL, 
					sampleHz, 
					AudioFormat.CHANNEL_OUT_MONO, 
					AudioFormat.ENCODING_PCM_16BIT,  
					bufsize,
					AudioTrack.MODE_STREAM);
		
	}

	@Override
	public void run() {
		try {
			while (!Thread.interrupted()) {
				byte[] pkt = queue.take();
				int len = stream.write(pkt, 0, pkt.length);
				Log.d("Audiocast", "played "+len+" bytes");
			}
		} catch (InterruptedException e) {
		} finally {
			stream.stop();
			stream.release();
		}
	}
	
	public void pause(boolean pause) {
		if (pause) stream.stop(); 
		else stream.play();	
		
		Log.i("Audiocast", "playback stream state=" + stream.getState());		
	}
}
