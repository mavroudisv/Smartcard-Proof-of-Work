 package miner;
 
 import javax.smartcardio.ResponseAPDU;

 
 public class ResponseTuple{
	public ResponseAPDU response;
	public long time = 0;
  
	public ResponseTuple(ResponseAPDU response, long time) {
		this.response = response;
		this.time = time;
	}
	
	public String ResponseToHex() {
        return toHex(this.response.getData(), 0, this.response.getData().length);
    }
	
	public String toHex(byte[] bytes, int offset, int len) {
        String result = "";
        for (int i = offset; i < offset + len; i++) {
            result += String.format("%02X", bytes[i]);
        }

        return result;
    }
	
}