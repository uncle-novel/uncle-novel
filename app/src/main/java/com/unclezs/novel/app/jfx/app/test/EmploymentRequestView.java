/*
 * Copyright 2016 Bekwam, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.unclezs.novel.app.jfx.app.test;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;

/**
 * @author carl
 */
public class EmploymentRequestView extends VBox {

  private final EmploymentRequestViewModel viewModel =
    new EmploymentRequestViewModel();
  private GridPane gp = new GridPane();
  private TextField tfName = new TextField();
  private TextField tfPosition = new TextField();
  private TextField tfAnnualSalary = new TextField();
  private Button btnSave = new Button("Save");
  private Button btnCancel = new Button("Cancel");
  private Button btnReset = new Button("Reset");

  public EmploymentRequestView() {

    createView();
    bindViewModel();
  }

  private void createView() {
    VBox gpwrap = new VBox();
    gpwrap.setAlignment(Pos.CENTER);

    gp.setPadding(new Insets(40));
    gp.setVgap(4);
    gp.add(new Label("Name"), 0, 0);
    gp.add(tfName, 1, 0);
    gp.add(new Label("Desired Position"), 0, 1);
    gp.add(tfPosition, 1, 1);
    gp.add(new Label("Current Annual Salary"), 0, 2);
    gp.add(tfAnnualSalary, 1, 2);

    final ColumnConstraints col = new ColumnConstraints();
    col.setPercentWidth(50);

    gp.getColumnConstraints().addAll(col, col);

    gpwrap.getChildren().add(gp);

    VBox.setVgrow(gpwrap, Priority.ALWAYS);

    btnSave.setOnAction(this::save);
    btnCancel.setOnAction(this::cancel);
    btnReset.setOnAction(this::reset);

    btnSave.setDefaultButton(true);

    ButtonBar buttonBar = new ButtonBar();
    buttonBar.setPadding(new Insets(20.0d));
    ButtonBar.setButtonData(btnSave, ButtonBar.ButtonData.OK_DONE);
    ButtonBar.setButtonData(btnCancel, ButtonBar.ButtonData.CANCEL_CLOSE);
    ButtonBar.setButtonData(btnReset, ButtonBar.ButtonData.APPLY);

    buttonBar.getButtons().addAll(btnSave, btnCancel, btnReset);

    this.getChildren().addAll(
      gpwrap,
      new Separator(),
      buttonBar);
  }

  private void bindViewModel() {

    tfName.textProperty().bindBidirectional(viewModel.nameProperty());

    tfPosition.textProperty().bindBidirectional(viewModel.positionProperty());

    Bindings.bindBidirectional(
      tfAnnualSalary.textProperty(),
      viewModel.annualSalaryProperty(),
      new NumberStringConverter()
    );
  }

  private void save(ActionEvent evt) {
    viewModel.save();
  }

  private void cancel(ActionEvent evt) {
    Platform.exit();
  }

  private void reset(ActionEvent evt) {
    viewModel.reset();
  }
}
