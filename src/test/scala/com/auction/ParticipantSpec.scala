package com.auction

import com.auction.AuctionSystemRegistry._
import org.scalatest.{FeatureSpec, GivenWhenThen}

class ParticipantSpec extends FeatureSpec with GivenWhenThen{

  info("As a API to register an participant")

  feature("Register a new Participant") {
    scenario("Register a new participant") {

      Given("a valid name")

      val name = "Participant 1"

      When("invoking the action registration")

      val participant = auctionSystem.createParticipant(name)

      Then(s"Participant name must be $name and ID must not be empty")

      assert(participant.name == name)
      assert(participant.id.nonEmpty)

    }

  }

}
