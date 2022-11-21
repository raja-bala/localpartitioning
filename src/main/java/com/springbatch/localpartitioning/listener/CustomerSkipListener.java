package com.springbatch.localpartitioning.listener;

import com.springbatch.localpartitioning.model.Customer;
import org.springframework.batch.core.*;
import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.core.annotation.OnSkipInWrite;

public class CustomerSkipListener  implements SkipListener{

    @Override
    public void onSkipInRead(Throwable throwable) {
        System.out.println("READ***************");
    }

    @Override
    public void onSkipInWrite(Object o, Throwable throwable) {
        System.out.println("WRITE*******************" + o.toString());
        System.out.println("WRITE ERROR*******************" + throwable.getMessage());
    }

    @Override
    public void onSkipInProcess(Object o, Throwable throwable) {
        System.out.println("PROCESS*******************");
    }
}
