package com.springbatch.localpartitioning.config;

import com.springbatch.localpartitioning.listener.CustomerSkipListener;
import com.springbatch.localpartitioning.mapper.CustomerRowMapper;
import com.springbatch.localpartitioning.model.ChildJobMeta;
import com.springbatch.localpartitioning.model.Customer;
import com.springbatch.localpartitioning.partitioner.ColumnRangePartitioner;
import com.springbatch.localpartitioning.processor.CustomerProcessor;
import com.springbatch.localpartitioning.writer.ConsoleItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    private  static int a = 1;



    @Bean
    public ColumnRangePartitioner partitioner()
    {
        ColumnRangePartitioner columnRangePartitioner = new ColumnRangePartitioner();
        columnRangePartitioner.setColumn("id");
        columnRangePartitioner.setDataSource(dataSource);
        columnRangePartitioner.setTable("customer");
        return columnRangePartitioner;
    }


    @StepScope
    @Bean
    public JdbcPagingItemReader<Customer> pagingItemReader(
            @Value("#{jobParameters['minValue']}") Long minValue,
            @Value("#{jobParameters['maxValue']}") Long maxValue,
             @Value("#{jobParameters['partitionId']}") Long partitionId
    )
    {

//        var minValue=1;
//        var maxValue=5;
        System.out.println("reading " + minValue + " to " + maxValue + " from partition " + partitionId);
//        if(minValue == 1)
//            throw  new ArithmeticException("TEst");


        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);

//        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("id, firstName, lastName, birthdate");
        queryProvider.setFromClause("from customer");
        queryProvider.setWhereClause("where id >= " + minValue + " and id <= " + maxValue);
        queryProvider.setSortKeys(sortKeys);

        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(this.dataSource);
        reader.setFetchSize(1000);
        reader.setRowMapper(new CustomerRowMapper());
        reader.setQueryProvider(queryProvider);

        return reader;
//        return  null;
    }
    @Bean
    @StepScope
    public JdbcBatchItemWriter<Customer> customerItemWriter()
    {
        JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO NEW_CUSTOMER VALUES (:id, :firstName, :lastName, :birthdate)");

        itemWriter.setItemSqlParameterSourceProvider
                (new BeanPropertyItemSqlParameterSourceProvider<>());
        itemWriter.afterPropertiesSet();

        return itemWriter;

//        JdbcBatchItemWriter<Customer> itemWriter1 = new JdbcBatchItemWriter<>();
//        itemWriter1.setDataSource(dataSource);
//        itemWriter1.setSql("UPDATE CUSTOMER SET firstName='PROCESSED' WHERE id=:id");
//
//        itemWriter.setItemSqlParameterSourceProvider
//                (new BeanPropertyItemSqlParameterSourceProvider<>());
//        itemWriter.afterPropertiesSet();
//
//        return itemWriter;
////        return  null;
    }


    @Bean
    public CompositeItemWriter compositeWriter()  {


        CompositeItemWriter compositeItemWriter = new CompositeItemWriter();
        List<ItemWriter> writers = new ArrayList<ItemWriter>();
        writers.add(firstTableWriter());
        writers.add(secondTableWriter());
        compositeItemWriter.setDelegates(writers);
        return compositeItemWriter;
    }

    @Bean
    public JdbcBatchItemWriter<Customer> firstTableWriter() {
        JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO NEW_CUSTOMER VALUES (:id, :firstName, :lastName, :birthdate)");
        itemWriter.setItemSqlParameterSourceProvider
                (new BeanPropertyItemSqlParameterSourceProvider<>());
        itemWriter.afterPropertiesSet();
        return itemWriter;
    }
    @Bean
    public JdbcBatchItemWriter<Customer> secondTableWriter() {
        JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("UPDATE CUSTOMER SET firstName='PROCESSED' WHERE id=:id");
        itemWriter.setItemSqlParameterSourceProvider
                (new BeanPropertyItemSqlParameterSourceProvider<>());
        itemWriter.afterPropertiesSet();
        return itemWriter;
    }
    @Bean
    @StepScope
    public ConsoleItemWriter<Customer> customerItemWriter1()
    {
        ConsoleItemWriter<Customer> itemWriter = new ConsoleItemWriter<>();
////        itemWriter.setDataSource(dataSource);
////        itemWriter.setSql("INSERT INTO NEW_CUSTOMER VALUES (:id, :firstName, :lastName, :birthdate)");
////
////        itemWriter.setItemSqlParameterSourceProvider
////                (new BeanPropertyItemSqlParameterSourceProvider<>());
////        itemWriter.afterPropertiesSet();
////
        return itemWriter;
//        return  null;
    }

    // Master
    @Bean
    public Step step1()
    {
        return stepBuilderFactory.get("step1")
                .partitioner("step2", partitioner())
//                .step(workerStep())
                .step(step2())
                .gridSize(4)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }


    public CustomerProcessor processor() {
        return new CustomerProcessor();
    }
    // slave step
    @Bean
    public Step workerStep(
            //            @Value("#{jobParameters['minValue']}") Long minValue,
//            @Value("#{jobParameters['maxValue']}") Long maxValue,
//            @Value("#{jobParameters['partitionId']}") Long partitionId
           )
    {
        return stepBuilderFactory.get("workerStep")
                .<Customer, Customer>chunk(1)
                .reader(pagingItemReader(
                        null
                        , null
                        , null
                ))
                .processor(processor())
                .writer(customerItemWriter())
                //.writer(compositeWriter())
                .faultTolerant()
                .skipPolicy(new AlwaysSkipItemSkipPolicy())
                .retry(Exception.class)
                .retryLimit(5)
                .listener(new CustomerSkipListener())
                .build();
    }

    @Bean
    @StepScope
    public Tasklet triggerChildJob(
            @Value("#{stepExecutionContext['minValue']}") Long minValue,
            @Value("#{stepExecutionContext['maxValue']}") Long maxValue,
            @Value("#{stepExecutionContext['partitionId']}") Long partitionId) {
        return (new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {



                final String uri = "http://localhost:8083";
                ChildJobMeta childJobMeta = new ChildJobMeta(minValue, maxValue, partitionId);
                WebClient webClient = WebClient.create(uri);




                    var test =   webClient.post()
                            .uri("/launch/child/job")
                            .body(Mono.just(childJobMeta), ChildJobMeta.class)
                            .retrieve().bodyToMono(String.class);

                    test.subscribe(tweet -> System.out.println(tweet));
              

               // System.out.println(test);

              //          var test =   webClient.get()
//                        .uri("/launch/child/job")
//                        .retrieve()
//                        .bodyToMono(String.class).block();
//                System.out.println(test);


                return RepeatStatus.FINISHED;
            }
        });
    }



    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
                .tasklet(triggerChildJob(null, null, null))
                .build();
    }
    @Bean
    public Job job() {
        return jobBuilderFactory.get("job")
                .start(step1())
                .build();
    }
    @Bean
    public Job childJob() {
        return jobBuilderFactory.get("childJob")
                .start(workerStep())
                .build();
    }
}
