package org.vaadin.noscrollgrid;

import java.util.Objects;

import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.DataChangeEvent.DataRefreshEvent;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.shared.Registration;

/**
 * {@link Grid} with disabled vertical scrolling and option to delegate
 * scrolling to any element via {@link #setShowMoreOnScrollToBottom(Element)}.
 * For example body or div element. Element may need "overflow: auto;" style
 * being set explicitly.
 * <p>
 * Adjust number of rows to show when scrolling to bottom of scroll container
 * element with {@link #setRowsShownMore(int)}. Or alternatively adjust pixel
 * size to show with {@link #setHeightShownMore(int)}.
 * </p>
 * 
 * @author Vaadin Ltd
 * 
 * @param <T> the grid bean type
 */
//@JsModule("./noscroll-grid.js")
@JavaScript("frontend://noscroll-grid.js")
public class NoScrollGrid<T> extends Grid<T> {

	private Registration dataProviderListener;
	
	public NoScrollGrid() {
		super();
	}

	public NoScrollGrid(int pageSize) {
		super(pageSize);
	}

	public NoScrollGrid(Class<T> beanType) {
		super(beanType);
	}

	public NoScrollGrid(Class<T> beanType, boolean autoCreateColumns) {
		super(beanType, autoCreateColumns);
	}
	
	@Override
	protected void initConnector() {
		super.initConnector();
		getUI().orElseThrow(() -> new IllegalStateException(
				"Connector can only be initialized for an attached Grid"))
		.getPage()
		.executeJavaScript("window.Vaadin.Flow.noscrollGridConnector.initLazy($0,$1)",
				getElement(), getPageSize());
	}
	
	@Override
	public void setDataProvider(DataProvider<T, ?> dataProvider) {
		if(dataProviderListener != null) {
			dataProviderListener.remove();
		}
		super.setDataProvider(dataProvider);
		dataProviderListener = dataProvider.addDataProviderListener(event -> {
            if (!(event instanceof DataRefreshEvent)) {
            	getElement().callFunction("resetHeight");
            }
        });
	}
	
	public void showMore() {
		getElement().callFunction("showMore");
	}
	
	public void setHeightShownMore(int heightPx) {
		getElement().callFunction("setShowMoreSize", heightPx);
	}
	
	public void setRowsShownMore(int rows) {
		if(rows<1) {
			throw new IllegalArgumentException(getClass().getSimpleName()
					+ ".setRowsShownMore(rows) requires integer larger than zero for 'rows'");
		}
		getElement().callFunction("setShowMoreRows", rows);
	}
	
	/**
	 * Set target scroll container with vertical scroll bar. Does not add vertical
	 * scroll bar. Adds listener to given element for scroll event into bottom to
	 * show more rows in grid. If element does not have visible scroll bar, then it
	 * listens for mouse wheel event and touch event to show more.
	 * 
	 * @param targetScrollContainer Target scroll container element. For example
	 *                              body or div.
	 */
	public void setShowMoreOnScrollToBottom(Element targetScrollContainer) {
		Objects.requireNonNull(targetScrollContainer, getClass().getSimpleName()
				+ ".setShowMoreOnScrollToBottom(targetScrollContainer) requires non-null target element. One target scroll container per grid instance.");
		getElement().callFunction("setShowMoreOnScrollToBottom", targetScrollContainer);
	}
}
