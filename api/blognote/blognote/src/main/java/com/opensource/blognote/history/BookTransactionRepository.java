package com.opensource.blognote.history;

import com.opensource.blognote.exception.OperationNotPermittedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookTransactionRepository extends JpaRepository<BookTransactionHistory,Integer> {

    @Query("""
            SELECT bookTransaction
            FROM BookTransactionHistory
            Where bookTransaction.user.id = :userId  
            """)
    Page<BookTransactionHistory> finAllByBorrowedBooks(Pageable pageable, Integer userId);

    @Query("""
            SELECT bookTransaction
            FROM BookTransactionHistory
            Where bookTransaction.book.owner.id = :userId
            """)
    Page<BookTransactionHistory> finAllByReturnedBooks(Pageable pageable, Integer userId);

    @Query("""
            SELECT bookTransaction
            FROM BookTransactionHistory
            Where bookTransaction.book.id = :bookId
            """)
    Optional<BookTransactionHistory> findByBookId(Integer bookId);

    @Query("""
            SELECT COUNT(*) > 0 AS isBorrowed
            FROM BookTransactionHistory bt
            Where bt.book.id = :bookId
            AND bt.book.user.id = :userId
            AND bt.returnApproved = true
            """)
    boolean isAlreadyBorrowedByUser(Integer bookId, Integer id);
}
