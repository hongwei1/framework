/*
 * Copyright 2009-2011 WorldWide Conferencing, LLC
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
package proto

import net.liftweb.common._
import net.liftweb.util._
import scala.reflect.Manifest

/**
 * OBP fork: webkit-free copy of `net.liftweb.http.Factory` (see the same shim in lift-mapper).
 *
 * `ProtoRules` extends `Factory` and exposes `emailRegexPattern` as a `FactoryMaker`. The
 * original mixed in webkit's `SessionVar`/`RequestVar` for request/session-scoped overrides,
 * which lift-proto never installs — so this copy drops them and is built purely on lift-util.
 */
trait Factory extends SimpleInjector {
  abstract class FactoryMaker[T](_default: Vendor[T])
                                (implicit man: Manifest[T]) extends
  StackableMaker[T] with Vendor[T] {
    registerInjection(this)(man)

    def theDefault: PSettableValueHolder[Vendor[T]] = default

    object default extends PSettableValueHolder[Vendor[T]] {
      private var value = _default
      def get = value
      def is = get
      def set(v: Vendor[T]): Vendor[T] = { value = v; v }
    }

    implicit def vend: T = make openOr default.is.apply()

    override implicit def make: Box[T] = super.make or Full(default.is.apply())
  }
}
