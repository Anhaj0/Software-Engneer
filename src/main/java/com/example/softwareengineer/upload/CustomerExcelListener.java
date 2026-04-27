package com.example.softwareengineer.upload;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CustomerExcelListener extends AnalysisEventListener<CustomerUploadRow> {

    private final List<CustomerUploadRow> buffer = new ArrayList<>();
    private final int batchSize;
    private final Consumer<List<CustomerUploadRow>> batchConsumer;

    public CustomerExcelListener(int batchSize, Consumer<List<CustomerUploadRow>> batchConsumer) {
        this.batchSize = batchSize;
        this.batchConsumer = batchConsumer;
    }

    @Override
    public void invoke(CustomerUploadRow data, AnalysisContext context) {
        buffer.add(data);
        if (buffer.size() >= batchSize) {
            flush();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        flush();
    }

    private void flush() {
        if (buffer.isEmpty()) {
            return;
        }
        batchConsumer.accept(List.copyOf(buffer));
        buffer.clear();
    }
}
