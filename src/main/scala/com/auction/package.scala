package com

import java.util.UUID

/** a package with possible functions to be reuse across the library */
package object auction {

  /** generate a unique ID */
  def uuid:String = UUID.randomUUID().toString
}
