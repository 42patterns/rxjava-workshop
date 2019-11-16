package com.pttrn42.rx.email;

import io.reactivex.disposables.Disposable;

import java.util.function.Consumer;

public interface EmailClient extends Disposable {

	void onEmail(Consumer<String> msg);

}
