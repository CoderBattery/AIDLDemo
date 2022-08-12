// IBookUpdateListener.aidl
package com.coderbattery.aidllib;

import com.coderbattery.aidllib.Book;

// Declare any non-default types here with import statements

interface IBookUpdateListener {

    void OnBookUpdate(in Book book);
}