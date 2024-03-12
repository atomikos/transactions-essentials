/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.spring.integrationtest;

import java.io.Closeable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SampleAtomikosApplication {

    public static void main(String... args) throws Exception {
        ApplicationContext context = SpringApplication.run(SampleAtomikosApplication.class, args);
        AccountService service = context.getBean(AccountService.class);
        AccountRepository repository = context.getBean(AccountRepository.class);
        service.createAccountAndNotify("josh");
        System.out.println("Count is " + repository.count());
        try {
            service.createAccountAndNotify("error");
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println("Count is " + repository.count());
        Thread.sleep(100);
        ((Closeable) context).close();
    }

}
