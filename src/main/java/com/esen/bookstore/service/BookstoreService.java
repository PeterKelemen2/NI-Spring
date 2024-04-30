package com.esen.bookstore.service;

import com.esen.bookstore.model.Book;
import com.esen.bookstore.model.Bookstore;
import com.esen.bookstore.repository.BookRepository;
import com.esen.bookstore.repository.BookstoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BookstoreService {
    private final BookstoreRepository bookstoreRepository;
    private final BookRepository bookRepository;

    @Transactional
    public void removeBookFromInventories(Book book) {
        bookstoreRepository.findAll()
                .forEach(bookstore -> {
                    bookstore.getInventory().remove(book);
                    bookstoreRepository.save(bookstore);
                });
    }

    public void save(Bookstore bookstore) {
        bookstoreRepository.save(bookstore);
    }

    public List<Bookstore> findAll() {
        return bookstoreRepository.findAll();
    }


    public void deleteBookstore(Long id) {
        var bookstore = bookstoreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find book."));
        bookstoreRepository.delete(bookstore);
    }

    public void updateBookStore(Long id, String loc, Double price, Double money) {
        if (Stream.of(loc, price, money).allMatch(Objects::isNull)) {
            throw new UnsupportedOperationException("There is nothing to update");
        }

        var bookstore = bookstoreRepository.findById(id).orElseThrow(() -> new RuntimeException("Cannot find bookstore"));

        if (loc != null) {
            bookstore.setLocation(loc);
        }

        if (price != null) {
            bookstore.setPriceModifier(price);
        }

        if (money != null) {
            bookstore.setMoneyInCashRegister(money);
        }

        bookstoreRepository.save(bookstore);
    }

    public Map<Bookstore, Double> findPrices(Long id) {
        var book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Cannot find book"));
        var bookStores = bookstoreRepository.findAll();

        Map<Bookstore, Double> priceMap = new HashMap<>();
        for (var b : bookStores) {
            if (b.getInventory().containsKey(book)) {
                Double currPrice = book.getPrice() * b.getPriceModifier();
                priceMap.put(b, currPrice);
            }
        }
        return priceMap;
    }

    public Map<Book, Integer> getStock(Long bookstoreId) {
        var bookstores = bookstoreRepository.findById(bookstoreId).orElseThrow(() -> new RuntimeException("Cannot find book"));
        return bookstores.getInventory();
    }

    public void changeStock(Long bookstoreId, Long bookId, Integer amount) {
        var bookstore = bookstoreRepository.findById(bookstoreId).orElseThrow(() -> new RuntimeException("Cannot find book"));
        var book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Cannot find book"));
        if (bookstore.getInventory().containsKey(book)) {
            var entry = bookstore.getInventory().get(book);
            if (entry + amount < 0) {
                throw new UnsupportedOperationException("Invalid amount");
            }
            bookstore.getInventory().replace(book, entry + amount);
        } else {
            if (amount > 0) {
                bookstore.getInventory().put(book, amount);
            } else {
                throw new UnsupportedOperationException("Invalid amount");
            }
        }
        bookstoreRepository.save(bookstore);
    }

}
