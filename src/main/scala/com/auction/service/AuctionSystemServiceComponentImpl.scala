package com.auction.service

import com.auction.datastore.AuctionRepositoryComponent
import com.auction.model._
import com.auction._

/**
 *
 */
trait AuctionSystemServiceComponentImpl { this: AuctionRepositoryComponent =>

  val auctionRepository: AuctionRepository

  class AuctionSystemServiceImpl{

    /** create a new item in case there isn't item with same name */
    def createItem(uniqueName: String, reservedPrice: Double, auctioneerId: String): Item = auctionRepository.createItem(uniqueName, reservedPrice, auctioneerId)

    def getItem(id: String): Option[Item] = auctionRepository.getItem(id)

    /** retrieves an auction */
    def getAuction(id: String): Option[Auction] = auctionRepository.getAuction(id)

    /** start an auction */
    def createAuction(auctioneerId: String, itemId: String): Option[Auction] = auctionRepository.createAuction(auctioneerId, itemId)

    /** Auctioneer calls the auction (when s/he makes the judgement on her own that there will be no more higher bids coming in).
      * in case invalid auction a none is send back*/
    def call(auctionId: String): Option[Auction] =  {

      //TODO need to deal with concurrent bids?
      getAuction(auctionId) match {
        case Some(auction) if auction.status == AuctionCompleteSuccess => None
        case Some(auction) if auction.status == AuctionCompleteFailure => None
        case Some(auction) if auction.status == AuctionInProgress =>

          val bidPrice = auction.highestBid.map(bid => bid.price).getOrElse(0d)

          val completeAuction =
            if(auction.item.reservedPrice < bidPrice){
              auction.copy(priceSold = bidPrice, status = AuctionCompleteSuccess)
            } else auction.copy( status = AuctionCompleteFailure, buyer = Option.empty)

          auctionRepository.save(completeAuction)
          Option(completeAuction)
      }

    }

    /**
     * Bids for an auction. In case the bid is not accepted because the price is
     * lowest than the last accepted bid it returns none .. otherwise the new bid
     * @param auctionId
     * @param participantId
     * @param price
     * @return
     */
    def bid(auctionId: String, participantId: String, price: Double): Option[Bid] = {
      //TODO this should deal with concurrent bids? Let's talk abut it


      getAuction(auctionId) match{
        case Some(auction) if auction.status == AuctionCompleteSuccess => None
        case Some(auction) if auction.status == AuctionCompleteFailure => None
        case Some(auction) if auction.status == AuctionInProgress =>
          val suggestedBid = Bid(uuid, participantId, price)

          val acceptedBid = auction.highestBid.map{ highestBidSoFar => highestBidSoFar.price < suggestedBid.price }.getOrElse(true)

          if(acceptedBid){
            val ac = auction.copy(highestBid = Option(suggestedBid), buyer = Option(participantId))
            auctionRepository.save(ac)
            Option(suggestedBid)
          }else Option.empty

        case _ => None

      }

    }


    /** Create an auctioneer */
    def createAuctioneer(name: String): Auctioneer = {
      // but could return a Option[Auctioneer], Try[Auctioneer] or, Future[Auctioneer] or something like Either[AuctionSystemError, Auctioneer]
      // .. lets talk about it :)

      auctionRepository.createAuctioneer(name)

    }

    /** retrieve auctioneer */
    def getAuctioneer(id: String): Option[Auctioneer] = auctionRepository.getAuctioneer(id)

    /** create participant */
    def createParticipant(name: String): Participant = {
      auctionRepository.createParticipant(name)
    }

  }

}
