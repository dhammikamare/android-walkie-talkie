package audiocast.audio;

import java.util.concurrent.BlockingQueue;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public final class Record extends Thread {

	private static final int MAXLEN = 1024;
	final AudioRecord stream;
	final BlockingQueue<byte[]> queue;	

	public Record(int sampleHz, BlockingQueue<byte[]> queue) {
		this.queue = queue;

		int bufsize = AudioRecord.getMinBufferSize(
				sampleHz, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
		Log.i("Audiocast","initialised recorder with buffer length "+ bufsize);
		
		stream = new AudioRecord(
					MediaRecorder.AudioSource.MIC,
					sampleHz,
					AudioFormat.CHANNEL_IN_MONO, 
					AudioFormat.ENCODING_PCM_16BIT , 
					bufsize);
	}

	@Override
	public void run() {
		try {
			byte[] pkt = new byte[MAXLEN];
			
			while (!Thread.interrupted()) {
				int len = stream.read(pkt, 0, pkt.length);							
				queue.put(pkt);
				Log.d("Audiocast", "recorded "+len+" bytes");
			}
		} catch (InterruptedException e) {
		} finally {
			stream.stop();
			stream.release();
		}

	}
	
	public void pause(boolean pause) {
		if (pause) stream.stop(); 
		else stream.startRecording();
		
		Log.i("Audiocast", "record stream state=" + stream.getState());	
	}
}
