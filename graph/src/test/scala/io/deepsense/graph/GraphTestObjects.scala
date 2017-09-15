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

package io.deepsense.graph

import org.scalatest.mock.MockitoSugar

import io.deepsense.deeplang._
import io.deepsense.deeplang.doperables.DOperableMock
import io.deepsense.deeplang.parameters.ParametersSchema

object RandomNodeFactory {
  def randomNode(operation: DOperation): Node = Node(Node.Id.randomId, operation)
}

object DClassesForDOperations extends MockitoSugar {
  trait A extends DOperableMock
  case class A1() extends A
  case class A2() extends A
}

object DOperationTestClasses {
  import io.deepsense.graph.DClassesForDOperations._

  trait DOperationBaseFields extends DOperation {
    // NOTE: id will be different for each instance
    override val id: DOperation.Id = DOperation.Id.randomId

    override val name: String = ""

    override val parameters: ParametersSchema = ParametersSchema()
  }

  case class DOperationCreateA1() extends DOperation0To1[A1] with DOperationBaseFields {
    override protected def _execute(context: ExecutionContext)(): A1 = ???
  }

  case class DOperationReceiveA1() extends DOperation1To0[A1] with DOperationBaseFields {
    override protected def _execute(context: ExecutionContext)(t0: A1): Unit = ???
  }

  case class DOperationA1ToA() extends DOperation1To1[A1, A] with DOperationBaseFields {
    override protected def _execute(context: ExecutionContext)(t1: A1): A = ???
  }

  case class DOperationAToA1A2() extends DOperation1To2[A, A1, A2] with DOperationBaseFields {
    override protected def _execute(context: ExecutionContext)(in: A): (A1, A2) = ???
  }

  case class DOperationA1A2ToA() extends DOperation2To1[A1, A2, A] with DOperationBaseFields {
    override protected def _execute(context: ExecutionContext)(t1: A1, t2: A2): A = ???
  }

  case class DOperationAToALogging() extends DOperation1To1[A, A] with DOperationBaseFields {
    logger.trace("Initializing logging to test the serialization")
    override protected def _execute(context: ExecutionContext)(t0: A): A = ???

    def trace(message: String): Unit = logger.trace(message)
  }
}
