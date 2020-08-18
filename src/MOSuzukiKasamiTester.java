package hr.pmf.dp.projekt;

public class MOSuzukiKasamiTester {

    public static void main(String[] args) throws Exception {
        Linker comm = null;
        try {
            String baseName = args[0];
            int myId = Integer.parseInt(args[1]);
            int numProc = Integer.parseInt(args[2]);
            comm = new Linker(baseName, myId, numProc);
            
            MOLock lock = new MOSuzukiKasami(comm,0);
            for (int i = 0; i < numProc; i++)
               if (i != myId)
                  (new ListenerThread(i, (MsgHandler)lock)).start();
            while (true) {
                System.out.println("***** " + myId + " does not have mobile object *****");
                Util.mySleep(2000);
                lock.acquire_object();
                Util.mySleep(2000);
                System.out.println("***** " + myId + " has mobile object *****");
                lock.release_object();
            }
	    }
        catch (Exception e) {
        	if ( comm != null ) comm.close();
            System.out.println(e);
            e.printStackTrace();
        }
    }
}

