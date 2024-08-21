package com.opensource.blognote.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookTransactionRepository extends JpaRepository<BookTransactionHistory,Integer> {

    @Query("""
            SELECT bookTransaction
            FROM BookTransactionHistory
            Where bookTransaction.user.id = :userId  
            """)
    Page<BookTransactionHistory> finAllByBorrowedBooks(Pageable pageable, Integer userId);
    //            AND bookTransaction.returned = false

}
