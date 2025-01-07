package com.transfer.controller;

import com.transfer.dto.TransactionRequestDTO;
import com.transfer.dto.TransactionResponseDTO;
import com.transfer.entity.Transaction;
import com.transfer.exception.custom.ResourceNotFoundException;
import com.transfer.exception.response.ErrorDetails;
import com.transfer.service.ITransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@CrossOrigin
public class TransactionController {

    private final ITransactionService transactionService;

    @Operation(summary = "Transfer money between accounts")
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = TransactionResponseDTO.class), mediaType = "application/json")})
    @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ErrorDetails.class), mediaType = "application/json")})
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDTO> transferMoney(@RequestBody TransactionRequestDTO request) throws ResourceNotFoundException {
        TransactionResponseDTO responseDTO = transactionService.transferMoney(request);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Get Transaction History by Account ID")
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = TransactionResponseDTO.class), mediaType = "application/json")})
    @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ErrorDetails.class), mediaType = "application/json")})
    @GetMapping("/history/{accountId}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionHistory(@PathVariable Long accountId) throws ResourceNotFoundException {
        List<Transaction> transactions = transactionService.getTransactionHistory(accountId);

        List<TransactionResponseDTO> responseDTOs = transactions.stream().map(transaction -> {
            TransactionResponseDTO responseDTO = new TransactionResponseDTO();
            responseDTO.setFromAccountNumber(transaction.getFromAccount().getAccountNumber());  // Use account number
            responseDTO.setToAccountNumber(transaction.getToAccount().getAccountNumber());      // Use account number
            responseDTO.setFromAccountName(transaction.getFromAccount().getCustomer().getName());
            responseDTO.setToAccountName(transaction.getToAccount().getCustomer().getName());
            responseDTO.setAmount(transaction.getAmount());
            responseDTO.setTransactionDate(transaction.getTransactionDate().toInstant().toString()); // Format to ISO 8601
            return responseDTO;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }
}
