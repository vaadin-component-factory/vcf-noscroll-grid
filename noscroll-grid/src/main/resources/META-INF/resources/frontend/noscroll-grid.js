window.Vaadin.Flow.noscrollGridConnector = {
  initLazy: function(grid, pageSize, showMoreOnInit) {
    if(grid.$noscrollConnector) {
      return;
    }

    const GridElement = window.Vaadin.Flow.Legacy.GridElement;

    grid.$noscrollConnector = {};
    grid.$noscrollConnector.pageSize = pageSize;
    grid.$noscrollConnector.initialScrollDone = false;
    grid.$noscrollConnector.showMoreRows = 20;

    grid.$noscrollConnector.prevTouchScrollTop = 0;
    grid.$noscrollConnector.targetElement = null;
    grid.$noscrollConnector.targetScrollTopElement = null;

    // waiting time before showing more rows initially if grid is 'loading'
    grid.$noscrollConnector.waitForLoadingMs = 50;

    grid.$noscrollConnector.scrollbarWidth = grid._scrollbarWidth;
    grid.$noscrollConnector.initialHeight = grid.style.height;

    grid.$noscrollConnector.showMoreOnInit = showMoreOnInit;

    grid.$noscrollConnector.showMoreAfterReady = function() {
      if(grid.$noscrollConnector.initialScrollDone) {
        return;
      }
      grid.$noscrollConnector._debounceJob = Polymer.Debouncer.debounce(grid.$noscrollConnector._debounceJob, Polymer.Async.timeOut.after(grid.$noscrollConnector.waitForLoadingMs), () => {
        if(!grid.loading) {
          grid.showMore();
          grid.$noscrollConnector.initialScrollDone = true;
          grid.$noscrollConnector.resetOriginalGridWheelAndTouchListeners();
        } else {
          grid.$noscrollConnector.showMoreAfterReady();
        }
      });
    }

    const regularScrollHandler = e => {
      if(grid.$noscrollConnector.targetElement.offsetHeight + grid.$noscrollConnector.targetElement.scrollTop >= grid.$noscrollConnector.targetElement.scrollHeight) {
        grid.showMore();
      }
    };
    const bodyScrollHandler = e => {
      if(grid.$noscrollConnector.targetElement.offsetHeight + grid.$noscrollConnector.targetScrollTopElement.scrollY >= grid.$noscrollConnector.targetElement.scrollHeight) {
        grid.showMore();
      }
    };
    const regularTouchMoveHandler = e => {
      grid.$noscrollConnector.targetScrollTopElement.removeEventListener("touchmove", regularTouchMoveHandler, false);
      grid.$noscrollConnector.targetScrollTopElement.removeEventListener("pointermove", regularTouchMoveHandler, false);
      grid.$noscrollConnector.targetScrollTopElement.removeEventListener("wheel", wheelHandler, false);
      grid.$.table.removeEventListener("wheel", wheelHandler, false);
      grid.$.table.addEventListener('wheel', grid.$.table.__wheelListener);
      grid.$noscrollConnector.initialScrollDone = true;
      grid.$noscrollConnector.targetElement.style.touchAction = "auto";
      grid.style.touchAction = "auto";
      grid.$.scroller.style.touchAction = "auto";
      grid.showMore();
    };
    const bodyTouchMoveHandler = e => {
      grid.$noscrollConnector.targetScrollTopElement.removeEventListener("touchmove", bodyTouchMoveHandler, false);
      grid.$noscrollConnector.targetScrollTopElement.removeEventListener("pointermove", bodyTouchMoveHandler, false);
      grid.$noscrollConnector.targetScrollTopElement.removeEventListener("wheel", wheelHandler, false);
      grid.$.table.removeEventListener("wheel", wheelHandler, false);
      grid.$.table.addEventListener('wheel', grid.$.table.__wheelListener);
      grid.$noscrollConnector.initialScrollDone = true;
      grid.$noscrollConnector.targetElement.style.touchAction = "auto";
      grid.style.touchAction = "auto";
      grid.$.scroller.style.touchAction = "auto";
      grid.showMore();
    };
    const wheelHandler = e => {
      if(e.deltaY > 0) {
          grid.$noscrollConnector.targetScrollTopElement.removeEventListener("wheel", wheelHandler, false);
          grid.$noscrollConnector.targetScrollTopElement.removeEventListener("touchmove", regularTouchMoveHandler, false);
          grid.$noscrollConnector.targetScrollTopElement.removeEventListener("pointermove", regularTouchMoveHandler, false);
          grid.$noscrollConnector.targetScrollTopElement.removeEventListener("touchmove", bodyTouchMoveHandler, false);
          grid.$noscrollConnector.targetScrollTopElement.removeEventListener("pointermove", bodyTouchMoveHandler, false);
          grid.$.table.removeEventListener("wheel", wheelHandler, false);
          grid.$.table.addEventListener('wheel', grid.$.table.__wheelListener); // add original wheel handler back
          grid.$noscrollConnector.initialScrollDone = true;
          grid.showMore();
          e.stopImmediatePropagation();
      }
    };

    const onKeyDown = e => {
      const key = e.key;
      switch (key) {
        case 'PageDown':
        case 'PageUp':
          if(!grid.$noscrollConnector.initialScrollDone) {
            break;
          }
          // let's leave page-up/page-down to default handlers
          return;
      }
      grid.$noscrollConnector._keyDown = true;
      try {
        grid._onKeyDown(e);
      } finally {
        grid.$noscrollConnector._keyDown = false;
      }
    };

    grid.$noscrollConnector.clearAllWheelTouchListeners = function() {
      grid.$noscrollConnector.targetElement.removeEventListener("touchmove", regularTouchMoveHandler);
      grid.$noscrollConnector.targetElement.removeEventListener("touchmove", bodyTouchMoveHandler);
      grid.$noscrollConnector.targetElement.removeEventListener("pointermove", regularTouchMoveHandler);
      grid.$noscrollConnector.targetElement.removeEventListener("pointermove", bodyTouchMoveHandler);
      grid.$noscrollConnector.targetScrollTopElement.removeEventListener("wheel", wheelHandler);
      grid.$.table.removeEventListener("wheel", wheelHandler);
    }

    grid.$noscrollConnector.resetOriginalGridWheelAndTouchListeners = function() {
      grid.$noscrollConnector.clearAllWheelTouchListeners();
      grid.$.table.addEventListener('wheel', grid.$.table.__wheelListener);
    }

    grid.setRowsShownMoreOnScrollToBottom = function(rowCount) {
      grid.$noscrollConnector.showMoreRows = rowCount;
    }

    grid.setShowMoreOnScrollToBottom = function(target) {
      if(!target) {
        return;
      }
      if(!grid.style.minHeight || "" === grid.style.minHeight) {
        grid.style.minHeight = grid.$noscrollConnector.initialHeight;
      }
      grid.pageSize = grid.$noscrollConnector.pageSize;
      grid.$connector.setVerticalScrollingEnabled(false);

      grid.$noscrollConnector.initialScrollDone = false;
      grid.$noscrollConnector.targetElement = target;
      grid.$noscrollConnector.targetScrollTopElement = target;

      grid.$noscrollConnector.targetElement.removeEventListener("scroll", regularScrollHandler);
      grid.$noscrollConnector.targetScrollTopElement.removeEventListener("scroll", bodyScrollHandler);
      grid.$noscrollConnector.clearAllWheelTouchListeners();

      const msTouch = !('ontouchstart' in window) && !!(navigator.maxTouchPoints > 0);
      if(target === document.body) {
        grid.$noscrollConnector.targetScrollTopElement = window;
        if(msTouch) {
          grid.$noscrollConnector.targetElement.style.touchAction = "none";
          grid.style.touchAction = "none";
          grid.$.scroller.style.touchAction = "none";
          grid.$noscrollConnector.targetElement.addEventListener("pointermove", bodyTouchMoveHandler);
        } else {
          grid.$noscrollConnector.targetElement.addEventListener("touchmove", bodyTouchMoveHandler);
        }
        grid.$noscrollConnector.targetScrollTopElement.addEventListener("scroll", bodyScrollHandler);
      } else {
        if(msTouch) {
          grid.$noscrollConnector.targetElement.style.touchAction = "none";
          grid.$noscrollConnector.targetElement.addEventListener("pointermove", regularTouchMoveHandler);
        } else {
          grid.$noscrollConnector.targetElement.addEventListener("touchmove", regularTouchMoveHandler);
        }
        grid.$noscrollConnector.targetElement.addEventListener("scroll", regularScrollHandler);
      }
      grid.$noscrollConnector.targetScrollTopElement.addEventListener("wheel", wheelHandler);
      grid.$.table.removeEventListener('wheel', grid.$.table.__wheelListener); // blocks wheel if not removed
      grid.$.table.addEventListener("wheel", wheelHandler);

      grid.removeEventListener('keydown', grid._onKeyDown);
      grid.addEventListener('keydown', onKeyDown);

      if(grid.$noscrollConnector.showMoreOnInit) {
        grid.$noscrollConnector.showMoreAfterReady();
      }
    }

    /* 'showMore' adjusts grid height. Increases height by showMoreRows when there are more items to show.
    *  Or decreases height by removing all extra space below last row. */
    grid.showMore = function() {
      if(!grid.$noscrollConnector.targetElement) {
        return;
      }
      let newGridHeight = this.$.scroller.clientHeight + grid.$noscrollConnector.getShowMorePixelSize();
      this.style.height = newGridHeight + 'px';
      this.notifyResize();

      const table = this.$.table;
      let contentHeight = this.$.items.clientHeight + this.$.header.clientHeight + this.$.footer.clientHeight;
      if((table.scrollLeft < table.scrollWidth - table.clientWidth) || table.scrollLeft > 0) {
        // grid has horizontal scroll bar
        contentHeight += grid.$noscrollConnector.scrollbarWidth;
      }

      if(contentHeight < newGridHeight) {
        this.style.height = contentHeight + 'px';
        this.notifyResize();
      }
    }

    grid.resetHeight = function() {
      grid.style.height = grid.$noscrollConnector.initialHeight;
      grid.setShowMoreOnScrollToBottom(grid.$noscrollConnector.targetElement);
    }

    grid.setWaitForLoading = function(millisecondsToWait) {
      if(millisecondsToWait >= 0) {
        grid.$noscrollConnector.waitForLoadingMs = millisecondsToWait;
      }
    }

    grid.$noscrollConnector.getShowMorePixelSize = function() {
      return grid.$noscrollConnector.showMoreRows * Math.ceil(grid._physicalSize / grid._physicalCount);
    }

    grid.$connector.fetchPageOriginal = grid.$connector.fetchPage;
    /* overriding gridConnector.js implementation to adjust buffer logic */
    grid.$connector.fetchPage = function(fetch, page, parentKey) {
      if(!grid.$noscrollConnector.targetElement) {
        grid.$connector.fetchPageOriginal(fetch, page, parentKey);
        return;
      }
      /* lets make sure that buffer is always same as given page size. Originally it would be number of visible rows.  */
      let start = grid._virtualStart;
      let physicalCount = grid._physicalCount;
      // grid._virtualEnd is read-only
      grid._physicalCount = grid.pageSize;
      grid._virtualStart = 0;

      grid.$connector.fetchPageOriginal(fetch, page, parentKey);
      // and revert back to original values
      grid._virtualStart = start;
      grid._physicalCount = physicalCount;
    }

    /* overriding function _scrollHandler to make sure that keyboard navigation do not scroll */
    grid._scrollHandler = function() {
      if(!grid.$noscrollConnector.targetElement || !grid.$noscrollConnector._keyDown) {
        GridElement.prototype._scrollHandler.call(grid);
        return;
      }
      grid.$.table.scrollTop = 0; // this will block scrolling
      if(!grid.$noscrollConnector.initialScrollDone) {
        grid.$noscrollConnector.resetOriginalGridWheelAndTouchListeners();
        grid.$noscrollConnector.initialScrollDone = true;
      }
      grid.showMore();
      GridElement.prototype._scrollHandler.call(grid);
    }

  }
}
