import java.util.Arrays;

public class MOSuzukiKasami extends Process implements MOLock {
    boolean interested;
    boolean object_present;
    int[] request_by;
    int[] obtained;

    public MOSuzukiKasami(Linker initComm, int coordinator) {
        super(initComm);

        interested = false;
        object_present = (myId == coordinator);
        request_by = new int[N];
        for (int i = 0; i < N; i++) request_by[i] = 0;

        obtained = new int[N];
        for (int j = 0; j < N; j++) {
            obtained[j] = 0;
        }
    }
    public synchronized void acquire_object() {
        interested = true;
        if( !object_present ){
            request_by[myId] = request_by[myId] + 1;
            broadcastMsg("REQUEST", myId);
            while ( !object_present ){
                myWait();
            }
        }
    }
    public synchronized void release_object() {
        interested = false;
        obtained[myId] = request_by[myId];

        int k = myId + 1;
        int option = 1;

        while(true){
            if( option == 1 && k == N ){
                k = 0;
                option = 2;
            }
            if( option == 2 && k == myId )
                return;

            if( request_by[k] > obtained[k] ) {
                object_present = false;
                sendMsg(k, "OBJECT", Arrays.toString(obtained));
                return;
            }
            k++;
        }

    }
    public synchronized void handleMsg(Msg m, int src, String tag) {
        if (tag.equals("OBJECT")) {
            object_present = true;

            // Extract numbers (sent obtained values from other process) from message
            String numbers = m.getMessage().replaceAll("\\D+"," ");
            numbers = numbers.trim().replaceAll("\\s{2,}", " ");
            String[] words = numbers.split(" ");

            int[] received_obtained = new int[N];
            int i = 0;

            // Update array obtained.
            for (String word : words) {
                if ( i >= N )
                    throw new Error("More integers representing values in obtained array in message than processes.");

                received_obtained[i] = Integer.parseInt(word);
                if (received_obtained[i] > obtained[i] )
                    obtained[i] = received_obtained[i];

                i++;
            }
            notifyAll();
        }
        else if (tag.equals("REQUEST")){
            int k = m.getSrcId();
            request_by[k] = request_by[k] + 1;
            if( object_present && !interested){
                object_present = false;
                sendMsg(k, "OBJECT", Arrays.toString(obtained));
            }
        }
    }
}


