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
package proto

/**
 * OBP fork: loadable, webkit-free substitutes for `net.liftweb.http.{RequestVar, SessionVar}`.
 *
 * lift-proto's `ProtoUser` tracks the logged-in user with a `RequestVar` (`curUser`) and
 * `SessionVar`s (`curUserId`, `loginRedirect`). The originals live in lift-webkit and touch
 * `S` / `LiftRules` / `LiftRulesMocker` at class-init — that was the Phase-0
 * `NoClassDefFoundError`. These substitutes are backed by a plain `ThreadLocal` with ZERO
 * reference to any webkit symbol, so they load and run under http4s.
 *
 * OBP authenticates via DirectLogin / OAuth (not Lift session), so `currentUser` is normally
 * `Empty` and `getCurrentUser` falls back to those mechanisms — the Vars only need to load and
 * behave as a per-thread holder, which this does.
 */
abstract class RequestVar[T](dflt: => T) {
  private[this] val tl = new ThreadLocal[Option[T]]()

  /** Overridable for source compatibility (webkit used it for unique naming); unused here. */
  protected lazy val __nameSalt: String = ""

  def get: T = tl.get match {
    case Some(v) => v
    case _       => dflt
  }
  def is: T = get
  def set(v: T): T = { tl.set(Some(v)); v }
  def apply(v: T): T = set(v)
  def remove(): Unit = tl.remove()

  /** Run `f` with this var temporarily set to `v`, restoring the prior value afterward. */
  def doWith[R](v: T)(f: => R): R = {
    val old = tl.get
    tl.set(Some(v))
    try f
    finally { if (old == null) tl.remove() else tl.set(old) }
  }

  override def toString: String = String.valueOf(get)
}

abstract class SessionVar[T](dflt: => T) extends RequestVar[T](dflt)

/** Marker trait — webkit cleared the var on session transition; a no-op in the fork. */
trait CleanRequestVarOnSessionTransition
