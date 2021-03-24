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

import lombok.Data;

/**
 * @author carl
 */
@Data
public class EmploymentRequest {

  private final String name;
  private final String position;
  private final Double annualSalary;

  public EmploymentRequest(String name,
    String position,
    Double annualSalary) {
    this.name = name;
    this.position = position;
    this.annualSalary = annualSalary;
  }

  public String getName() {
    return name;
  }

  public String getPosition() {
    return position;
  }

  public Double getAnnualSalary() {
    return annualSalary;
  }

  @Override
  public String toString() {
    return "EmploymentRequest{" +
      "name='" + name + '\'' +
      ", position='" + position + '\'' +
      ", annualSalary=" + annualSalary +
      '}';
  }
}
