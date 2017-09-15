/**
 * Copyright 2015, CodiLime Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.deepsense.deeplang.inference

import io.deepsense.deeplang.doperables.dataframe.{ColumnMetadata, DataFrameMetadata}
import io.deepsense.deeplang.parameters.ColumnType.ColumnType
import io.deepsense.deeplang.parameters.{SingleColumnSelection, ColumnSelection}

/**
 * Represents possibility that some exception will be thrown upon execution.
 */
abstract class InferenceWarning(val message: String)


case class MultipleColumnsMayNotExistWarning(
    selection: ColumnSelection,
    metadata: DataFrameMetadata)
  extends InferenceWarning(
    s"Column from specified selection: $selection may not exist in $metadata")

case class SingleColumnMayNotExistWarning(
    selection: SingleColumnSelection,
    metadata: DataFrameMetadata)
  extends InferenceWarning(
    s"Column from specified selection: $selection may not exist in $metadata")

case class ConversionMayNotBePossibleWarning(
    columnMetadata: ColumnMetadata,
    expectedType: ColumnType)
  extends InferenceWarning(
    s"Column ${columnMetadata.name} with type ${columnMetadata.columnType}" +
      s" may not be convertible to $expectedType")

