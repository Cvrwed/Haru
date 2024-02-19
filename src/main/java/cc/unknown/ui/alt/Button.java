package cc.unknown.ui.alt;


public class Button {

    private String name;

    private boolean hovered;


    public Button(String name) {
        this.name = name;
        this.hovered = false;
    }

    public void updateState(boolean state) {
        if(hovered != state) {
            hovered = state;
        }
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isHovered() {
		return hovered;
	}

	public void setHovered(boolean hovered) {
		this.hovered = hovered;
	}


}