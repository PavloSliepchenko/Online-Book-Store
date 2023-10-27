package com.example.onlinebookstore.repository.impl;

import com.example.onlinebookstore.exception.EntityNotFoundException;
import com.example.onlinebookstore.model.Book;
import com.example.onlinebookstore.repository.BookRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {
    private final EntityManagerFactory entityManagerFactory;

    @Override
    public Book save(Book book) {
        EntityTransaction transaction = null;
        try (EntityManager manager = entityManagerFactory.createEntityManager()) {
            transaction = manager.getTransaction();
            transaction.begin();
            manager.persist(book);
            transaction.commit();
            return book;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new EntityNotFoundException("Cannot add a book " + book, e);
        }
    }

    @Override
    public List<Book> findAll() {
        try (EntityManager manager = entityManagerFactory.createEntityManager()) {
            return manager.createQuery("SELECT b FROM Book b", Book.class)
                    .getResultList();
        } catch (Exception e) {
            throw new EntityNotFoundException("Cannot get all books", e);
        }
    }

    @Override
    public Optional<Book> findBookById(Long id) {
        try (EntityManager manager = entityManagerFactory.createEntityManager()) {
            Book book = manager.find(Book.class, id);
            return Optional.ofNullable(book);
        } catch (Exception e) {
            throw new EntityNotFoundException("Cannot find a book by id " + id, e);
        }
    }
}
