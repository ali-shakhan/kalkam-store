package kz.orda.components.widgetset.client.codecatcher;

import com.vaadin.shared.annotations.DelegateToWidget;

public class CodeCatcherState extends com.vaadin.shared.AbstractComponentState {
	private static final long serialVersionUID = 2282111190660042420L;
	
	@DelegateToWidget
	public int codeStartKeyCode;
	@DelegateToWidget
	public int codeEndKeyCode;
	@DelegateToWidget
	public int codeMaxLenght = 0;
	@DelegateToWidget
	public boolean isSkipFollowingReturn = false;
	@DelegateToWidget
	public boolean isAlwaysOn = false;

}