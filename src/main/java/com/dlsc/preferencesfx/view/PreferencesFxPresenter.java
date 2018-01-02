package com.dlsc.preferencesfx.view;

import com.dlsc.preferencesfx.model.PreferencesFxModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PreferencesFxPresenter implements Presenter {
  private static final Logger LOGGER =
      LogManager.getLogger(PreferencesFxPresenter.class.getName());

  private PreferencesFxModel model;
  private PreferencesFxView preferenceView;

  public PreferencesFxPresenter(PreferencesFxModel model, PreferencesFxView preferenceView) {
    this.model = model;
    this.preferenceView = preferenceView;
    init();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void initializeViewParts() {
    loadDividerPosition();
  }

  private void loadDividerPosition() {
    preferenceView.preferencesPane.setDividerPosition(
        model.getStorageHandler().loadDividerPosition()
    );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setupEventHandlers() {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setupValueChangedListeners() {
    // Whenever the divider position is changed, it's position is saved.
    preferenceView.preferencesPane.dividerPositionProperty().addListener(
        (observable, oldValue, newValue) ->
            model.saveDividerPosition(
                preferenceView.preferencesPane.getDividerPosition()
            )
    );

    // When the displayed category is changed, load the divider position again
    model.displayedCategoryProperty().addListener(
        (observable, oldCategory, newCategory) ->
            loadDividerPosition()
    );

    // When the displayedCategory in the model changes, set the view in the CategoryController
    preferenceView.categoryController.addListener(model.displayedCategoryProperty());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setupBindings() {

  }
}