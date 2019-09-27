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
 * element with {@link #setRowsShownMoreOnScrollToBottom(int)}. Real space that is show more in
 * grid is based on calculating average row height from the currently visible
 * rows. This means that if row heights varies a lot, real number of rows being
 * shown is not that accurate.
 * </p>
 * <p>
 * If scroll container does not have visible vertical scroll bar initially, then
 * mouse wheel down or touch event will trigger {@link #showMore()} to increase
 * grid size if there are any more rows.
 * </p>
 * <p>
 * Initial height set with
 * {@link #setHeight(String)}/{@link #setSizeFull()}/{@link #setHeightFull()} is
 * always reverted back when data provider changes its items by
 * filtering/adding/removing.
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
	
	/**
	 * @see {@link Grid#Grid()}
	 */
	public NoScrollGrid() {
		super();
		setRowsShownMoreOnScrollToBottom(getPageSize());
	}

	/**
	 * @see {@link Grid#Grid(int)}
	 */
	public NoScrollGrid(int pageSize) {
		super(pageSize);
		setRowsShownMoreOnScrollToBottom(getPageSize());
	}

	/**
	 * @see {@link Grid#Grid(beanType)}
	 */
	public NoScrollGrid(Class<T> beanType) {
		super(beanType);
		setRowsShownMoreOnScrollToBottom(getPageSize());
	}

	/**
	 * @see {@link Grid#Grid(beanType, autoCreateColumns)}
	 */
	public NoScrollGrid(Class<T> beanType, boolean autoCreateColumns) {
		super(beanType, autoCreateColumns);
		setRowsShownMoreOnScrollToBottom(getPageSize());
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
	
	/**
	 * Set how many rows is shown more in grid on scroll to bottom.
	 * 
	 * @param rows number of rows to show more
	 */
	public void setRowsShownMoreOnScrollToBottom(int rows) {
		if(rows<1) {
			throw new IllegalArgumentException(getClass().getSimpleName()
					+ ".setRowsShownMoreOnScrollToBottom(rows) requires integer larger than zero for 'rows'");
		}
		getElement().callFunction("setRowsShownMoreOnScrollToBottom", rows);
	}
	
	/**
	 * Set target scroll container. Does not add vertical scroll bar. Adds listener
	 * to given element for scroll event into bottom to show more rows in grid. If
	 * element does not have visible scroll bar, then it listens for mouse wheel
	 * event and touch event to show more.
	 * 
	 * @param targetScrollContainer Target scroll container element. For example
	 *                              body or div.
	 */
	public void setShowMoreOnScrollToBottom(Element targetScrollContainer) {
		Objects.requireNonNull(targetScrollContainer, getClass().getSimpleName()
				+ ".setShowMoreOnScrollToBottom(targetScrollContainer) requires non-null target element. One target scroll container per grid instance.");
		getElement().callFunction("setShowMoreOnScrollToBottom", targetScrollContainer);
	}
	
	@Override
	public void setHeightByRows(boolean heightByRows) {
		throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support setHeightByRows(int).");
	}
}
