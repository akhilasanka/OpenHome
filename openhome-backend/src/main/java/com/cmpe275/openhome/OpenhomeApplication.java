package com.cmpe275.openhome;

import com.cmpe275.openhome.config.AppProperties;
import com.cmpe275.openhome.service.ReservationService;
import com.cmpe275.openhome.util.SystemDateTime;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
@EnableScheduling
public class OpenhomeApplication extends SpringBootServletInitializer {

    @Autowired
    private ReservationService reservationService;
    
	public static void main(String[] args) {
		SpringApplication.run(OpenhomeApplication.class, args);
		
		System.out.println("Server has started running on port 8080");
	}
	
    @Bean
    public TaskScheduler taskScheduler() {
        final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        return scheduler;
    }
    
    @Scheduled(cron="0 * * * * *")
    public void schedulePendingReservationProcess() {
    	LocalDateTime currentDateTime = SystemDateTime.getCurSystemTime();
    	
    	// schedule 'checkPendingReservations' at 3am
    	LocalDateTime threeAM = currentDateTime.withHour(3);
    	long diff = Math.abs(ChronoUnit.SECONDS.between(currentDateTime, threeAM));
    	if (diff < 30) {
    		reservationService.checkPendingReservations();
    	}
    }
    
    @Scheduled(cron="0 * * * * *")
    public void scheduleCheckedInReservationProcess() {
    	LocalDateTime currentDateTime = SystemDateTime.getCurSystemTime();
    	
    	// schedule 'checkPendingReservations' at 3am
    	LocalDateTime elevenAM = currentDateTime.withHour(11);
    	long diff = Math.abs(ChronoUnit.SECONDS.between(currentDateTime, elevenAM));
    	if (diff < 30) {
    		reservationService.checkCheckedInReservations();
    	}
    }
}
