package org.vaadin.noscrollgrid;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.fluttercode.datafactory.impl.DataFactory;
import org.vaadin.componentfactory.NoScrollTreeGrid;
import org.vaadin.noscrollgrid.data.Item;
import org.vaadin.noscrollgrid.data.TreeItem;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("treegrid")
public class TreeGridDemoView extends Div {

	public TreeGridDemoView() {
		setSizeFull();
		getStyle().set("overflow", "auto");
		getStyle().set("display", "flex");
		getStyle().set("flex-direction", "column");

		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidthFull();
		layout.getStyle().set("flex-grow", "1");

		NoScrollTreeGrid<TreeItem> grid = new NoScrollTreeGrid<>();
		grid.setShowMoreOnScrollToBottom(getElement());
		grid.setHeightFull();

		grid.addHierarchyColumn(Item::getName).setHeader("Name").setSortable(true).setFooter("Footer");
		grid.addColumn(Item::getAge).setHeader("Age").setSortable(true);
		grid.addColumn(Item::getDob).setHeader("Birth date").setWidth("200px").setSortable(true);

		grid.setItems(generateDummyItems(100, null), item -> generateDummyItems(3, item));

		Checkbox setWideColumnButton = new Checkbox("Make grid content wide");
		setWideColumnButton.addValueChangeListener(event -> {
			if (event.getValue()) {
				grid.getColumns().get(grid.getColumns().size() - 1).setWidth("2000px");
			} else {
				grid.getColumns().get(grid.getColumns().size() - 1).setWidth("200px");
			}
		});

		VerticalLayout leftSide = new VerticalLayout();
		leftSide.setWidth("400px");

		layout.add(leftSide, grid);
		layout.setFlexGrow(1f, grid);
		add(layout);
	}

	private List<TreeItem> generateDummyItems(int count, TreeItem parent) {
		if (parent != null && parent.getLevel() > 1) {
			return Collections.emptyList();
		}
		List<TreeItem> items = new ArrayList<>();
		final DataFactory dataFactory = new DataFactory();

		for (int index = 0; index < count; index++) {
			TreeItem item = new TreeItem();
			item.setId(UUID.randomUUID().toString());
			if (parent != null) {
				item.setLevel(parent.getLevel() + 1);
			}
			item.setName(dataFactory.getName());
			item.setDob(dataFactory.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
			item.setAge(LocalDate.now().getYear() - item.getDob().getYear());
			items.add(item);
		}
		return items;
	}
}
