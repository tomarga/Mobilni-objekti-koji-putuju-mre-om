package hr.pmf.dp.projekt;

public class MOSpanTree extends SpanTree implements MOLock{
	
	private boolean interested;
	private IntLinkedList queue;
	private boolean present;

	public MOSpanTree( Linker initComm, boolean isRoot ) {
		super( initComm, isRoot );
		
		interested = false;
		present = isRoot;
		queue = new IntLinkedList();
	}
	
	@Override
	public synchronized void acquire_object() {
		interested = true;
				
		if ( !present ) {
			queue.add( myId );
			if ( queue.size() == 1 ) {
				sendMsg( parent, "REQUEST" );
			}
			while( !present ) {
				myWait();
			}
		}
	}

	@Override
	public synchronized void release_object() {
		interested = false;
				
		if ( !queue.isEmpty() ) {
			int head = queue.removeHead();

			sendMsg( head, "OBJECT" );
			parent = head;
			present = false;
			
			if ( !queue.isEmpty() ) {
				sendMsg( parent, "REQUEST" );
			}
		}
	}
	
	@Override
	public synchronized void handleMsg( Msg m, int src, String tag ) {
		super.handleMsg( m, src, tag );
		
		
		if ( tag.equals( "REQUEST" ) ) {

			if ( present ) {
				if ( interested ) {
					queue.add( src );
				} else {
					sendMsg( src, "OBJECT" );
					parent = src;
					present = false;
				}
			} else {
				queue.add( src );
				if ( queue.size() == 1 ) {
					sendMsg( parent, "REQUEST" );
				}
			}
		}
		
		if ( tag.contentEquals( "OBJECT" ) ) {
			
			int head = queue.removeHead();
			if ( myId == head ) {
				parent = myId;
				present = true;
				notify();
			} else {
				sendMsg( head, "OBJECT" );
				parent = head;
				if ( !queue.isEmpty() ) {
					sendMsg( parent, "REQUEST" );
				}
			}
		}
	}

}
