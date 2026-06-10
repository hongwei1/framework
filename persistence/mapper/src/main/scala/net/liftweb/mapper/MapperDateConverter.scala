/*
 * Copyright 2006-2024 WorldWide Conferencing, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */

package net.liftweb
package mapper

import java.text.SimpleDateFormat
import java.util.{Date, Locale, TimeZone}

import net.liftweb.common.Box
import net.liftweb.util.Helpers.tryo

/**
 * Webkit-free replacement for the date parsing/formatting that the mapper
 * field classes (MappedDate / MappedDateTime / MappedTime) used to obtain via
 * `net.liftweb.http.LiftRules.dateTimeConverter()`.
 *
 * The formats mirror Lift's default `DateTimeConverter`
 * (`net.liftweb.util.DefaultDateTimeConverter`), which is backed by the
 * SimpleDateFormat instances declared in `net.liftweb.util.TimeHelpers`:
 *   - internetDateFormatter: "EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US, UTC
 *   - dateFormatter:         "yyyy/MM/dd"
 *   - hourFormat:            "HH:mm:ss"
 *   - timeFormatter:         "HH:mm zzz"
 *
 * Keeping these self-contained removes the last webkit reference from the
 * mapper field types while preserving byte-for-byte parsing/formatting
 * compatibility.
 */
object MapperDateConverter {

  /** "EEE, d MMM yyyy HH:mm:ss 'GMT'" in Locale.US, pinned to UTC. */
  private def internetDateFormat: SimpleDateFormat = {
    val fmt = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US)
    fmt.setTimeZone(TimeZone.getTimeZone("UTC"))
    fmt
  }

  /** "yyyy/MM/dd" — date only. */
  private def dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd")

  /** "HH:mm:ss" — time including seconds, no time zone. */
  private def hourFormat: SimpleDateFormat = new SimpleDateFormat("HH:mm:ss")

  /** "HH:mm zzz" — time with time zone, no seconds. */
  private def timeFormat: SimpleDateFormat = new SimpleDateFormat("HH:mm zzz")

  // -- formatting -----------------------------------------------------------

  def formatDateTime(d: Date): String = internetDateFormat.format(d)

  def formatDate(d: Date): String = dateFormat.format(d)

  /** Uses the hour format which includes seconds but not the time zone. */
  def formatTime(d: Date): String = hourFormat.format(d)

  // -- parsing --------------------------------------------------------------

  def parseDateTime(s: String): Box[Date] = tryo { internetDateFormat.parse(s) }

  def parseDate(s: String): Box[Date] = tryo { dateFormat.parse(s) }

  /** Tries the seconds-bearing hour format first, then the zoned time format. */
  def parseTime(s: String): Box[Date] = tryo { hourFormat.parse(s) } or tryo { timeFormat.parse(s) }
}
