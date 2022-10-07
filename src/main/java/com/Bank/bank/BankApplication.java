package com.Bank.bank;

import java.util.List;
import java.util.stream.Stream;

import com.Bank.bank.entities.Account;
import com.Bank.bank.services.AccountService;
//import com.Bank.bank.services.OperationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
@OpenAPIDefinition(servers = {@Server(url = "/api/bank/", description = "Default Server URL")})
@SpringBootApplication
@ComponentScan(basePackages="com.Bank")
public class BankApplication {
    int numAccount=1000;
	public static void main(String[] args) {
		SpringApplication.run(BankApplication.class, args);
	}
	
	@Bean
    CommandLineRunner commandLineRunner(AccountService accountService){
    	return args -> {
           Stream.of("Hassan","Imane","Mohamed").forEach(name->{
        	   
               Account account=new Account();
               account.setCustomerName(name);
               account.setCustomerEmail(name+"@gmail.com");
               account.setNumAccount(String.valueOf(numAccount));
               account.setBalance(Math.random()*90000);
               numAccount+=1000;
               try {
				accountService.saveAccount(account);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			//} catch (CustomerNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
              
           });
            List<Account> accounts = accountService.AccountList();
            for (Account account:accounts){
                for (int i = 0; i <2 ; i++) {
                    Long accountId;
                    //if(account instanceof AccountDTO){
                        accountId= account.getId();
                    //} else{
                        //accountId=((AccountDTO) account).getId();
                    //}
                    accountService.credit(account.getNumAccount(),2000);
                    accountService.debit(account.getNumAccount(),1000);
                }
            }
        };
    }
	
	  @Bean
	    public OpenAPI openApi() {
			return new OpenAPI()
					.info(new Info()
							.title("Bank")
							.description("API Bank")
							.version("v1.0")
							//.contact(new Contact().name("Arun").url("https://asbnotebook.com")
									//.email("asbnotebook@gmail.com"))
							.termsOfService("TOC")
							.license(new License().name("License").url("#"))
					);
	    }

}
