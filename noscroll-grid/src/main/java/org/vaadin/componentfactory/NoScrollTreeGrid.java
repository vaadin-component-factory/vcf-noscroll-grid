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
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.DataChangeEvent.DataRefreshEvent;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.internal.UsageStatistics;
import com.vaadin.flow.shared.Registration;
import com.vaadin.pro.licensechecker.LicenseChecker;

/**
 * {@link TreeGrid} with disabled vertical scrolling and option to delegate
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
public class NoScrollTreeGrid<T> extends TreeGrid<T> {

	private static String PROJECT_VERSION = "1.0.0";
    private static String PROJECT_NAME = "vcf-noscroll-grid";
    
	private Registration dataProviderListener;
	private Element targetScrollContainer;
	
	/**
	 * When showMoreOnInit is true, grid height will be adjusted initially to show
	 * more rows to force scroll bar to appear to scroll container. False means that
	 * user has to use mouse wheel down/touch scroll to trigger event that shows
	 * more rows.
	 */
	protected boolean showMoreOnInit = true;
	
	/**
	 * @see com.vaadin.flow.component.grid.TreeGrid#Grid()
	 */
	public NoScrollTreeGrid() {
		super();
		setRowsShownMoreOnScrollToBottom(getPageSize());
		verifyLicense(UI.getCurrent().getSession().getConfiguration().isProductionMode());
	}

	/**
	 * @see com.vaadin.flow.component.grid.TreeGrid#Grid(Class beanType)
	 * @param beanType
     *            the bean type to use, not <code>null</code>
	 */
	public NoScrollTreeGrid(Class<T> beanType) {
		super(beanType);
		setRowsShownMoreOnScrollToBottom(getPageSize());
		verifyLicense(UI.getCurrent().getSession().getConfiguration().isProductionMode());
	}

	/**
	 * @see com.vaadin.flow.component.grid.TreeGrid#Grid(HierarchicalDataProvider dataProvider)
	 * @param dataProvider
     *            the data provider, not {@code null}
	 */
	public NoScrollTreeGrid(HierarchicalDataProvider<T, ?> dataProvider) {
		super(dataProvider);
	}
	
	@Override
	protected void initConnector() {
		super.initConnector();
		getUI().orElseThrow(() -> new IllegalStateException(
				"Connector can only be initialized for an attached Grid"))
		.getPage()
		.executeJavaScript("window.Vaadin.Flow.noscrollGridConnector.initLazy($0,$1,$2)",
				getElement(), getPageSize(), showMoreOnInit);
	}
	
	/**
	 * @see com.vaadin.flow.component.grid.Grid#setDataProvider(com.vaadin.flow.data.provider.DataProvider dataProvider)
	 */
	@Override
	public void setDataProvider(DataProvider<T, ?> dataProvider) {
		if(dataProviderListener != null) {
			dataProviderListener.remove();
		}
		super.setDataProvider(dataProvider);
		dataProviderListener = dataProvider.addDataProviderListener(event -> {
            if (targetScrollContainer != null && !(event instanceof DataRefreshEvent)) {
            	getElement().callFunction("resetHeight");
            }
        });
	}
	
	/**
	 * Increases grid size by approximate height needed for number of rows given via
	 * {@link #setRowsShownMoreOnScrollToBottom(int)}. Or less, if data provider
	 * does not have more rows to show.
	 */
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
		this.targetScrollContainer = targetScrollContainer;
		getElement().callFunction("setShowMoreOnScrollToBottom", targetScrollContainer);
	}
	
	/**
	 * Set target scroll container. Does not add vertical scroll bar. Adds listener
	 * to given element for scroll event into bottom to show more rows in grid. If
	 * element does not have visible scroll bar, then it listens for mouse wheel
	 * event and touch event to show more.
	 * 
	 * @param targetScrollContainer Target scroll container element. For example
	 *                              body or div.
	 * @param waitForLoadingTimeout Set milliseconds to wait before showing more
	 *                              rows initially after grid is done loading. This
	 *                              has effect only when {@link #showMoreOnInit} is
	 *                              set to true (default). Default is 50
	 *                              milliseconds. Zero or positive integer.
	 */
	public void setShowMoreOnScrollToBottom(Element targetScrollContainer, int waitForLoadingTimeout) {
		getElement().callFunction("setWaitForLoading", waitForLoadingTimeout);
		setShowMoreOnScrollToBottom(targetScrollContainer);
	}
	
	private void verifyLicense(boolean productionMode) {
		if (!productionMode) {
			LicenseChecker.checkLicense(PROJECT_NAME, PROJECT_VERSION);
			UsageStatistics.markAsUsed(PROJECT_NAME, PROJECT_VERSION);
		}
	}
}
