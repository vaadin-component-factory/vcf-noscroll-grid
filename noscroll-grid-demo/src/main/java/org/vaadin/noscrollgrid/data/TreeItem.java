package org.vaadin.noscrollgrid.data;

public class TreeItem extends Item {

	private String id;
	private int level;

	public int getLevel() {
		return level;
	}

	public String getId() {
		return id;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setId(String id) {
		this.id = id;
	}

}
