package com.auction.datastore

import com.auction.model.{Auction, Item, Participant, Auctioneer}
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap


import com.auction._

/** Using a cake implementation .. humm boilerplate .. maybe using implicit on the design to reduce the boilerplate? How about Monad Reader? */
trait AuctionRepositoryComponent {

  trait AuctionRepository {

    /** create and starts an auction for an item. */
    //TODO What should happen in case try to create an auction using an invalid item? The API should return an Either? Failure? Option? Nothing? let's code review it
    def createAuction(auctioneerId: String, itemId: String): Option[Auction]

    /** store/update an auction */
    def save(auction: Auction): Auction

    /** retrieve an item in case it is there */
    def getItem(id: String): Option[Item]

    /** Create participant for an auction */
    def createParticipant(name: String): Participant

    /** add auctioneer to the system */
    def createAuctioneer(name: String): Auctioneer

    /** retrieve an auctioneer */
    def getAuctioneer(id: String): Option[Auctioneer]

    /** create a new item. In case already there is an item with the same name a failure is raised */ //Should be a Try[Item] ?
    def createItem(uniqueName: String, reservedPrice: Double, auctioneerId: String): Item

    /** returns an auction in case it is there */
    def getAuction(id: String): Option[Auction]

  }
}


trait AuctionRepositoryComponentImpl extends AuctionRepositoryComponent {

  class AuctionRepositoryImpl extends AuctionRepository{

    /** using an unique key-value for storing all 'data'. It means has to deal with 'Any' */
    val store = new SimpleKeyValue()

    /** create a new item. In case already there is an item with the same name a failure is raised */ //Should be a Try[Item] ?
    def createItem(uniqueName: String, reservedPrice: Double, auctioneerId: String): Item = {
      val id = uuid
      store[Item](id, Item(id, uniqueName, reservedPrice, auctioneerId))
    }

    /** add auctioneer to the system */
    override def createAuctioneer(name: String): Auctioneer = {
      val id = uuid
      store[Auctioneer](id, Auctioneer(id, name))
    }

    /** retrieve an auctioneer */
    override def getAuctioneer(id: String): Option[Auctioneer] = store.get[Auctioneer](id)

    /** Create participant for an auction */
    override def createParticipant(name: String): Participant = {
      val id = uuid
      store[Participant](id, Participant(id, name))
    }

    /** retrieve an item in case it is there */
    override def getItem(id: String): Option[Item] = store.get[Item](id)

    /** create and starts an auction for an item. In case not item available returns 'empty'*/
    override def createAuction(auctioneerId: String, itemId: String): Option[Auction] = {

      //remove item from be available to another auction
      //TODO What should happen in case the item is no longer available? It changes the API design?
      val optionItem = store.remove[Item](itemId)
      val auction = (item: Item) => store[Auction](item.name, Auction(item.name, item, auctioneerId))

      optionItem.map(auction)

    }

    /** returns an auction in case it is there */
    override def getAuction(id: String): Option[Auction] = store.get[Auction](id)

    /** store/update an auction */
    override def save(auction: Auction): Auction = store[Auction](auction.id, auction)
  }

}


/**
 * This is a simple key-value repository in memory
 */
final class SimpleKeyValue()  {

  /**
   * A simple concurrent map for storing the key-value
   */
  private[datastore] val store = new ConcurrentLinkedHashMap.Builder[String, Any].maximumWeightedCapacity(5000).build()

  /**
   * Retrieve an object
   * @param key
   * @tparam T
   * @return
   */
  def get[T](key: String): Option[T] = Option(store.get(key).asInstanceOf[T])

  /**
   * Add a key-value
   * @param key
   * @param value
   * @tparam T
   * @return
   */
  def apply[T](key: String, value: T): T = {

    store.put(key, value) match {
      case null => value
      case existing => existing.asInstanceOf[T]
    }
  }

  /**
   * Clean completely the key-value store
   */
  def clear() { store.clear() }

  /**
   * Remove case the key is present
   * @param key
   * @tparam T
   * @return
   */
  def remove[T](key: String): Option[T] = Option(store.remove(key).asInstanceOf[T])

}