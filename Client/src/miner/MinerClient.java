package miner;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import javax.smartcardio.CommandAPDU;

public class MinerClient {
    public static byte[] APPLET_AID = {0x55, 0x6e, 0x69, 0x74, 0x54, 0x65, 0x73, 0x74, 0x73};
    public static boolean PHYSICAL = true;


    public static void main(String[] args) throws Exception {
        MinerClient client = new MinerClient();
        client.run();
    }

    public void run() {
        try {
			CardConfig runCfg = CardConfig.getDefaultConfig();
            if (PHYSICAL){
				runCfg.testCardType = CardConfig.CARD_TYPE.PHYSICAL;
				runExamples(runCfg);
			}
			else{
				runCfg.testCardType = CardConfig.CARD_TYPE.JCARDSIMLOCAL;
				runCfg.appletToSimulate = MinerApplet.class;
				runExamples(runCfg);
			}

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

	
    boolean runExamples(CardConfig cardCfg) {
        try {

			CardManager cardMngr = new CardManager(false, APPLET_AID); //The boolean parameter controls debugging/timing info.

            // Connnect to card - simulator or real card is used based on cardCfg
            System.out.print("Connecting to card...");
            if (!cardMngr.Connect(cardCfg)) {
                System.out.println(" Failed.");
                return false;
            }

			CommandAPDU cmd;
			ResponseTuple resptuple;
			float hrate;
			long cputime;
			
			System.out.print("\nEstimating the communication overhead...");
			cmd = new CommandAPDU(0x00, 0xaa, 0x00, 0x00, 0x00);
			cardMngr.transmit(cmd); //The simulator does some sort of initialization
			resptuple = cardMngr.transmit(cmd);
			long overhead = resptuple.time;
			System.out.printf(" %d ms.\n", overhead);
			
			
			System.out.print("Estimating the hashrate (100)...  ");
			cmd = new CommandAPDU(0x00, 0xbb, 0x00, 0x64, 0x00); //0x64->100
			resptuple = cardMngr.transmit(cmd);
			int respcode = resptuple.response.getSW();
			if (respcode == 36864){ //0x9000
				cputime = resptuple.time - overhead;
				hrate = (float)100/((float)cputime/1000);
				System.out.printf(" %f H/s.\n", hrate);
			} else{
				String swStr = String.format("%02X", respcode);
				System.out.printf(" %s. Failed \n", swStr);
			}
			
			System.out.print("Estimating the hashrate (1000)... ");
			cmd = new CommandAPDU(0x00, 0xbb, 0x03, 0xE8, 0x00); //0x03E8->1,000
			resptuple = cardMngr.transmit(cmd);
			respcode = resptuple.response.getSW();
			if (respcode == 36864){ //0x9000
				cputime = resptuple.time - overhead;
				hrate = (float)1000/((float)cputime/1000);
				System.out.printf(" %f H/s.\n", hrate);
			} else{
				String swStr = String.format("%02X", respcode);
				System.out.printf(" %s. Failed \n", swStr);
			}

			/*
			System.out.print("Estimating the hashrate (10000)...");
			cmd = new CommandAPDU(0x00, 0xbb, 0x27, 0x10, 0x00); //0x2710->10,000
			resptuple = cardMngr.transmit(cmd);
			respcode = resptuple.response.getSW();
			if (respcode == 36864){ //0x9000
				cputime = resptuple.time - overhead;
				hrate = (float)10000/((float)cputime/1000);
				System.out.printf(" %f H/s.\n", hrate);
			} else{
				String swStr = String.format("%02X", respcode);
				System.out.printf(" %s. Failed \n", swStr);
			}
			

			System.out.print("Estimating the hashrate (50000)...");
			cmd = new CommandAPDU(0x00, 0xbb, 0xc3, 0x50, 0x00); //0xC350->50,000
			resptuple = cardMngr.transmit(cmd);
			respcode = resptuple.response.getSW();
			if (respcode == 36864){ //0x9000
				cputime = resptuple.time - overhead;
				hrate = (float)50000/((float)cputime/1000);
				System.out.printf(" %f H/s.\n", hrate);
			} else{
				String swStr = String.format("%02X", respcode);
				System.out.printf(" %s. Failed \n", swStr);
			}
			*/
			
			System.out.print("Mining one zero byte... ");
			cmd = new CommandAPDU(0x00, 0x01, 0x00, 0x00, 0x00);
			resptuple = cardMngr.transmit(cmd);
			respcode = resptuple.response.getSW();
			if (respcode == 36864){ //0x9000
				System.out.printf(" %s \n", resptuple.ResponseToHex());
			} else{
				String swStr = String.format("%02X", respcode);
				System.out.printf(" %s. Failed \n", swStr);
			}
			
			System.out.print("Mining two zero bytes... ");
			cmd = new CommandAPDU(0x00, 0x02, 0x00, 0x00, 0x00);
			resptuple = cardMngr.transmit(cmd);
			respcode = resptuple.response.getSW();
			if (respcode == 36864){ //0x9000
				System.out.printf(" %s \n", resptuple.ResponseToHex());
			} else{
				String swStr = String.format("%02X", respcode);
				System.out.printf(" %s. Failed \n", swStr);
			}
			
			
            System.out.print("Disconnecting from card...");
            cardMngr.Disconnect(true);
            System.out.println(" Done.");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
        
    /**
     * Concatenates two separate arrays into single bigger one
     * @param a first array
     * @param b second array
     * @return concatenated array
     */
    public static byte[] concat(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] c = new byte[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
}







