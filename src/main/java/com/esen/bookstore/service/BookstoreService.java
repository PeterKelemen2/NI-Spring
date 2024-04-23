package com.esen.bookstore.service;

import com.esen.bookstore.model.Book;
import com.esen.bookstore.model.Bookstore;
import com.esen.bookstore.repository.BookstoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BookstoreService {
    private final BookstoreRepository bookstoreRepository;
    private final BookstoreService bookstoreService;

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
}
