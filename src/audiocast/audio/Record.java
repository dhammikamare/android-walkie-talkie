/**
 * Record
 * This join to multicast group, then recursively reads audio data from the audio hardware 
 * for recording into a buffer, made Datagram packets and sends to multicast group.
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

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public final class Record extends Thread {

	private static final int MAXLEN = 1024;
	private static final int PORT = 8888;
	private static final String IP = "224.2.2.3";
	
	final AudioRecord stream;

	public Record(int sampleHz) {
		
		int bufsize = AudioRecord.getMinBufferSize(
				sampleHz,
				AudioFormat.CHANNEL_IN_MONO, 
				AudioFormat.ENCODING_PCM_16BIT);
		
		Log.i("Audiocast", "initialised recorder with buffer length " + bufsize);

		stream = new AudioRecord(
				MediaRecorder.AudioSource.MIC, 
				sampleHz,
				AudioFormat.CHANNEL_IN_MONO, 
				AudioFormat.ENCODING_PCM_16BIT,
				bufsize);
	}

	@Override
	public void run() {
		MulticastSocket socket = null;
		InetAddress address = null;
		DatagramPacket sendPacket = null;
		try {
			// Prepare to join multicast group
			socket = new MulticastSocket(PORT);
			address = InetAddress.getByName(IP);
			socket.joinGroup(address);

			byte[] pkt = new byte[MAXLEN];

			while (!Thread.interrupted()) {
				// Reads audio data from the audio hardware for recording into a buffer.
				int len = stream.read(pkt, 0, pkt.length);

				// Make and sends UDP packets to multicast group
				sendPacket = new DatagramPacket(pkt, pkt.length, address, PORT);
				socket.send(sendPacket);
				
				Log.d("Audiocast", "recorded " + len + " bytes");
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
		}
	}

	public void pause(boolean pause) {
		if (pause) {
			stream.stop();
		} else {
			stream.startRecording();
		}
		Log.i("Audiocast", "record stream state=" + stream.getState());
	}
}
