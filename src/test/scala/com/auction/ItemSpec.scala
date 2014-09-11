package com.auction

import org.scalatest.{Matchers, GivenWhenThen, FeatureSpec}
import AuctionSystemRegistry._

class ItemSpec extends FeatureSpec with GivenWhenThen with Matchers{

  info("As a API to add item")

  feature("Auctioneer adds an item") {
    scenario("Auctioneer is preparing an auction then needs to add an item which will be used in an auction") {

      Given("a valid auctioneer")

      val name = "John Smith"
      val auctioneer = auctionSystem.createAuctioneer(name)

      When("auctioneer add a item")

      val item = auctionSystem.createItem("bike", 25.0, auctioneer.id)

      Then(s"Item must have the reserved price as 25.00")

      assert(item.reservedPrice == 25.0)
      assert(item.id.nonEmpty)

    }

    scenario("Auctioneer is preparing an auction then needs to add an item and retrieve it") {

      Given("a valid auctioneer")

      val name = "John Smith"
      val auctioneer = auctionSystem.createAuctioneer(name)

      When("auctioneer retrieve a item")

      val item = auctionSystem.createItem("bike", 25.0, auctioneer.id)

      auctionSystem.getItem("bike")

      Then(s"Item must match the created item")

      assert(item.reservedPrice == 25.0)
      assert(item.id.nonEmpty)

    }
  }

}
