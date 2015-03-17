/**
 * AudioPacket
 * Definition of application message format.
 * 
 * @author e11258 Marasinghe, M.M.D.B. @dhammika-marasinghe
 * @author e11269 Naranpanawa, D.N.U. @nathashanaranpanawa
 */

package audiocast.model;

public class AudioPacket {

	int seqNo;
	byte[] audio;

	public AudioPacket(int seqNo, byte[] audio) {

	}

	public AudioPacket(byte[] packet) {

	}

	public byte[] getBytes() {
		return null;
	}

	public int getSeqNo() {
		return seqNo;
	}

	public byte[] getAudioData() {
		return audio;
	}
}
