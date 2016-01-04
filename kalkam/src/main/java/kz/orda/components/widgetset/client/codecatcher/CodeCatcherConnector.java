package kz.orda.components.widgetset.client.codecatcher;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;

import elemental.events.KeyboardEvent.KeyCode;
import kz.orda.components.widgetset.CodeCatcher;

@Connect(CodeCatcher.class)
public class CodeCatcherConnector extends AbstractExtensionConnector implements NativePreviewHandler{
    private static final long serialVersionUID = 4550594626391658435L;

    private final CodeCatcherServerRpc rpc = RpcProxy.create(CodeCatcherServerRpc.class, this);

    private int codeStartKeyCode;
    private int codeEndKeyCode;
    private int codeMaxLenght = 0;
    private boolean isSkipFollowingReturn = false;
    private boolean isAlwaysOn = false;

    private boolean isReadingCode = false;
    private int codeLength = 0;
    private String code = "";
    private boolean isReadingJustEnded = false;

    private transient HandlerRegistration handler;

    public CodeCatcherConnector() {
    }

    @Override
    protected void extend(ServerConnector target) {
        handler = Event.addNativePreviewHandler(this);
    }

    @Override
    public void onUnregister() {
        handler.removeHandler();
        super.onUnregister();
    }

    //nativeEvent.getCtrlKey() && nativeEvent.getShiftKey() &&
    @Override
    public void onPreviewNativeEvent(NativePreviewEvent event) {
        if (event.getNativeEvent().getType().contains("key")) {

            int charCode = -1;
            if (event.getNativeEvent().getType().equals("keypress")) {
                charCode = event.getNativeEvent().getCharCode();
                 //check if a "function" key was pressed.
                if (charCode == 0) {
                    int keyCode = event.getNativeEvent().getKeyCode();
                    // remap enter and maybe other keys in the future.
                    if (keyCode == KeyCode.ENTER) {
                        charCode = keyCode;
                    } else {
                        return;
                    }

                }

                if (this.isReadingJustEnded && this.isSkipFollowingReturn) {
                    this.isReadingJustEnded = false;
                     //check if "Return"-key was pressed.
                    if (charCode == KeyCode.ENTER) {
                        event.cancel();
                        return;
                    }
                }

                if (this.isAlwaysOn) {
                    processAlwaysOn(event, charCode);
                } else {
                    processNormalMode(event, charCode);
                }
            }
        }
    }

    /**
     * Processing when intercepting all keys.
     *
     * @param event
     * @param charCode
     */
    private void processAlwaysOn(NativePreviewEvent event, int charCode) {
        if (charCode == codeStartKeyCode) {
            // reset buffer
            this.code = "";
        } else if (charCode == codeEndKeyCode) {
            this.sendBufferToServer();
            this.isReadingJustEnded = true;
        } else if (charCode == KeyCode.ENTER) {
            // send with codeEndKeyCode or Return
            this.sendBufferToServer();
        } else {
            // default: append new char
            this.code += (char) charCode;
        }

        event.cancel();
    }


    /**
     * Normal processing with start and end key
     *
     * @param event
     * @param charCode
     */
    private void processNormalMode(NativePreviewEvent event, int charCode) {
        if (!this.isReadingCode && charCode == codeStartKeyCode) {
            this.isReadingCode = true;
            event.cancel();
            this.code = "";
            this.codeLength = 0;
        } else if (this.isReadingCode) {
            if (charCode == codeEndKeyCode || charCode == KeyCode.ENTER) {
                this.isReadingCode = false;
                this.isReadingJustEnded = true;
                event.cancel();
                this.sendBufferToServer();
                this.codeLength = 0;
            } else {
                if (this.codeMaxLenght == 0
                        || this.codeLength < this.codeMaxLenght) {
                    event.cancel();
                    this.code += (char) charCode;
                } else {
                    // errore, scarto il codice
                    this.isReadingCode = false;
                }
            }
        }
    }


    private void sendBufferToServer() {
        CodeCatchedEvent serverEvent = new CodeCatchedEvent();
        serverEvent.setCode(this.code);
        rpc.onCodeCatched(serverEvent);
        this.code = "";
    }

    @Override
    public CodeCatcherState getState() {
        return (CodeCatcherState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        CodeCatcherState state = this.getState();
        this.codeStartKeyCode = state.codeStartKeyCode;
        this.codeEndKeyCode = state.codeEndKeyCode;
        this.codeMaxLenght = state.codeMaxLenght;
        this.isSkipFollowingReturn = state.isSkipFollowingReturn;
        this.isAlwaysOn = state.isAlwaysOn;
    }
}

