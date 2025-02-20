package com.tems.models;

import javafx.scene.layout.HBox;

/**
 * Interface for getDisplay 
 */
public interface Displayable {
    
    /**
     * Builds and returns a HBox display of teh displayable
     * @return displayType (0 for TR view, 1 for displayable view)
     * @return HBox representing the displayable with available actions
     */
    public HBox getDisplay(int displayType);
}
