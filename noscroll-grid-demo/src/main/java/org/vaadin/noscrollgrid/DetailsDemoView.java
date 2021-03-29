package org.vaadin.noscrollgrid;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.fluttercode.datafactory.impl.DataFactory;
import org.vaadin.componentfactory.NoScrollGrid;
import org.vaadin.noscrollgrid.data.Item;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;

@Route("details")
public class DetailsDemoView extends VerticalLayout {

    private Div content;

    public DetailsDemoView() {
        setSizeFull();
        setSpacing(false);

        HorizontalLayout navLayout = new HorizontalLayout();
        add(navLayout);
        navLayout.setSpacing(true);
        Button r5Btn = new Button("5 rows");
        navLayout.add(r5Btn);
        r5Btn.addClickListener(event -> buildGrid(5));
        Button r100Btn = new Button("100 rows");
        navLayout.add(r100Btn);
        r100Btn.addClickListener(event -> buildGrid(100));
        Span info = new Span("Click row to expand details");
        navLayout.add(info);

        content = new Div();
        add(content);
        content.setWidthFull();
        content.setHeight("370px");
        content.getElement().getStyle()
                .set("overflow", "auto")
                .set("padding", "20px")
                .set("box-sizing", "border-box");

        buildGrid(5);
    }

    private void buildGrid(int rows) {
        content.removeAll();

        NoScrollGrid<Item> grid = new NoScrollGrid<>();
        content.add(grid);

        grid.setShowMoreOnScrollToBottom(content.getElement());
        grid.setHeightFull();
        grid.setWidthFull();

        grid.addColumn(Item::getName).setHeader("Name");
        grid.addColumn(Item::getAge).setHeader("Age");
        grid.addColumn(Item::getDob).setHeader("Birth date");

        grid.setItemDetailsRenderer(
                new ComponentRenderer<>(item -> {
                    Div layout = new Div();
                    layout.setWidthFull();
                    layout.setHeight("150px");
                    layout.getElement().getStyle()
                            .set("background-color", "#555555");
                    Span info = new Span("Details");
                    info.getElement().getStyle()
                            .set("font-size", "3rem")
                            .set("color", "white");
                    layout.add(info);
                    return layout;
                }));

        grid.setItems(generateDummyItems(rows));
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
