package examsApp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import examsApp.model.Account;
import examsApp.service.AccountService;
import examsApp.util.constants.Authority;

@Component
public class SeedData implements CommandLineRunner{

    @Autowired
    private AccountService accountService;

    @Override
    public void run(String... args) throws Exception {
        Account user01 = new Account();
        Account user02 = new Account();

        user01.setEmail("user@user.com");
        user01.setPassword("pass987");
        user01.setAuthorities(Authority.USER.toString());
        accountService.save(user01);

        user02.setEmail("admin@admin.com");
        user02.setPassword("pass987");
        user02.setAuthorities(Authority.ADMIN.toString() +" "+Authority.USER.toString() );
        accountService.save(user02);
        
    }
    
}
