package com.auction

import com.auction.AuctionSystemRegistry._
import com.auction.model.AuctionCompleteSuccess
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

class AuctionSpec extends FeatureSpec with GivenWhenThen with Matchers{

  info("As a API to add item")

  feature("Auction process") {
    scenario("Auctioneer is preparing an auction then needs to add an item which will be used in an auction") {

      Given("a valid auctioneer")

      val name = "John Smith"
      val auctioneer = auctionSystem.createAuctioneer(name)

      When("auctioneer add a item")

      val item = auctionSystem.createItem("bike", 25.0, auctioneer.id)

      assert(item.reservedPrice == 25.0)
      assert(item.id.nonEmpty)

      val optionAuction = auctionSystem.createAuction(auctioneer.id, item.id)

      Then(s"The item used must not be available to another auction")

      optionAuction should not be empty
      val checkItem = auctionSystem.getItem(item.id)
      checkItem shouldBe empty

    }


    scenario("Participants submit bids to an auction, a new bid has to have a price higher than the current highest bid otherwise it's not allowed") {

      Given("a valid auction")

      val auctioneer = auctionSystem.createAuctioneer("John Smith")

      val participant1 = auctionSystem.createParticipant("participant 1")
      val participant2 = auctionSystem.createParticipant("participant 2")
      val participant3 = auctionSystem.createParticipant("participant 3")

      val item = auctionSystem.createItem("bike", 25.0, auctioneer.id)

      val optionAuction = auctionSystem.createAuction(auctioneer.id, item.id)

      When("Auction is started participants start bidding")

      val bid1 = auctionSystem.bid(optionAuction.get.id, participant1.id, 10)
      val bid2 = auctionSystem.bid(optionAuction.get.id, participant2.id, 15)
      val bid3 = auctionSystem.bid(optionAuction.get.id, participant3.id, 12)
      val bid4 = auctionSystem.bid(optionAuction.get.id, participant1.id, 22)
      val bid5 = auctionSystem.bid(optionAuction.get.id, participant2.id, 18)


      Then("The auction must contain the highest bid so far")

      bid1 should not be empty
      bid2 should not be empty
      bid3 shouldBe empty
      bid4 should not be empty
      bid5 shouldBe empty

      val checkingAuction = auctionSystem.getAuction(optionAuction.get.id)
      checkingAuction should not be empty
      checkingAuction.get.highestBid.get should be (bid4.get)
    }


    scenario("Auctioneer calls the auction (when s/he makes the judgement on her own that there will be no more higher bids coming in).") {

      Given("a valid auction")

      val auctioneer = auctionSystem.createAuctioneer("John Smith")

      val participant1 = auctionSystem.createParticipant("participant 1")
      val participant2 = auctionSystem.createParticipant("participant 2")
      val participant3 = auctionSystem.createParticipant("participant 3")

      val item = auctionSystem.createItem("bike", 25.0, auctioneer.id)

      val optionAuction = auctionSystem.createAuction(auctioneer.id, item.id)

      val bid1 = auctionSystem.bid(optionAuction.get.id, participant1.id, 10)
      val bid2 = auctionSystem.bid(optionAuction.get.id, participant2.id, 15)
      val bid3 = auctionSystem.bid(optionAuction.get.id, participant3.id, 12)
      val bid4 = auctionSystem.bid(optionAuction.get.id, participant1.id, 22)
      val bid5 = auctionSystem.bid(optionAuction.get.id, participant2.id, 18)
      val bid6 = auctionSystem.bid(optionAuction.get.id, participant3.id, 28)

      When("Auctioneer calls the auction")

      val calledAuction = auctionSystem.call(optionAuction.get.id).get

      Then("the auction is deemed as a success")

      calledAuction.highestBid.get should be (bid6.get)
      calledAuction.buyer.get should be (bid6.get.participantId)
      calledAuction.priceSold should be (bid6.get.price)
      calledAuction.status should be (AuctionCompleteSuccess)
    }


    scenario("Participant/Auctioneer queries the latest auction of an item by item name") {

      Given("a valid auction")

      val auctioneer = auctionSystem.createAuctioneer("John Smith")

      val participant1 = auctionSystem.createParticipant("participant 1")
      val participant2 = auctionSystem.createParticipant("participant 2")
      val participant3 = auctionSystem.createParticipant("participant 3")

      val item = auctionSystem.createItem("bike", 25.0, auctioneer.id)

      val optionAuction = auctionSystem.createAuction(auctioneer.id, item.id)

      val bid1 = auctionSystem.bid(optionAuction.get.id, participant1.id, 10)
      val bid2 = auctionSystem.bid(optionAuction.get.id, participant2.id, 15)
      val bid3 = auctionSystem.bid(optionAuction.get.id, participant3.id, 12)
      val bid4 = auctionSystem.bid(optionAuction.get.id, participant1.id, 22)
      val bid5 = auctionSystem.bid(optionAuction.get.id, participant2.id, 18)
      val bid6 = auctionSystem.bid(optionAuction.get.id, participant3.id, 28)

      When("Auctioneer calls the auction")

      auctionSystem.call(optionAuction.get.id)

      val calledAuction = auctionSystem.getAuction(item.name).get


      Then("the auction is deemed as a success")

      calledAuction.highestBid.get should be (bid6.get)
      calledAuction.buyer.get should be (bid6.get.participantId)
      calledAuction.priceSold should be (bid6.get.price)
      calledAuction.status should be (AuctionCompleteSuccess)
    }

  }

}
