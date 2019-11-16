package com.pttrn42.rx.user;

import com.google.common.collect.ImmutableList;

public class Order {

	private final ImmutableList<Item> items;

	public Order(ImmutableList<Item> items) {
		this.items = items;
	}

	public ImmutableList<Item> getItems() {
		return items;
	}

	@Override
	public String toString() {
		return "Order{" +
				"items=" + items +
				'}';
	}
}
