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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author carl
 */
public class EmploymentRequestViewModel {

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty position = new SimpleStringProperty("");
    private final DoubleProperty annualSalary = new SimpleDoubleProperty();

    private final EmploymentRequestConverter converter =
            new EmploymentRequestConverter();

    private final EmploymentRequestModel model = new EmploymentRequestModel();

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getPosition() {
        return position.get();
    }

    public StringProperty positionProperty() {
        return position;
    }

    public void setPosition(String position) {
        this.position.set(position);
    }

    public double getAnnualSalary() {
        return annualSalary.get();
    }

    public DoubleProperty annualSalaryProperty() {
        return annualSalary;
    }

    public void setAnnualSalary(double annualSalary) {
        this.annualSalary.set(annualSalary);
    }

    public void save() {
        EmploymentRequest data = converter.toEmploymentRequest( this );
        model.save( data );
    }

    public void reset() {
        this.name.set("");
        this.position.set("");
        this.annualSalary.set(0.0d);
    }
}
