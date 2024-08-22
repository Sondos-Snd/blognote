package com.opensource.blognote.feedback;

import com.opensource.blognote.book.Book;
import com.opensource.blognote.book.BookRepository;
import com.opensource.blognote.exception.OperationNotPermittedException;
import com.opensource.blognote.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;
    private FeedbackRepository feedbackRepository;

    public Integer saveFeedback(FeedbackRequest request, Authentication connectedUser) {
        Book bookForfeedback = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + request.bookId()));

        if (bookForfeedback.isArchived()) {
            throw new OperationNotPermittedException("You cannnot give feedback for the book with id: " + request.bookId());
        }

        //a book you own
        User user = (User) connectedUser.getPrincipal();
        if (Objects.equals(bookForfeedback.getOwner(),user)) {
            throw new OperationNotPermittedException("You cannnot give feedback for a book that you own");
        }

        Feedback feedback = feedbackMapper.toFeedback(request);

        return feedbackRepository.save(feedback).getId();

    }
}
