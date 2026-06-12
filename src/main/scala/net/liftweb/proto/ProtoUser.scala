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

/*
 * OBP fork: lift-proto is decoupled from lift-webkit so the webkit jar can leave the
 * classpath (clean SBOM). All `net.liftweb.http` / `net.liftweb.sitemap` imports are gone.
 *
 * What was removed: the entire Lift Web UI surface (SiteMap menus, NodeSeq form builders,
 * S/SHtml/JsCmds plumbing) — OBP runs API-only and never rendered these. What remains is the
 * authentication/identity state machine OBP depends on at runtime: `curUser`/`curUserId`/
 * `loginRedirect` (now backed by the webkit-free RequestVar/SessionVar in ProtoVars.scala,
 * same package), `logUserIn`/`logUserOut`/`loggedIn_?`/`doWithUser`, the abstract DB/field
 * bridge, and the mail bodies. A handful of web entry points (`login`, `signup`, `loginXhtml`,
 * `signupXhtml`, `lostPassword`, `lostPasswordXhtml`, `passwordResetXhtml`, `validateUser`,
 * `localForm`) are kept as webkit-free `NodeSeq` stubs purely so OBP's `AuthUser` overrides of
 * them still type-check — they are not invoked in the API-only runtime.
 */

import scala.xml.{NodeSeq, Node, Text, Elem}
import net.liftweb.util.Helpers._
import net.liftweb.util._
import net.liftweb.common._
// OBP fork: Mailer removed — sendValidationEmail/sendPasswordReset are stubbed; OBP overrides both

/**
 * A prototypical user class with abstractions to the underlying storage
 */
trait ProtoUser {
  /**
   * The underlying record for the User
   */
  type TheUserType

  /**
   * Bridges from TheUserType to methods used in this class
   */
  protected trait UserBridge {
    /**
     * Convert the user's primary key to a String
     */
    def userIdAsString: String

    /**
     * Return the user's first name
     */
    def getFirstName: String

    /**
     * Return the user's last name
     */
    def getLastName: String

    /**
     * Get the user's email
     */
    def getEmail: String

    /**
     * Is the user a superuser
     */
    def superUser_? : Boolean

    /**
     * Has the user been validated?
     */
    def validated_? : Boolean

    /**
     * Does the supplied password match the actual password?
     */
    def testPassword(toTest: Box[String]): Boolean

    /**
     * Set the validation flag on the user and return the user
     */
    def setValidated(validation: Boolean): TheUserType

    /**
     * Set the unique ID for this user to a new value
     */
    def resetUniqueId(): TheUserType

    /**
     * Return the unique ID for the user
     */
    def getUniqueId(): String

    /**
     * Validate the user
     */
    def validate: List[FieldError]

    /**
     * Given a list of string, set the password
     */
    def setPasswordFromListString(in: List[String]): TheUserType

    /**
     * Save the user to backing store
     */
    def save: Boolean

    /**
     * Get a nice name for the user
     */
    def niceName: String = (getFirstName, getLastName, getEmail) match {
      case (f, l, e) if f.length > 1 && l.length > 1 => f+" "+l+" ("+e+")"
      case (f, _, e) if f.length > 1 => f+" ("+e+")"
      case (_, l, e) if l.length > 1 => l+" ("+e+")"
      case (_, _, e) => e
    }

    /**
     * Get a short name for the user
     */
    def shortName: String = (getFirstName, getLastName) match {
      case (f, l) if f.length > 1 && l.length > 1 => f+" "+l
      case (f, _) if f.length > 1 => f
      case (_, l) if l.length > 1 => l
      case _ => getEmail
    }
  }

  /**
   * Convert an instance of TheUserType to the Bridge trait
   */
  protected implicit def typeToBridge(in: TheUserType): UserBridge

  /**
   * Get a nice name for the user
   */
  def niceName(inst: TheUserType): String = inst.niceName

  /**
   * Get a nice name for the user
   */
  def shortName(inst: TheUserType): String = inst.shortName

  /**
   * A generic representation of a field.  For example, this represents the
   * abstract "name" field and is used along with an instance of TheCrudType
   * to compute the BaseField that is the "name" field on the specific instance
   * of TheCrudType
   */
  type FieldPointerType

  /**
   * Based on a FieldPointer, build a FieldPointerBridge
   */
  protected implicit def buildFieldBridge(from: FieldPointerType): FieldPointerBridge

  protected trait FieldPointerBridge {
    /**
     * What is the display name of this field?
     */
    def displayHtml: NodeSeq

    /**
     * Does this represent a pointer to a Password field
     */
    def isPasswordField_? : Boolean
  }

  /**
   * The list of fields presented to the user at sign-up
   */
  def signupFields: List[FieldPointerType]


  /**
   * The list of fields presented to the user for editing
   */
  def editFields: List[FieldPointerType]

  /**
   * What template are you going to wrap the various nodes in
   */
  def screenWrap: Box[Node] = Empty

  /**
   * The base path for the user related URLs.  Override this
   * method to change the base path
   */
  def basePath: List[String] = "user_mgt" :: Nil

  /**
   * The path suffix for the sign up screen
   */
  def signUpSuffix: String = "sign_up"

  /**
   * The computed path for the sign up screen
   */
  lazy val signUpPath = thePath(signUpSuffix)

  /**
   * The path suffix for the login screen
   */
  def loginSuffix = "login"

  /**
   * The computed path for the login screen
   */
  lazy val loginPath = thePath(loginSuffix)

  /**
   * The path suffix for the lost password screen
   */
  def lostPasswordSuffix = "lost_password"

  /**
   * The computed path for the lost password screen
   */
  lazy val lostPasswordPath = thePath(lostPasswordSuffix)

  /**
   * The path suffix for the reset password screen
   */
  def passwordResetSuffix = "reset_password"

  /**
   * The computed path for the reset password screen
   */
  lazy val passwordResetPath = thePath(passwordResetSuffix)

  /**
   * The path suffix for the change password screen
   */
  def changePasswordSuffix = "change_password"

  /**
   * The computed path for change password screen
   */
  lazy val changePasswordPath = thePath(changePasswordSuffix)

  /**
   * The path suffix for the logout screen
   */
  def logoutSuffix = "logout"

  /**
   * The computed pat for logout
   */
  lazy val logoutPath = thePath(logoutSuffix)

  /**
   * The path suffix for the edit screen
   */
  def editSuffix = "edit"

  /**
   * The computed path for the edit screen
   */
  lazy val editPath = thePath(editSuffix)

  /**
   * The path suffix for the validate user screen
   */
  def validateUserSuffix = "validate_user"

  /**
   * The calculated path to the user validation screen
   */
  lazy val validateUserPath = thePath(validateUserSuffix)

  /**
   * The application's home page
   */
  def homePage = "/"

  /**
   * If you want to redirect a user to a different page after login,
   * put the page here
   */
  object loginRedirect extends SessionVar[Box[String]](Empty)

  /**
   * Calculate the path given a suffix by prepending the basePath to the suffix
   */
  protected def thePath(end: String): List[String] = basePath ::: List(end)

  /**
   * Return the URL of the "login" page
   */
  def loginPageURL = loginPath.mkString("/","/", "")

  /**
   * Inverted loggedIn_?
   */
  def notLoggedIn_? = !loggedIn_?

  /**
   * Is there a user logged in and are they a superUser?
   */
  def superUser_? : Boolean = currentUser.map(_.superUser_?) openOr false

  var onLogIn: List[TheUserType => Unit] = Nil

  var onLogOut: List[Box[TheUserType] => Unit] = Nil

  /**
   * This function is given a chance to log in a user
   * programmatically when needed
   */
  var autologinFunc: Box[()=>Unit] = Empty

  def loggedIn_? = {
    if(!currentUserId.isDefined)
      for(f <- autologinFunc) f()
    currentUserId.isDefined
  }

  def logUserIdIn(id: String) {
    curUser.remove()
    curUserId(Full(id))
  }

  def logUserIn(who: TheUserType, postLogin: () => Nothing): Nothing = {
    // OBP fork: webkit's `destroySessionOnLogin` session-rotation branch removed (no Lift
    // session in the API-only runtime). Just establish the user and continue.
    logUserIn(who)
    postLogin()
  }

  def logUserIn(who: TheUserType) {
    curUserId.remove()
    curUser.remove()
    curUserId(Full(who.userIdAsString))
    curUser(Full(who))
    onLogIn.foreach(_(who))
  }

  def logoutCurrentUser = logUserOut()

  def logUserOut() {
    onLogOut.foreach(_(curUser.get))
    curUserId.remove()
    curUser.remove()
    // OBP fork: webkit `S.session.foreach(_.destroySession())` removed — no Lift session.
  }

  /**
   * There may be times when you want to be another user
   * for some stack frames.  Here's how to do it.
   */
  def doWithUser[T](u: Box[TheUserType])(f: => T): T =
    curUserId.doWith(u.map(_.userIdAsString)) {
      curUser.doWith(u) {
        f
      }
    }


  private object curUserId extends SessionVar[Box[String]](Empty)


  def currentUserId: Box[String] = curUserId.get

  private object curUser extends RequestVar[Box[TheUserType]](currentUserId.flatMap(userFromStringId))  with CleanRequestVarOnSessionTransition


  /**
   * Given a String representing the User ID, find the user
   */
  protected def userFromStringId(id: String): Box[TheUserType]

  def currentUser: Box[TheUserType] = curUser.get

  /**
   * Should the e-mail validation step be skipped at sign-up? OBP overrides this from props.
   */
  def skipEmailValidation = false

  /**
   * OBP fork: webkit-free stub. OBP's `AuthUser` overrides this; the proto base only needs a
   * compatible signature (the original built a NodeSeq sign-up form via S/SHtml).
   */
  def signupXhtml(user: TheUserType): NodeSeq = NodeSeq.Empty


  def signupMailBody(user: TheUserType, validationLink: String): Elem = {
    (<html>
        <head>
          <title>{"sign.up.confirmation"}</title>
        </head>
        <body>
          <p>{"dear"} {user.getFirstName},
            <br/>
            <br/>
            {"sign.up.validation.link"}
            <br/><a href={validationLink}>{validationLink}</a>
            <br/>
            <br/>
            {"thank.you"}
          </p>
        </body>
     </html>)
  }

  def signupMailSubject = "sign.up.confirmation"

  /**
   * Send validation email to the user.  The XHTML version of the mail
   * body is generated by calling signupMailBody.  You can customize the
   * mail sent to users by override generateValidationEmailBodies to
   * send non-HTML mail or alternative mail bodies.
   */
  def sendValidationEmail(user: TheUserType): Unit = {
    // OBP fork: Mailer removed; OBP's AuthUser overrides this method entirely
  }

  /**
   * Generate the mail bodies to send with the valdiation link.
   * By default, just an HTML mail body is generated by calling signupMailBody
   * but you can send additional or alternative mail by override this method.
   */
  // OBP fork: Mailer removed — method kept as stub (unreachable; OBP overrides sendValidationEmail)
  protected def generateValidationEmailBodies(user: TheUserType, resetLink: String): List[Any] = Nil

  /**
   * Override this method to do something else after the user signs up
   *
   * OBP fork: webkit-free. The original notified via `S.notice` and redirected via
   * `S.redirectTo`; OBP overrides this method, so the base just persists and (optionally) logs in.
   */
  protected def actionsAfterSignup(theUser: TheUserType, func: () => Nothing): Nothing = {
    theUser.setValidated(skipEmailValidation).resetUniqueId()
    theUser.save
    if (!skipEmailValidation) {
      sendValidationEmail(theUser)
      func()
    } else {
      logUserIn(theUser, () => func())
    }
  }

  /**
   * Override this method to validate the user signup (eg by adding captcha verification)
   */
  def validateSignup(user: TheUserType): List[FieldError] = user.validate

  /**
   * Create a new instance of the User
   */
  protected def createNewUserInstance(): TheUserType

  /**
   * If there's any mutation to do to the user on creation for
   * signup, override this method and mutate the user.  This can
   * be used to pull query parameters from the request and assign
   * certain fields. . Issue #722
   *
   * @param user the user to mutate
   * @return the mutated user
   */
  protected def mutateUserOnSignup(user: TheUserType): TheUserType = user

  /**
   * OBP fork: webkit-free stub. OBP's `AuthUser` overrides this; the proto base only needs a
   * compatible signature (the original rendered the sign-up form + submit binding via S/SHtml).
   */
  def signup: NodeSeq = NodeSeq.Empty

  def emailFrom = "noreply@localhost"

  def bccEmail: Box[String] = Empty

  /**
   * OBP fork: webkit-free stub. OBP's `AuthUser` overrides this; the original verified the link
   * and logged the user in via S.notice/S.redirectTo.
   */
  def validateUser(id: String): NodeSeq = {
    findUserByUniqueId(id) match {
      case Full(user) if !user.validated_? =>
        user.setValidated(true).resetUniqueId().save
        logUserIn(user)
      case _ =>
    }
    NodeSeq.Empty
  }

  /**
   * How do we prompt the user for the username.  By default it's the literal
   * "email.address" key; you can change it to something else.
   */
  def userNameFieldString: String = "email.address"

  /**
   * The string that's generated when the user name is not found.  By
   * default the literal "email.address.not.found" key.
   */
  def userNameNotFoundString: String = "email.address.not.found"

  /**
   * OBP fork: webkit-free stub. OBP's `AuthUser` overrides this; the original rendered the
   * login form via S/SHtml.
   */
  def loginXhtml: NodeSeq = NodeSeq.Empty

  /**
   * Given an username (probably email address), find the user
   */
  protected def findUserByUserName(username: String): Box[TheUserType]

  /**
   * Given a unique id, find the user
   */
  protected def findUserByUniqueId(id: String): Box[TheUserType]

  /**
   * By default, destroy the session on login.
   * Change this is some of the session information needs to
   * be preserved.
   */
  protected def destroySessionOnLogin = true

  /**
   * If there's any state that you want to capture pre-login
   * to be set post-login (the session is destroyed),
   * then set the state here.  Just make a function
   * that captures the state... that function will be applied
   * post login.
   */
  protected def capturePreLoginState(): () => Unit = () => {}

  /**
   * OBP fork: webkit-free stub. OBP's `AuthUser` overrides this (returns Nil). The original
   * returned `List[Loc.LocParam[Unit]]` (sitemap login-menu params, lift-webkit); the menu
   * machinery is gone and OBP never consumes the result, so a neutral `List[Any]` suffices.
   */
  protected def loginMenuLocParams: List[Any] = Nil

  /**
   * OBP fork: webkit-free stub. OBP's `AuthUser` overrides this; the original processed the
   * posted login form (S.post_?/S.param) and logged the user in.
   */
  def login: NodeSeq = NodeSeq.Empty

  /**
   * OBP fork: webkit-free stub. OBP's `AuthUser` overrides this; the original rendered the
   * lost-password form via S/SHtml.
   */
  def lostPasswordXhtml: NodeSeq = NodeSeq.Empty

  def passwordResetMailBody(user: TheUserType, resetLink: String): Elem = {
    (<html>
        <head>
          <title>{"reset.password.confirmation"}</title>
        </head>
        <body>
          <p>{"dear"} {user.getFirstName},
            <br/>
            <br/>
            {"click.reset.link"}
            <br/><a href={resetLink}>{resetLink}</a>
            <br/>
            <br/>
            {"thank.you"}
          </p>
        </body>
     </html>)
  }

  /**
   * Generate the mail bodies to send with the password reset link.
   * By default, just an HTML mail body is generated by calling
   * passwordResetMailBody
   * but you can send additional or alternative mail by overriding this method.
   */
  // OBP fork: Mailer removed — method kept as stub (unreachable; OBP overrides sendPasswordReset)
  protected def generateResetEmailBodies(user: TheUserType, resetLink: String): List[Any] = Nil


  def passwordResetEmailSubject = "reset.password.request"

  /**
   * Send password reset email to the user.  The XHTML version of the mail
   * body is generated by calling passwordResetMailBody.  You can customize the
   * mail sent to users by overriding generateResetEmailBodies to
   * send non-HTML mail or alternative mail bodies.
   *
   * OBP fork: webkit-free. The original built the link from `S.hostAndPath` and notified via
   * `S.notice`/`S.redirectTo`; OBP overrides this method, so the base uses a relative path.
   */
  def sendPasswordReset(email: String): Unit = {
    // OBP fork: Mailer removed; OBP's AuthUser overrides this method entirely
  }

  /**
   * OBP fork: webkit-free stub. OBP's `AuthUser` overrides this; the original rendered the
   * lost-password form + submit binding via S/SHtml.
   */
  def lostPassword: NodeSeq = NodeSeq.Empty

  /**
   * OBP fork: webkit-free stub. OBP's `AuthUser` overrides this; the original rendered the
   * password-reset form via S/SHtml.
   */
  def passwordResetXhtml: NodeSeq = NodeSeq.Empty

  /**
   * If there's any mutation to do to the user on retrieval for
   * editing, override this method and mutate the user.  This can
   * be used to pull query parameters from the request and assign
   * certain fields. Issue #722
   *
   * @param user the user to mutate
   * @return the mutated user
   */
  protected def mutateUserOnEdit(user: TheUserType): TheUserType = user

  /**
   * Given an instance of TheCrudType and FieldPointerType, convert
   * that to an actual instance of a BaseField on the instance of TheCrudType
   */
  protected def computeFieldFromPointer(instance: TheUserType, pointer: FieldPointerType): Box[BaseField]


  /**
   * OBP fork: webkit-free stub. OBP's `AuthUser` overrides this; the original rendered each
   * field's form row via the BaseField `toForm` machinery.
   */
  protected def localForm(user: TheUserType, ignorePassword: Boolean, fields: List[FieldPointerType]): NodeSeq =
    NodeSeq.Empty
}
