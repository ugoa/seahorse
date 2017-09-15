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

package io.deepsense.deeplang.doperables

import org.apache.spark.mllib.classification.{LogisticRegressionModel, LogisticRegressionWithLBFGS}

import io.deepsense.deeplang.doperables.dataframe.DataFrame
import io.deepsense.deeplang._
import io.deepsense.deeplang.inference.{InferenceWarnings, InferContext}
import io.deepsense.reportlib.model.ReportContent

case class UntrainedLogisticRegression(
    model: Option[LogisticRegressionWithLBFGS])
  extends LogisticRegression
  with Trainable {

  def this() = this(None)

  override def toInferrable: DOperable = new UntrainedLogisticRegression()

  override val train = new DMethod1To1[Trainable.Parameters, DataFrame, Scorable] {
    override def apply(
        context: ExecutionContext)(
        parameters: Trainable.Parameters)(
        dataFrame: DataFrame): Scorable = {

      val featureColumns = dataFrame.getColumnNames(parameters.featureColumns.get)
      val labelColumn = dataFrame.getColumnName(parameters.targetColumn.get)
      val labeledPoints = dataFrame.toSparkLabeledPointRDD(featureColumns, labelColumn)
      labeledPoints.cache()
      val trainedModel: LogisticRegressionModel = model.get.run(labeledPoints)
      val result = TrainedLogisticRegression(
        Some(trainedModel),
        Some(featureColumns),
        Some(labelColumn))
      saveScorable(context, result)
      result
    }

    override def infer(
        context: InferContext)(
        parameters: Trainable.Parameters)(
        dataframeKnowledge: DKnowledge[DataFrame]): (DKnowledge[Scorable], InferenceWarnings) = {
      (DKnowledge(new TrainedLogisticRegression()), InferenceWarnings.empty)
    }
  }

  override def report: Report = Report(ReportContent("Report for UntrainedLogisticRegression"))

  override def save(context: ExecutionContext)(path: String): Unit =
    throw new UnsupportedOperationException

}
