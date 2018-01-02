package com.dlsc.preferencesfx.model;

import static com.dlsc.preferencesfx.util.Constants.DEFAULT_CATEGORY;

import com.dlsc.formsfx.model.util.TranslationService;
import com.dlsc.preferencesfx.history.History;
import com.dlsc.preferencesfx.util.PreferencesFxUtils;
import com.dlsc.preferencesfx.util.SearchHandler;
import com.dlsc.preferencesfx.util.StorageHandler;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PreferencesFxModel {
  private static final Logger LOGGER =
      LogManager.getLogger(PreferencesFxModel.class.getName());

  private ObjectProperty<Category> displayedCategory = new SimpleObjectProperty<>();

  private StringProperty searchText = new SimpleStringProperty();

  private List<Category> categories;
  private List<Category> flatCategoriesLst;
  private StorageHandler storageHandler;
  private SearchHandler searchHandler;
  private History history;
  private ObjectProperty<TranslationService> translationService = new SimpleObjectProperty<>();

  private boolean persistWindowState = false;
  private boolean historyDebugState = false;
  private BooleanProperty buttonsVisible = new SimpleBooleanProperty(true);

  public PreferencesFxModel(StorageHandler storageHandler, SearchHandler searchHandler, History history, Category[] categories) {
    this.storageHandler = storageHandler;
    this.searchHandler = searchHandler;
    this.history = history;
    this.categories = Arrays.asList(categories);
    flatCategoriesLst = PreferencesFxUtils.flattenCategories(this.categories);
    initializeCategoryTranslation();
    initializeDisplayedCategory();
    loadSettingValues();
  }

  /**
   * Sets up a binding of the TranslationService on the model, so that the Category's title gets
   * translated properly according to the TranslationService used.
   */
  private void initializeCategoryTranslation() {
    flatCategoriesLst.forEach(category -> {
      translationServiceProperty().addListener((observable, oldValue, newValue) -> {
        category.translate(newValue);
        // listen for i18n changes in the TranslationService for this Category
        newValue.addListener(() -> category.translate(newValue));
      });
    });
  }

  private void initializeDisplayedCategory() {
    Category displayCategory;
    if (persistWindowState) {
      displayCategory = loadSelectedCategory();
    } else {
      displayCategory = getCategories().get(DEFAULT_CATEGORY);
    }
    setDisplayedCategory(displayCategory);
  }

  private void loadSettingValues() {
    createBreadcrumbs(categories);
    PreferencesFxUtils.categoriesToSettings(flatCategoriesLst)
        .forEach(setting -> {
          LOGGER.trace("Loading: " + setting.getBreadcrumb());
          setting.loadSettingValue(storageHandler);
          history.attachChangeListener(setting);
        });
  }

  private void createBreadcrumbs(List<Category> categories) {
    categories.forEach(category -> {
      if (!Objects.equals(category.getGroups(), null)) {
        category.getGroups().forEach(group -> group.addToBreadcrumb(category.getBreadcrumb()));
      }
      if (!Objects.equals(category.getChildren(), null)) {
        category.createBreadcrumbs(category.getChildren());
      }
    });
  }


  public List<Category> getCategories() {
    return categories;
  }

  public boolean isPersistWindowState() {
    return persistWindowState;
  }

  public void setPersistWindowState(boolean persistWindowState) {
    this.persistWindowState = persistWindowState;
  }

  public History getHistory() {
    return history;
  }

  public StorageHandler getStorageHandler() {
    return storageHandler;
  }

  public boolean getHistoryDebugState() {
    return historyDebugState;
  }

  public void setHistoryDebugState(boolean historyDebugState) {
    this.historyDebugState = historyDebugState;
  }


  // ------ StorageHandler work -------------

  /**
   * Saves the current selected Category.
   */
  public void saveSelectedCategory() {
    storageHandler.saveSelectedCategory(displayedCategory.get().getBreadcrumb());
  }

  /**
   * Loads the last selected Category before exiting the Preferences window.
   *
   * @return last selected Category
   */
  public Category loadSelectedCategory() {
    String breadcrumb = storageHandler.loadSelectedCategory();
    Category defaultCategory = getCategories().get(DEFAULT_CATEGORY);
    if (breadcrumb == null) {
      return defaultCategory;
    }
    return flatCategoriesLst.stream()
        .filter(category -> category.getDescription().equals(breadcrumb))
        .findAny().orElse(defaultCategory);
  }

  public void saveDividerPosition(double dividerPosition) {
    storageHandler.saveDividerPosition(dividerPosition);
  }

  public double loadDividerPosition() {
    return storageHandler.loadDividerPosition();
  }

  public Category getDisplayedCategory() {
    return displayedCategory.get();
  }

  public void setDisplayedCategory(Category displayedCategory) {
    LOGGER.trace("Change displayed category to: " + displayedCategory);
    this.displayedCategory.set(displayedCategory);
  }

  public ReadOnlyObjectProperty<Category> displayedCategoryProperty() {
    return displayedCategory;
  }

  public String getSearchText() {
    return searchText.get();
  }

  public void setSearchText(String searchText) {
    this.searchText.set(searchText);
  }

  public StringProperty searchTextProperty() {
    return searchText;
  }

  public List<Category> getFlatCategoriesLst() {
    return flatCategoriesLst;
  }

  public SearchHandler getSearchHandler() {
    return searchHandler;
  }

  public boolean getButtonsVisible() {
    return buttonsVisible.get();
  }

  public BooleanProperty buttonsVisibleProperty() {
    return buttonsVisible;
  }

  public void setButtonsVisible(boolean buttonsVisible) {
    this.buttonsVisible.set(buttonsVisible);
  }

  public TranslationService getTranslationService() {
    return translationService.get();
  }

  public ObjectProperty<TranslationService> translationServiceProperty() {
    return translationService;
  }

  public void setTranslationService(TranslationService translationService) {
    this.translationService.set(translationService);
  }
}