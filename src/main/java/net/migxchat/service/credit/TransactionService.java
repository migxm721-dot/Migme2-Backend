package net.migxchat.service.credit;

import net.migxchat.model.credit.Transaction;
import net.migxchat.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Page<Transaction> getUserTransactions(String userId, Pageable pageable) {
        return transactionRepository.findByUserId(userId, pageable);
    }
}
