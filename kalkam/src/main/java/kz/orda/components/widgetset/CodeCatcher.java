package kz.orda.components.widgetset;

import java.util.LinkedList;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;
import kz.orda.components.widgetset.client.codecatcher.CodeCatchedEvent;
import kz.orda.components.widgetset.client.codecatcher.CodeCatcherServerRpc;
import kz.orda.components.widgetset.client.codecatcher.CodeCatcherState;

public class CodeCatcher extends AbstractExtension {
	private static final long serialVersionUID = 558846424963386852L;

	public interface CodeCatchedListener {
		void onCodeCatch(CodeCatchedEvent event);
	}
	
	private LinkedList<CodeCatchedListener> listeners = new LinkedList<CodeCatchedListener>();
	
	private CodeCatcherServerRpc rpc = new CodeCatcherServerRpc() {
		private static final long serialVersionUID = 114033653705512975L;

		@Override
		public void onCodeCatched(CodeCatchedEvent event) {
			for(CodeCatchedListener listener : listeners) {
				listener.onCodeCatch(event);
			}
		}
	}; 
	
	public CodeCatcher(AbstractClientConnector connector) {
		registerRpc(rpc);
		extend(connector);
	}

	@Override
	public CodeCatcherState getState() {
		return (CodeCatcherState) super.getState();
	}
	
	public void addListener(CodeCatchedListener listener) {
		this.listeners.add(listener);
	}
	
	public boolean removeListener(CodeCatchedListener listener) {
		return listeners.remove(listener);
	}

	public char getStartCharacter() {
		return (char)getState().codeStartKeyCode;
	}

	public void setStartCharacter(char character) {
		getState().codeStartKeyCode = (int)character;
		this.markAsDirty();
	}

	public char getEndCharacter() {
		return (char)getState().codeEndKeyCode;
	}

	public void setEndCharacter(char character) {
		int value = (int)character;
		getState().codeEndKeyCode = value;
		this.markAsDirty();
	}

	public int getMaxLenght() {
		return getState().codeMaxLenght;
	}

	public void setMaxLenght(int codeMaxLenght) {
		getState().codeMaxLenght = codeMaxLenght;
		this.markAsDirty();
	}
	
	public boolean isSkipFollowingReturn(){
		return getState().isSkipFollowingReturn;
	}
	
	/**
	 * If active the first "Return"-key after the end of the scan-state will be skipped.
	 * @param isSkip
	 */
	public void setSkipFollowingReturn(boolean isSkip){
		getState().isSkipFollowingReturn = isSkip;
		this.markAsDirty();
	}
	
	public boolean isAlwaysOn(){
		return getState().isAlwaysOn;
	}
	
	/**
	 * Activates a global interception of all keypresses. When a start 
	 * character is read the buffer is cleared and when a end character is 
	 * read the buffer is send to server.
	 * @param isAlwaysOn
	 */
	public void setAlwaysOn(boolean isAlwaysOn){
		getState().isAlwaysOn = isAlwaysOn;
		this.markAsDirty();
	}
	
}

