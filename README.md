# Component Factory NoScrollGrid for Vaadin 19+

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

## License

This Add-on is distributed under Apache 2.0.

Component Factory NoScrollGrid is written by Vaadin Ltd.

## Sponsored development
Major pieces of development of this add-on has been sponsored by multiple customers of Vaadin. Read more  about Expert on Demand at: [Support](https://vaadin.com/support) and  [Pricing](https://vaadin.com/pricing)
