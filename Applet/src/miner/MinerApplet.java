package miner;

//import javacard.framework.APDU;
//import javacard.framework.Applet;
//import javacard.framework.JCSystem;
//import javacard.framework.Util;
import javacard.framework.*;
import javacard.security.RandomData;
import javacard.security.MessageDigest;



public class MinerApplet extends Applet {
    
	final static byte OVERHEAD	= (byte) 0xaa;
    final static byte HASHRATE	= (byte) 0xbb;	
	final static byte ONE 		= (byte) 0x01;
    final static byte TWO	 	= (byte) 0x02;
    final static byte THREE	 	= (byte) 0x03;
    final static byte FOUR 		= (byte) 0x04;
	
	private static RandomData random;
    private static byte[] rnddata;// = {(byte) 0x68, (byte) 0x65, (byte) 0x6c, (byte) 0x6c, (byte) 0x6f, (byte) 0x77, (byte) 0x6f, (byte) 0x72, (byte) 0x6c, (byte) 0x64};
    private static MessageDigest sha256;

	
	public static byte[] shortToByteArray(short s) {
		return new byte[] { (byte) ((s & (short)0xFF00) >> 8), (byte) (s & (short)0x00FF) };
	}
	
    public MinerApplet() {
        // Pre-allocate all helper structures
        // Pre-allocate standard SecP256r1 curve and two EC points on this curve
        
    }
    
    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new MinerApplet().register();
    }

    public boolean select() {
                return true;
    }
    
    public void process(APDU apdu) {
        if (selectingApplet()) { return; }

        byte[] buffer = apdu.getBuffer();       
		byte p1 = buffer[ISO7816.OFFSET_P1];
		byte p2 = buffer[ISO7816.OFFSET_P2];
		
		//Header of previous block
		random = RandomData.getInstance(RandomData.ALG_SECURE_RANDOM);
		rnddata = JCSystem.makeTransientByteArray((short)32, JCSystem.CLEAR_ON_DESELECT);       
        random.generateData(rnddata, (short)0, (short)32);
		
		sha256 = MessageDigest.getInstance(MessageDigest.ALG_SHA_256, false);		
		short counter1 = 0;
		short counter2 = 0;
		
		switch (buffer[ISO7816.OFFSET_INS]) {
		case OVERHEAD:
			return;

        case HASHRATE:
			short iterations = Util.makeShort(p1, p2);
	        for (short i = 0; i < iterations; i++){
				//sha256.reset();
				sha256.doFinal(rnddata, (short)0, (short)rnddata.length, buffer, (short) 0);
			}
			return;

        case ONE:
			while(true){
				counter1 = (short)(counter1+1);
	            Util.arrayCopyNonAtomic(shortToByteArray(counter1), (short) 0, rnddata, (short) 30, (short) 2);
				//sha256.reset();
				sha256.doFinal(rnddata, (short)0, (short)rnddata.length, buffer, (short) 0);
				if (buffer[(short)0]==(byte)0x00){
					break;
				}
			}
			apdu.setOutgoingAndSend((short)0, (short)32);
			return;
        
		case TWO:
			counter2 = (short) 0;
			Util.arrayCopyNonAtomic(shortToByteArray(counter2), (short) 0, rnddata, (short) 28, (short) 2);
			while(true){
				if (counter1 == (short) 32767){
					counter1 = (short) 0;
					counter2 = (short)(counter2+1);
					Util.arrayCopyNonAtomic(shortToByteArray(counter1), (short) 0, rnddata, (short) 30, (short) 2);
				}else{
					counter1 = (short)(counter1+1);
					Util.arrayCopyNonAtomic(shortToByteArray(counter1), (short) 0, rnddata, (short) 30, (short) 2);
				}
					            
				//sha256.reset();
				sha256.doFinal(rnddata, (short)0, (short)rnddata.length, buffer, (short) 0);
				if (buffer[(short)0]==(byte)0x00 && buffer[(short)1]==(byte)0x00){
					break;
				}
			}
			apdu.setOutgoingAndSend((short)0, (short)32);
			return;
			
        default:
            ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED );
        }

    }
}
