package com.auction


import org.scalatest.{Matchers, GivenWhenThen, FeatureSpec}
import AuctionSystemRegistry._

class AuctioneerSpec extends FeatureSpec with GivenWhenThen with Matchers{

  info("As a API to register an auctioneer")

  feature("Register a new Auctioneer") {
    scenario("Register a new auctioneer") {

      Given("a valid name")

      val name = "John Smith"

      When("invoking the action registration")

      val auctioneer = auctionSystem.createAuctioneer(name)

      Then(s"Auctioneer name must be $name and ID must not be empty")

      assert(auctioneer.name == name)
      assert(auctioneer.id.nonEmpty)

    }

    scenario("Register a new auctioneer and retrieve him") {

      Given("a valid name")

      val name = "John Smith"

      When("invoking the action registration")

      val auctioneer = auctionSystem.createAuctioneer(name)
      val optionAuctioneer = auctionSystem.getAuctioneer(auctioneer.id)

      Then(s"Those auctioneers must be the same")

      optionAuctioneer should not be empty
      assert(auctioneer.name == optionAuctioneer.get.name)
      assert(auctioneer.id.nonEmpty)
      assert(auctioneer.id == optionAuctioneer.get.id)

    }
  }

}
