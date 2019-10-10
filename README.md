# Component Factory NoScrollGrid for Vaadin 14+

Extension for Grid to delegate vertical scrolling outside the Grid's scroll container. Grid itself will never show vertical scroll bar. Scrolling down to bottom of the target scroll container (or optionally mouse wheel down/touchmove event) will increase grid's height automatically by predefined number of rows.  
Grid's data provider defines the maximum size Grid can grow. Higher the Grid is set initially or by scrolling, more rows are being fetched and cached in client.  

## Usage

Quick sample how to use NoScrollGrid. NoScrollGrid extends Grid, so it works like Grid for most parts.  

```java
NoScrollGrid<Item> grid = new NoScrollGrid<>();
grid.setShowMoreOnScrollToBottom(getElement());
grid.setHeightFull();

```

getElement() in this sample return a `com.vaadin.flow.dom.Element` which can be `<body>`. Or it can be any other element like `<div>` as long as it is vertically scrollable ("overflow: auto;" for example). Given element should be ancestor for NoScrollGrid element in DOM hierarchy.  

To change number of items to show more, you can call `setRowsShownMoreOnScrollToBottom(int)` explicitly. NoScrollGrid calls this method in constructor already and sets it to same as Grid's page size. 

```java
NoScrollGrid<Item> grid = new NoScrollGrid<>(20);
grid.setShowMoreOnScrollToBottom(getElement());
grid.setHeightFull();

```

Grid's page size affects the number of rows browser requests from the data provider and NoScrollGrid uses it to define size of the buffer for requested rows. Real number of rows requested and cached to client is usually number of rows that fit in NoScrollGrid's height plus buffer size. 

Override `NoScrollGrid.showMoreOnInit` to false (default is true) to hide scroll bar initially.

## Installation

To build both demo and addon, run following command in root folder:
```
mvn clean install
```

## Running demo

Starting the demo server at /noscroll-grid-demo:
```
cd noscroll-grid-demo
mvn jetty:run
```

This deploys demo at http://localhost:8080
  
See demo page for body-element as scroll container at http://localhost:8080/body

## Contributing

To contribute to the component, please read [the guideline](https://github.com/vaadin/vaadin-core/blob/master/CONTRIBUTING.md) first.

## Vaadin Prime

This component is available in the Vaadin Prime subscription. It is still open source, but you need to have a valid CVAL license in order to use it. Read more at: https://vaadin.com/pricing

## License

Commercial Vaadin Add-on License version 3 (CVALv3). For license terms, see LICENSE.

Vaadin collects development time usage statistics to improve this product. For details and to opt-out, see https://github.com/vaadin/vaadin-usage-statistics.