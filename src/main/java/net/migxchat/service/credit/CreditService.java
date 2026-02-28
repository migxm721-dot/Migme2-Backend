package net.migxchat.service.credit;

import net.migxchat.exception.InsufficientCreditException;
import net.migxchat.exception.UserNotFoundException;
import net.migxchat.model.credit.Credit;
import net.migxchat.model.credit.Transaction;
import net.migxchat.repository.CreditRepository;
import net.migxchat.repository.TransactionRepository;
import net.migxchat.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CreditService {

    private static final Logger log = LoggerFactory.getLogger(CreditService.class);

    @Autowired
    private CreditRepository creditRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    public Credit getBalance(String userId) {
        return creditRepository.findByUserId(userId).orElseGet(() -> {
            Credit credit = new Credit();
            credit.setUserId(userId);
            credit.setBalance(BigDecimal.ZERO);
            credit.setTotalEarned(BigDecimal.ZERO);
            credit.setTotalSpent(BigDecimal.ZERO);
            return creditRepository.save(credit);
        });
    }

    @Transactional
    public Credit rechargeCredit(String userId, BigDecimal amount, String paymentMethod, String transactionId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Credit credit = getBalance(userId);
        credit.setBalance(credit.getBalance().add(amount));
        credit.setTotalEarned(credit.getTotalEarned().add(amount));
        creditRepository.save(credit);

        Transaction transaction = new Transaction();
        transaction.setType("recharge");
        transaction.setAmount(amount);
        transaction.setToUserId(userId);
        transaction.setPaymentMethod(paymentMethod);
        transaction.setDescription("Credit recharge via " + paymentMethod);
        transaction.setStatus("completed");
        transactionRepository.save(transaction);

        return credit;
    }

    @Transactional
    public void deductCredit(String userId, BigDecimal amount, String description) {
        Credit credit = getBalance(userId);
        if (credit.getBalance().compareTo(amount) < 0) {
            throw new InsufficientCreditException("Insufficient credits");
        }
        credit.setBalance(credit.getBalance().subtract(amount));
        credit.setTotalSpent(credit.getTotalSpent().add(amount));
        creditRepository.save(credit);

        Transaction transaction = new Transaction();
        transaction.setType("deduct");
        transaction.setAmount(amount);
        transaction.setFromUserId(userId);
        transaction.setDescription(description);
        transaction.setStatus("completed");
        transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction transferCredit(String senderId, String recipientUsername, BigDecimal amount, String message) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        String recipientId = userRepository.findByUsername(recipientUsername)
            .map(u -> u.getUserId())
            .orElseThrow(() -> new UserNotFoundException("Recipient not found: " + recipientUsername));

        Credit senderCredit = getBalance(senderId);
        if (senderCredit.getBalance().compareTo(amount) < 0) {
            throw new InsufficientCreditException("Insufficient credits for transfer");
        }

        senderCredit.setBalance(senderCredit.getBalance().subtract(amount));
        senderCredit.setTotalSpent(senderCredit.getTotalSpent().add(amount));
        creditRepository.save(senderCredit);

        Credit recipientCredit = getBalance(recipientId);
        recipientCredit.setBalance(recipientCredit.getBalance().add(amount));
        recipientCredit.setTotalEarned(recipientCredit.getTotalEarned().add(amount));
        creditRepository.save(recipientCredit);

        Transaction transaction = new Transaction();
        transaction.setType("transfer");
        transaction.setAmount(amount);
        transaction.setFromUserId(senderId);
        transaction.setToUserId(recipientId);
        transaction.setDescription(message != null ? message : "Credit transfer to " + recipientUsername);
        transaction.setStatus("completed");
        return transactionRepository.save(transaction);
    }
}
