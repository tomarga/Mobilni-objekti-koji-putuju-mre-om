package hr.pmf.dp.projekt;

public class MOSpanTreeTester {

	public static void main(String[] args) throws Exception {
		
        Linker comm = null;
        try {
            String baseName = args[0];
            int myId = Integer.parseInt(args[1]);
            int numProc = Integer.parseInt(args[2]);
            comm = new Linker(baseName, myId, numProc);
            MOSpanTree lock = null;

            lock = new MOSpanTree(comm,myId == 0);

            for (int i = 0; i < numProc; i++) 
               if (i != myId)
                  (new ListenerThread(i, (MsgHandler)lock)).start();
            
            lock.waitForDone();
        	Util.println(myId + ":" + ((SpanTree) lock).children.toString());
        	System.out.println(myId + "'s parent: " + ((SpanTree) lock).parent);
        	System.out.println();

            while (true) {
            	
            	System.out.println(myId + " is not in CS");
                Util.mySleep(2000);
                lock.requestCS();
                Util.mySleep(2000);
                System.out.println(myId + " is in CS *****");
                lock.releaseCS();
            	
            }
        }
        catch (Exception e) {
        	if ( comm != null ) comm.close();
            System.out.println(e);
            e.printStackTrace();
        }
    }
}
