package org.vaadin.noscrollgrid;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.fluttercode.datafactory.impl.DataFactory;
import org.vaadin.componentfactory.NoScrollGrid;
import org.vaadin.noscrollgrid.data.Item;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

@Route("")
public class DemoView extends Div {

	 public DemoView() {
		 setSizeFull();
		 getStyle().set("overflow", "auto");
		 getStyle().set("display", "flex");
		 getStyle().set("flex-direction", "column");
		 
		 HorizontalLayout layout = new HorizontalLayout();
		 layout.setWidthFull();
		 layout.getStyle().set("flex-grow", "1");
		 
		 NoScrollGrid<Item> grid = new NoScrollGrid<>(20);
		 grid.setShowMoreOnScrollToBottom(getElement());
		 grid.setHeightFull();
		 
		 grid.addColumn(Item::getName).setHeader("Name").setSortable(true).setFooter("Footer");
		 grid.addColumn(Item::getAge).setHeader("Age").setSortable(true);
		 grid.addColumn(Item::getDob).setHeader("Birth date").setWidth("200px").setSortable(true);
		 
		 ListDataProvider<Item> dataprovider = new ListDataProvider<Item>(generateDummyItems(10));
		 grid.setDataProvider(dataprovider);
		 
		 Button addItemsButton = new Button("Add more items to Grid DataProvider");
		 addItemsButton.addClickListener(event -> {
			 dataprovider.getItems().addAll(generateDummyItems(10));
			 dataprovider.refreshAll();
		 });
		 Button remItemsButton = new Button("Remove all items from Grid DataProvider");
		 remItemsButton.addClickListener(event -> {
			 dataprovider.getItems().clear();
			 dataprovider.refreshAll();
		 });
		 Checkbox setWideColumnButton = new Checkbox("Make grid content wide");
		 setWideColumnButton.addValueChangeListener(event -> {
			 if(event.getValue()) {
				 grid.getColumns().get(grid.getColumns().size()-1).setWidth("2000px");
			 } else {
				 grid.getColumns().get(grid.getColumns().size()-1).setWidth("200px");
			 }
		 });
		 
		 VerticalLayout leftSide = new VerticalLayout();
		 leftSide.setWidth("400px");
		 
		 TextField nameFilterField = new TextField("Name filter", "Search by name");
		 nameFilterField.addValueChangeListener(event -> dataprovider
				.addFilter(item -> StringUtils.containsIgnoreCase(item.getName(), nameFilterField.getValue())));
		 nameFilterField.setValueChangeMode(ValueChangeMode.EAGER);
		 
		 leftSide.add(nameFilterField, addItemsButton, remItemsButton, setWideColumnButton);
		 
		 layout.add(leftSide, grid);
		 layout.setFlexGrow(1f, grid);
		 add(layout);
	 }
	 
	 private List<Item> generateDummyItems(int count) {
		 List<Item> items = new ArrayList<>();
		 final DataFactory dataFactory = new DataFactory();
		 
		for (int index = 0; index < count; index++) {
			Item item = new Item();
			item.setName(dataFactory.getName());
			item.setDob(dataFactory.getBirthDate().toInstant()
				      .atZone(ZoneId.systemDefault())
				      .toLocalDate());
			item.setAge(LocalDate.now().getYear() - item.getDob().getYear());
			items.add(item);
		 }
		 return items;
	 }
}
