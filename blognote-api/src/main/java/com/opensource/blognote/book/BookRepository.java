package com.opensource.blognote.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {

    @Query("""
            SELECT book
            FROM Book
            Where book.shareable = true
            AND book.archived= false
            AND book.owner.id != :userId        
            """)
    Page<Book> findAllDisplayableBooks(Pageable pageable, Integer userId);

    @Query("""
            SELECT book
            FROM Book
            Where book.id = :bookId
            AND book.owner.id = :userId        
            """)
    Optional<Book> findById(Integer bookId);
}
