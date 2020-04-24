package com.yury.demo;

import com.yury.demo.book.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;

import static com.yury.demo.book.Order.OrdType.LIMIT;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OrderBookTests {

	@Test
	public void testOrderBookCreation() {
		OrderBookManager orderBookManager = new OrderBookManager();
		assertNotNull(orderBookManager.getSellOrders());
		assertNotNull(orderBookManager.getBuyOrders());
	}

	@Test
	public void testOrderBookBuyOrderInsert() {
		OrderBookManager orderBookManager = new OrderBookManager();
		Order order1 = new Order("1", LIMIT, true, "AAAA",
				5, 15.0, "111", 111.0);
		orderBookManager.processOrder(order1);
		assertEquals(orderBookManager.getBuyOrders().size(), 1);
		assertEquals(orderBookManager.getBuyOrders().get(0), new BuyOrder(order1));
	}

	@Test
	public void testOrderBookBuyOrderInsertOrder() {
		OrderBookManager orderBookManager = new OrderBookManager();
		Order order1 = new Order("1", LIMIT, true, "AAAA",
				5, 45.0, "111", 111.0);
		Order order2 = new Order("2", LIMIT, true, "AAAA",
				5, 15.0, "111", 111.0);
		Order order3 = new Order("1", LIMIT, true, "AAAA",
				5, 5.0, "111", 111.0);
		Order order4 = new Order("1", LIMIT, true, "AAAA",
				5, 15.0, "111", 111.0);

		orderBookManager.processOrder(order1);
		orderBookManager.processOrder(order2);
		assertEquals(orderBookManager.getBuyOrders().get(1).getClOrdID(), order2.getClOrdID());
		assertEquals(orderBookManager.getBuyOrders().get(0).getClOrdID(), order1.getClOrdID());

		orderBookManager.processOrder(order3);
		assertEquals(orderBookManager.getBuyOrders().get(2).getClOrdID(), order3.getClOrdID());
		assertEquals(orderBookManager.getBuyOrders().get(1).getClOrdID(), order2.getClOrdID());
		assertEquals(orderBookManager.getBuyOrders().get(0).getClOrdID(), order1.getClOrdID());

		orderBookManager.processOrder(order4);
		assertEquals(orderBookManager.getBuyOrders().get(3).getClOrdID(), order3.getClOrdID());
		assertEquals(orderBookManager.getBuyOrders().get(2).getClOrdID(), order4.getClOrdID());
		assertEquals(orderBookManager.getBuyOrders().get(1).getClOrdID(), order2.getClOrdID());
		assertEquals(orderBookManager.getBuyOrders().get(0).getClOrdID(), order1.getClOrdID());
	}

	@Test
	public void testOrderBookTransactionSamePriceSameQty() {
		OrderBookManager orderBookManager = new OrderBookManager();
		Order buyOrder1 = new Order("1", LIMIT, true, "AAAA",
				5, 10.0, "111", 111.0);
		Order buyOrder2 = new Order("2", LIMIT, true, "AAAA",
				5, 10.0, "234", 111.0);
		Order sellOrder1 = new Order("1", LIMIT, true, "AAAA",
				5, 10.0, "456", 111.0);
		Order sellOrder2 = new Order("2", LIMIT, true, "AAAA",
				5, 10.0, "7656", 111.0);

		//Add two buy orders
		ArrayList<Transaction> t1 = orderBookManager.processOrder(buyOrder1);
		assertTrue(t1.isEmpty()); //Check no transactions happened
		ArrayList<Transaction> t2 = orderBookManager.processOrder(buyOrder2);
		assertTrue(t2.isEmpty()); //Check no transactions happened
		assertEquals(orderBookManager.getBuyOrders().get(1), new BuyOrder(buyOrder1));
		assertEquals(orderBookManager.getBuyOrders().get(0), new BuyOrder(buyOrder2));

		//Add matching sell order
		ArrayList<Transaction> t3 = orderBookManager.processOrder(buyOrder1);
		//Check that a transaction happened
		assertFalse(t3.isEmpty());
		ArrayList<Transaction> t4 = orderBookManager.processOrder(buyOrder1);
		//Check that a transaction happened
		assertFalse(t4.isEmpty());

		assertTrue(orderBookManager.getBuyOrders().isEmpty());
		assertTrue(orderBookManager.getSellOrders().isEmpty());

		//Add same sell order (order book should be empty)
		ArrayList<Transaction> t5 = orderBookManager.processOrder(buyOrder1);
		//Check that no transaction happened
		assertTrue(t5.isEmpty());
	}

}
