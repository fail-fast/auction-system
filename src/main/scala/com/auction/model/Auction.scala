package com.auction.model

/** Defined the possible status of an auction */
sealed trait Status
case object AuctionInProgress extends Status
case object AuctionCompleteSuccess extends Status
case object AuctionCompleteFailure extends Status


/**
 *
 * @param id
 * @param item
 * @param auctioneerId
 * @param highestBid Keep the highest bid received so far
 * @param priceSold
 * @param status
 * @param buyer
 */
case class Auction(id: String, item: Item, auctioneerId: String, highestBid: Option[Bid] = None, priceSold: Double = 0, status: Status = AuctionInProgress, buyer: Option[String] = None)
