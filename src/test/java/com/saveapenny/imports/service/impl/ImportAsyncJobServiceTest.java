package com.saveapenny.imports.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.saveapenny.imports.entity.Import;
import com.saveapenny.imports.entity.ImportRow;
import com.saveapenny.imports.entity.ImportRowStatus;
import com.saveapenny.imports.entity.ImportStatus;
import com.saveapenny.imports.repository.ImportRepository;
import com.saveapenny.imports.repository.ImportRowRepository;
import com.saveapenny.transaction.dto.TransactionResponse;
import com.saveapenny.transaction.exception.InvalidTransferException;
import com.saveapenny.transaction.service.TransactionService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ImportAsyncJobServiceTest {

    @Mock
    private ImportRepository importRepository;

    @Mock
    private ImportRowRepository importRowRepository;

    @Mock
    private TransactionService transactionService;

    private final ImportRowParser importRowParser = new ImportRowParser();

    private ImportAsyncJobService importAsyncJobService;

    @BeforeEach
    void setUp() {
        importAsyncJobService = new ImportAsyncJobService(
                importRepository,
                importRowRepository,
                transactionService,
                importRowParser);
    }

    @Test
    void processImportAsync_createsTransactionsAndMarksDuplicateRowsAsSkipped() {
        UUID importId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID categoryOneId = UUID.randomUUID();
        UUID categoryTwoId = UUID.randomUUID();
        UUID categoryThreeId = UUID.randomUUID();
        Import importEntity = Import.builder()
                .id(importId)
                .userId(userId)
                .status(ImportStatus.RUNNING)
                .totalRows(3)
                .build();

        ImportRow row1 = ImportRow.builder()
                .id(UUID.randomUUID())
                .importId(importId)
                .rowNumber(2)
                .rawData("EXPENSE,2026-05-01,25.00,USD," + accountId + "," + categoryOneId + ",Coffee")
                .status(ImportRowStatus.VALID)
                .build();

        ImportRow row2 = ImportRow.builder()
                .id(UUID.randomUUID())
                .importId(importId)
                .rowNumber(3)
                .rawData("EXPENSE,2026-05-01,25.00,USD," + accountId + "," + categoryTwoId + ",Coffee")
                .status(ImportRowStatus.VALID)
                .build();

        ImportRow row3 = ImportRow.builder()
                .id(UUID.randomUUID())
                .importId(importId)
                .rowNumber(4)
                .rawData("EXPENSE,2026-05-02,10.00,USD," + accountId + "," + categoryThreeId + ",Snack")
                .status(ImportRowStatus.VALID)
                .build();

        when(importRepository.findById(importId)).thenReturn(Optional.of(importEntity));
        when(importRowRepository.findAllByImportIdOrderByRowNumberAsc(importId)).thenReturn(List.of(row1, row2, row3));
        when(transactionService.create(eq(userId), any()))
                .thenReturn(TransactionResponse.builder().id(UUID.randomUUID()).build());

        importAsyncJobService.processImportAsync(importId);

        assertEquals(ImportRowStatus.IMPORTED, row1.getStatus());
        assertEquals(ImportRowStatus.SKIPPED, row2.getStatus());
        assertEquals("Duplicate transaction detected", row2.getErrorMessage());
        assertEquals(ImportRowStatus.IMPORTED, row3.getStatus());

        assertEquals(2, importEntity.getImportedRows());
        assertEquals(0, importEntity.getFailedRows());
        assertEquals(ImportStatus.COMPLETED, importEntity.getStatus());

        verify(transactionService, times(2)).create(eq(userId), any());
        verify(importRowRepository, times(3)).save(any(ImportRow.class));
        verify(importRepository).save(importEntity);
    }

    @Test
    void processImportAsync_marksRowFailedWhenTransactionCreationFailsAndContinues() {
        UUID importId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID categoryOneId = UUID.randomUUID();
        UUID categoryTwoId = UUID.randomUUID();

        Import importEntity = Import.builder()
                .id(importId)
                .userId(userId)
                .status(ImportStatus.RUNNING)
                .totalRows(2)
                .build();

        ImportRow row1 = ImportRow.builder()
                .id(UUID.randomUUID())
                .importId(importId)
                .rowNumber(2)
                .rawData("EXPENSE,2026-05-01,250.00,USD," + accountId + "," + categoryOneId + ",Large purchase")
                .status(ImportRowStatus.VALID)
                .build();

        ImportRow row2 = ImportRow.builder()
                .id(UUID.randomUUID())
                .importId(importId)
                .rowNumber(3)
                .rawData("EXPENSE,2026-05-02,10.00,USD," + accountId + "," + categoryTwoId + ",Snack")
                .status(ImportRowStatus.VALID)
                .build();

        when(importRepository.findById(importId)).thenReturn(Optional.of(importEntity));
        when(importRowRepository.findAllByImportIdOrderByRowNumberAsc(importId)).thenReturn(List.of(row1, row2));
        doThrow(new InvalidTransferException("Account not found or inactive: " + accountId))
                .when(transactionService)
                .create(eq(userId), argThat(request -> request.getAmount().doubleValue() == 250.0d));
        when(transactionService.create(eq(userId), argThat(request -> request.getAmount().doubleValue() == 10.0d)))
                .thenReturn(TransactionResponse.builder().id(UUID.randomUUID()).build());

        importAsyncJobService.processImportAsync(importId);

        assertEquals(ImportRowStatus.FAILED, row1.getStatus());
        assertEquals("Account not found or inactive: " + accountId, row1.getErrorMessage());
        assertEquals(ImportRowStatus.IMPORTED, row2.getStatus());
        assertEquals(1, importEntity.getImportedRows());
        assertEquals(1, importEntity.getFailedRows());
        assertEquals(ImportStatus.COMPLETED, importEntity.getStatus());

        verify(transactionService, times(2)).create(eq(userId), any());
        verify(importRowRepository, times(2)).save(any(ImportRow.class));
        verify(importRepository).save(importEntity);
    }
}
