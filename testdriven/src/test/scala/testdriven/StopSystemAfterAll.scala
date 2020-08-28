package testdriven

import org.scalatest.{ Suite, BeforeAndAfterAll }
import akka.testkit.TestKit

trait StopSystemAfterAll extends BeforeAndAfterAll { //Extends from the BeforeAndAfterAll ScalaTest trait
  this: TestKit with Suite => //This trait can only be used if it's mixed in with a test that uses the TestKit
  override protected def afterAll(): Unit = {
    super.afterAll()
    system.terminate() //Shuts down the system provided by the TestKit after all tests have executed
  }
}
