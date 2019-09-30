package org.vaadin.componentfactory;

/*
 * #%L
 * NoScrollGrid for Vaadin 13+
 * %%
 * Copyright (C) 2017 - 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * See the file license.html distributed with this software for more
 * information about licensing.
 * You should have received a copy of the CVALv3 along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import java.util.Objects;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.DataChangeEvent.DataRefreshEvent;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.internal.UsageStatistics;
import com.vaadin.flow.shared.Registration;
import com.vaadin.pro.licensechecker.LicenseChecker;

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

	private static String PROJECT_VERSION = "1.0.0";
    private static String PROJECT_NAME = "vaadin-noscroll-grid";
    
	private Registration dataProviderListener;
	
	/**
	 * @see com.vaadin.flow.component.grid.Grid#Grid()
	 */
	public NoScrollGrid() {
		super();
	}

	/**
	 * @see com.vaadin.flow.component.grid.Grid#Grid(int pageSize)
	 * @param pageSize
     *            the page size. Must be greater than zero.
	 */
	public NoScrollGrid(int pageSize) {
		super(pageSize);
		setRowsShownMoreOnScrollToBottom(getPageSize());
		verifyLicense(UI.getCurrent().getSession().getConfiguration().isProductionMode());
	}

	/**
	 * @see com.vaadin.flow.component.grid.Grid#Grid(Class beanType)
	 * @param beanType
     *            the bean type to use, not <code>null</code>
	 */
	public NoScrollGrid(Class<T> beanType) {
		super(beanType);
	}

	/**
	 * @see com.vaadin.flow.component.grid.Grid#Grid(Class beanType, boolean autoCreateColumns)
	 * @param beanType
     *            the bean type to use, not <code>null</code>
     * @param autoCreateColumns
     *            when <code>true</code>, columns are created automatically for
     *            the properties of the beanType
	 */
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
	
	private void verifyLicense(boolean productionMode) {
		if (!productionMode) {
			LicenseChecker.checkLicense(PROJECT_NAME, PROJECT_VERSION);
			UsageStatistics.markAsUsed(PROJECT_NAME, PROJECT_VERSION);
		}
	}
}
