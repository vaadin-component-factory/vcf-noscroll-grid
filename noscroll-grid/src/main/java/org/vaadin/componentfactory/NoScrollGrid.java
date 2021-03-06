/*
 * Copyright 2000-2020 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.vaadin.componentfactory;

import java.util.Objects;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
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
@JsModule("./noscroll-grid.js")
@CssImport("./noscroll-grid-spinner.css")
public class NoScrollGrid<T> extends Grid<T> {

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
		addClassName("show-spinner");
		setRowsShownMoreOnScrollToBottom(getPageSize());
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
		.executeJs("window.Vaadin.Flow.noscrollGridConnector.initLazy($0,$1,$2)",
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
            	getElement().callJsFunction("resetHeight");
            }
        });
	}
	
	/**
	 * Increases grid size by approximate height needed for number of rows given via
	 * {@link #setRowsShownMoreOnScrollToBottom(int)}. Or less, if data provider
	 * does not have more rows to show.
	 */
	public void showMore() {
		getElement().callJsFunction("showMore");
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
		getElement().callJsFunction("setRowsShownMoreOnScrollToBottom", rows);
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
		getElement().callJsFunction("setShowMoreOnScrollToBottom", targetScrollContainer);
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
		getElement().callJsFunction("setWaitForLoading", waitForLoadingTimeout);
		setShowMoreOnScrollToBottom(targetScrollContainer);
	}
}
