package com.yury.demo;

import com.yury.demo.book.*;
import com.yury.demo.util.TimeStampGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;

import static com.yury.demo.book.Order.OrdType.LIMIT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class OrderBookTests {

	@InjectMocks
	OrderBookManager orderBookManager;
	@InjectMocks
	OrderBookManager orderBookManager1;
	@InjectMocks
	OrderBookManager orderBookManager2;
	@InjectMocks
	OrderBookManager orderBookManager3;
	@Mock
	TimeStampGenerator timeStampGenerator;

	@Test
	public void testOrderBookCreation() {
		assertNotNull(orderBookManager.getSellOrders());
		assertNotNull(orderBookManager.getBuyOrders());
	}

	@Test
	public void testOrderBookBuyOrderInsert() {
		Order order1 = new Order("1", LIMIT, true, "AAAA",
				5, 15.0, "111", 111.0);
		orderBookManager1.processOrder(order1);
		assertEquals(orderBookManager1.getBuyOrders().size(), 1);
		assertEquals(orderBookManager1.getBuyOrders().get(0).getClOrdID(), order1.getClOrdID());
	}

	@Test
	public void testOrderBookBuyOrderInsertOrder() {
		Order order1 = new Order("1", LIMIT, true, "AAAA",
				5, 45.0, "111", 111.0);
		Order order2 = new Order("2", LIMIT, true, "AAAA",
				5, 15.0, "111", 111.0);
		Order order3 = new Order("1", LIMIT, true, "AAAA",
				5, 5.0, "111", 111.0);
		Order order4 = new Order("1", LIMIT, true, "AAAA",
				5, 15.0, "111", 111.0);

		orderBookManager2.processOrder(order1);
		orderBookManager2.processOrder(order2);
		assertEquals(orderBookManager2.getBuyOrders().get(1).getClOrdID(), order2.getClOrdID());
		assertEquals(orderBookManager2.getBuyOrders().get(0).getClOrdID(), order1.getClOrdID());

		orderBookManager2.processOrder(order3);
		assertEquals(orderBookManager2.getBuyOrders().get(2).getClOrdID(), order3.getClOrdID());
		assertEquals(orderBookManager2.getBuyOrders().get(1).getClOrdID(), order2.getClOrdID());
		assertEquals(orderBookManager2.getBuyOrders().get(0).getClOrdID(), order1.getClOrdID());

		orderBookManager2.processOrder(order4);
		assertEquals(orderBookManager2.getBuyOrders().get(3).getClOrdID(), order3.getClOrdID());
		assertEquals(orderBookManager2.getBuyOrders().get(2).getClOrdID(), order4.getClOrdID());
		assertEquals(orderBookManager2.getBuyOrders().get(1).getClOrdID(), order2.getClOrdID());
		assertEquals(orderBookManager2.getBuyOrders().get(0).getClOrdID(), order1.getClOrdID());
	}

	@Test
	public void testOrderBookTransactionSamePriceSameQty() {
		when(timeStampGenerator.getCurrentTimeStamp()).thenReturn("111");

		Order buyOrder1 = new Order("1", LIMIT, true, "AAAA",
				5, 10.0, "111", 111.0);
		Order buyOrder2 = new Order("2", LIMIT, true, "AAAA",
				5, 10.0, "234", 111.0);
		Order sellOrder1 = new Order("1", LIMIT, false, "AAAA",
				5, 10.0, "456", 111.0);
		Order sellOrder2 = new Order("2", LIMIT, false, "AAAA",
				5, 10.0, "7656", 111.0);

		//Add two buy orders
		ArrayList<Transaction> t1 = orderBookManager3.processOrder(buyOrder1);
		assertTrue(t1.isEmpty()); //Check no transactions happened
		ArrayList<Transaction> t2 = orderBookManager3.processOrder(buyOrder2);
		assertTrue(t2.isEmpty()); //Check no transactions happened
		assertEquals(orderBookManager3.getBuyOrders().get(1).getClOrdID(), buyOrder1.getClOrdID());
		assertEquals(orderBookManager3.getBuyOrders().get(0).getClOrdID(), buyOrder2.getClOrdID());

		//Add matching sell order
		ArrayList<Transaction> t3 = orderBookManager3.processOrder(sellOrder1);
		//Check that a transaction happened
		assertFalse(t3.isEmpty());
		ArrayList<Transaction> t4 = orderBookManager3.processOrder(sellOrder2);
		//Check that a transaction happened
		assertFalse(t4.isEmpty());

		assertTrue(orderBookManager3.getBuyOrders().isEmpty());
		assertTrue(orderBookManager3.getSellOrders().isEmpty());

		//Add same sell order (order book should be empty)
		ArrayList<Transaction> t5 = orderBookManager3.processOrder(sellOrder1);
		//Check that no transaction happened
		assertTrue(t5.isEmpty());
	}

}
