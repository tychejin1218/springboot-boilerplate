package com.example.boilerplate.config;

import com.example.boilerplate.common.constants.Constants;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class MainRoutingDataSource extends AbstractRoutingDataSource {

  @Override
  protected Object determineCurrentLookupKey() {
    return TransactionSynchronizationManager.isCurrentTransactionReadOnly()
        ? Constants.MAIN_READER_KEY : Constants.MAIN_WRITER_KEY;
  }
}
