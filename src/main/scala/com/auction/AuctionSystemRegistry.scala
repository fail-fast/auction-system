package com.auction

import com.auction.datastore.AuctionRepositoryComponentImpl
import com.auction.service.AuctionSystemServiceComponentImpl


/**
 * A simple component using the cake pattern to inject the repository and service
 */
object AuctionSystemRegistry extends AuctionSystemServiceComponentImpl with AuctionRepositoryComponentImpl{

  val auctionRepository:  AuctionRepository = new AuctionRepositoryImpl

  val auctionSystem = new AuctionSystemServiceImpl

}
