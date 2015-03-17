/**
 * AudioPacket
 * Definition of application message format.
 * 
 * @author e11258 Marasinghe, M.M.D.B. @dhammika-marasinghe
 * @author e11269 Naranpanawa, D.N.U. @nathashanaranpanawa
 */

package audiocast.model;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class AudioPacket {

	private static final int MAXLEN = 1024;

	ByteBuffer buf;

	int seqNo;
	byte[] audio;

	public AudioPacket(int seqNo, byte[] audio) {
		this.seqNo = seqNo;
		this.audio = audio;
		buf = ByteBuffer.allocate(MAXLEN);
		buf.putInt(seqNo);
		buf.put(audio);
	}

	public AudioPacket(byte[] packet) {
		buf = ByteBuffer.wrap(packet);
		this.seqNo = buf.getInt();
		this.audio = new byte[MAXLEN - 4];
		buf.get(this.audio);
	}

	public byte[] getBytes() {
		return buf.array();
	}

	public int getSeqNo() {
		return seqNo;
	}

	public byte[] getAudioData() {
		return audio;
	}

	@Override
	public String toString() {
		return "SeqNo: " + seqNo + ", audio length: " + audio.length;
	}

	public static void main(String args[]) {

		String audio = null;
		for (int i = 0; i < MAXLEN - 8; i++) {
			audio += "a";
		}
		System.out.println("recorded. audio length: " + audio.length());

		int seqNo = 1;
		byte test[] = audio.getBytes();

		System.out.println("\ntest: AudioPacket(int seqNo, byte[] audio)");
		AudioPacket a = new AudioPacket(seqNo, test);
		System.out.println(a);

		System.out.println("\ntest: AudioPacket(byte[] packet)");
		AudioPacket b = new AudioPacket(a.getBytes());
		System.out.println(b);

		// assert Arrays.equals(a.getBytes(), b.getBytes());
		if (Arrays.equals(a.getBytes(), b.getBytes())) {
			System.out.println("\nAudioPackets are equal.");
		} else {
			System.out.println("\nAudioPackets are not equal.");
		}
	}
}
