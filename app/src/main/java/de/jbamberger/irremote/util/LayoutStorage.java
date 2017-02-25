package de.jbamberger.irremote.util;

import java.util.Set;

/**
 * Created: 04.01.2016
 *
 * @author Jannik
 * @version 04.01.2016
 */
public class LayoutStorage {
    private Set<LayoutButton> buttons;
    private int gridHeight;
    private int gridWidth;

    public int getGridWidth() {
        return gridWidth;
    }

    public void setGridWidth(int gridWidth) {
        if(1 < gridWidth && gridWidth < 10) {
            this.gridWidth = gridWidth;
        } else {
            //TODO: error
        }
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public void setGridHeight(int gridHeight) {
        this.gridHeight = gridHeight;
    }

    public void addButton(LayoutButton button, int x, int y) {

    }

    public void addButton(LayoutButton button, int position) {

    }

    public void removeButton(int x, int y) {

    }

    public void removeButton(int position) {

    }

    public  void generateLayout() {

    }



    private void tzest() {

    }


}
