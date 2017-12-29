package com.dlsc.preferencesfx2.view;

import com.dlsc.preferencesfx2.model.PreferencesModel;
import javafx.scene.layout.StackPane;

public class CategoryView extends StackPane implements View {
  private PreferencesModel preferencesModel;
  public CategoryView(PreferencesModel preferencesModel) {
    this.preferencesModel = preferencesModel;
    init();
    getChildren().add(preferencesModel.getCategories().get(0).getCategoryPane());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void initializeSelf() {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void initializeParts() {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void layoutParts() {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void bindFieldsToModel() {

  }
}
