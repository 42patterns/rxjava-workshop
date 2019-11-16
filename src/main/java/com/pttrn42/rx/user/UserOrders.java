package com.pttrn42.rx.user;

import com.google.common.collect.ImmutableList;
import io.reactivex.Maybe;

public class UserOrders {

	public static Maybe<Order> lastOrderOf(User user) {
		return Maybe.fromCallable(() -> {
			if (user.getId() > 10) {
				return new Order(ImmutableList.of(new Item("Item of " + user.getId())));
			} else {
				return null;
			}
		});
	}

}
