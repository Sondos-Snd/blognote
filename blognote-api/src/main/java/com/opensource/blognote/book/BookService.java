package com.opensource.blognote.book;

import com.opensource.blognote.exception.OperationNotPermittedException;
import com.opensource.blognote.file.FileStorageService;
import com.opensource.blognote.history.BookTransactionHistory;
import com.opensource.blognote.history.BookTransactionRepository;
import com.opensource.blognote.user.User;
import com.opensource.blognote.common.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookService {

    private BookMapper bookMapper;
    private BookRepository bookRepository;
    private BookTransactionRepository bookTransactionRepository;
    private FileStorageService fileStorageService;

    public Integer save(BookRequest request, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Book book = bookMapper.toBook(request);
        book.setOwner(user);

        return bookRepository.save(book).getId();
    }

    public BookResponse findById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException());
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponse).toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()), pageable);
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponse).toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = bookTransactionRepository.finAllByBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> borrowedBookResponses = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse).toList();
        return new PageResponse<>(
                borrowedBookResponses,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }


    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = bookTransactionRepository.finAllByReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> borrowedBookResponses = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse).toList();
        return new PageResponse<>(
                borrowedBookResponses,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book bookToUpdate = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        User user = (User) connectedUser.getPrincipal();
        // only owner can update
        if (!Objects.equals(user.getId(), bookToUpdate.getOwner().getId())) {
            throw new OperationNotPermittedException("You cannnot update a book you don't own");
        }
        bookToUpdate.setShareable(!bookToUpdate.isShareable());
        bookRepository.save(bookToUpdate);

        return bookId;
    }

    public Integer updateArchiveStatus(Integer bookId, Authentication connectedUser) {
        Book bookToArchive = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        User user = (User) connectedUser.getPrincipal();
        // only owner can archive
        if (!Objects.equals(user.getId(), bookToArchive.getOwner().getId())) {
            throw new OperationNotPermittedException("You cannnot archive a book you don't own");
        }
        bookToArchive.setArchived(!bookToArchive.isArchived());
        bookRepository.save(bookToArchive);

        return bookId;
    }

    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book bookToBorrow = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        if (!bookToBorrow.isShareable() || bookToBorrow.isArchived()) {
            throw new OperationNotPermittedException("You cannnot borrow the book with id: " + bookId);
        }
        User user = (User) connectedUser.getPrincipal();
        if (Objects.equals(user.getId(), bookToBorrow.getOwner().getId())) {
            throw new OperationNotPermittedException("You cannnot borrow your own book");
        }

        final boolean isAlreadyBorrowed = bookTransactionRepository.isAlreadyBorrowedByUser(bookId, user.getId());

        if (isAlreadyBorrowed) {
            throw new OperationNotPermittedException("Already borrowed book");
        }

        BookTransactionHistory bookTransaction = BookTransactionHistory.builder()
                .book(bookToBorrow)
                .user(user)
                .returned(false)
                .returnApproved(false)
                .build();

        return bookTransactionRepository.save(bookTransaction).getId();
    }

    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {

        // checks at book entity level
        Book bookToBorrow = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        if (!bookToBorrow.isShareable() || bookToBorrow.isArchived()) {
            throw new OperationNotPermittedException("You cannnot borrow the book with id: " + bookId);
        }

        User user = (User) connectedUser.getPrincipal();
        if (Objects.equals(user.getId(), bookToBorrow.getOwner().getId())) {
            throw new OperationNotPermittedException("You cannnot borrow or return your own book");
        }

        // checks at book transaction level
        BookTransactionHistory bookToReturn = bookTransactionRepository.findByBookIdAnUserid(bookId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found for the book with id: " + bookId));

        bookToReturn.setReturned(true);

        return bookTransactionRepository.save(bookToReturn).getId();
    }

    public Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser) {

        // checks at book entity level
        Book bookToBorrow = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        if (!bookToBorrow.isShareable() || bookToBorrow.isArchived()) {
            throw new OperationNotPermittedException("You cannnot borrow the book with id: " + bookId);
        }

        User user = (User) connectedUser.getPrincipal();
        if (Objects.equals(user.getId(), bookToBorrow.getOwner().getId())) {
            throw new OperationNotPermittedException("You cannnot borrow / return or approve return for your own book");
        }

        // checks at book transaction level
        BookTransactionHistory bookToReturn = bookTransactionRepository.findByBookIdAnOwnerid(bookId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("The book is not returned yet."));

        bookToReturn.setReturnApproved(true);

        return bookTransactionRepository.save(bookToReturn).getId();
    }

    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));
        User user = (User) connectedUser.getPrincipal();

        var bookCover = fileStorageService.saveFile(file, user.getId());

        book.setBookCover(bookCover);
        bookRepository.save(book);

    }


}
