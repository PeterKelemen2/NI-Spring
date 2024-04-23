package com.esen.bookstore.service;

import com.esen.bookstore.model.Book;
import com.esen.bookstore.repository.BookstoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookstoreService {
    private final BookstoreRepository bookstoreRepository;

    @Transactional
    public void removeBookFromInventories(Book book){
        bookstoreRepository.findAll()
                .forEach(bookstore -> {
                    bookstore.getInventory().remove(book);
                    bookstoreRepository.save(bookstore);
                });
    }
}
