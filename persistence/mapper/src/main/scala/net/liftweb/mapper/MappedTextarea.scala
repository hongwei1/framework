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

import common._

// OBP fork: webkit removed — the SHtml/S-based _toForm textarea builder had no OBP call-sites.
abstract class MappedTextarea[T<:Mapper[T]](owner : T, maxLen: Int) extends MappedString[T](owner, maxLen) {

  override def toString: String = {
    val v = get
    if (v == null || v.length < 100) super.toString
    else v.substring(0,40)+" ... "+v.substring(v.length - 40)
  }

  def textareaRows  = 8

  def textareaCols = 20

}

