/**
 * Play
 * This join to multicast group, then recursively receives a packet from multicast group to byte buffer. 
 * Then writes the audio data to the audio hardware for playback.
 * 
 * @author (C) ziyan maraikar
 * @modified-by e11258 Marasinghe, M.M.D.B. @dhammika-marasinghe
 * @modified-by e11269 Naranpanawa, D.N.U. @nathashanaranpanawa
 */

package audiocast.audio;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.BlockingQueue;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public final class Play extends Thread {

	private static final int PORT = 8888;
	private static final String IP = "224.2.2.3";
	
	final AudioTrack stream;
	final BlockingQueue<byte[]> queue;

	public Play(int sampleHz, BlockingQueue<byte[]> queue) {
		this.queue = queue;

		int bufsize = AudioTrack.getMinBufferSize(
				sampleHz,
				AudioFormat.CHANNEL_OUT_MONO, 
				AudioFormat.ENCODING_PCM_16BIT);
		
		Log.i("Audiocast", "initialised player with buffer length " + bufsize);

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
		MulticastSocket socket = null;
		InetAddress address = null;
		DatagramPacket inPacket = null;
		try {
			// Prepare to join multicast group
			socket = new MulticastSocket(PORT);
			address = InetAddress.getByName(IP);
			socket.joinGroup(address);

			byte[] inBuf = new byte[512];

			while (!Thread.interrupted()) {
				// byte[] pkt = queue.take();

				// Receives a packet from multicast group
				inPacket = new DatagramPacket(inBuf, inBuf.length);
				socket.receive(inPacket);

				// Writes the audio data to the audio hardware for playback.
				int len = stream.write(inBuf, 0, inBuf.length);

				Log.d("Audiocast", "played " + len + " bytes");
			}
		} catch (IOException e) {
			Log.e("Audiocast", e.getMessage());
		} finally {
			stream.stop();
			stream.release();
			try {
				socket.leaveGroup(address);
			} catch (IOException e) {
				Log.e("Audiocast", e.getMessage());
			}
			socket.close();
		}
	}

	public void pause(boolean pause) {
		if (pause) {
			stream.stop();
		} else {
			stream.play();
		}
		Log.i("Audiocast", "playback stream state=" + stream.getState());
	}
}
