/*
 * Copyright 2016 Bersenev Dmitry molasdin@outlook.com
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

package org.molasdin.wbase.scala.storage

import java.util.Optional

import org.molasdin.wbase.transaction.runner.Transactional
import org.molasdin.wbase.transaction.Transaction
import org.molasdin.wbase.transaction.manager.Engine

/**
  * Created by dbersenev on 16.02.2016.
  */
object Wrapper {

  implicit def javaOptionalToScala[U](o: Optional[U]): Option[U] = {
    if (o.isPresent) {
      Some(o.get())
    } else {
      None
    }
  }

  implicit def scalaOptionToJava[U](o: Option[U]): Optional[U] = {
    o match {
      case Some(item) => Optional.of(item)
      case _ => Optional.empty()
    }
  }

  implicit def scalaTxToJavaTx[U <: Engine, F](code: (Transaction[U] => F)): Transactional[U, F] = {
    new Transactional[U, F] {
      def run(tx: Transaction[U]): F = {
        code(tx)
      }
    }
  }
}
