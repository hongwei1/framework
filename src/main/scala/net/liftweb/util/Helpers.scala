/*
 * Copyright 2006-2011 WorldWide Conferencing, LLC
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

package net.liftweb
package util

import scala.xml._

/**
 * The Helpers object provides a lot of utility functions:<ul>
 * <li>Time and date
 * <li>URL
 * <li>Hash generation
 * <li>Class instantiation
 * <li>Control abstractions
 * <li>Basic types conversions
 * <li>XML bindings
 * </ul>
 */

object Helpers extends TimeHelpers with StringHelpers with ListHelpers
with SecurityHelpers
with IoHelpers with BasicTypesHelpers
with ClassHelpers with ControlHelpers

/**
 * The superclass for all Lift flow of control exceptions
 */
class LiftFlowOfControlException(msg: String) extends RuntimeException(msg) {
  override def fillInStackTrace = this
}
