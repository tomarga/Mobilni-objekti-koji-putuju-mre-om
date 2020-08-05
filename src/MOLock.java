public interface MOLock extends MsgHandler {
    public void acquire_object (); //may block
    public void release_object ();
}
