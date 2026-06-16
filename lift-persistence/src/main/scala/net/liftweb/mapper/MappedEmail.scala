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
package mapper

// OBP fork: webkit removed — S.? validation message inlined as a literal.
import util.FieldError
import proto._

import scala.xml.Text

object MappedEmail {
  /** RFC 5321 caps the whole address at 254 chars. Rejecting longer input up
   *  front also stops the validation regex from running on multi-KB strings,
   *  whose repeated-group structure can recurse into a StackOverflowError. */
  val MaxEmailLength = 254

  def emailPattern = ProtoRules.emailRegexPattern.vend

  def validEmailAddr_?(email: String): Boolean =
    email != null && email.length <= MaxEmailLength && emailPattern.matcher(email).matches
}

abstract class MappedEmail[T <: Mapper[T]](owner: T, maxLen: Int) extends MappedString[T](owner, maxLen) {

  override def setFilter = notNull _ :: toLower _ :: trim _ :: super.setFilter

  override def validate =
    (if (MappedEmail.validEmailAddr_?(i_is_!)) Nil else List(FieldError(this, Text("invalid.email.address")))) :::
    super.validate

}

