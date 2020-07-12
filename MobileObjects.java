import java.util.ArrayList;
import java.util.List;

public class MobileObjects extends Process implements Lock {
    boolean interested;
    boolean object_present;
    int[] request_by;
    static Integer[] obtained;

    public MobileObjects(Linker initComm, int coordinator) {
        super(initComm);
        // System.out.print("MobileObject init!\n");
        interested = false;
        object_present = (myId == coordinator);
        request_by = new int[N];
        for (int i = 0; i < N; i++) request_by[i] = 0;

        obtained = new Integer[N];
        for (int j = 0; j < N; j++) {
            obtained[j] = 0;
        }
    }
    // acquire_object  ~ requestCS
    public synchronized void requestCS() {
        // System.out.print("MobileObject acquire_object!\n");
        interested = true;
        if( !object_present ){
            request_by[myId] = request_by[myId] + 1;
            broadcastMsg("REQUEST", myId);
            while ( !object_present ){
                myWait();
            }
        }
    }
    // release_object() ~ releaseCS()
    public synchronized void releaseCS() {
        // System.out.print("MobileObject release_object!\n");
        interested = false;
        obtained[myId] = request_by[myId];

        for(int k = myId + 1; k < N; k++){
            if( request_by[k] > obtained[k] ){
                object_present = false;
                sendMsg(k, "OBJECT");
                return;
            }
        }
        for(int k = 0; k < myId; k++){
            // System.out.print("myId = " + myId + "; k = " + k + "request_by[k] =" + request_by[k] + "; obtained[k] = " + obtained[k] + "\n");
            if( request_by[k] > obtained[k]){
                object_present = false;
                sendMsg(k, "OBJECT");
                return;
            }
        }
    }
    // handle messages OBJECT and REQUEST
    public synchronized void handleMsg(Msg m, int src, String tag) {
        // System.out.print("MobileObject handleMsg!\n");
        if (tag.equals("OBJECT")) {
            object_present = true;
            notifyAll();
        }
        else if (tag.equals("REQUEST")){
            int k = m.getSrcId();
            request_by[k] = request_by[k] + 1;
            if( object_present && !interested){
                object_present = false;
                sendMsg(k, "OBJECT");
            }
        }
    }
}

