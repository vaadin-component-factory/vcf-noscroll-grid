package org.vaadin.noscrollgrid;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.fluttercode.datafactory.impl.DataFactory;
import org.vaadin.componentfactory.NoScrollGrid;
import org.vaadin.noscrollgrid.data.Item;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

@Route("body")
public class BodyDemoView extends VerticalLayout {

	 public BodyDemoView() {
		 UI.getCurrent().getElement().getStyle().set("overflow-x", "hidden");
		 
		 setMinHeight("100%");
		 setWidthFull();
		 setPadding(false);
		 setSpacing(false);
		 
		 Div header = new Div();
		 header.setText("HEADER");
		 header.setHeight("50px");
		 header.setWidthFull();
		 header.getStyle().set("background-color", "#ccc");
		 
		 HorizontalLayout wrapper1 = new HorizontalLayout();
		 wrapper1.setPadding(false);
		 wrapper1.setWidthFull();
		 
		 VerticalLayout wrapper2 = new VerticalLayout();
		 wrapper2.getStyle().set("flex-grow", "1");
		 wrapper2.setPadding(false);
		 
		 HorizontalLayout wrapper3 = new HorizontalLayout();
		 wrapper3.setWidthFull();
		 wrapper3.getStyle().set("flex-grow", "1");
		 wrapper3.setSpacing(false);
		 wrapper3.setPadding(false);
		 
		 NoScrollGrid<Item> grid = new NoScrollGrid<>(50);
		 grid.setShowMoreOnScrollToBottom(UI.getCurrent().getElement());
		 grid.setMinHeight("100%");
		 
		 grid.addColumn(Item::getName).setHeader("Name").setSortable(true).setFooter("Footer");
		 grid.addColumn(Item::getAge).setHeader("Age").setSortable(true);
		 grid.addColumn(Item::getDob).setHeader("Birth date").setWidth("200px").setSortable(true);
		 
		 ListDataProvider<Item> dataprovider = new ListDataProvider<Item>(generateDummyItems(500));
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
		 leftSide.getStyle().set("background-color", "#eee");
		 
		 TextField nameFilterField = new TextField("Name filter", "Search by name");
		 nameFilterField.addValueChangeListener(event -> dataprovider
				.addFilter(item -> StringUtils.containsIgnoreCase(item.getName(), nameFilterField.getValue())));
		 nameFilterField.setValueChangeMode(ValueChangeMode.EAGER);
		 
		 leftSide.add(nameFilterField, addItemsButton, remItemsButton, setWideColumnButton);
		 
		 wrapper3.add(leftSide, grid);
		 wrapper3.setFlexGrow(1f, grid);
		 wrapper2.add(wrapper3);
		 wrapper1.add(wrapper2);
		 add(header, wrapper1);
		 setFlexGrow(1, wrapper1);
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
