package com.fastcampus.pt.job.notification;

import com.fastcampus.pt.repository.booking.BookingEntity;
import com.fastcampus.pt.repository.notification.NotificationEntity;
import com.fastcampus.pt.repository.notification.NotificationEvent;
import com.fastcampus.pt.repository.notification.NotificationModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.persistence.EntityManagerFactory;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SendNotificationBeforeClassJobConfig {
    private final int CHUNK_SIZE = 10;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final SendNotificationItemWriter sendNotificationItemWriter;

    @Bean
    public Job sendNotificationBeforeClassJob() {
        return this.jobBuilderFactory.get("SendNotificationItemWriter")
                .start(addNotificationStep())
                .next(sendNotificationStep())
                .build();
    }

    @Bean
    public Step addNotificationStep(){
        return this.stepBuilderFactory.get("addNotificationStep")
                .<BookingEntity, NotificationEntity>chunk(CHUNK_SIZE)
                .reader(addNotificationItemReader())
                .processor(addNotificationItemProcessor())
                .writer(addNotificationItemWriter())
                .build();
    }
    /**
     * JpaPagingItemReader: JPA?????? ???????????? ????????? ???????????????.
     * ?????? ??? pageSize?????? ???????????? ?????? PagingItemReader??? ??????????????? Thread-safe ?????????.
     */
    public JpaPagingItemReader<BookingEntity> addNotificationItemReader() {
        // ??????(status)??? ???????????????, ????????????(startedAt)??? 10??? ??? ???????????? ????????? ?????? ????????? ?????????.
        return new JpaPagingItemReaderBuilder<BookingEntity>()
                .name("addNotificationItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("select b from BookingEntity b join fetch b.userEntity where b.status = :status and b.startedAt <= startedAt <= :startedAt order by b.bookingSeq")
                .build();
    }

    public ItemProcessor<BookingEntity,NotificationEntity> addNotificationItemProcessor() {
        return bookingEntity -> NotificationModelMapper.INSTANCE.toNotificationEntity(bookingEntity, NotificationEvent.BEFORE_CLASS);
    }

    public JpaItemWriter<NotificationEntity> addNotificationItemWriter() {
        return new JpaItemWriterBuilder<NotificationEntity>()
                .entityManagerFactory(entityManagerFactory)
                .build();

    }
    @Bean
    public Step sendNotificationStep(){
        return this.stepBuilderFactory.get("sedNotificationStep")
                .<NotificationEntity,NotificationEntity>chunk(CHUNK_SIZE)
                .reader(sendNotificationItemReader())
                .writer(sendNotificationItemWriter)
                // taskExecutor??? ?????? ??????????????? step??? ????????? ??? ????????? ????????? ????????? ????????? ????????????.
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean
    public SynchronizedItemStreamReader<NotificationEntity> sendNotificationItemReader(){
        // ?????????(evenet)??? ?????? ?????????, ?????? ??????(sent)??? ???????????? ????????? ?????? ????????? ??????.
        JpaCursorItemReader<NotificationEntity> itemReader = new JpaCursorItemReaderBuilder<NotificationEntity>()
                .name("sendNotificationItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select n from NotificationEntity n where n.event = :event and n.sent = :sent")
                .parameterValues(Map.of("event",NotificationEvent.BEFORE_CLASS, "sent", false))
                .build();

        return new SynchronizedItemStreamReaderBuilder<NotificationEntity>()
                .delegate(itemReader) //delegate : ?????? ????????? ????????? ???????????? ?????? ?????? ????????? ????????? ???????????? ???
                .build();
    }
}
