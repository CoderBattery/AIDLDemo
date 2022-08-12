// BookManager.aidl
package com.coderbattery.aidllib;

import com.coderbattery.aidllib.Book;
import com.coderbattery.aidllib.IBookUpdateListener;

// Declare any non-default types here with import statements

interface BookManager {

    boolean addBook(in Book book);

    boolean removeBook(in Book book);

    void registerListener(IBookUpdateListener listener);

    void unregisterListener(IBookUpdateListener listener);
}